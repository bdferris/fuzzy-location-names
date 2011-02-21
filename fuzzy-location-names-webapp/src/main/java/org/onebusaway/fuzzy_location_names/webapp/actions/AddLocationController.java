package org.onebusaway.fuzzy_location_names.webapp.actions;

import org.apache.struts2.rest.DefaultHttpHeaders;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class AddLocationController extends AbstractApiActionSupport {

  private static final long serialVersionUID = 1L;

  private String _user;

  private String _name;

  private String _geometry;

  public void setUser(String user) {
    _user = user;
  }

  public String getUser() {
    return _user;
  }

  public void setName(String name) {
    _name = name;
  }

  public String getName() {
    return _name;
  }

  public void setGeometry(String geometry) {
    _geometry = geometry;
  }

  public DefaultHttpHeaders index() throws ParseException {

    Geometry geometry = new WKTReader(new GeometryFactory()).read(_geometry);

    _service.addLocation(_user, _name, geometry);

    return setOkResponse(null);
  }
}
