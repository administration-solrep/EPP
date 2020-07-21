(function (jQuery) {

		jQuery.fn.fixedHeaderTable = function (method) {

	        // plugin's default options
	        var defaults = {
	            
	            width:          '100%',
	            height:         '100%',
	            themeClass:     'fht-default',
	            borderCollapse:  true,
	            fixedColumns:    0, // fixed first columns
	            sortable:        false,
	            autoShow:        true, // hide table after its created
	            footer:          false, // show footer
	            cloneHeadToFoot: false, // clone head and use as footer
	            autoResize:      false, // resize table if its parent wrapper changes size
	            create:          null // callback after plugin completes
	        };

	        var settings = {};

	        // public methods
	        var methods = {
	            init: function (options) {
	                settings = jQuery.extend({}, defaults, options);

	                // iterate through all the DOM elements we are attaching the plugin to
	                return this.each(function () {
	                    var jQueryself = jQuery(this), // reference the jQuery version of the current DOM element
	                    self = this; // reference to the actual DOM element
	                    
	                    if (helpers._isTable(jQueryself)) {
	                        methods.setup.apply(this, Array.prototype.slice.call(arguments, 1));
	                        jQuery.isFunction(settings.create) && settings.create.call(this);
	                    } else {
	                    	jQuery.error('Invalid table mark-up');
			    }
	                });
	            },
		    
		    /*
		     * Setup table structure for fixed headers and optional footer
		     */
	            setup: function (options) {
	                var jQueryself  = jQuery(this),
	                self   = this,
	                jQuerythead = jQueryself.find('thead'),
	                jQuerytfoot = jQueryself.find('tfoot'),
	                jQuerytbody = jQueryself.find('tbody'),
	                jQuerywrapper,
	                jQuerydivHead,
	                jQuerydivFoot,
	                jQuerydivBody,
	                jQueryfixedHeadRow,
	                jQuerytemp,
	                tfootHeight = 0;
	                
	                settings.includePadding = helpers._isPaddingIncludedWithWidth();
	                settings.scrollbarOffset = helpers._getScrollbarWidth();
			settings.themeClassName = settings.themeClass;
			
			if (settings.width.search('%') > -1) {
			    var widthMinusScrollbar = jQueryself.parent().width() - settings.scrollbarOffset;
			} else {
			    var widthMinusScrollbar = settings.width - settings.scrollbarOffset;				
			}
			
	                jQueryself.css({
		            width: widthMinusScrollbar
		        });
		        

	                if (!jQueryself.closest('.fht-table-wrapper').length) {
	                    jQueryself.addClass('fht-table');
	                    jQueryself.wrap('<div class="fht-table-wrapper"></div>');
	                }

	                jQuerywrapper = jQueryself.closest('.fht-table-wrapper');
	                
	                if (settings.fixedColumns > 0 && jQuerywrapper.find('.fht-fixed-column').length == 0) {
	                    jQueryself.wrap('<div class="fht-fixed-body"></div>');
	                    
	                    var jQueryfixedColumns = jQuery('<div class="fht-fixed-column"></div>').prependTo(jQuerywrapper),
	                    jQueryfixedBody	 = jQuerywrapper.find('.fht-fixed-body');
	                }
	                
	                jQuerywrapper.css({
		            width: settings.width,
		            height: settings.height
		        })
		            .addClass(settings.themeClassName);

	                if (!jQueryself.hasClass('fht-table-init')) {
	                    
	                    jQueryself.wrap('<div class="fht-tbody"></div>');
	                    
	                }
			jQuerydivBody = jQueryself.closest('.fht-tbody');
			
	                var tableProps = helpers._getTableProps(jQueryself);
	                
	                helpers._setupClone(jQuerydivBody, tableProps.tbody);

	                if (!jQueryself.hasClass('fht-table-init')) {
	                    if (settings.fixedColumns > 0) {
	                	jQuerydivHead = jQuery('<div class="fht-thead"><table class="dataOutput fht-table"></table></div>').prependTo(jQueryfixedBody);
	                    } else {
	                	jQuerydivHead = jQuery('<div class="fht-thead"><table class="dataOutput fht-table"></table></div>').prependTo(jQuerywrapper);
	                    }
	                    
	                    jQuerythead.clone().appendTo(jQuerydivHead.find('table'));
	                } else {
	                    jQuerydivHead = jQuerywrapper.find('div.fht-thead');
	                }

	                helpers._setupClone(jQuerydivHead, tableProps.thead);
	                
	                jQueryself.css({
	                    'margin-top': -jQuerydivHead.outerHeight(true)
	                });
	                
	                /*
	                 * Check for footer
	                 * Setup footer if present
	                 */
	                if (settings.footer == true) {

	                    helpers._setupTableFooter(jQueryself, self, tableProps);
	                    
	                    if (!jQuerytfoot.length) {
	                	jQuerytfoot = jQuerywrapper.find('div.fht-tfoot table');
	                    }
	                    
	                    tfootHeight = jQuerytfoot.outerHeight(true);
	                }

	                var tbodyHeight = jQuerywrapper.height() - jQuerythead.outerHeight(true) - tfootHeight - tableProps.border;
	                
	                jQuerydivBody.css({
	                	'height': tbodyHeight
		        });
	                
	                jQueryself.addClass('fht-table-init');
	                
	                if (typeof(settings.altClass) !== 'undefined') {
	                    methods.altRows.apply(self);
	                }
	                
	                if (settings.fixedColumns > 0) {
	                    helpers._setupFixedColumn(jQueryself, self, tableProps);
	                }
	                
	                if (!settings.autoShow) {
	                    jQuerywrapper.hide();
	                }
	                
	                helpers._bindScroll(jQuerydivBody, tableProps);
	                
	                return self;
	            },
	            
	            /*
	             * Resize the table
	             * Incomplete - not implemented yet
	             */
	            resize: function(options) {
	            	var jQueryself = jQuery(this),
	            	self  = this;
	            	return self;
	            },
	            
	            /*
	             * Add CSS class to alternating rows
	             */
	            altRows: function(arg1) {
	            	var jQueryself       = jQuery(this),
	            	self            = this,
	            	altClass        = (typeof(arg1) !== 'undefined') ? arg1 : settings.altClass;
	            	
	            	jQueryself.closest('.fht-table-wrapper')
	            	    .find('tbody tr:odd:not(:hidden)')
	            	    .addClass(settings.baseClass);
	            	jQueryself.closest('.fht-table-wrapper')
            	    .find('tbody tr:odd:not(:hidden)')
            	    .addClass(altClass);
	            },
	            
	            /*
	             * Show a hidden fixedHeaderTable table
	             */
	            show: function(arg1, arg2, arg3) {
	                var jQueryself		= jQuery(this),
	                self  		= this,
	                jQuerywrapper 	= jQueryself.closest('.fht-table-wrapper');

			// User provided show duration without a specific effect
	                if (typeof(arg1) !== 'undefined' && typeof(arg1) === 'number') {
	                    
	                    jQuerywrapper.show(arg1, function() {
	                	jQuery.isFunction(arg2) && arg2.call(this);
	                    });

	                    return self;
	                    
	                } else if (typeof(arg1) !== 'undefined' && typeof(arg1) === 'string'
	                	    && typeof(arg2) !== 'undefined' && typeof(arg2) === 'number') {
			    // User provided show duration with an effect
			    
	                    jQuerywrapper.show(arg1, arg2, function() {
	                	jQuery.isFunction(arg3) && arg3.call(this);
	                    });
	                    
	                    return self;
	                    
	                }
	                
	            	jQueryself.closest('.fht-table-wrapper')
	                    .show();
	                jQuery.isFunction(arg1) && arg1.call(this);
	                
	                return self;
	            },
	            
	            /*
	             * Hide a fixedHeaderTable table
	             */
	            hide: function(arg1, arg2, arg3) {
	                var jQueryself 		= jQuery(this),
	                self		= this,
	                jQuerywrapper 	= jQueryself.closest('.fht-table-wrapper');
	                
	                // User provided show duration without a specific effect
	                if (typeof(arg1) !== 'undefined' && typeof(arg1) === 'number') {
	                    jQuerywrapper.hide(arg1, function() {
	                	jQuery.isFunction(arg3) && arg3.call(this);
	                    });
	                    
	                    return self;
	                } else if (typeof(arg1) !== 'undefined' && typeof(arg1) === 'string'
	                	    && typeof(arg2) !== 'undefined' && typeof(arg2) === 'number') {

	                    jQuerywrapper.hide(arg1, arg2, function() {
	                	jQuery.isFunction(arg3) && arg3.call(this);
	                    });
	                    
	                    return self;
	                }
	                
	                jQueryself.closest('.fht-table-wrapper')
	                    .hide();
	                
	                jQuery.isFunction(arg3) && arg3.call(this);
	                
	                
	                
	                return self;
	            },
	            
	            /*
	             * Destory fixedHeaderTable and return table to original state
	             */
	            destroy: function() {
	                var jQueryself    = jQuery(this),
	                self     = this,
	                jQuerywrapper = jQueryself.closest('.fht-table-wrapper');
	                
	                jQueryself.insertBefore(jQuerywrapper)
	                    .removeAttr('style')
	                    .append(jQuerywrapper.find('tfoot'))
	                    .removeClass('fht-table fht-table-init')
	                    .find('.fht-cell')
	                    .remove();
	                
	                jQuerywrapper.remove();
	                
	                return self;
	            }

	        }

	        // private methods
	        var helpers = {

		    /*
		     * return boolean
		     * True if a thead and tbody exist.
		     */
	            _isTable: function(jQueryobj) {
	                var jQueryself = jQueryobj,
	                hasTable = jQueryself.is('table'),
	                hasThead = jQueryself.find('thead').length > 0,
	                hasTbody = jQueryself.find('tbody').length > 0;

	                if (hasTable && hasThead && hasTbody) {
	                    return true;
	                }
	                
	                return false;

	            },
	            
	            /*
	             * return void
	             * bind scroll event
	             */
	            _bindScroll: function(jQueryobj, tableProps) {
	            	var jQueryself = jQueryobj,
	            	jQuerywrapper = jQueryself.closest('.fht-table-wrapper'),
	            	jQuerythead = jQueryself.siblings('.fht-thead'),
	            	jQuerytfoot = jQueryself.siblings('.fht-tfoot');
	            	
	            	jQueryself.bind('scroll', function() {
	            	    if (settings.fixedColumns > 0) {
	            	        var jQueryfixedColumns = jQuerywrapper.find('.fht-fixed-column');
	            	        
	            	        jQueryfixedColumns.find('.fht-tbody table')
	            	            .css({
	            	                'margin-top': -jQueryself.scrollTop()
	            	            });
	            	    }
	            	    
	            	    jQuerythead.find('table')
	            		.css({
	            		    'margin-left': -this.scrollLeft
	            		});
	            	    
	            	    if (settings.cloneHeadToFoot) {
	            		jQuerytfoot.find('table')
		            	    .css({
		            		'margin-left': -this.scrollLeft
		            	    });
	            	    }
	            	});
	            },
	            
	            /*
	             * return void
	             */
	            _fixHeightWithCss: function (jQueryobj, tableProps) {
	            	if (settings.includePadding) {
		            jQueryobj.css({
		            	'height': jQueryobj.height() + tableProps.border
		            });
	            	} else {
	            	    jQueryobj.css({
	            		'height': jQueryobj.parent().height() + tableProps.border
	            	    });
	            	}
	            },
	            
	            /*
	             * return void
	             */
	            _fixWidthWithCss: function(jQueryobj, tableProps, width) {
	            	if (settings.includePadding) {
	            	    jQueryobj.each(function(index) {
				jQuery(this).css({
	            		    'width': width == undefined ? jQuery(this).width() + tableProps.border : width + tableProps.border
				});
	            	    }); 
	            	} else {
	            	    jQueryobj.each(function(index) {
				jQuery(this).css({
	            		    'width': width == undefined ? jQuery(this).parent().width() + tableProps.border : width + tableProps.border
				});
	            	    });
	            	}

	            },
	            
	            /*
	             * return void
	             */
		    _setupFixedColumn: function (jQueryobj, obj, tableProps) {
			var jQueryself		= jQueryobj,
			self			= obj,
			jQuerywrapper		= jQueryself.closest('.fht-table-wrapper'),
			jQueryfixedBody		= jQuerywrapper.find('.fht-fixed-body'),
			jQueryfixedColumn		= jQuerywrapper.find('.fht-fixed-column'),
			jQuerythead			= jQuery('<div class="fht-thead"><table class="dataOutput fht-table"><thead><tr></tr></thead></table></div>'),
			jQuerytbody			= jQuery('<div class="fht-tbody"><table class="dataOutput fht-table"><tbody></tbody></table></div>'),
			jQuerytfoot			= jQuery('<div class="fht-tfoot"><table class="dataOutput fht-table"><thead><tr></tr></thead></table></div>'),
			jQueryfirstThChildren,//	= jQueryfixedBody.find('.fht-thead thead tr th:first-child'),
			jQueryfirstTdChildren,
			fixedColumnWidth,//	= jQueryfirstThChild.outerWidth(true) + tableProps.border,
			fixedBodyWidth		= jQuerywrapper.width(),
			fixedBodyHeight		= jQueryfixedBody.find('.fht-tbody').height() - settings.scrollbarOffset,
			jQuerynewRow;

			jQueryfirstThChildren = jQueryfixedBody.find('.fht-thead thead tr th:lt(' + settings.fixedColumns + ')');
			fixedColumnWidth = settings.fixedColumns * tableProps.border;
			jQueryfirstThChildren.each(function(index) {
			    fixedColumnWidth += jQuery(this).outerWidth(true);
			});

			// Fix cell heights
			helpers._fixHeightWithCss(jQueryfirstThChildren, tableProps);
			helpers._fixWidthWithCss(jQueryfirstThChildren, tableProps);

			var tdWidths = [];
			jQueryfirstThChildren.each(function(index) {
			    tdWidths.push(jQuery(this).width());
			});

			firstTdChildrenSelector = 'tbody tr td:not(:nth-child(n+' + (settings.fixedColumns + 1) + '))';
			jQueryfirstTdChildren = jQueryfixedBody.find(firstTdChildrenSelector)
			    .each(function(index) {
				helpers._fixHeightWithCss(jQuery(this), tableProps);
				helpers._fixWidthWithCss(jQuery(this), tableProps, tdWidths[index % settings.fixedColumns] );
			    });

			// clone header
			jQuerythead.appendTo(jQueryfixedColumn)
			    .find('tr')
			    .append(jQueryfirstThChildren.clone());
			
			jQuerytbody.appendTo(jQueryfixedColumn)
			    .css({
				'margin-top': -1,
				'height': fixedBodyHeight + tableProps.border
			    });

			var jQuerynewRow;
			jQueryfirstTdChildren.each(function(index) {
			    if (index % settings.fixedColumns == 0) {
					jQuerynewRow = jQuery('<tr></tr>').appendTo(jQuerytbody.find('tbody'));
	
					jQuerynewRow.addClass(settings.baseClass);
	        
					if (settings.altClass && jQuery(this).parent().hasClass(settings.altClass)) {
					    jQuerynewRow.addClass(settings.altClass);
					}
					
					jQuerynewRow.addClass(jQuery(this).parent().attr('class'));
			    }
			    
			    jQuery(this).clone()
				.appendTo(jQuerynewRow);
			});
			
			// set width of fixed column wrapper
			jQueryfixedColumn.css({
			    'height': 0,
			    'width': fixedColumnWidth
			})


			// bind mousewheel events
			var maxTop = jQueryfixedColumn.find('.fht-tbody .fht-table').height() - jQueryfixedColumn.find('.fht-tbody').height();
			jQueryfixedColumn.find('.fht-table').bind('mousewheel', function(event, delta, deltaX, deltaY) {
			    if (deltaY == 0) return;
			    var top = parseInt(jQuery(this).css('marginTop'), 10) + (deltaY > 0 ? 120 : -120);
			    if (top > 0) top = 0;
			    if (top < -maxTop) top = -maxTop;
			    jQuery(this).css('marginTop', top);
			    jQueryfixedBody.find('.fht-tbody').scrollTop(-top).scroll();
			    return false;
			});

			
			// set width of body table wrapper
			jQueryfixedBody.css({
			    'width': fixedBodyWidth
			});
			
			// setup clone footer with fixed column
			if (settings.footer == true || settings.cloneHeadToFoot == true) {
			    var jQueryfirstTdFootChild = jQueryfixedBody.find('.fht-tfoot thead tr th:lt(' + settings.fixedColumns + ')');
			    
			    helpers._fixHeightWithCss(jQueryfirstTdFootChild, tableProps);
			    jQuerytfoot.appendTo(jQueryfixedColumn)
				.find('tr')
				.append(jQueryfirstTdFootChild.clone());
			    jQuerytfoot.css({
				'top': settings.scrollbarOffset
			    });
			}
		    },
	            
	            /*
	             * return void
	             */
	            _setupTableFooter: function (jQueryobj, obj, tableProps) {
	            	
	            	var jQueryself 		= jQueryobj,
	            	self  		= obj,
	            	jQuerywrapper 	= jQueryself.closest('.fht-table-wrapper'),
	            	jQuerytfoot		= jQueryself.find('tfoot'),
	            	jQuerydivFoot	= jQuerywrapper.find('div.fht-tfoot');
	            	
	            	if (!jQuerydivFoot.length) {
	            	    if (settings.fixedColumns > 0) {
	            		jQuerydivFoot = jQuery('<div class="fht-tfoot"><table class="dataOutput fht-table"></table></div>').appendTo(jQuerywrapper.find('.fht-fixed-body'));
	            	    } else {
	            		jQuerydivFoot = jQuery('<div class="fht-tfoot"><table class="dataOutput fht-table"></table></div>').appendTo(jQuerywrapper);
	            	    }
	            	}

	            	switch (true) {
	            	case !jQuerytfoot.length && settings.cloneHeadToFoot == true && settings.footer == true:
	            	    
	            	    var jQuerydivHead = jQuerywrapper.find('div.fht-thead');
	            	    
	            	    jQuerydivFoot.empty();
	            	    jQuerydivHead.find('table')
	            		.clone()
	            		.appendTo(jQuerydivFoot);
	            	    
	            	    break;
	            	case jQuerytfoot.length && settings.cloneHeadToFoot == false && settings.footer == true:
	            	    
	            	    jQuerydivFoot.find('table')
	            		.append(jQuerytfoot)
		                .css({
		                    'margin-top': -tableProps.border
		                });
	            	    
	            	    helpers._setupClone(jQuerydivFoot, tableProps.tfoot);
	            	    
	            	    break;
	            	}
	            	
	            },
	            
	            /*
	             * return object
	             * Widths of each thead cell and tbody cell for the first rows.
	             * Used in fixing widths for the fixed header and optional footer.
	             */
	            _getTableProps: function(jQueryobj) {
	                var tableProp = {
	                    thead: {},
	                    tbody: {},
	                    tfoot: {},
	                    border: 0
	                },
	                borderCollapse = 1;
	                
	                if (settings.borderCollapse == true) {
	                    borderCollapse = 2;
	                }
			
			tableProp.border = (jQueryobj.find('th:first-child').outerWidth() - jQueryobj.find('th:first-child').innerWidth()) / borderCollapse;
			
	                jQueryobj.find('thead tr:first-child th').each(function(index) {
	                    tableProp.thead[index] = jQuery(this).width() + tableProp.border;
	                });
	                
	                jQueryobj.find('tfoot tr:first-child td').each(function(index) {
	                    tableProp.tfoot[index] = jQuery(this).width() + tableProp.border;
	                });
	                
	                jQueryobj.find('tbody tr:first-child td').each(function(index) {
	                    tableProp.tbody[index] = jQuery(this).width() + tableProp.border;
	                });

	                return tableProp;
	            },
	            
	            /*
	             * return void
	             * Fix widths of each cell in the first row of obj.
	             */
	            _setupClone: function(jQueryobj, cellArray) {
	                var jQueryself    = jQueryobj,
	                selector = (jQueryself.find('thead').length) ?
	                    'thead th' : 
	                    (jQueryself.find('tfoot').length) ?
	                    'tfoot td' :
	                    'tbody td',
	                jQuerycell;
	                
	                jQueryself.find(selector).each(function(index) {
	                    jQuerycell = (jQuery(this).find('div.fht-cell').length) ? jQuery(this).find('div.fht-cell') : jQuery('<div class="fht-cell"></div>').appendTo(jQuery(this));
			    
	                    jQuerycell.css({
	                        'width': parseInt(cellArray[index])
	                    });
	                    
	                    /*
	                     * Fixed Header and Footer should extend the full width
	                     * to align with the scrollbar of the body 
	                     */
	                    if (!jQuery(this).closest('.fht-tbody').length && jQuery(this).is(':last-child') && !jQuery(this).closest('.fht-fixed-column').length) {
	                    	var padding = ((jQuery(this).innerWidth() - jQuery(this).width()) / 2) + settings.scrollbarOffset;
	                    	jQuery(this).css({
	                    	    'padding-right': padding + 'px'
	                    	});
	                    }
	                });
	            },
	            
	            /*
	             * return boolean
	             * Determine how the browser calculates fixed widths with padding for tables
	             * true if width = padding + width
	             * false if width = width
	             */
	            _isPaddingIncludedWithWidth: function() {
	            	var jQueryobj 			= jQuery('<table class="dataOutput fht-table"><tr><td style="padding: 10px; font-size: 10px;">test</td></tr></table>'),
	            	defaultHeight,
	            	newHeight;
	            	
	            	jQueryobj.appendTo('body');
	            	
	            	defaultHeight = jQueryobj.find('td').height();
	            	
	            	jQueryobj.find('td')
	            	    .css('height', jQueryobj.find('tr').height());
	            	
	            	newHeight = jQueryobj.find('td').height();
	            	jQueryobj.remove();

	            	if (defaultHeight != newHeight) {
	            	    return true;
	            	} else {
	            	    return false;
	            	}
	            	
	            },
	            
	            /*
	             * return int
	             * get the width of the browsers scroll bar
	             */
	            _getScrollbarWidth: function() {
	            	var scrollbarWidth = 0;
	            	
	            	if (!scrollbarWidth) {
        			    if (jQuery.browser.msie) {
        				var jQuerytextarea1 = jQuery('<textarea cols="10" rows="2"></textarea>')
        				    .css({ position: 'absolute', top: -1000, left: -1000 }).appendTo('body'),
        				jQuerytextarea2 = jQuery('<textarea cols="10" rows="2" style="overflow: hidden;"></textarea>')
        				    .css({ position: 'absolute', top: -1000, left: -1000 }).appendTo('body');
        				scrollbarWidth = jQuerytextarea1.width() - jQuerytextarea2.width() + 2; // + 2 for border offset
        				jQuerytextarea1.add(jQuerytextarea2).remove();
        			    } else {
        				var jQuerydiv = jQuery('<div />')
        				    .css({ width: 100, height: 100, overflow: 'auto', position: 'absolute', top: -1000, left: -1000 })
        				    .prependTo('body').append('<div />').find('div')
        				    .css({ width: '100%', height: 200 });
        				scrollbarWidth = 100 - jQuerydiv.width();
        				jQuerydiv.parent().remove();
        			    }
			         }
			
			       return scrollbarWidth;
	            }

	        }


	        // if a method as the given argument exists
	        if (methods[method]) {

	            // call the respective method
	            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));

	            // if an object is given as method OR nothing is given as argument
	        } else if (typeof method === 'object' || !method) {

	            // call the initialization method
	            return methods.init.apply(this, arguments);

	            // otherwise
	        } else {

	            // trigger an error
	            jQuery.error('Method "' +  method + '" does not exist in fixedHeaderTable plugin!');

	        }

	    };
	})(jQuery);