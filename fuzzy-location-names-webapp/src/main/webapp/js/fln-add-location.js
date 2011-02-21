
function fuzzy_location_names_add_location() {
	var initialColor = '#0000ff';
	serviceBaseUrl = "localhost:8080";
	
    var latlng = new google.maps.LatLng(47.618, -122.318);
    var myOptions = {
        zoom: 13,
        center: latlng,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
  
    var map = new google.maps.Map(document.getElementById("mapCanvas"), myOptions);
    var path = new google.maps.MVCArray();

	var polyOptions = {
	  strokeColor: initialColor,
	  strokeOpacity: 0.6,
	  strokeWeight: 3,
	  clickable: true,
	  geodesic: false,
	  path: path
	};
	
    var poly = new google.mapsextensions.Polyline(polyOptions);
    poly.setMap(map);

    var b = jQuery( '#btnEnableEditing' );
    
    jQuery( '#btnEnableEditing' ).click(function( event ) {
	    event.preventDefault();
	    poly.enableEditing();
    });

    jQuery( '#btnEnableDrawing' ).bind( 'click', function( event ) {
	    event.preventDefault();
	    poly.enableDrawing();
    });

    jQuery( '#btnDisableEditing' ).bind( 'click', function( event ) {
	    event.preventDefault();
	    poly.disableEditing();
    });
    
	jQuery( '#color' ).val( initialColor );
	

    jQuery("#submit").click(function() {
      
      var wkt = constructWkt();
      
      var params = {};
      params.user = 'map';
      params.name = jQuery('#myloc').val();
      params.geometry = wkt;
      
      jQuery.getJSON('add-location.json',params, function(result) {
    	 console.log('result=' + result);
      });

      // this is wired to a form submit handler, return false to prevent a full-page reload
      return false;
    });
    
    var constructWkt = function() {
        var wkt = "POLYGON((";
    	var vertices = poly.getPath();
    	
    	for (var i =0; i < vertices.length; i++) {
    		if( i > 0)
    			wkt += ',';
            var xy = vertices.getAt(i);
            wkt += xy.lng() + ' ' + xy.lat();
        }
    	
    	wkt += '))';
    	return wkt;
    };
}




