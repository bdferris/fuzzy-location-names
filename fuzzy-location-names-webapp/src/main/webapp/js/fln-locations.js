var fuzzy_location_names_locations = function() {
	
	var handler = function(json) {
		if( ! json || json.code != 200)
			return;
		
		var list = jQuery('#locations');
		var template = jQuery('.locationsEntryTemplate');
		
		list.empty();
		jQuery.each(json.data, function() {
			
			var content = template.clone();
			content.removeClass('locationsEntryTemplate');
			content.addClass('locationsEntry');
			
			var anchor = content.find('a');
			anchor.text(this.name);
			anchor.attr('href','location.html?id=' + this.id);
			
			content.appendTo(list);
			content.show();
		});
		
	};
	
	jQuery.getJSON('locations.json',{},handler);
};