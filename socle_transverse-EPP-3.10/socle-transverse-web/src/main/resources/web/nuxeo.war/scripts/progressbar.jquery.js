
(function(jQuery) {	
	jQuery.fn.reportprogress = function(val,maxVal) {			
		var max=100;
		if(maxVal)
			max=maxVal;
		if(val>max)
			val=max;
		return this.each(
			function(){		
				var div=jQuery(this);
				var innerdiv=div.find(".progress");
				
				if(innerdiv.length!=1){						
					innerdiv=jQuery("<div class='progress'></div>");					
					div.append("<div class='text'>&nbsp;</div>");
					jQuery("<span class='text'>&nbsp;</span>").css("width",div.width()).appendTo(innerdiv);					
					div.append(innerdiv);					
				}
				var width=Math.round(val/max*100);
				innerdiv.css("width",width+"%");	
				div.find(".text").html(width+" %");
			}
		);
	};
})(jQuery);