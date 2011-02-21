
function fuzzy_location_names_location() {
	
    var latlng = new google.maps.LatLng(47.618, -122.318);
    var myOptions = {
        zoom: 13,
        center: latlng,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
  
    var map = new google.maps.Map(document.getElementById("mapCanvas"), myOptions);
    
    var params = FLN.Core.parseLocation();
    
    var parsePoints = function(pointsAsString) {
    	var points = [];
    	var tokens = pointsAsString.split(/,\s+/);
    	jQuery.each(tokens, function() {
    		var lnglat = this.split(/\s+/);
    		var p = new google.maps.LatLng(lnglat[1],lnglat[0]);
    		points.push(p);
    	});
    	return points;
    };
    
    var parseWKT = function(wkt) {
    	var m = wkt.match(/MULTIPOLYGON\s+\(\((.*)\)\)/);
    	if( m ) {
    		var points = [];
    		if( false ) {
    		var regex = /,\s+\(\((.*?)\)\)/g;    		
    		while( m = regex.exec(wkt) ) {
    			points.push(parsePoints(m[1]));
    		}
    		}
    		return points;
    	}
    	else {
    		m = wkt.match(/POLYGON\s*\((.*)\)/);
    		if( m ) {
    			var allPoints = [];
    			var regex = /(,\s*){0,1}\(\((.*?)\)\)/g;
    			while( m = regex.exec(wkt)) {
    				var pString = m[2];
    				var points = parsePoints(pString);
    				allPoints.push(points);
    			}    			
    			return [allPoints];
    		}
    	}
    	
    	return [];
    };

    jQuery.getJSON('location/' + params.id + '.json',{},function(entry) {
    	var data = entry.data;
    	var center = data.center;
    	var p = new google.maps.LatLng(center.lat,center.lon);
    	new google.maps.Marker({position: p, map: map});
    	
    	var bounds = new google.maps.LatLngBounds();
    	bounds.extend(p);
    	
    	jQuery.each(data.layers,function() {
    		
    		var allPoints = parseWKT(this.geometry);
    		jQuery.each(allPoints, function() {
    			if( allPoints.length == 0)
    				return;
        		var polyOptions = {};
        		polyOptions.fillColor = '#00ff00';
        		polyOptions.fillOpacity = 0.02;
        		polyOptions.strokeOpacity = 0.0;
        		polyOptions.map = map;
        		polyOptions.paths = this;
        		var p = new google.maps.Polygon(polyOptions);
        		p.setPaths(this[0]);
    		});
    	});
    	
    	//map.fitBounds(bounds);
    });
}




