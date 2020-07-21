var ie = /MSIE/.test(navigator.userAgent);

var currentDiv = null;

function resizableObject() {
	this.el = null; // the div
	this.dir = ""; // direction
	this.grabx = null;
	this.width = null;
}

function getDirection(el) {
	var xPos, offset, dir;
	dir = "";

	xPos = window.event.offsetX;

	offset = 10;

	if (xPos > el.offsetWidth - offset)
		dir += "e";

	return dir;
}

function doDown() {
	var el = getReal(event.srcElement, "className", "openedPanelGrid");

	if (el == null) {
		currentDiv = null;
		return;
	}

	dir = getDirection(el);
	if (dir == "")
		return;

	currentDiv = new resizableObject();

	currentDiv.el = el;
	currentDiv.dir = dir;

	currentDiv.grabx = window.event.clientX;
	currentDiv.width = el.offsetWidth;

	window.event.returnValue = false;
	window.event.cancelBubble = true;
}

function doUp() {
	if (currentDiv != null) {
		currentDiv = null;
	}
}

function doMove() {
	var el, xPos, str, xMin, xMax, divR, posX, els, tmpel, iediff;
	xMin = 200; // min width
	xMax = 600; // max width

	el = getReal(event.srcElement, "className", "openedPanelGrid");

	if (el != null && el.className == "openedPanelGrid") {
		str = getDirection(el);

		if (str == "")
			str = "default";
		else
			str = "col-resize";
		el.style.cursor = str;
	}

	if (currentDiv != null && currentDiv.el != null) {
		if (dir.indexOf("e") != -1) {
			posX = Math.max(xMin, Math.min(xMax, currentDiv.width
					+ window.event.clientX - currentDiv.grabx));
			currentDiv.el.style.width = posX + "px";
			
		}

		window.event.returnValue = false;
		window.event.cancelBubble = true;
	}

	resize();
}

function getReal(el, type, value) {
	temp = el;
	while ((temp != null) && (temp.tagName != "BODY")) {
		if (eval("temp." + type) == value) {
			el = temp;
			return el;
		}
		temp = temp.parentElement;
	}
	return null;
}

document.onmousedown = doDown;
document.onmouseup = doUp;
document.onmousemove = doMove;

function extendElementModel() {
	HTMLElement.prototype.__defineGetter__("parentElement", function() {
		if (this.parentNode == this.ownerDocument)
			return null;
		return this.parentNode;
	});

	HTMLElement.prototype.__defineGetter__("children", function() {
		var tmp = [];
		var j = 0;
		var n;
		for ( var i = 0; i < this.childNodes.length; i++) {
			n = this.childNodes[i];
			if (n.nodeType == 1) {
				tmp[j++] = n;
				if (n.name) { // named children
					if (!tmp[n.name])
						tmp[n.name] = [];
					tmp[n.name][tmp[n.name].length] = n;
				}
				if (n.id) // child with id
					tmp[n.id] = n
			}
		}
		return tmp;
	});

	HTMLElement.prototype.contains = function(oEl) {
		if (oEl == this)
			return true;
		if (oEl == null)
			return false;
		return this.contains(oEl.parentNode);
	};
}
function extendEventObject() {
	Event.prototype.__defineSetter__("returnValue", function(b) {
		if (!b)
			this.preventDefault();
		return b;
	});
	Event.prototype.__defineSetter__("cancelBubble", function(b) {
		if (b)
			this.stopPropagation();
		return b;
	});
	Event.prototype.__defineGetter__("srcElement", function() {
		var node = this.target;
		if (!node)
			return;
		while (node.nodeType != 1) {
			node = node.parentNode;
			if (!node)
				return;
		}
		return node;
	});
	Event.prototype.__defineGetter__("fromElement", function() {
		var node;
		if (this.type == "mouseover")
			node = this.relatedTarget;
		else if (this.type == "mouseout")
			node = this.target;
		if (!node)
			return;
		while (node.nodeType != 1) {
			node = node.parentNode;
			if (!node)
				return;
		}
		return node;
	});
	Event.prototype.__defineGetter__("toElement", function() {
		var node;
		if (this.type == "mouseout")
			node = this.relatedTarget;
		else if (this.type == "mouseover")
			node = this.target;
		if (!node)
			return;
		while (node.nodeType != 1) {
			node = node.parentNode;
			if (!node)
				return;
		}
		return node;
	});
	Event.prototype.__defineGetter__("offsetX", function() {
		return this.layerX;
	});
	Event.prototype.__defineGetter__("offsetY", function() {
		return this.layerY;
	});
}

function emulateEventHandlers(eventNames) {
	for ( var i = 0; i < eventNames.length; i++) {
		document.addEventListener(eventNames[i], function(e) {
			window.event = e;
		}, true); // using capture
	}
}

if (!ie) {
	extendElementModel();
	extendEventObject();
	emulateEventHandlers( [ "mousemove", "mousedown", "mouseup" ]);
}

// fonction de resize de la contentview
function resize() {

	var elems;
	if (!ie) {
		elems = document.getElementsByName('containerTabDiv');
	} else {
		// pas de support de getElementsByName sur IE...
		elems = getElementsByName_iefix('div', 'containerTabDiv')
	}

	for ( var i = 0; i < elems.length; i++) {
		var containerTabDiv = elems[i];
		if (containerTabDiv != null) {

			var compensator = 0;

			var parent = containerTabDiv.offsetParent;
			var diff = 0;
			while (parent != null) {
				diff += parent.offsetLeft;
				parent = parent.offsetParent;
			}

			var parentN = containerTabDiv.parentNode;
			var currentPadding;
			var padding;
			while (parentN != null) {
				try {
					currentPadding = jQuery(parentN).css('padding-left');
					padding = parseInt(currentPadding.replace('px', ''));
					if (!isNaN(padding)) {
						compensator += padding;
					}
					currentPadding = jQuery(parentN).css('margin-left');
					padding = parseInt(currentPadding.replace('px', ''));
					if (!isNaN(padding)) {
						compensator += padding;
					}
					currentPadding = jQuery(parentN).css('margin-right');
					padding = parseInt(currentPadding.replace('px', ''));
					if (!isNaN(padding)) {
						compensator += padding;
					}
					currentPadding = jQuery(parentN).css('padding-right');
					padding = parseInt(currentPadding.replace('px', ''));
					if (!isNaN(padding)) {
						compensator += padding;
					}
				} catch (e) {
					compensator += 1;
				}
				parentN = parentN.parentNode;
			}
			
			compensator += 2;

			var userMetaServicesSearchDiv = document
					.getElementById('userMetaServicesSearchDiv');
			var finalWidth = (userMetaServicesSearchDiv.offsetWidth - diff - compensator)
					+ "px";

			containerTabDiv.style.width = finalWidth;

			// si child avec class=fht-fixed-body ou class=fht-table il faut les
			// resize aussi
			// => resize des tables avec colonnes fixes
			var fixedBody = jQuery(containerTabDiv).find(".fht-fixed-body");
			if (fixedBody) {
				fixedBody.width(finalWidth);
				var innerTable = jQuery(containerTabDiv)
						.find(".fht-table-init");
				if (innerTable) {
					innerTable.width(finalWidth);
				}
			}
		}
	}
}

function getElementsByName_iefix(tag, name) {

	var elem = document.getElementsByTagName(tag);
	var arr = new Array();
	for (i = 0, iarr = 0; i < elem.length; i++) {
		att = elem[i].getAttribute("name");
		if (att == name) {
			arr[iarr] = elem[i];
			iarr++;
		}
	}
	return arr;
}
