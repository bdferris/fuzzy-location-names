package org.onebusaway.fuzz_location_names.core.services;

import java.util.List;

import org.onebusaway.fuzz_location_names.core.model.LayerBean;
import org.onebusaway.fuzz_location_names.core.model.LocationCollection;

public interface FuzzyLocationGeometryIntegrationStrategy {
  public List<LayerBean> createLayers(LocationCollection collection);
}
