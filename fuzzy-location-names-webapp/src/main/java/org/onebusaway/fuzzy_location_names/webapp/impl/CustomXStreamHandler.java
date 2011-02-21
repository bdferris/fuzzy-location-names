package org.onebusaway.fuzzy_location_names.webapp.impl;

import org.apache.struts2.rest.handler.XStreamHandler;
import org.onebusaway.fuzz_location_names.core.model.LayerBean;
import org.onebusaway.fuzz_location_names.core.model.LocationBean;
import org.onebusaway.fuzzy_location_names.webapp.model.ResponseBean;
import org.onebusaway.fuzzy_location_names.webapp.model.ValidationErrorBean;
import org.onebusaway.geospatial.model.CoordinatePoint;

import com.thoughtworks.xstream.XStream;

public class CustomXStreamHandler extends XStreamHandler {

  @Override
  protected XStream createXStream() {
    XStream xstream = super.createXStream();
    xstream.setMode(XStream.NO_REFERENCES);
    xstream.alias("response", ResponseBean.class);
    xstream.alias("validationError", ValidationErrorBean.class);
    xstream.alias("location", LocationBean.class);
    xstream.alias("layer", LayerBean.class);
    xstream.alias("point", CoordinatePoint.class);
    return xstream;
  }
}
