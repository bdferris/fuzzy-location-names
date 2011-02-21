package org.onebusaway.fuzz_location_names.core.model;

import java.io.Serializable;
import java.util.List;

import org.onebusaway.geospatial.model.CoordinatePoint;

public class LocationBean implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;

  private String name;

  private CoordinatePoint center;

  private String geometry;

  private List<LayerBean> layers;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CoordinatePoint getCenter() {
    return center;
  }

  public void setCenter(CoordinatePoint center) {
    this.center = center;
  }

  public String getGeometry() {
    return geometry;
  }

  public void setGeometry(String geometry) {
    this.geometry = geometry;
  }

  public List<LayerBean> getLayers() {
    return layers;
  }

  public void setLayers(List<LayerBean> layers) {
    this.layers = layers;
  }
}
