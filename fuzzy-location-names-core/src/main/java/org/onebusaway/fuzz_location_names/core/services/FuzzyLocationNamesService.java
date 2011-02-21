package org.onebusaway.fuzz_location_names.core.services;

import java.util.List;

import org.onebusaway.fuzz_location_names.core.model.LocationBean;

import com.vividsolutions.jts.geom.Geometry;

public interface FuzzyLocationNamesService {

  public List<LocationBean> getAllLocations();

  public LocationBean getLocationForId(String id);

  public void addLocation(String user, String name, Geometry geometry);

  public void removeLocationForId(String id);
}
