package org.onebusaway.fuzzy_location_names.webapp.actions;

import java.util.List;

import org.apache.struts2.rest.DefaultHttpHeaders;
import org.onebusaway.fuzz_location_names.core.model.LocationBean;

import com.vividsolutions.jts.io.ParseException;

public class LocationsController extends AbstractApiActionSupport {

  private static final long serialVersionUID = 1L;

  public DefaultHttpHeaders index() throws ParseException {

    List<LocationBean> neighborhoods = _service.getAllLocations();

    return setOkResponse(neighborhoods);
  }
}
