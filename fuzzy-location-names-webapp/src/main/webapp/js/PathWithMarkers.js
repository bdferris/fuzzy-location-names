/**
* A path with markers that can be dragged.  Adding points to this using insertAt
*	will create a point on the path as well as add a marker to the map.
*
* @class google.mapsextensions.PathWithMarkers
*/
/**
* The path to add points to
* @config path
* @type {object}
*/
/**
* The map the path is located on
* @config map
* @type {object} (optional)
*/
/**
* The color of the markers, of the style you would use for CSS.
* @config color
* @type {string}
*/
google.mapsextensions.PathWithMarkers = function( opts ) {
	extend(this, opts);
	
	this.initMarkerCollection();
};

extend( google.mapsextensions.PathWithMarkers.prototype, {
	initMarkerCollection: function() {
		if( !this.markerCollection ) {
			this.markerCollection = new google.mapsextensions.MarkersCollection();
		}
	},

	/**
	* insert a new point into the path, makes a marker for the point
	* @method insertAt
	* @param {number} index - index where to place point
	* @param {object} latLng - latitude and longitude of the point
	*/
	insertAt: function( index, latLng ) {
		this.path.insertAt( index, latLng );

		var marker = this.createMarker( index, latLng );
		this.markerCollection.addMarker( marker, index );

		google.maps.event.addListener( marker, 'drag', bind( function( latLng ) {
			this.onMarkerDrag( marker );
		}, this ) );

		google.maps.event.addListener( marker, 'click', bind( function( latLng ) {
			this.onMarkerClick( marker );
		}, this ) );
				
		google.maps.event.trigger( this, 'lineupdated' );
	},
	
	/**
	* A marker factory method
	* @method createMarker
	* @param {number} index - index on path where to place marker
	* @param {object} latLng - latitude and longitude to place the marker
	* @return {object} a google.mapsextensions.PointMarker
	* @private
	*/
	createMarker: function( index, latLng ) {
		var marker = new google.mapsextensions.PointMarker( {
			position: latLng,
			map: this.map,
			color: this.color || '#000000',
			visible: !!this.editingEnabled,
			draggable: !!this.editingEnabled
		} );
		
		return marker;
	},
	
	/**
	* initiate the dragging of a marker
	* @method dragMarker
	* @param {number} index of marker to start dragging
	*/
	dragMarker: function( index ) {
		var marker = this.markerCollection.getAt( index );
		if( marker ) {
			marker.setDraggable( true );
			marker.startDrag();
		}
	},
		
	onMarkerDrag: function( marker ) {
		var index = this.markerCollection.getIndex( marker );
		if( index > -1 ) {
			this.path.setAt( index, marker.getPosition() );
		}
	},

	onMarkerClick: function( marker ) {
		var index = this.markerCollection.getIndex( marker );
		var len = this.path.getLength();
		if( index === 0 && len > 1 ) {
			google.maps.event.trigger( this, 'endline', marker.getPosition() );
		}
	},
	
	/**
	* set the path editable
	* @method setEditable
	* @param {bool} editable - whether path is editable or not
	*/
	setEditable: function( editable ) {
		this.editingEnabled = !!editable;
		this.markerCollection.setEditable( editable );
	},

	/**
	* set the map of the path
	* @method setMap
	* @param {object} map - map where the path is located
	*/
	setMap: function( map ) {
		this.map = map;
	},
	
	/**
	* Set the options
	* @method setOptions
	* @param {object} opts - options to set
	*/
	setOptions: function( opts ) {
		if( opts.color ) {
			this.color = opts.color;
			this.markerCollection.forEach( function( marker, index ) {
				marker.setColor( opts.color );
			} );
		}
	},
	
	/**
	* gets a line segment on the path.  a segment is a line between two points.
	*	segment 0 would be the segment between points 0 and 1, segment 1 would be
	*	the segment between points 1 and 2.
	* @method getSegment
	* @param {number} index - index of the segment to get
	* @return {object} segment if available, undefined otw.
	*/
	getSegment: function( index ) {
		var startPoint = this.path.getAt( index );
		var endPoint = this.path.getAt( index + 1 );
		
		var segment;
		if( startPoint && endPoint ) {
			segment = new google.mapsextensions.Segment( {
				startPoint: startPoint,
				endPoint: endPoint
			} );
		}
		
		return segment;
	},
	
	/**
	* gets the index of the segment containing the specified point.
	* @method getIndexOfSegmentContainingPoint
	* @param {LatLng} latLng - latLng to search for
	* @return {number} index of segment if available, -1 otherwise.
	*/
	getIndexOfSegmentContainingPoint: function( latLng ) {
		for( var index = 0, segment; segment = this.getSegment( index ); ++index ) {
			if( segment.containsPoint( latLng ) ) {
				return index;
			}
		}
		return -1;
	}
	
} );


