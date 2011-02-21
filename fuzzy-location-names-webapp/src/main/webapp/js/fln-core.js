var FLN = window.FLN || {};

FLN.Core = function() {
	
	var that = {};
	
	that.parseLocation = function() {
        var params = {};
        var href = window.location.href;
        var index = href.indexOf('?');
        if (index == -1)
                return params;
        href = href.slice(index + 1);
        index = href.indexOf('#');
        if (index != -1)
                href = href.slice(0, index);
        var tokens = href.split('&');
        jQuery.each(tokens, function() {
                var kvp = this.split('=');
                var key = decodeURIComponent(kvp[0]);
                var value = decodeURIComponent(kvp[1]);
                if (!(key in params))
                	params[key] = [];
                params[key].push(value);
        });

        return params;
    };
	
	return that;
	
}();


