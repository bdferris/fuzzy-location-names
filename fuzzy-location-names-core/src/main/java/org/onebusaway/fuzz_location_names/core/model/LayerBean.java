package org.onebusaway.fuzz_location_names.core.model;

import java.io.Serializable;

public class LayerBean implements Serializable, Comparable<LayerBean> {

  private static final long serialVersionUID = 1L;

  private String geometry;

  private double score;

  public String getGeometry() {
    return geometry;
  }

  public void setGeometry(String geometry) {
    this.geometry = geometry;
  }

  public double getScore() {
    return score;
  }

  public void setScore(double score) {
    this.score = score;
  }

  /****
   * {@link Comparable} Interface
   ****/

  @Override
  public int compareTo(LayerBean o) {
    return Double.compare(this.score, o.score);
  }
}
