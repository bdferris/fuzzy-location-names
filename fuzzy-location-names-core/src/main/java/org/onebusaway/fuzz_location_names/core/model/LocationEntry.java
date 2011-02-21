package org.onebusaway.fuzz_location_names.core.model;

import com.vividsolutions.jts.geom.Geometry;

public class LocationEntry {

  private long time;

  private String user;

  private Geometry geometry;

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

}
