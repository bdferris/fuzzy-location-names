package org.onebusaway.fuzzy_location_names.webapp.actions;

import org.apache.struts2.rest.DefaultHttpHeaders;
import org.onebusaway.exceptions.ServiceException;
import org.onebusaway.fuzz_location_names.core.model.LocationBean;

import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;

public class LocationController extends AbstractApiActionSupport {

  private static final long serialVersionUID = 1L;

  private String _id;

  @RequiredFieldValidator
  public void setId(String id) {
    _id = id;
  }

  public String getId() {
    return _id;
  }

  public DefaultHttpHeaders show() throws ServiceException {

    if (hasErrors())
      return setValidationErrorsResponse();

    LocationBean neighborhood = _service.getLocationForId(_id);

    if (neighborhood == null)
      return setResourceNotFoundResponse();

    return setOkResponse(neighborhood);
  }
}
