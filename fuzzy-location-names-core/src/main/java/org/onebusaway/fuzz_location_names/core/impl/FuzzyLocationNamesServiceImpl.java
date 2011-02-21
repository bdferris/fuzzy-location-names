package org.onebusaway.fuzz_location_names.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.onebusaway.fuzz_location_names.core.model.LayerBean;
import org.onebusaway.fuzz_location_names.core.model.LocationBean;
import org.onebusaway.fuzz_location_names.core.model.LocationCollection;
import org.onebusaway.fuzz_location_names.core.model.LocationEntry;
import org.onebusaway.fuzz_location_names.core.services.FuzzyLocationNamesService;
import org.onebusaway.geospatial.model.CoordinateBounds;
import org.onebusaway.geospatial.model.CoordinatePoint;
import org.onebusaway.geospatial.services.SphericalGeometryLibrary;
import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.TopologyException;
import com.vividsolutions.jts.io.WKTWriter;
import com.vividsolutions.jts.operation.overlay.snap.GeometrySnapper;

@Component
class FuzzyLocationNamesServiceImpl implements FuzzyLocationNamesService {

  private static WKTWriter _wktWriter = new WKTWriter();

  private static GeometryFactory _geometryFactory = new GeometryFactory();

  private ConcurrentMap<String, LocationCollection> _neighborhoodsById = new ConcurrentHashMap<String, LocationCollection>();

  private ConcurrentMap<String, LocationCollection> _neighborhoodsByName = new ConcurrentHashMap<String, LocationCollection>();

  @Override
  public List<LocationBean> getAllLocations() {
    List<LocationBean> beans = new ArrayList<LocationBean>();
    for (LocationCollection n : _neighborhoodsById.values()) {
      LocationBean bean = getNeighborhoodAsBean(n);
      beans.add(bean);
    }
    return beans;
  }

  @Override
  public LocationBean getLocationForId(String id) {

    LocationCollection n = _neighborhoodsById.get(id);

    if (n == null)
      return null;

    return getNeighborhoodAsBean(n);
  }

  @Override
  public void removeLocationForId(String id) {
    LocationCollection n = _neighborhoodsById.remove(id);

    if (n != null) {
      _neighborhoodsByName.remove(n.getName());
    }
  }

  @Override
  public void addLocation(String user, String name, Geometry geometry) {

    LocationCollection n = getNeighborhoodByName(name);

    addEntryToCollection(n, user, geometry);
  }

  /****
   * Private Methods
   ****/

  private LocationCollection getNeighborhoodByName(String name) {

    name = name.toLowerCase();

    LocationCollection n = _neighborhoodsByName.get(name);

    if (n == null) {

      String id = Long.toString(System.currentTimeMillis());

      n = new LocationCollection();
      n.setId(id);
      n.setName(name);

      LocationCollection n2 = _neighborhoodsByName.putIfAbsent(name, n);
      if (n2 != null) {
        // Already something present, use it instead
        n = n2;
      } else {
        // It really was new, so add it to id list
        _neighborhoodsById.put(id, n);
      }
    }

    return n;
  }

  private void addEntryToCollection(LocationCollection n, String user,
      Geometry geometry) {

    LocationEntry entry = new LocationEntry();
    entry.setTime(System.currentTimeMillis());
    entry.setUser(user);
    entry.setGeometry(geometry);

    List<LocationEntry> entries = n.getEntries();

    synchronized (entries) {
      entries.add(entry);
    }
  }

  private LocationBean getNeighborhoodAsBean(
      LocationCollection collection) {

    LocationBean bean = new LocationBean();
    bean.setId(collection.getId());
    bean.setName(collection.getName());

    List<LayerBean> layers = new ArrayList<LayerBean>();
    Geometry geo = getNeighborhoodAsGeometry(collection, layers);

    Point centroid = geo.getCentroid();
    CoordinatePoint p = new CoordinatePoint(centroid.getY(), centroid.getX());
    bean.setCenter(p);

    String geoString = _wktWriter.write(geo);
    bean.setGeometry(geoString);

    bean.setLayers(layers);

    return bean;
  }

