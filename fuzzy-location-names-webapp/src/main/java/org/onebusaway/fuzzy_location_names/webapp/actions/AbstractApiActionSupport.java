package org.onebusaway.fuzzy_location_names.webapp.actions;

import java.util.ArrayList;

import org.apache.struts2.rest.DefaultHttpHeaders;
import org.onebusaway.fuzz_location_names.core.services.FuzzyLocationNamesService;
import org.onebusaway.fuzzy_location_names.webapp.ResponseCodes;
import org.onebusaway.fuzzy_location_names.webapp.model.ResponseBean;
import org.onebusaway.fuzzy_location_names.webapp.model.ValidationErrorBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

public abstract class AbstractApiActionSupport extends ActionSupport implements
    ModelDriven<ResponseBean> {

  private static final long serialVersionUID = 1L;

  private ResponseBean _response;

  protected FuzzyLocationNamesService _service;
  
  @Autowired
  public void setService(FuzzyLocationNamesService service) {
    _service = service;
  }

  public ResponseBean getModel() {
    return _response;
  }

  /*****************************************************************************
   * Response Bean Generation Methods
   ****************************************************************************/

  protected DefaultHttpHeaders setOkResponse(Object data) {
    _response = new ResponseBean(getReturnVersion(), ResponseCodes.RESPONSE_OK,
        "OK", data);
    return new DefaultHttpHeaders();
  }

  protected DefaultHttpHeaders setValidationErrorsResponse() {
    ValidationErrorBean bean = new ValidationErrorBean(new ArrayList<String>(
        getActionErrors()), getFieldErrors());
    _response = new ResponseBean(getReturnVersion(),
        ResponseCodes.RESPONSE_INVALID_ARGUMENT, "validation error", bean);
    return new DefaultHttpHeaders().withStatus(_response.getCode());
  }

  protected DefaultHttpHeaders setResourceNotFoundResponse() {
    _response = new ResponseBean(getReturnVersion(),
        ResponseCodes.RESPONSE_RESOURCE_NOT_FOUND, "resource not found", null);
    return new DefaultHttpHeaders().withStatus(_response.getCode());
  }

  protected DefaultHttpHeaders setExceptionResponse() {
    _response = new ResponseBean(getReturnVersion(),
        ResponseCodes.RESPONSE_SERVICE_EXCEPTION, "internal error", null);
    return new DefaultHttpHeaders().withStatus(_response.getCode());
  }

  protected int getReturnVersion() {
    return 1;
  }
}
