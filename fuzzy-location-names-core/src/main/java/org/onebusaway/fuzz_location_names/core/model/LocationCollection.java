package org.onebusaway.fuzz_location_names.core.model;

import java.util.ArrayList;
import java.util.List;

public class LocationCollection {

  private String id;

  private String name;

  private List<String> alternateNames;

  private List<LocationEntry> entries = new ArrayList<LocationEntry>();

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

  public List<String> getAlternateNames() {
    return alternateNames;
  }

  public void setAlternateNames(List<String> alternateNames) {
    this.alternateNames = alternateNames;
  }

  public List<LocationEntry> getEntries() {
    return entries;
  }

  public void setEntries(List<LocationEntry> entries) {
    this.entries = entries;
  }

}