  private Geometry getNeighborhoodAsGeometry(LocationCollection collection,
      List<LayerBean> cells) {

    List<LocationEntry> entries = collection.getEntries();

    List<GeometryCount> geometryCounts = new ArrayList<GeometryCount>();
    int maxCount = 0;

    synchronized (entries) {

      maxCount = entries.size();

      for (LocationEntry entry : entries) {

        List<GeometryCount> newCounts = new ArrayList<GeometryCount>();

        Geometry geo = entry.getGeometry();

        for (GeometryCount gc : geometryCounts) {
          Geometry existingGeometry = gc.getGeometry();

          // Does it have an overlap?
          Geometry overlap = existingGeometry.intersection(geo);
          if (hasArea(overlap)) {

            int count = gc.getCount() + 1;
            addGeometryCount(newCounts, overlap, count);
          }

          Geometry difference = existingGeometry.difference(geo);
          if (hasArea(difference)) {
            GeometryCount differenceA = new GeometryCount(difference,
                gc.getCount());
            newCounts.add(differenceA);
          }

          // Geo gets whatever is left over
          geo = geo.difference(existingGeometry);
        }

        if (hasArea(geo)) {
          GeometryCount gc = new GeometryCount(geo, 1);
          newCounts.add(gc);
        }

        geometryCounts = newCounts;
      }
    }

    double step = 1.0 / 10;
    Geometry lastGeo = null;

    for (double limit = 0; limit < 1.0; limit += step) {
      Geometry geo = null;
      int count = (int) (limit * maxCount);
      for (GeometryCount gc : geometryCounts) {
        if (gc.getCount() >= count) {
          if (geo == null) {
            geo = gc.getGeometry();
          } else {
            Geometry g = gc.getGeometry();

            try {
              geo = geo.union(g);
              break;
            } catch (TopologyException ex) {

              double snapTolerance = GeometrySnapper.computeOverlaySnapTolerance(
                  geo, g);

              while (true) {

                geo = snapGeometry(geo, snapTolerance);
                g = snapGeometry(g, snapTolerance);

                try {
                  geo = geo.union(g);
                  break;
                } catch (TopologyException ex2) {
                  snapTolerance = snapTolerance * 10;
                }
              }
            }
          }
        }
      }

      geo = cleanUpGeometry(geo);

      Point p = geo.getCentroid();
      CoordinateBounds b = SphericalGeometryLibrary.bounds(p.getY(), p.getX(),
          10);
      double r = (b.getMaxLat() - b.getMinLat() + b.getMaxLon() - b.getMinLon()) / 2;

      geo = geo.buffer(r);

      LayerBean cell = new LayerBean();
      cell.setGeometry(_wktWriter.write(geo));
      cell.setScore(limit);
      cells.add(cell);

      lastGeo = geo;
    }

    return lastGeo;
  }

  private boolean hasArea(Geometry geo) {
    boolean empty = geo.isEmpty();
    int dim = geo.getDimension();
    double area = geo.getArea();
    return !empty && dim == 2 && area > 0.0;
  }

  private void addGeometryCount(List<GeometryCount> geometryCounts,
      Geometry geo, int count) {

    if (geo instanceof GeometryCollection) {
      GeometryCollection mp = (GeometryCollection) geo;
      for (int i = 0; i < mp.getNumGeometries(); i++) {
        addGeometryCount(geometryCounts, mp.getGeometryN(i), count);
      }
    } else if (geo instanceof Polygon) {
      geo = cleanUpGeometry(geo);
      GeometryCount gc = new GeometryCount(geo, count);
      geometryCounts.add(gc);
    }
  }

  private Geometry snapGeometry(Geometry geo, double snapTolerance) {
    GeometrySnapper snapper = new GeometrySnapper(geo);
    Geometry snapped = snapper.snapTo(geo, snapTolerance);
    /**
     * need to "clean" snapped geometry - use buffer(0) as a simple way to do
     * this
     */
    return snapped.buffer(0);
  }

  private Geometry cleanUpGeometry(Geometry geo) {

    if (geo instanceof MultiPolygon) {
      MultiPolygon mp = (MultiPolygon) geo;
      Polygon[] polygons = new Polygon[mp.getNumGeometries()];
      for (int i = 0; i < mp.getNumGeometries(); i++)
        polygons[i] = (Polygon) cleanUpGeometry(mp.getGeometryN(i));
      return _geometryFactory.createMultiPolygon(polygons);
    } else if (geo instanceof Polygon) {

      Polygon p = (Polygon) geo;

      LineString exteriorRing = p.getExteriorRing();
      List<LineString> interiorRings = new ArrayList<LineString>();

      for (int i = 0; i < p.getNumInteriorRing(); i++) {
        LineString lineString = p.getInteriorRingN(i);
        if (lineString.getArea() > 0)
          interiorRings.add(lineString);
      }

      LinearRing outside = _geometryFactory.createLinearRing(exteriorRing.getCoordinates());
      LinearRing[] holes = new LinearRing[interiorRings.size()];
      for (int i = 0; i < holes.length; i++)
        holes[i] = _geometryFactory.createLinearRing(interiorRings.get(i).getCoordinates());
      return _geometryFactory.createPolygon(outside, holes);
    }

    return geo;
  }

  private static class GeometryCount {

    private final Geometry geometry;
    private final int count;

    public GeometryCount(Geometry geometry, int count) {
      this.geometry = geometry;
      this.count = count;
    }

    public Geometry getGeometry() {
      return geometry;
    }

    public int getCount() {
      return count;
    }

    @Override
    public String toString() {
      return "count=" + count + " geo=" + geometry;
    }
  }
}
