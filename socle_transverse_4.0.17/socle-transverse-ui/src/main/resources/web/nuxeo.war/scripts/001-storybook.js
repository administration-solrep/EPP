







// Autocomplete options.
// Doc here: https://github.com/ecedi/aria-autocomplete
var $ariaAutocompleteTabIndexed = [];
window.ariaAutocompleteDefaults = {
  minLength: 3,
  delay: 200,
  confirmOnBlur: false,
  showAllControl: false,
  noResultsText: "Pas de résultat",
  srAssistiveText: "Lorsque les résultats sont disponibles, utilisez les flèches 'haut' et 'bas' pour consulter et 'entrez' pour sélectionner. Utilisateurs d'appareils tactiles, explorez au toucher ou avec des gestes de balayage.",
  srResultsText: function srResultsText(length) {
    return "" + length + " " + (length === 1 ? "résultat disponible" : "résultats disponibles");
  },
  srDeleteText: "supprimer",
  srDeletedText: "supprimé",
  srDeleteAllText: "Supprimer toutes les options sélectionnées",
  srSelectedText: "sélectionné",
  srListLabelText: "Suggestions de recherche",
  onItemRender: function onItemRender(itemData) {
    var content = window.ariaAutocompleteDefaults.markMatch(itemData.label, this._currentQuery).html();
    var label = itemData.label.replace(/'/g, "&apos;"); // encode single quote

    return "<span aria-label='" + label + "'>" + content + "</span>";
  },
  onOpen: function onOpen(list) {
    // Tabindexed parent bugfix
    // Storing tabindexed parents
    // Disable parent tabindex
    // (tabindex causes autocomplete list to close on scrollbars)
    $ariaAutocompleteTabIndexed = $(list).parents("[tabindex=0]");
    $ariaAutocompleteTabIndexed.each(function () {
      $(this).removeAttr("tabindex");
      $ariaAutocompleteTabIndexed.push($(this));
    });
  },
  onClose: function onClose() {
    // Tabindexed parent bugfix
    // Restore parent original tabIndex (beforeOpen)
    $($ariaAutocompleteTabIndexed).each(function () {
      $(this).attr("tabindex", 0);
    });
  },
  onSearch: function onSearch(value) {
    this._currentQuery = value;
  },
  // Local vars
  _currentQuery: null
};

window.ariaAutocompleteDefaults.markMatch = function (text, term) {
  // Find where the match is
  var match = text.toUpperCase().indexOf(term.toUpperCase());
  var $result = $("<span></span>"); // If there is no match, move on

  if (match < 0) {
    return $result.text(text);
  } // Put in whatever text is before the match


  $result.text(text.substring(0, match)); // Mark the match

  var $match = $("<mark></mark>");
  $match.text(text.substring(match, match + term.length)); // Append the matching text

  $result.append($match); // Put in whatever is after the match

  $result.append(text.substring(match + term.length));
  return $result;
};








/**
 * File upload logic
 **/
window.initSimpleFilePicker = function () {
  //Querying all form files
  var formFiles = document.querySelectorAll(".form-file");
  formFiles.forEach(function (form) {
    if (!form.classList.contains("mounted")) {
      form.classList.add("mounted");
      new SimpleFilePicker(form);
    }
  });
};

var SimpleFilePicker = function SimpleFilePicker(container) {
  //Getting html elements
  this.container = container;
  this.input = container.querySelector("input");
  this.fileList = container.querySelector(".form-file__file-label-list");
  this.isMultiple = container.getAttribute("data-multiple") === "true";
  this.trigerer = container.querySelector(".form-file__label");
  this.template = container.querySelector(".form-file__file-label-item[hidden]").cloneNode(true);
  this.isUploadEnabled = true; //Binding according to type

  this.bindUploadEvent();
  this.checkUploadAvailability();
  this.updateFileList();
}; //Simple File picker


SimpleFilePicker.prototype.bindUploadEvent = function () {
  var _this = this; //Disabling behaviour for older browser that open files


  this.input.addEventListener("dragover", function (event) {
    event.preventDefault();
  }); //Handling manually drop actions

  this.input.addEventListener("drop", function (event) {
    event.preventDefault();

    _this.handleFileUpload(event.dataTransfer.files);
  }); //Handling simple file selection

  this.input.addEventListener("change", function () {
    if (_this.isUploadEnabled) {
      _this.handleFileUpload(_this.input.files);
    }
  });
}; //Handle for simple file Picker


SimpleFilePicker.prototype.handleFileUpload = function (files) {
  var _this = this; // input event handler


  function inputEventHandler(event) {
    event.preventDefault();
    event.stopPropagation();
  } // eslint-disable-next-line no-unreachable


  Array.prototype.forEach.call(files, function (file) {
    // Clone template and set vars.
    var newNode = _this.template.cloneNode(true);

    var input = newNode.querySelector(".js-file-label-input");
    var inputText = newNode.querySelector(".js-file-label-input-text");
    var fileInfos = newNode.querySelector(".js-file-label-file-infos");
    input.setAttribute("id", input.getAttribute("id") + "_" + randomId());
    input.setAttribute("name", input.getAttribute("data-name"));

    if (inputText != null) {
      inputText.setAttribute("id", inputText.getAttribute("id") + "_" + randomId()); // Create temp DataTransfer. Only way to set cloned imput files.

      inputText.setAttribute("name", inputText.getAttribute("data-name"));
    }

    var dt = new DataTransfer();
    dt.items.add(file);
    input.files = dt.files;
    var newFile = input.files[0];

    if (inputText !== null) {
      inputText.setAttribute("title", "Changer le nom du fichier : " + newFile.name);
      inputText.value = newFile.name.replace(/\.[^/.]+$/, "");
      fileInfos.appendChild(document.createTextNode("." + newFile.name.split(".").pop()));
      fileInfos.setAttribute("aria-hidden", "true");
    } else {
      var srSpan = document.createElement("span");
      srSpan.classList.add("sr-only");
      srSpan.textContent = "Fichier sélectionné : ";
      fileInfos.appendChild(srSpan);
      fileInfos.appendChild(document.createTextNode(newFile.name));
    }

    newNode.removeAttribute("hidden"); // Input type file "read only" workaround

    input.addEventListener("click", inputEventHandler);
    input.addEventListener("keyup", inputEventHandler);
    input.setAttribute("aria-disabled", "true");
    input.setAttribute("aria-hidden", "true");
    input.setAttribute("tabindex", "-1");
    var deleteButton = newNode.querySelector(".file-label__button");
    deleteButton.classList.remove("tooltipMounted");
    deleteButton.addEventListener("click", function () {
      var nodeIndex = Array.prototype.indexOf.call(newNode.parentNode.children, newNode);
      newNode.parentNode.removeChild(newNode);

      _this.checkUploadAvailability();

      _this.handleFocusPosition(nodeIndex);

      _this.updateFileList();
    });
    var fileDeleteText = deleteButton.querySelector(".sr-only");

    if (fileDeleteText) {
      fileDeleteText.textContent = fileDeleteText.textContent + " " + newFile.name;
    }

    _this.fileList.appendChild(newNode);
  }); // eslint-disable-next-line no-unreachable

  this.input.value = "";
  this.checkUploadAvailability();
  this.updateFileList();
  window.initToolTip(); // if single upload, move focus to the only delete button

  if (!_this.isMultiple) {
    _this.handleFocusPosition(1);
  }
}; //Controls utilities


SimpleFilePicker.prototype.checkUploadAvailability = function () {
  if (!this.isMultiple) {
    if (this.getFileCount() === 1) {
      this.disableUpload();
    } else {
      this.enableUpload();
    }
  }
}; // Update filelist.


SimpleFilePicker.prototype.updateFileList = function () {
  if (this.getFileCount() > 0) {
    this.fileList.removeAttribute("hidden");
  } else {
    this.fileList.setAttribute("hidden", "hidden");
  }
};

SimpleFilePicker.prototype.enableUpload = function () {
  this.isUploadEnabled = true;
  this.trigerer.classList.remove("form-file__label--disabled");
  this.input.removeAttribute("disabled");
};

SimpleFilePicker.prototype.disableUpload = function () {
  this.isUploadEnabled = false;
  this.trigerer.classList.add("form-file__label--disabled");
  this.input.setAttribute("disabled", "disabled");
};

SimpleFilePicker.prototype.getFileCount = function () {
  return this.fileList.children.length - 1;
};

SimpleFilePicker.prototype.handleFocusPosition = function (index) {
  var _this = this;

  var potentialFocus = _this.input; // seeking for next or previous available file in list

  if (this.fileList.childNodes.length > 1) {
    var nextChild = _this.fileList.childNodes[index];
    var prevChild = _this.fileList.childNodes[index - 1];

    if (nextChild) {
      potentialFocus = nextChild.querySelector(".file-label__button");
    } else if (prevChild) {
      potentialFocus = prevChild.querySelector(".file-label__button");
    }
  } // if no file available, focus on the initial input to upload new files


  potentialFocus.focus();
}; // eslint-disable-next-line no-unused-vars


function randomId() {
  return Math.floor(Math.random() * new Date().getTime());
}




/**
 * This file handle all the menu logic
 */
//Defining some const to ease code
var CLASS_TOGGLE_OPENED = "header-menu__toggle--opened";
var CLASS_MENU_OPENED = "header-menu--opened";
var CLASS_MENU_ITEM_HIDDEN = "header-menu-item--hidden";
var CLASS_MENU_ITEM_ACTIVE = "header-menu-item--active";
var CLASS_MENU_SUBITEMLIST_ACTIVE = "header-menu-item__subitem-list--visible";

window.initHeaderMenu = function () {
  var menusContainer = document.querySelectorAll(".header-menu");

  if (menusContainer) {
    menusContainer.forEach(function (container) {
      if (!container.classList.contains("mounted")) {
        container.classList.add("mounted"); //Handle close/open logic

        var toggleMenu = function toggleMenu() {
          if (MenuManager.isMenuOpened(container)) {
            MenuManager.closeMenu(container);
          } else {
            MenuManager.openMenu(container);
            MenuManager.MainMenu.focusFirstItemInMenu(container);
          }
        };

        MenuManager.getMenuToggle(container).addEventListener("click", toggleMenu);
        MenuManager.getMenuToggle(container).addEventListener("keydown", function (event) {
          if (event.key === "Enter" || event.key === " ") {
            toggleMenu();
            event.preventDefault();
          }
        }); // Close container while clicking outside of it

        document.body.addEventListener("click", function () {
          if (!$(document.activeElement).parent().is("[class*='header-menu-item'")) {
            // if clicked element's parent doesn't have a class or does not have header-menu-item class, then it's an outside click
            MenuManager.closeMenu(container);
          }
        });
        document.addEventListener("keydown", function (event) {
          if (event.key === "Escape" && MenuManager.isMenuOpened(container)) {
            MenuManager.closeMenu(container);
          }
        });
        MenuManager.getMenu(container).addEventListener("click", function (e) {
          if (e.target.classList.contains("header-menu__backdrop")) {
            MenuManager.closeMenu(container);
          }
        }); //Handling submenu display

        MenuManager.getMenuItemsLinks(container).forEach(function (item) {
          if (MenuManager.itemHasChildren(item)) {
            item.addEventListener("click", function () {
              MenuManager.itemClick(container, item.parentNode);
            });
            item.addEventListener("keydown", function (event) {
              event.preventDefault();

              if (event.key === "Enter" || event.key === " " || event.key === "ArrowRight") {
                MenuManager.itemClick(container, item.parentNode);
              }
            });
          } else {
            // Or close on navigation
            item.addEventListener("click", function () {
              MenuManager.closeMenu(container);
            });
            item.addEventListener("keydown", function (event) {
              if (event.key === "Enter" || event.key === " ") {
                if (this.href === window.location) {
                  MenuManager.closeMenu(container);
                }
              }
            });
          }
        });
        /**
         * Keyboard navigation event
         */
        //Going into the menu

        window.addEventListener("keyup", function (e) {
          var focusedElement = document.activeElement; //Case if we're not in the menu

          if (focusedElement.classList.contains("header-menu__toggle")) {
            MenuManager.MainMenu.focusFirstItemInMenu(container);
          }

          if (focusedElement.classList.contains("header-menu__focustrap-last")) {
            e.preventDefault();
            MenuManager.closeMenu(container);
            MenuManager.MainMenu.focusActiveItem(container);
          } else if (!$(focusedElement).is("[class*='header-menu'")) {
            MenuManager.closeMenu(container);
          }
        }); //Navigating through the main menu items

        MenuManager.getMenuItemsLinks(container).forEach(function (item) {
          //Checking if we're in an opened menu or not
          item.addEventListener("keydown", function (event) {
            var position = MenuManager.getMainItemPosition(container, item);
            var itemsCount = MenuManager.getMenuItemsLinks(container).length;

            if (MenuManager.isSubMenuOpened(item)) {
              if (event.shiftKey && event.key === "Tab") {
                event.preventDefault();
                MenuManager.backToMainMenu(container, item);
              } else if (event.key === "ArrowDown") {
                //we're gonna go to the first submenu item
                var focusedElement = document.activeElement;

                if (focusedElement.classList.contains("header-menu-item__label")) {
                  MenuManager.SubMenu.focusFirstItem(focusedElement);
                }
              }
            } else {
              if (event.key === "ArrowDown") {
                if (position < itemsCount) {
                  MenuManager.MainMenu.goToNextItem(container, item);
                }
              }

              if (event.key === "ArrowUp") {
                if (position > 0) {
                  MenuManager.MainMenu.goToPreviousItem(container, item);
                }
              }
            }
          }); //Navigation trough submenuitems

          var subItems = MenuManager.getSublistItems(MenuManager.getItemSublist(item.parentNode));

          if (subItems) {
            item.addEventListener("click", function (e) {
              e.preventDefault();
            });
            subItems.forEach(function (subitem) {
              subitem.addEventListener("click", function () {
                // Close menu on subitem click
                MenuManager.closeMenu(container);
              });
              subitem.addEventListener("keydown", function (event) {
                var position = MenuManager.getSubItemPosition(item.parentNode, subitem);
                var itemsCount = MenuManager.getSublistItems(item.parentNode).length;

                if ((event.key === "Tab" || event.key === "ArrowDown") && position >= itemsCount - 1) {
                  // if last item
                  event.preventDefault();
                  MenuManager.backToMainMenu(container);
                  MenuManager.MainMenu.goToNextItem(container, item);
                }

                if (event.key === "ArrowDown") {
                  if (position < itemsCount) {
                    MenuManager.SubMenu.goToNextItem(item.parentNode, subitem);
                  }
                }

                if (event.key === "ArrowLeft") {
                  MenuManager.backToMainMenu(container, item);
                }

                if (event.key === "ArrowUp") {
                  if (position === 0) {
                    MenuManager.SubMenu.focusLabel(subitem);
                  } else {
                    MenuManager.SubMenu.goToPreviousItem(item.parentNode, subitem);
                  }
                }

                if (event.key === "Enter" || event.key === " ") {
                  if (event.target.href === window.location) {
                    MenuManager.closeMenu(container);
                  }
                }
              });
            });
          }
        });
      }
    });
  }
};

var MenuManager = {
  //Helpers functions
  isMenuOpened: function isMenuOpened(container) {
    return container.classList.contains(CLASS_MENU_OPENED);
  },
  isItemActive: function isItemActive(item) {
    return item.classList.contains(CLASS_MENU_ITEM_ACTIVE);
  },
  getMainItemPosition: function getMainItemPosition(container, selectedItem) {
    var count = 0;
    var doCount = true;
    MenuManager.getMenuItemsLinks(container).forEach(function (item) {
      if (item === selectedItem) {
        doCount = false;
      } else if (doCount) {
        count++;
      }
    });
    return count;
  },
  isSubMenuOpened: function isSubMenuOpened(item) {
    var parent = item.parentNode;
    var subMenu = parent.querySelector(".header-menu-item__subitem-list");

    if (!subMenu) {
      return false;
    }

    return subMenu.classList.contains("header-menu-item__subitem-list--visible");
  },
  getSubItemPosition: function getSubItemPosition(item, selectedItem) {
    var count = 0;
    var doCount = true;
    MenuManager.getSublistItems(item).forEach(function (subitem) {
      if (subitem === selectedItem) {
        doCount = false;
      } else if (doCount) {
        count++;
      }
    });
    return count;
  },
  itemHasChildren: function itemHasChildren(item) {
    return !!MenuManager.getItemSublist(item.parentNode);
  },
  //Element getters functions
  getMenuToggle: function getMenuToggle(container) {
    return container.querySelector(".header-menu__toggle");
  },
  getMenu: function getMenu(container) {
    return container.querySelector(".header-menu__menu-list");
  },
  getMenuItems: function getMenuItems(container) {
    return container.querySelectorAll(".header-menu .header-menu__menu-list .header-menu-item");
  },
  getMenuItemsLinks: function getMenuItemsLinks(container) {
    return container.querySelectorAll(".header-menu .header-menu__menu-list .header-menu-item .header-menu-item__label");
  },
  getItemSublist: function getItemSublist(item) {
    return item.querySelector(".header-menu-item__subitem-list");
  },
  getSublistItems: function getSublistItems(sublist) {
    return sublist && sublist.querySelectorAll(".header-menu-item__subitem");
  },
  //Actions functions
  openMenu: function openMenu(container) {
    MenuManager.getMenuToggle(container).classList.add(CLASS_TOGGLE_OPENED);
    MenuManager.getMenuToggle(container).setAttribute("tabindex", "-1");
    container.classList.add(CLASS_MENU_OPENED);
    container.parentNode.parentNode.classList.add("header--menu-opened");
    MenuManager.getMenuToggle(container).setAttribute("aria-expanded", "true");
    window.toggleNoScroll(false);
  },
  closeMenu: function closeMenu(container) {
    MenuManager.backToMainMenu(container);
    MenuManager.getMenuToggle(container).classList.remove(CLASS_TOGGLE_OPENED);
    MenuManager.getMenuToggle(container).setAttribute("tabindex", "0");
    container.parentNode.parentNode.classList.remove("header--menu-opened");
    container.classList.remove(CLASS_MENU_OPENED);
    MenuManager.getMenuToggle(container).setAttribute("aria-expanded", "false");
    window.toggleNoScroll(true);
  },
  itemClick: function itemClick(container, selectedItem) {
    if (MenuManager.isItemActive(selectedItem)) {
      selectedItem.classList.remove(CLASS_MENU_ITEM_ACTIVE);
      MenuManager.getItemSublist(selectedItem).classList.remove(CLASS_MENU_SUBITEMLIST_ACTIVE);
      MenuManager.backToMainMenu(container);
    } else {
      MenuManager.activeItem(container, selectedItem);
    }
  },
  activeItem: function activeItem(container, selectedItem) {
    MenuManager.getMenuItems(container).forEach(function (item) {
      var link = item.querySelector("a");
      var back = item.querySelector(".header-menu-item__back");

      if (item === selectedItem) {
        item.classList.add(CLASS_MENU_ITEM_ACTIVE);
        MenuManager.getItemSublist(item).classList.add(CLASS_MENU_SUBITEMLIST_ACTIVE);

        if (link) {
          link.setAttribute("aria-expanded", "true");
        }

        if (back) {
          back.setAttribute("aria-hidden", "false");
        }

        MenuManager.SubMenu.focusFirstItem(item, true);
      } else {
        item.classList.add(CLASS_MENU_ITEM_HIDDEN);

        if (link) {
          link.setAttribute("aria-expanded", "false");
        }

        if (back) {
          back.setAttribute("aria-hidden", "true");
        }
      }
    });
  },
  backToMainMenu: function backToMainMenu(container, currentItem) {
    MenuManager.getMenuItems(container).forEach(function (item) {
      var link = item.querySelector("a");
      var back = item.querySelector(".header-menu-item__back");

      if (item.classList.contains(CLASS_MENU_ITEM_HIDDEN)) {
        item.classList.remove(CLASS_MENU_ITEM_HIDDEN);
      }

      if (item.classList.contains(CLASS_MENU_ITEM_ACTIVE)) {
        item.classList.remove(CLASS_MENU_ITEM_ACTIVE);
      }

      if (link) {
        link.setAttribute("aria-expanded", "false");
      }

      if (back) {
        back.setAttribute("aria-hidden", "true");
      }

      var sub = item.querySelector("." + CLASS_MENU_SUBITEMLIST_ACTIVE);

      if (sub) {
        sub.classList.remove(CLASS_MENU_SUBITEMLIST_ACTIVE);
      }
    });

    if (currentItem) {
      MenuManager.MainMenu.focusItemInMenu(container, MenuManager.getMainItemPosition(container, currentItem));
    } else {
      MenuManager.MainMenu.focusFirstItemInMenu(container);
    }
  },
  //Keyboard navigation
  MainMenu: {
    focusFirstItemInMenu: function focusFirstItemInMenu(container) {
      MenuManager.getMenuItemsLinks(container)[0].focus();
    },
    focusItemInMenu: function focusItemInenu(container, itemPosition) {
      MenuManager.getMenuItemsLinks(container)[itemPosition].focus();
    },
    goToNextItem: function goToNextItem(container, currentItem) {
      var nextItem = MenuManager.getMenuItemsLinks(container)[MenuManager.getMainItemPosition(container, currentItem) + 1];

      if (nextItem) {
        nextItem.focus();
      }
    },
    goToPreviousItem: function goToPreviousItem(container, currentItem) {
      var previousItem = MenuManager.getMenuItemsLinks(container)[MenuManager.getMainItemPosition(container, currentItem) - 1];

      if (previousItem) {
        previousItem.focus();
      }
    },
    focusActiveItem: function focusActiveItem(container) {
      MenuManager.getMenuItems(container).forEach(function (item) {
        if (item.getAttribute("data-current") === "true") {
          item.querySelector("a").focus();
        }
      });
    }
  },
  SubMenu: {
    focusFirstItem: function focusFirstItem(item) {
      var noParent = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
      var menu = undefined;

      if (noParent) {
        menu = item.querySelector(".header-menu-item__subitem-list");
      } else {
        menu = item.parentNode.querySelector(".header-menu-item__subitem-list");
      }

      if (menu) {
        MenuManager.getSublistItems(menu)[0].querySelector("a").focus();
      }
    },
    goToNextItem: function goToNextItem(container, currentItem) {
      var nextItem = MenuManager.getSublistItems(container)[MenuManager.getSubItemPosition(container, currentItem) + 1];

      if (nextItem) {
        nextItem.querySelector("a").focus();
      }
    },
    goToPreviousItem: function goToPreviousItem(container, currentItem) {
      var nextItem = MenuManager.getSublistItems(container)[MenuManager.getSubItemPosition(container, currentItem) + -1];

      if (nextItem) {
        nextItem.querySelector("a").focus();
      }
    },
    focusLabel: function focusLabel(item) {
      var label = item.parentNode.parentNode.querySelector(".header-menu-item__label");
      label.focus();
    }
  }
};




window.initSelectAutocomplete = function () {
  var selects = document.querySelectorAll("select.js-aria-autocomplete-select");
  selects.forEach(function (select) {
    var multiple = select.hasAttribute("multiple");
    var minLength = $(select).attr("data-min-length") ? $(select).attr("data-min-length") : window.ariaAutocompleteDefaults.minLength; // Autocomplete options.
    // Doc here: https://github.com/ecedi/aria-autocomplete

    var optionsSelect = {
      autoGrow: true,
      multiple: multiple,
      deleteOnBackspace: true,
      deleteAllControl: true,
      minLength: minLength
    }; // Merge default settings and options

    if (window.ariaAutocompleteDefaults) {
      optionsSelect = $.extend({}, window.ariaAutocompleteDefaults, optionsSelect);
    }

    if (!select.classList.contains("mounted")) {
      select.classList.add("mounted");
      AriaAutocomplete(select, optionsSelect);
    }
  });
};



var debouncedTextUpdate = debounce(maxlengthTextUpdate, 500);
document.body.addEventListener("input", listenFormMaxChar);
document.body.addEventListener("paste", listenFormMaxChar);
document.body.addEventListener("cut", listenFormMaxChar);

function listenFormMaxChar(e) {
  var textInput = e.target;

  if (textInput.classList.contains("js-text-input-max-length")) {
    updateInputValueLengthCounter(textInput);
    debouncedTextUpdate(textInput);
  }
}

function updateInputValueLengthCounter(textInput) {
  var maxLengthValue = $(textInput).closest(".form-input").get(0).querySelector(".form-input__max-length__value");

  if (maxLengthValue) {
    maxLengthValue.innerText = textInput.value.length;
  }
}

function maxlengthTextUpdate(textInput) {
  var maxLengthValue = $(textInput).closest(".form-input").get(0).querySelector(".form-input__max-length__value");

  if (maxLengthValue) {
    var maxLength = parseInt($(textInput).closest(".form-input").get(0).querySelector(".form-input__max-length__max").textContent);
    var maxLengthIndication = $(textInput).closest(".form-input").get(0).querySelector(".form-input__max-length-reached");

    if (textInput.value.length === maxLength) {
      maxLengthIndication.textContent = "Nombre maximum de " + maxLength + " caractères atteint.";
    } else {
      maxLengthIndication.textContent = textInput.value.length + " caractères saisis sur " + maxLength + ".";
    }
  }
}

function debounce(func, wait, immediate) {
  var timeout;
  return function executedFunction() {
    var context = this;
    var args = arguments;

    var later = function later() {
      timeout = null;
      if (!immediate) func.apply(context, args);
    };

    var callNow = immediate && !timeout;
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
    if (callNow) func.apply(context, args);
  };
}




window.initBackToTop = function () {
  var btts = document.querySelectorAll(".back-to-top");
  btts.forEach(function (btt) {
    btt.addEventListener("click", function () {
      window.scrollTo({
        top: 0,
        behavior: "smooth"
      });
      setTimeout(function () {
        document.getElementById("topmenubutton").focus(); //set focus on top menu
      }, 500);
    });
  });
  window.addEventListener("scroll", function () {
    var scrollPos = window.scrollY;

    if (scrollPos >= window.innerHeight) {
      var _btts = document.querySelectorAll(".back-to-top");

      _btts.forEach(function (btt) {
        btt.classList.add("back-to-top--visible");
      });
    } else {
      var _btts2 = document.querySelectorAll(".back-to-top");

      _btts2.forEach(function (btt) {
        if (btt.classList.contains("back-to-top--visible")) {
          btt.classList.remove("back-to-top--visible");
        }
      });
    }
  });
};



if (!SVGElement.prototype.contains) {
  SVGElement.prototype.contains = HTMLDivElement.prototype.contains;
}




window.initDropdown = function () {
  window.dropDowns = {};
  var dropdowns = document.querySelectorAll(".base-drop-down__button");
  dropdowns.forEach(function (dropDown) {
    if (!dropDown.classList.contains("mounted")) {
      var tempElement = null;
      dropDown.classList.add("mounted"); //Registering to drop down group

      var dropDownGroup = dropDown.parentNode.getAttribute("data-drop-group");

      if (dropDownGroup !== "") {
        window.dropDowns[dropDownGroup] = null;
      }

      dropDown.addEventListener("click", function () {
        var isDropDownActive = dropDown.parentNode.querySelector(".base-drop-down__button--opened");

        if (isDropDownActive) {
          dropDown.parentNode.querySelector(".base-drop-down__button").classList.remove("base-drop-down__button--opened");
          tempElement = dropDown.parentNode.querySelector(".base-drop-down__drop");
          tempElement.classList.remove("base-drop-down__drop--visible");

          if (dropDownGroup !== "") {
            window.dropDowns[dropDownGroup] = null;
          }

          dropDown.setAttribute("aria-expanded", "false");
        } else {
          //Closing other dropdown from the group
          if (dropDownGroup !== "" && window.dropDowns[dropDownGroup]) {
            var element = window.dropDowns[dropDownGroup];
            element.querySelector(".base-drop-down__button").classList.remove("base-drop-down__button--opened");
            tempElement = element.querySelector(".base-drop-down__drop");
            tempElement.classList.remove("base-drop-down__drop--visible");
            tempElement.setAttribute("aria-expanded", "false");
          }

          dropDown.parentNode.querySelector(".base-drop-down__button").classList.add("base-drop-down__button--opened");
          tempElement = dropDown.parentNode.querySelector(".base-drop-down__drop");
          tempElement.classList.add("base-drop-down__drop--visible");
          dropDown.setAttribute("aria-expanded", "true");
          tempElement.style.minWidth = dropDown.parentNode.querySelector(".base-drop-down__button").offsetWidth + "px";

          if (dropDownGroup !== "") {
            window.dropDowns[dropDownGroup] = dropDown.parentNode;
          } // Set right position for ellipses in tables


          if (dropDown.parentNode.classList.contains("table-line__drop-down--right") || dropDown.parentNode.classList.contains("table-line__drop-down--ellipse")) {
            var btn = dropDown.parentNode.querySelector(".base-drop-down__button");
            var right = document.documentElement.clientWidth - btn.getBoundingClientRect().right; // offsetRight of toggler

            dropDown.parentNode.querySelector(".base-drop-down__drop").style.right = right + "px";
          }
        }
      }); //Handling focus out

      var parent = dropDown.parentNode;
      parent.addEventListener("focusout", function (event) {
        var isChild = parent.contains(event.relatedTarget);

        if (!isChild) {
          parent.querySelector(".base-drop-down__button").classList.remove("base-drop-down__button--opened");
          tempElement = parent.querySelector(".base-drop-down__drop");
          tempElement.classList.remove("base-drop-down__drop--visible");
          parent.querySelector(".base-drop-down__button").setAttribute("aria-expanded", "false");
        }
      });
    }
  });
};












/*
 *   This content is licensed according to the W3C Software License at
 *   https://www.w3.org/Consortium/Legal/2015/copyright-software-and-document
 *
 *   File:   datepicker.js
 */
var monthPickerMode = {
  NONE: "none",
  FIRST: "first",
  LAST: "last"
};

var CalendarButtonInput = function CalendarButtonInput(inputNode, buttonNode, datepicker) {
  this.inputNode = inputNode;
  this.buttonNode = buttonNode;
  this.imageNode = false;
  this.datepicker = datepicker;
  this.defaultLabel = "Choisir une date";
  this.keyCode = Object.freeze({
    ENTER: 13,
    SPACE: 32
  });
};

CalendarButtonInput.prototype.init = function () {
  this.buttonNode.addEventListener("click", this.handleClick.bind(this));
  this.buttonNode.addEventListener("keydown", this.handleKeyDown.bind(this));
  this.buttonNode.addEventListener("focus", this.handleFocus.bind(this));
};

CalendarButtonInput.prototype.handleKeyDown = function (event) {
  var flag = false;

  switch (event.keyCode) {
    case this.keyCode.SPACE:
    case this.keyCode.ENTER:
      this.datepicker.show();
      this.datepicker.setFocusDay();
      flag = true;
      break;

    default:
      break;
  }

  if (flag) {
    event.stopPropagation();
    event.preventDefault();
  }
};

CalendarButtonInput.prototype.handleClick = function () {
  if (!this.datepicker.isOpen()) {
    this.datepicker.show();
    this.datepicker.setFocusDay();
  } else {
    this.datepicker.hide();
  }

  event.stopPropagation();
  event.preventDefault();
};

CalendarButtonInput.prototype.setLabel = function (str) {
  if (typeof str === "string" && str.length) {
    str = ", " + str;
  }

  this.buttonNode.setAttribute("aria-label", this.defaultLabel + str);
};

CalendarButtonInput.prototype.setFocus = function () {
  this.buttonNode.focus();
};

CalendarButtonInput.prototype.setDate = function (day) {
  if (this.datepicker.monthPicker) {
    switch (this.datepicker.monthDayPick) {
      case monthPickerMode.FIRST:
        this.inputNode.value = "01/" + formatTwoDigits(day.getMonth() + 1) + "/" + day.getFullYear();

        if (this.datepicker.nextField && this.datepicker.nextField.value === "") {
          this.datepicker.nextField.value = lastDayOfMonth(day.getFullYear(), day.getMonth()) + "/" + formatTwoDigits(day.getMonth() + 1) + "/" + day.getFullYear();
        }

        break;

      case monthPickerMode.LAST:
        this.inputNode.value = lastDayOfMonth(day.getFullYear(), day.getMonth()) + "/" + formatTwoDigits(day.getMonth() + 1) + "/" + day.getFullYear();

        if (this.datepicker.prevField && this.datepicker.prevField.value === "") {
          this.datepicker.prevField = "01/" + formatTwoDigits(day.getMonth() + 1) + "/" + day.getFullYear();
        }

        break;

      default:
        this.inputNode.value = formatTwoDigits(day.getMonth() + 1) + "/" + day.getFullYear();
        break;
    }
  } else {
    this.inputNode.value = formatTwoDigits(day.getDate()) + "/" + formatTwoDigits(day.getMonth() + 1) + "/" + day.getFullYear();
  }
};

CalendarButtonInput.prototype.getDate = function () {
  return this.inputNode.value;
};

CalendarButtonInput.prototype.getDateLabel = function () {
  var label = "";
  var parts = this.inputNode.value.split("/");

  if (parts.length === 3 && Number.isInteger(parseInt(parts[0])) && Number.isInteger(parseInt(parts[1])) && Number.isInteger(parseInt(parts[2]))) {
    var month = parseInt(parts[1]);
    var day = parseInt(parts[0]) - 1;
    var year = parseInt(parts[2]);
    label = this.datepicker.getDateForButtonLabel(year, month, day);
  }

  return label;
};

CalendarButtonInput.prototype.handleFocus = function () {
  var dateLabel = this.getDateLabel();

  if (dateLabel) {
    this.setLabel("la date sélectionnée est le " + dateLabel);
  } else {
    this.setLabel("");
  }
}; // Initialize menu button date picker


var DatePickerDay = function DatePickerDay(domNode, datepicker, index, row, column) {
  this.index = index;
  this.row = row;
  this.column = column;
  this.day = new Date();
  this.domNode = domNode;
  this.datepicker = datepicker;
  this.keyCode = Object.freeze({
    TAB: 9,
    ENTER: 13,
    ESC: 27,
    SPACE: 32,
    PAGEUP: 33,
    PAGEDOWN: 34,
    END: 35,
    HOME: 36,
    LEFT: 37,
    UP: 38,
    RIGHT: 39,
    DOWN: 40
  });
};

DatePickerDay.prototype.init = function () {
  this.domNode.setAttribute("tabindex", "-1");
  this.domNode.addEventListener("mousedown", this.handleMouseDown.bind(this));
  this.domNode.addEventListener("keydown", this.handleKeyDown.bind(this));
  this.domNode.addEventListener("focus", this.handleFocus.bind(this));
  this.domNode.innerHTML = "-1";
};

DatePickerDay.prototype.isDisabled = function () {
  return this.domNode.classList.contains("disabled");
};

DatePickerDay.prototype.updateDay = function (disable, day) {
  if (disable) {
    this.domNode.classList.add("disabled");
  } else {
    this.domNode.classList.remove("disabled");
  }

  this.day = new Date(day);
  this.domNode.innerHTML = this.day.getDate();
  this.domNode.setAttribute("tabindex", "-1");
  this.domNode.removeAttribute("aria-selected");
  var d = this.day.getDate().toString();

  if (this.day.getDate() < 9) {
    d = "0" + d;
  }

  var m = this.day.getMonth() + 1;

  if (this.day.getMonth() < 9) {
    m = "0" + m;
  }

  this.domNode.setAttribute("data-date", this.day.getFullYear() + "-" + m + "-" + d);
};

DatePickerDay.prototype.handleKeyDown = function (event) {
  var flag = false;

  switch (event.keyCode) {
    case this.keyCode.ESC:
      this.datepicker.hide();
      break;

    case this.keyCode.TAB:
      this.datepicker.cancelButtonNode.focus();

      if (event.shiftKey) {
        this.datepicker.nextYearNode.focus();
      }

      this.datepicker.setMessage("");
      flag = true;
      break;

    case this.keyCode.ENTER:
    case this.keyCode.SPACE:
      this.datepicker.setTextboxDate(this.day);
      this.datepicker.hide();
      flag = true;
      break;

    case this.keyCode.RIGHT:
      this.datepicker.moveFocusToNextDay();
      flag = true;
      break;

    case this.keyCode.LEFT:
      this.datepicker.moveFocusToPreviousDay();
      flag = true;
      break;

    case this.keyCode.DOWN:
      this.datepicker.moveFocusToNextWeek();
      flag = true;
      break;

    case this.keyCode.UP:
      this.datepicker.moveFocusToPreviousWeek();
      flag = true;
      break;

    case this.keyCode.PAGEUP:
      if (event.shiftKey) {
        this.datepicker.moveToPreviousYear();
      } else {
        this.datepicker.moveToPreviousMonth();
      }

      flag = true;
      break;

    case this.keyCode.PAGEDOWN:
      if (event.shiftKey) {
        this.datepicker.moveToNextYear();
      } else {
        this.datepicker.moveToNextMonth();
      }

      flag = true;
      break;

    case this.keyCode.HOME:
      this.datepicker.moveFocusToFirstDayOfWeek();
      flag = true;
      break;

    case this.keyCode.END:
      this.datepicker.moveFocusToLastDayOfWeek();
      flag = true;
      break;
  }

  if (flag) {
    event.stopPropagation();
    event.preventDefault();
  }
};

DatePickerDay.prototype.handleMouseDown = function (event) {
  if (this.isDisabled()) {
    this.datepicker.moveFocusToDay(this.date);
  } else {
    this.datepicker.setTextboxDate(this.day);
    this.datepicker.hide();
  }

  event.stopPropagation();
  event.preventDefault();
};

DatePickerDay.prototype.handleFocus = function () {
  this.datepicker.setMessage(this.datepicker.messageCursorKeys);
};

var DatePicker = function DatePicker(inputNode, buttonNode, dialogNode) {
  var monthPicker = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : false;
  var monthDayPick = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : monthPickerMode.NONE;
  var nextField = arguments.length > 5 && arguments[5] !== undefined ? arguments[5] : null;
  var prevField = arguments.length > 6 && arguments[6] !== undefined ? arguments[6] : null;
  this.dayLabels = ["Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"];
  this.monthLabels = ["Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"];
  this.messageCursorKeys = "Cursor keys can navigate dates";
  this.lastMessage = "";
  this.inputNode = inputNode;
  this.buttonNode = buttonNode;
  this.dialogNode = dialogNode;
  this.messageNode = dialogNode.querySelector(".message");
  this.monthPicker = monthPicker;
  this.monthDayPick = monthDayPick;
  this.prevField = prevField;
  this.nextField = nextField;
  this.dateInput = new CalendarButtonInput(this.inputNode, this.buttonNode, this);
  this.MonthYearNode = this.dialogNode.querySelector(".monthYear");
  this.prevYearNode = this.dialogNode.querySelector(".prevYear");
  this.prevMonthNode = this.dialogNode.querySelector(".prevMonth");
  this.nextMonthNode = this.dialogNode.querySelector(".nextMonth");
  this.nextYearNode = this.dialogNode.querySelector(".nextYear");
  this.okButtonNode = this.dialogNode.querySelector('button[value="ok"]');
  this.cancelButtonNode = this.dialogNode.querySelector('button[value="ok"]');
  this.tbodyNode = this.dialogNode.querySelector("table.dates tbody");
  this.lastRowNode = null;
  this.days = [];
  this.focusDay = new Date();
  this.selectedDay = new Date(0, 0, 1);
  this.isMouseDownOnBackground = false;
  this.keyCode = Object.freeze({
    TAB: 9,
    ENTER: 13,
    ESC: 27,
    SPACE: 32,
    PAGEUP: 33,
    PAGEDOWN: 34,
    END: 35,
    HOME: 36,
    LEFT: 37,
    UP: 38,
    RIGHT: 39,
    DOWN: 40
  });
};

DatePicker.prototype.init = function () {
  this.dateInput.init();
  this.okButtonNode.addEventListener("click", this.handleOkButton.bind(this));
  this.okButtonNode.addEventListener("keydown", this.handleOkButton.bind(this));
  this.cancelButtonNode.addEventListener("click", this.handleCancelButton.bind(this));
  this.cancelButtonNode.addEventListener("keydown", this.handleCancelButton.bind(this));
  this.prevMonthNode.addEventListener("click", this.handlePreviousMonthButton.bind(this));
  this.nextMonthNode.addEventListener("click", this.handleNextMonthButton.bind(this));
  this.prevYearNode.addEventListener("click", this.handlePreviousYearButton.bind(this));
  this.nextYearNode.addEventListener("click", this.handleNextYearButton.bind(this));
  this.prevMonthNode.addEventListener("keydown", this.handlePreviousMonthButton.bind(this));
  this.nextMonthNode.addEventListener("keydown", this.handleNextMonthButton.bind(this));
  this.prevYearNode.addEventListener("keydown", this.handlePreviousYearButton.bind(this));
  this.nextYearNode.addEventListener("keydown", this.handleNextYearButton.bind(this));
  document.body.addEventListener("mousedown", this.handleBackgroundMouseDown.bind(this), true);
  document.body.addEventListener("mouseup", this.handleBackgroundMouseUp.bind(this), true); // Create Grid of Dates

  this.tbodyNode.innerHTML = "";
  var index = 0;

  for (var i = 0; i < 6; i++) {
    var row = this.tbodyNode.insertRow(i);
    this.lastRowNode = row;
    row.classList.add("dateRow");

    for (var j = 0; j < 7; j++) {
      var cell = document.createElement("td");
      cell.classList.add("dateCell");
      var cellButton = document.createElement("button");
      cellButton.classList.add("dateButton");
      cell.appendChild(cellButton);
      row.appendChild(cell);
      var dpDay = new DatePickerDay(cellButton, this, index, i, j);
      dpDay.init();
      this.days.push(dpDay);
      index++;
    }
  }

  this.updateGrid();
  this.setFocusDay();
};

DatePicker.prototype.updateGrid = function () {
  var i, flag;
  var fd = this.focusDay; //Checking next and prev dates

  var nextDate = null;

  if (this.nextField && this.nextField.value !== "") {
    var splittedNext = this.nextField.value.split("/");
    nextDate = prevDate = new Date(splittedNext[2] + "-" + splittedNext[1] + "-" + splittedNext[0]);
  }

  var prevDate = null;

  if (this.prevField && this.prevField.value !== "") {
    var splittedPrev = this.prevField.value.split("/");
    prevDate = new Date(splittedPrev[2] + "-" + splittedPrev[1] + "-" + splittedPrev[0]);
    prevDate.setHours(0);
  }

  this.MonthYearNode.innerHTML = this.monthLabels[fd.getMonth()] + " " + fd.getFullYear();
  var firstDayOfMonth = new Date(fd.getFullYear(), fd.getMonth(), 1);
  var daysInMonth = new Date(fd.getFullYear(), fd.getMonth() + 1, 0).getDate();
  var dayOfWeek = firstDayOfMonth.getDay() === 0 ? 6 : firstDayOfMonth.getDay() - 1;
  firstDayOfMonth.setDate(firstDayOfMonth.getDate() - dayOfWeek);
  var d = new Date(firstDayOfMonth);

  for (i = 0; i < this.days.length; i++) {
    flag = d.getMonth() !== fd.getMonth();
    this.days[i].updateDay(flag, d);

    if (d.getFullYear() === this.selectedDay.getFullYear() && d.getMonth() === this.selectedDay.getMonth() && d.getDate() === this.selectedDay.getDate()) {
      this.days[i].domNode.setAttribute("aria-selected", "true");
    }

    d.setDate(d.getDate() + 1);
    this.days[i].domNode.classList.remove("dateButton--ranged");
    this.days[i].domNode.classList.remove("dateButton--other-selected");
    var renderedDate = this.days[i].day; //Hightlight range

    if (this.selectedDay && this.selectedDay.getTime() !== -2208989361000 && this.selectedDay.getTime() !== -2208992400000) {
      if (nextDate && renderedDate.getTime() < nextDate.getTime() && renderedDate.getTime() > this.selectedDay.getTime()) {
        this.days[i].domNode.classList.add("dateButton--ranged");
      }

      if (prevDate && renderedDate.getTime() > prevDate.getTime() && renderedDate.getTime() < this.selectedDay.getTime()) {
        this.days[i].domNode.classList.add("dateButton--ranged");
      }
    } //Highlight other selected day


    if (prevDate && renderedDate.getFullYear() === prevDate.getFullYear() && renderedDate.getMonth() === prevDate.getMonth() && renderedDate.getDate() === prevDate.getDate()) {
      this.days[i].domNode.classList.add("dateButton--other-selected");
    }

    if (nextDate && renderedDate.getFullYear() === nextDate.getFullYear() && renderedDate.getMonth() === nextDate.getMonth() && renderedDate.getDate() === nextDate.getDate()) {
      this.days[i].domNode.classList.add("dateButton--other-selected");
    } //Disable selected after and before


    if (nextDate && renderedDate.getTime() > nextDate.getTime()) {
      this.days[i].domNode.classList.add("disabled");
    }

    if (prevDate && renderedDate.getTime() < prevDate.getTime()) {
      this.days[i].domNode.classList.add("disabled");
    }
  }

  if (dayOfWeek + daysInMonth < 36) {
    this.hideLastRow();
  } else {
    this.showLastRow();
  }
};

DatePicker.prototype.hideLastRow = function () {
  this.lastRowNode.style.visibility = "hidden";
};

DatePicker.prototype.showLastRow = function () {
  this.lastRowNode.style.visibility = "visible";
};

DatePicker.prototype.setFocusDay = function (flag) {
  if (typeof flag !== "boolean") {
    flag = true;
  }

  var fd = this.focusDay;

  function checkDay(d) {
    d.domNode.setAttribute("tabindex", "-1");

    if (d.day.getDate() == fd.getDate() && d.day.getMonth() == fd.getMonth() && d.day.getFullYear() == fd.getFullYear()) {
      d.domNode.setAttribute("tabindex", "0");

      if (flag) {
        d.domNode.focus();
      }
    }
  }

  this.days.forEach(checkDay.bind(this));
};

DatePicker.prototype.updateDay = function (day) {
  var d = this.focusDay;
  this.focusDay = day;

  if (d.getMonth() !== day.getMonth() || d.getFullYear() !== day.getFullYear()) {
    this.updateGrid();
    this.setFocusDay();
  }
};

DatePicker.prototype.getDaysInLastMonth = function () {
  var fd = this.focusDay;
  var lastDayOfMonth = new Date(fd.getFullYear(), fd.getMonth(), 0);
  return lastDayOfMonth.getDate();
};

DatePicker.prototype.getDaysInMonth = function () {
  var fd = this.focusDay;
  var lastDayOfMonth = new Date(fd.getFullYear(), fd.getMonth() + 1, 0);
  return lastDayOfMonth.getDate();
};

DatePicker.prototype.show = function () {
  this.dialogNode.style.display = "block";
  this.dialogNode.style.zIndex = 2;
  this.getDateInput();
  this.updateGrid();
  this.setFocusDay();
};

DatePicker.prototype.isOpen = function () {
  return $(this.dialogNode).is(":visible");
};

DatePicker.prototype.hide = function () {
  this.setMessage("");
  this.dialogNode.style.display = "none";
  this.hasFocusFlag = false;
  this.dateInput.setFocus();
  this.buttonNode.dispatchEvent(new Event("hideDatepicker"));  
};

DatePicker.prototype.handleBackgroundMouseDown = function (event) {
  if (!this.buttonNode.contains(event.target) && !this.dialogNode.contains(event.target)) {
    this.isMouseDownOnBackground = true;

    if (this.isOpen()) {
      this.hide();
      event.stopPropagation();
      event.preventDefault();
    }
  }
};

DatePicker.prototype.handleBackgroundMouseUp = function () {
  this.isMouseDownOnBackground = false;
};

DatePicker.prototype.handleOkButton = function (event) {
  var flag = false;

  switch (event.type) {
    case "keydown":
      switch (event.keyCode) {
        case this.keyCode.ENTER:
        case this.keyCode.SPACE:
          this.setTextboxDate();
          this.hide();
          flag = true;
          break;

        case this.keyCode.TAB:
          if (!event.shiftKey) {
            this.prevYearNode.focus();
            flag = true;
          }

          break;

        case this.keyCode.ESC:
          this.hide();
          flag = true;
          break;

        default:
          break;
      }

      break;

    case "click":
      this.setTextboxDate();
      this.hide();
      flag = true;
      break;

    default:
      break;
  }

  if (flag) {
    event.stopPropagation();
    event.preventDefault();
  }
};

DatePicker.prototype.handleCancelButton = function (event) {
  var flag = false;

  switch (event.type) {
    case "keydown":
      switch (event.keyCode) {
        case this.keyCode.ENTER:
        case this.keyCode.SPACE:
          this.hide();
          flag = true;
          break;

        case this.keyCode.ESC:
          this.hide();
          flag = true;
          break;

        default:
          break;
      }

      break;

    case "click":
      this.hide();
      flag = true;
      break;

    default:
      break;
  }

  if (flag) {
    event.stopPropagation();
    event.preventDefault();
  }
};

DatePicker.prototype.handleNextYearButton = function (event) {
  var flag = false;

  switch (event.type) {
    case "keydown":
      switch (event.keyCode) {
        case this.keyCode.ESC:
          this.hide();
          flag = true;
          break;

        case this.keyCode.ENTER:
        case this.keyCode.SPACE:
          this.moveToNextYear();
          this.setFocusDay(false);
          flag = true;
          break;
      }

      break;

    case "click":
      this.moveToNextYear();
      this.setFocusDay(false);
      break;

    default:
      break;
  }

  if (flag) {
    event.stopPropagation();
    event.preventDefault();
  }
};

DatePicker.prototype.handlePreviousYearButton = function (event) {
  var flag = false;

  switch (event.type) {
    case "keydown":
      switch (event.keyCode) {
        case this.keyCode.ENTER:
        case this.keyCode.SPACE:
          this.moveToPreviousYear();
          this.setFocusDay(false);
          flag = true;
          break;

        case this.keyCode.TAB:
          if (event.shiftKey) {
            this.okButtonNode.focus();
            flag = true;
          }

          break;

        case this.keyCode.ESC:
          this.hide();
          flag = true;
          break;

        default:
          break;
      }

      break;

    case "click":
      this.moveToPreviousYear();
      this.setFocusDay(false);
      break;

    default:
      break;
  }

  if (flag) {
    event.stopPropagation();
    event.preventDefault();
  }
};

DatePicker.prototype.handleNextMonthButton = function (event) {
  var flag = false;

  switch (event.type) {
    case "keydown":
      switch (event.keyCode) {
        case this.keyCode.ESC:
          this.hide();
          flag = true;
          break;

        case this.keyCode.ENTER:
        case this.keyCode.SPACE:
          this.moveToNextMonth();
          this.setFocusDay(false);
          flag = true;
          break;
      }

      break;

    case "click":
      this.moveToNextMonth();
      this.setFocusDay(false);
      break;

    default:
      break;
  }

  if (flag) {
    event.stopPropagation();
    event.preventDefault();
  }
};

DatePicker.prototype.handlePreviousMonthButton = function (event) {
  var flag = false;

  switch (event.type) {
    case "keydown":
      switch (event.keyCode) {
        case this.keyCode.ESC:
          this.hide();
          flag = true;
          break;

        case this.keyCode.ENTER:
        case this.keyCode.SPACE:
          this.moveToPreviousMonth();
          this.setFocusDay(false);
          flag = true;
          break;
      }

      break;

    case "click":
      this.moveToPreviousMonth();
      this.setFocusDay(false);
      flag = true;
      break;

    default:
      break;
  }

  if (flag) {
    event.stopPropagation();
    event.preventDefault();
  }
};

DatePicker.prototype.moveToNextYear = function () {
  this.focusDay.setFullYear(this.focusDay.getFullYear() + 1);
  this.updateGrid();
};

DatePicker.prototype.moveToPreviousYear = function () {
  this.focusDay.setFullYear(this.focusDay.getFullYear() - 1);
  this.updateGrid();
};

DatePicker.prototype.moveToNextMonth = function () {
  this.focusDay.setMonth(this.focusDay.getMonth() + 1);
  this.updateGrid();
};

DatePicker.prototype.moveToPreviousMonth = function () {
  this.focusDay.setMonth(this.focusDay.getMonth() - 1);
  this.updateGrid();
};

DatePicker.prototype.moveFocusToDay = function (day) {
  var d = this.focusDay;
  this.focusDay = day;

  if (d.getMonth() !== this.focusDay.getMonth() || d.getYear() !== this.focusDay.getYear()) {
    this.updateGrid();
  }

  this.setFocusDay();
};

DatePicker.prototype.moveFocusToNextDay = function () {
  var d = new Date(this.focusDay);
  d.setDate(d.getDate() + 1);
  this.moveFocusToDay(d);
};

DatePicker.prototype.moveFocusToNextWeek = function () {
  var d = new Date(this.focusDay);
  d.setDate(d.getDate() + 7);
  this.moveFocusToDay(d);
};

DatePicker.prototype.moveFocusToPreviousDay = function () {
  var d = new Date(this.focusDay);
  d.setDate(d.getDate() - 1);
  this.moveFocusToDay(d);
};

DatePicker.prototype.moveFocusToPreviousWeek = function () {
  var d = new Date(this.focusDay);
  d.setDate(d.getDate() - 7);
  this.moveFocusToDay(d);
};

DatePicker.prototype.moveFocusToFirstDayOfWeek = function () {
  var d = new Date(this.focusDay);
  d.setDate(d.getDate() - d.getDay());
  this.moveFocusToDay(d);
};

DatePicker.prototype.moveFocusToLastDayOfWeek = function () {
  var d = new Date(this.focusDay);
  d.setDate(d.getDate() + (6 - d.getDay()));
  this.moveFocusToDay(d);
};

DatePicker.prototype.setTextboxDate = function (day) {
  if (day) {
    this.dateInput.setDate(day);
  } else {
    this.dateInput.setDate(this.focusDay);
  }
};

DatePicker.prototype.getDateInput = function () {
  var parts = this.dateInput.getDate().split("/");

  if (parts.length === 3 && Number.isInteger(parseInt(parts[0])) && Number.isInteger(parseInt(parts[1])) && Number.isInteger(parseInt(parts[2]))) {
    this.focusDay = new Date(parseInt(parts[2]), parseInt(parts[1]) - 1, parseInt(parts[0]));
    this.selectedDay = new Date(this.focusDay);
  } else {
    // If not a valid date (MM/DD/YY) initialize with todays date
    this.focusDay = new Date();
    this.selectedDay = new Date(0, 0, 1);
  }
};

DatePicker.prototype.getDateForButtonLabel = function (year, month, day) {
  if (typeof year !== "number" || typeof month !== "number" || typeof day !== "number") {
    this.selectedDay = this.focusDay;
  } else {
    this.selectedDay = new Date(year, month, day);
  }

  var label = this.dayLabels[this.selectedDay.getDay()];
  label += " " + this.monthLabels[this.selectedDay.getMonth()];
  label += " " + this.selectedDay.getDate();
  label += ", " + this.selectedDay.getFullYear();
  return label;
};

DatePicker.prototype.setMessage = function (str) {
  function setMessageDelayed() {
    this.messageNode.textContent = str;
  }

  if (str !== this.lastMessage) {
    setTimeout(setMessageDelayed.bind(this), 200);
    this.lastMessage = str;
  }
};

window.initDatePicker = function () {
  var datePickers = document.querySelectorAll(".datepicker");
  datePickers.forEach(function (dp) {
    if (!dp.classList.contains("mounted")) {
      dp.classList.add("mounted");
      var inputNode = dp.querySelector("input");
      var buttonNode = dp.querySelector("button");
      var dialogNode = dp.querySelector("[role=dialog]");
      var isMonthPicker = dp.getAttribute("data-monthpicker") === "true";
      var monthPickerMode = dp.getAttribute("data-monthpickermode");
      var nextField = dp.getAttribute("data-nextfield");
      var prevField = dp.getAttribute("data-prevfield");
      var nextFieldElem = null;

      if (nextField) {
        nextFieldElem = dp.parentNode.querySelector("input[name=" + nextField + "]");
      }

      var prevFieldElem = null;

      if (prevField) {
        prevFieldElem = dp.parentNode.querySelector("input[name=" + prevField + "]");
      }

      var datePicker = new DatePicker(inputNode, buttonNode, dialogNode, isMonthPicker, monthPickerMode, nextFieldElem, prevFieldElem);
      datePicker.init(); //Validator

      var input = dp.querySelector("input");

      if (input) {
        input.addEventListener("keyup", function () {
          input.classList.remove("form-date-picker-input__form-input--error");
          input.removeAttribute("aria-invalid");
          var value = input.value;

          if (value !== "") {
            var pattern = "^([0-2][0-9]|(3)[0-1])(\\/)(((0)[0-9])|((1)[0-2]))(\\/)\\d{4}$";

            if (isMonthPicker) {
              pattern = "(((0)[0-9])|((1)[0-2]))(\\/)\\d{4}$";
            }

            if (!value.match(pattern)) {
              input.classList.add("form-date-picker-input__form-input--error");
              input.setAttribute("aria-invalid", "true");
            }
          }
        });
      }
    }
  });
  var monthPickerButtons = document.querySelectorAll(".date-picker-dialog__dialog-button--month-picker");
  monthPickerButtons.forEach(function (button) {
    button.addEventListener("click", function (e) {
      var calendarGrid = e.target.parentNode.parentNode;
      var field = calendarGrid.parentNode;
      var dayMode = field.getAttribute("data-monthpickermode");
      var calendarHeader = calendarGrid.querySelector(".header").querySelector("h2");
      var selection = monthYearStringToInputFormat(calendarHeader.innerText, dayMode);
      var input = calendarGrid.parentNode.querySelector("input");
      input.value = selection; //Closing grid

      calendarGrid.style.display = "none"; //focusing input button

      var button = calendarGrid.parentNode.querySelector("button");
      button.focus();
    });
  });
};

function formatTwoDigits(input) {
  if (input.length === 1 || input < 10) {
    return "0" + input;
  }

  return input;
}

function lastDayOfMonth(year, month) {
  return new Date(year, month + 1, 0).getDate();
}

function monthYearStringToInputFormat(string, dayMode) {
  var monthYear = string.split(" ");
  var monthConversion = {
    janvier: "01",
    février: "02",
    mars: "03",
    avril: "04",
    mai: "05",
    juin: "06",
    juillet: "07",
    août: "08",
    septembre: "09",
    octobre: "10",
    novembre: "11",
    décembre: "12"
  };

  if (dayMode === "first") {
    return "01/" + monthConversion[monthYear[0].toLowerCase()] + "/" + monthYear[1];
  }

  if (dayMode === "last") {
    return lastDayOfMonth(monthYear[1], monthConversion[monthYear[0].toLowerCase()]) + "/" + monthConversion[monthYear[0].toLowerCase()] + "/" + monthYear[1];
  }

  return monthConversion[monthYear[0].toLowerCase()] + "/" + monthYear[1];
}



document.body.addEventListener("click", function (e) {
  var button = $(e.target).closest(".loader-overlay__trigger").get(0);

  if (button) {
    var targetId = button.getAttribute("aria-controls");
    $("#" + targetId).fadeIn("slow");
  }
});
document.body.addEventListener("click", function (e) {
  var button = $(e.target).closest(".loader-overlay__close").get(0);

  if (button) {
    var targetId = button.getAttribute("aria-controls");
    $("#" + targetId).fadeOut("slow");
  }
});



document.body.addEventListener("click", function (e) {
  var button = $(e.target).closest(".js-form-input-action").get(0);

  if (button) {
    var inputContainer = $(button).closest(".form-input").get(0);
    var input = inputContainer.querySelector(".form-input__field");

    if (input.classList.contains("form-input__field--type-password")) {
      handlePasswordVisibility(inputContainer);
    }
  }
});

function handlePasswordVisibility(container) {
  var input = container.querySelector(".form-input__field");
  var icon = container.querySelector(".form-input__action-icon");
  var button = container.querySelector(".form-input__action");
  var isVisible = input.getAttribute("type") === "text";

  if (isVisible) {
    input.setAttribute("type", "password");
    icon.classList.remove("icon--eye");
    icon.classList.add("icon--stroked-eye");
    button.title = "Voir le mot de passe";
    button.querySelector(".sr-only").innerHTML = "Voir le mot de passe";
  } else {
    input.setAttribute("type", "text");
    icon.classList.remove("icon--stroked-eye");
    icon.classList.add("icon--eye");
    button.title = "Masquer le mot de passe";
    button.querySelector(".sr-only").innerHTML = "Masquer le mot de passe";
  }
}



window.initRichText = function () {
  window.tinyMCE.init({
    selector: ".form-rich-text-field__field",
    body_class: "form-rich-text-field__editor",
    // tinydrive_token_provider: "URL_TO_YOUR_TOKEN_PROVIDER",
    // tinydrive_dropbox_app_key: "YOUR_DROPBOX_APP_KEY",
    // tinydrive_google_drive_key: "YOUR_GOOGLE_DRIVE_KEY",
    // tinydrive_google_drive_client_id: "YOUR_GOOGLE_DRIVE_CLIENT_ID",
    plugins: "paste fullscreen table help",
    // menubar: "file edit view insert format table help",
    toolbar: "undo redo styleselect bold italic subscript superscript alignleft aligncenter alignright alignjustify outdent indent fullscreen",
    toolbar_mode: "wrap",
    height: 400,
    content_css: "css/main.css",
    importcss_append: true,
    // autosave_ask_before_unload: true,
    // autosave_interval: "30s",
    // autosave_prefix: "{path}{query}-{id}-",
    // autosave_restore_when_empty: false,
    // autosave_retention: "2m",
    // image_advtab: true,
    // image_caption: true,
    // spellchecker_dialog: true,
    a11y_advanced_options: true,
    language: "fr_FR"
  });
};



// ESM COMPAT FLAG






































// CONCATENATED MODULE: ./src/libs/main.js
//Polyfills first
 //JS for components

































 // inits

window.onload = function () {
  initWindowHelpers();
  initHeaderMenu();
  initBackToTop();
  initComplexSelects();
  initToolTip();
  initSimpleFilePicker();
  initSelectAutocomplete();
  initRichText();
  initProgressBar();
  initLetterSlider();
  initInterstitial();
  initSeeMore();
  initEllipsedContent();
  initDropdown();
  initDatePicker();
  initCustomTable();
  initActionTable();
  initEllipsedContent();
  initTabs();
  initProgressBar();
  initDataGrid();
  initStickyColumnTable();
};
// CONCATENATED MODULE: ./src/main.js











window.initEllipsedContent = function () {
  var MAX_CHAR = 95;
  var ellipseds = document.querySelectorAll(".ellipsed-content");
  ellipseds.forEach(function (ellipse) {
    if (!ellipse.classList.contains("mounted")) {
      ellipse.classList.add("mounted");
      var containerFullContent = ellipse.querySelector(".ellipsed-content__full-content");
      var containerEllipsedContent = ellipse.querySelector(".ellipsed-content__content");
      var fullContent = $(ellipse).find(".ellipsed-content__full-content").children().first().text();

      if (fullContent.length <= MAX_CHAR) {
        $(containerEllipsedContent).html($("<p></p>").addClass("ellipsed-content__paragraph").text(fullContent));
      } else {
        $(containerEllipsedContent).prepend($("<p></p>").addClass("ellipsed-content__paragraph").text(fullContent.substring(0, MAX_CHAR) + "...")); //Event listener

        var fullContentArrow = containerFullContent.querySelector(".ellipsed-content__full-content-button");
        var ellipsedContentArrow = containerEllipsedContent.querySelector(".ellipsed-content__content-button");

        if (fullContentArrow) {
          fullContentArrow.addEventListener("click", function () {
            containerFullContent.classList.remove("ellipsed-content__full-content--expanded");
            containerFullContent.setAttribute("hidden", "hidden");
            containerEllipsedContent.classList.remove("ellipsed-content__content--hidden");
            containerEllipsedContent.removeAttribute("hidden");
            containerFullContent.removeAttribute("tabindex");
            ellipsedContentArrow.focus({
              preventScroll: true
            });
          });
        }

        if (ellipsedContentArrow) {
          ellipsedContentArrow.addEventListener("click", function () {
            containerFullContent.classList.add("ellipsed-content__full-content--expanded");
            containerFullContent.removeAttribute("hidden");
            containerEllipsedContent.classList.add("ellipsed-content__content--hidden");
            containerEllipsedContent.setAttribute("hidden", "hidden");
            containerFullContent.setAttribute("tabindex", "-1");
            containerFullContent.focus();
          });
        }
      }
    }
  });
};




window.initDataGrid = function () {
  var datagrids = document.querySelectorAll(".file-tree-table__table");
  datagrids.forEach(function (datagrid) {
    if (!datagrid.classList.contains("mounted")) {
      datagrid.classList.add("mounted");
      new DataGrid(datagrid);
    }
  });
};

var DataGrid = function DataGrid(table) {
  this.table = table;
  this.initGridCellAttributes();
  this.initGridCellEvents();
};

DataGrid.prototype.getAllCells = function () {
  return this.table.querySelectorAll("td, th");
};

DataGrid.prototype.getActiveCell = function () {
  return this.table.querySelector("td[tabindex='0'], th[tabindex='0']");
};

DataGrid.prototype.getTableLines = function () {
  return this.table.querySelectorAll("tr");
};

DataGrid.prototype.getLineCells = function (line) {
  return line.querySelectorAll("th, td");
};

DataGrid.prototype.initGridCellAttributes = function () {
  this.getAllCells().forEach(function (cell, index) {
    if (index === 0) {
      cell.setAttribute("tabindex", "0");
    } else {
      cell.setAttribute("tabindex", "-1");
    }
  });
};

DataGrid.prototype.initGridCellEvents = function () {
  var _this = this;

  this.table.addEventListener("keydown", function (event) {
    switch (event.keyCode) {
      case KeyCodeHelper.UP:
        _this.navigateUp();

        break;

      case KeyCodeHelper.DOWN:
        _this.navigateDown();

        break;

      case KeyCodeHelper.LEFT:
        _this.navigateLeft();

        break;

      case KeyCodeHelper.RIGHT:
        _this.navigateRight();

        break;

      case KeyCodeHelper.TAB:
        if (event.target.parentNode.classList.contains("table-line__action-column-container")) {
          event.preventDefault();

          var elements = _this.getFocusableElements(_this.getActiveCell());

          var activeIndex = undefined;
          elements.forEach(function (elem, index) {
            if (elem === event.target) {
              activeIndex = index;
            }
          });

          if (event.shiftKey) {
            if (activeIndex === 0) {
              _this.navigateLeft();
            } else {
              if (elements[activeIndex - 1]) {
                elements[activeIndex - 1].focus();
              }
            }
          } else {
            if (elements[activeIndex + 1]) {
              elements[activeIndex + 1].focus();
            }
          }
        }

        break;
    }
  });
};

DataGrid.prototype.navigateUp = function () {
  var activeCell = this.getActiveCell();
  var cellCoord = this.getCellCoordinates(activeCell);
  var newCell = this.getCellAtCoord(cellCoord.line - 1, cellCoord.column);

  if (newCell) {
    this.focusCell(newCell);
  }
};

DataGrid.prototype.navigateDown = function () {
  var activeCell = this.getActiveCell();
  var cellCoord = this.getCellCoordinates(activeCell);
  var newCell = this.getCellAtCoord(cellCoord.line + 1, cellCoord.column);

  if (newCell) {
    this.focusCell(newCell);
  }
};

DataGrid.prototype.navigateLeft = function () {
  var activeCell = this.getActiveCell();
  var cellCoord = this.getCellCoordinates(activeCell);
  var newCell = this.getCellAtCoord(cellCoord.line, cellCoord.column - 1);

  if (newCell) {
    this.focusCell(newCell);
  }
};

DataGrid.prototype.navigateRight = function () {
  var activeCell = this.getActiveCell();
  var cellCoord = this.getCellCoordinates(activeCell);
  var newCell = this.getCellAtCoord(cellCoord.line, cellCoord.column + 1);

  if (newCell) {
    this.focusCell(newCell);
  }
};

DataGrid.prototype.getCellAtCoord = function (line, column) {
  var _this = this;

  var newCell = undefined;
  this.getTableLines().forEach(function (tableLine, lineIndex) {
    if (lineIndex === line && !newCell) {
      _this.getLineCells(tableLine).forEach(function (tableColumn, columnIndex) {
        if (columnIndex === column && !newCell) {
          newCell = tableColumn;
        }
      });
    }
  });
  return newCell;
};

DataGrid.prototype.getCellCoordinates = function (cell) {
  return {
    line: this.getCellLine(cell),
    column: this.getCellColumn(cell)
  };
};

DataGrid.prototype.getCellLine = function (cell) {
  var activeLine = undefined;
  var line = cell.parentNode;
  this.getTableLines().forEach(function (tableLine, index) {
    if (tableLine === line && !activeLine) {
      activeLine = index;
    }
  });
  return activeLine;
};

DataGrid.prototype.getCellColumn = function (cell) {
  var activeColumn = undefined;
  var line = cell.parentNode;
  line.querySelectorAll("th, td").forEach(function (cellLine, index) {
    if (cell === cellLine && !activeColumn) {
      activeColumn = index;
    }
  });
  return activeColumn;
};

DataGrid.prototype.focusCell = function (cell) {
  var activeCell = this.getActiveCell();
  activeCell.setAttribute("tabindex", "-1");
  cell.setAttribute("tabindex", 0);

  if (this.findFocusableElement(cell)) {
    this.findFocusableElement(cell).focus();
  } else {
    cell.focus();
  }
};

DataGrid.prototype.findFocusableElement = function (cell) {
  var focusable = cell.querySelectorAll('button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])');

  if (focusable.length > 0) {
    return focusable[0];
  }

  return undefined;
};

DataGrid.prototype.getFocusableElements = function (cell) {
  return cell.querySelectorAll('button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])');
};

var KeyCodeHelper = {
  UP: 38,
  DOWN: 40,
  LEFT: 37,
  RIGHT: 39,
  TAB: 9
};



window.initProgressBar = function () {
  var bars = document.getElementsByClassName("js-progressbar__bar");
  Array.prototype.forEach.call(bars, function (el) {
    var value = el.getAttribute("aria-valuenow");
    el.style.width = value + "%";
  });
};




window.initToolTip = function () {
  var tooltips = document.querySelectorAll("[data-tippy-content]");
  tooltips.forEach(function (tooltip) {
    if (!tooltip.classList.contains("tooltipMounted")) {
      tooltip.classList.add("tooltipMounted"); // Tooltip wrapper

      var wrapper; // Tippy instance
      var placement = "top";
      if (tooltip.getAttribute("data-placement") != null) {
    	  placement = tooltip.getAttribute("data-placement");
      }

      var tip = tippy(tooltip, {
    	placement: placement,
        aria: {
          content: null,
          expanded: false
        },
        maxWidth: "none",
        appendTo: function appendTo(tooltip) {
          var el = document.createElement("div");
          el.setAttribute("class", "tooltip-wrapper");
          insertAfter(tooltip, el);
          wrapper = el;
          return el;
        },
        onHidden: function onHidden() {
          if (wrapper) {
            wrapper.parentNode.removeChild(wrapper);
          }
        }
      });
      bindTipCloseOnKey(tooltip, tip);
      bindTipCloseOnBlur(tooltip, tip);
    }
  });
  var customButton = document.querySelector("#myButton");

  if (customButton && !customButton.classList.contains("tooltipMounted")) {
    customButton.classList.add("tooltipMounted");
    var secondTip = tippy("#myButton", {
      content: "This tooltip is defined in javascript!",
      appendTo: customButton.parentNode
    });
    bindTipCloseOnKey(document.querySelector("#myButton"), secondTip[0]);
    bindTipCloseOnBlur(document.querySelector("#myButton"), secondTip[0]);
  }
};

window.reloadTooltip = function () {
  var tooltips = document.querySelectorAll("[data-tippy-content]");
  tooltips.forEach(function (tooltip) {
    var instance = tooltip._tippy;
    instance.setContent(tooltip.getAttribute("data-tippy-content"));
  });
};

function bindTipCloseOnKey(element, tip) {
  element.addEventListener("keydown", function (event) {
    if (event.key === "Escape" || event.key === "Esc") {
      tip.hide();
    }
  });
}

function bindTipCloseOnBlur(element, tip) {
  element.addEventListener("blur", function () {
    tip.hide();
  });
}
/**
 * Instert element right after referenceNode;
 * @param referenceNode
 * @param newNode
 */


function insertAfter(referenceNode, newNode) {
  referenceNode.parentNode.insertBefore(newNode, referenceNode.nextSibling);
}










var TABLE_ACTION_CLASS = "action-table";
var TABLE_ACTION_ITEM_SELECTOR = ".table-action .table-action__item.base-btn";
var BASE_DROPDOWN_BUTTON_SELECTOR = ".table-action .base-drop-down__button";
var TABLE_ACTION_LINE_CHECKBOX_SELECTOR = ".js-custom-table-line-check";
var TABLE_ACTION_HEADER_CHECKBOX_SELECTOR = ".js-custom-table-header-check";
var DISABLED_TOOLTIP_TEXT = "Sélectionner un élément pour activer les actions";

window.initActionTable = function () {
  var tableActions = document.querySelectorAll("." + TABLE_ACTION_CLASS);

  if (!tableActions.length) {
    return;
  }

  var actionBars = null;
  var actionBarButtons = null;
  var actionBarDropDownButtons = null;
  var lineCheckboxes = null;
  var globalTableActionCheckbox = null;
  var _iteratorNormalCompletion = true;
  var _didIteratorError = false;
  var _iteratorError = undefined;

  try {
    for (var _iterator = tableActions[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
      var tableAction = _step.value;

      if (!tableAction.classList.contains("mounted")) {
        tableAction.classList.add("mounted");
        actionBarButtons = tableAction.querySelectorAll(TABLE_ACTION_ITEM_SELECTOR);
        actionBarDropDownButtons = tableAction.querySelectorAll(BASE_DROPDOWN_BUTTON_SELECTOR);
        lineCheckboxes = tableAction.querySelectorAll(TABLE_ACTION_LINE_CHECKBOX_SELECTOR);
        globalTableActionCheckbox = tableAction.querySelector(TABLE_ACTION_HEADER_CHECKBOX_SELECTOR);
        actionBars = tableAction.querySelectorAll(".table-action__action-container");
        ActionTableManager.initEvents(lineCheckboxes, globalTableActionCheckbox, actionBarButtons, actionBarDropDownButtons, actionBars);
      }
    }
  } catch (err) {
    _didIteratorError = true;
    _iteratorError = err;
  } finally {
    try {
      if (!_iteratorNormalCompletion && _iterator.return != null) {
        _iterator.return();
      }
    } finally {
      if (_didIteratorError) {
        throw _iteratorError;
      }
    }
  }
};

var ActionTableManager = {
  initEvents: function initEvents(checkboxes, mainCheckbox, buttons, dropdowns, actionBars) {
    if (!checkboxes.length) {
      return;
    }

    var _iteratorNormalCompletion2 = true;
    var _didIteratorError2 = false;
    var _iteratorError2 = undefined;

    try {
      for (var _iterator2 = checkboxes[Symbol.iterator](), _step2; !(_iteratorNormalCompletion2 = (_step2 = _iterator2.next()).done); _iteratorNormalCompletion2 = true) {
        var checkbox = _step2.value;
        checkbox.addEventListener("change", function (event) {
          var hasCheckedElement = event.target.checked;
          var _iteratorNormalCompletion6 = true;
          var _didIteratorError6 = false;
          var _iteratorError6 = undefined;

          try {
            for (var _iterator6 = checkboxes[Symbol.iterator](), _step6; !(_iteratorNormalCompletion6 = (_step6 = _iterator6.next()).done); _iteratorNormalCompletion6 = true) {
              var checkbox = _step6.value;

              if (checkbox.checked) {
                hasCheckedElement = true;
              }
            }
          } catch (err) {
            _didIteratorError6 = true;
            _iteratorError6 = err;
          } finally {
            try {
              if (!_iteratorNormalCompletion6 && _iterator6.return != null) {
                _iterator6.return();
              }
            } finally {
              if (_didIteratorError6) {
                throw _iteratorError6;
              }
            }
          }

          var _iteratorNormalCompletion7 = true;
          var _didIteratorError7 = false;
          var _iteratorError7 = undefined;

          try {
            for (var _iterator7 = buttons[Symbol.iterator](), _step7; !(_iteratorNormalCompletion7 = (_step7 = _iterator7.next()).done); _iteratorNormalCompletion7 = true) {
              var button = _step7.value;
              button.disabled = hasCheckedElement ? false : "disabled";
              var tippyInstance = button.parentNode._tippy;

              if (!hasCheckedElement) {
                tippyInstance.setContent(DISABLED_TOOLTIP_TEXT);
              } else {
                var textContainer = button.querySelector(".sr-only");
                var content = DISABLED_TOOLTIP_TEXT;

                if (textContainer) {
                  content = textContainer.textContent;
                }

                tippyInstance.setContent(content);
              }
            }
          } catch (err) {
            _didIteratorError7 = true;
            _iteratorError7 = err;
          } finally {
            try {
              if (!_iteratorNormalCompletion7 && _iterator7.return != null) {
                _iterator7.return();
              }
            } finally {
              if (_didIteratorError7) {
                throw _iteratorError7;
              }
            }
          }

          var _iteratorNormalCompletion8 = true;
          var _didIteratorError8 = false;
          var _iteratorError8 = undefined;

          try {
            for (var _iterator8 = dropdowns[Symbol.iterator](), _step8; !(_iteratorNormalCompletion8 = (_step8 = _iterator8.next()).done); _iteratorNormalCompletion8 = true) {
              var dropdown = _step8.value;
              dropdown.disabled = hasCheckedElement ? false : "disabled";
              var tippyInstanceDrop = dropdown.parentNode.parentNode._tippy;

              if (!hasCheckedElement) {
                tippyInstanceDrop.setContent(DISABLED_TOOLTIP_TEXT);
              } else {
                var textContainerDrop = dropdown.querySelector(".sr-only");
                var contentDrop = DISABLED_TOOLTIP_TEXT;

                if (textContainerDrop) {
                  contentDrop = textContainerDrop.textContent;
                }

                tippyInstanceDrop.setContent(contentDrop);
              }
            }
          } catch (err) {
            _didIteratorError8 = true;
            _iteratorError8 = err;
          } finally {
            try {
              if (!_iteratorNormalCompletion8 && _iterator8.return != null) {
                _iterator8.return();
              }
            } finally {
              if (_didIteratorError8) {
                throw _iteratorError8;
              }
            }
          }

          actionBars.forEach(function (actionBar) {
            if (actionBar.classList.contains("table-action__action-container--disabled")) {
              actionBar.classList.remove("table-action__action-container--disabled");
            }

            if (!hasCheckedElement) {
              actionBar.classList.add("table-action__action-container--disabled");
            }
          });
        });
      }
    } catch (err) {
      _didIteratorError2 = true;
      _iteratorError2 = err;
    } finally {
      try {
        if (!_iteratorNormalCompletion2 && _iterator2.return != null) {
          _iterator2.return();
        }
      } finally {
        if (_didIteratorError2) {
          throw _iteratorError2;
        }
      }
    }

    mainCheckbox.addEventListener("change", function (event) {
      var _iteratorNormalCompletion3 = true;
      var _didIteratorError3 = false;
      var _iteratorError3 = undefined;

      try {
        for (var _iterator3 = checkboxes[Symbol.iterator](), _step3; !(_iteratorNormalCompletion3 = (_step3 = _iterator3.next()).done); _iteratorNormalCompletion3 = true) {
          var checkbox = _step3.value;
          checkbox.checked = event.target.checked;
        }
      } catch (err) {
        _didIteratorError3 = true;
        _iteratorError3 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion3 && _iterator3.return != null) {
            _iterator3.return();
          }
        } finally {
          if (_didIteratorError3) {
            throw _iteratorError3;
          }
        }
      }

      var _iteratorNormalCompletion4 = true;
      var _didIteratorError4 = false;
      var _iteratorError4 = undefined;

      try {
        for (var _iterator4 = buttons[Symbol.iterator](), _step4; !(_iteratorNormalCompletion4 = (_step4 = _iterator4.next()).done); _iteratorNormalCompletion4 = true) {
          var button = _step4.value;
          button.disabled = event.target.checked ? false : "disabled";
          var tippyInstance = button.parentNode._tippy;

          if (!event.target.checked) {
            tippyInstance.setContent(DISABLED_TOOLTIP_TEXT);
          } else {
            var textContainer = button.querySelector(".sr-only");
            var content = DISABLED_TOOLTIP_TEXT;

            if (textContainer) {
              content = textContainer.textContent;
            }

            tippyInstance.setContent(content);
          }
        }
      } catch (err) {
        _didIteratorError4 = true;
        _iteratorError4 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion4 && _iterator4.return != null) {
            _iterator4.return();
          }
        } finally {
          if (_didIteratorError4) {
            throw _iteratorError4;
          }
        }
      }

      var _iteratorNormalCompletion5 = true;
      var _didIteratorError5 = false;
      var _iteratorError5 = undefined;

      try {
        for (var _iterator5 = dropdowns[Symbol.iterator](), _step5; !(_iteratorNormalCompletion5 = (_step5 = _iterator5.next()).done); _iteratorNormalCompletion5 = true) {
          var dropdown = _step5.value;
          dropdown.disabled = event.target.checked ? false : "disabled";
          var tippyInstanceDrop = dropdown.parentNode.parentNode._tippy;

          if (!event.target.checked) {
            tippyInstanceDrop.setContent(DISABLED_TOOLTIP_TEXT);
          } else {
            var textContainerDrop = dropdown.querySelector(".sr-only");
            var contentDrop = DISABLED_TOOLTIP_TEXT;

            if (textContainerDrop) {
              contentDrop = textContainerDrop.textContent;
            }

            tippyInstanceDrop.setContent(contentDrop);
          }
        }
      } catch (err) {
        _didIteratorError5 = true;
        _iteratorError5 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion5 && _iterator5.return != null) {
            _iterator5.return();
          }
        } finally {
          if (_didIteratorError5) {
            throw _iteratorError5;
          }
        }
      }

      actionBars.forEach(function (actionBar) {
        if (actionBar.classList.contains("table-action__action-container--disabled")) {
          actionBar.classList.remove("table-action__action-container--disabled");
        }

        if (!event.target.checked) {
          actionBar.classList.add("table-action__action-container--disabled");
        }
      });
    });
  }
};




window.initStickyColumnTable = function () {
  var STICKY_COLS_COUNT = 3;
  var sctList = document.querySelectorAll(".js-sticky-column-table");
  sctList.forEach(function (sct) {
    if (!sct.classList.contains("mounted")) {
      sct.classList.add("mounted");
      new StickyColumnTable(sct, STICKY_COLS_COUNT);
    }
  });
};

var StickyColumnTable = function StickyColumnTable(sct, stickyColsCount) {
  this.sct = sct;
  this.table = this.sct.querySelector("table");
  this.scroller = this.sct.querySelector(".table-scroller");
  this.stickyColsCount = stickyColsCount || 1;
  this.stickyCells = this.table.querySelectorAll("th:nth-child(-n+" + this.stickyColsCount + "), td:nth-child(-n+" + this.stickyColsCount + ")");
  this.initTable();
};
/**
 * Init
 */


StickyColumnTable.prototype.initTable = function () {
  var that = this; // Scop.
  // Add class to tbody sticky cells.

  var cells = this.stickyCells;

  for (var i = 0; i < cells.length; i++) {
    var cell = cells[i];
    cell.classList.add("sticky-col");
  } // Add shadow


  var shadowedCells = this.table.querySelectorAll("td:nth-child(" + this.stickyColsCount + ")");

  for (var j = 0; j < shadowedCells.length; j++) {
    var sCell = shadowedCells[j];
    sCell.classList.add("table-line__cell--shadow");
  } // Listen "scroll" event.


  this.scroller.addEventListener("scroll", function (e) {
    var scrollLeft = e.target.scrollLeft || 0;
    that.updateSkickyCells(scrollLeft, that.stickyCells);
  });
};
/**
 * Update sicky cells
 * @param offset
 * @param cells
 */


StickyColumnTable.prototype.updateSkickyCells = function (offset, cells) {
  for (var i = 0; i < cells.length; i++) {
    var cell = cells[i];
    cell.style.transform = "translateX(" + offset + "px)";
  }
};



document.body.addEventListener("click", function (e) {
  var button = $(e.target).closest(".js-alert-action").get(0);

  if (button) {
    var toastContainer = $(button).closest(".alert").get(0);
    fadeOut(toastContainer);
    setTimeout(function () {
      toastContainer.remove();
    }, 500);
  }
});

function fadeOut(el) {
  el.style.opacity = 1;

  (function fade() {
    if ((el.style.opacity -= 0.1) < 0) {
      el.style.display = "none";
    } else {
      requestAnimationFrame(fade);
    }
  })();
}




var toggleComponentWithAutoHeight = ["tree-navigation__list", "accordion__content", "sidebar__content"];
document.body.addEventListener("click", function (e) {
  var button = $(e.target).closest(".js-toggle").get(0);
  var zeroG = $(e.target).closest(".js-toggle-zerog").get(0);

  if (button) {
    e.preventDefault();
    var targetId = button.getAttribute("aria-controls");
    toggleDOMElement(targetId, button);
  } else if (zeroG) {
    var realButton = document.getElementById(zeroG.dataset.simulate);
    var realTargetId = realButton.getAttribute("aria-controls");
    toggleDOMElement(realTargetId, realButton);
  }
});

function toggleDOMElement(targetId, button) {
  //Getting base elements
  var BASE_SELECTOR_TOGGLE = button.getAttribute("data-toggle");
  var target = document.getElementById(targetId);
  var targetStatus = target.getAttribute("aria-hidden");
  var iconOpened = button.getAttribute("data-icon-opened");
  var iconClosed = button.getAttribute("data-icon-closed");
  var icon = button.querySelector(".icon"); //If it's hidden

  if (targetStatus === "true") {
    //We update the button
    button.setAttribute("aria-expanded", "true");

    if (icon) {
      icon.classList.remove("icon--" + iconClosed);
      icon.classList.add("icon--" + iconOpened);
    }

    if (!toggleComponentWithAutoHeight.includes(BASE_SELECTOR_TOGGLE)) {
      if (BASE_SELECTOR_TOGGLE !== "sidebar") {
        var totalHeight = 24;
        $(target).children().each(function () {
          totalHeight += $(this).outerHeight(true);
        });
        target.style.height = totalHeight + "px";
      }
    } //We update the target


    target.setAttribute("aria-hidden", "false");
    target.classList.add(BASE_SELECTOR_TOGGLE + "--is-opened");
    target.classList.remove(BASE_SELECTOR_TOGGLE + "--is-closed");

    if (target.classList.contains("sidebar__content")) {
      var focusable = target.parentNode.querySelectorAll('button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])');

      if (focusable[0]) {
        focusable[0].focus();
      }
    }
  } else {
    //We update the button
    target.setAttribute("aria-hidden", "true");
    button.setAttribute("aria-expanded", "false");

    if (icon) {
      icon.classList.remove("icon--" + iconOpened);
      icon.classList.add("icon--" + iconClosed);
    } //We update the target


    target.setAttribute("aria-hidden", "true");
    target.classList.remove(BASE_SELECTOR_TOGGLE + "--is-opened");
    target.classList.add(BASE_SELECTOR_TOGGLE + "--is-closed");
  }
}



function copyText(button) {
  var targetId = $(button).attr("data-copy");
  var target = document.getElementById(targetId);
  var range, selection;
  button.addEventListener("blur", function () {
    resetTippyContent(button);
  });

  if (window.getSelection) {
    selection = window.getSelection();
    range = document.createRange();
    range.selectNodeContents(target);
    selection.removeAllRanges();
    selection.addRange(range);
  } else if (document.body.createTextRange) {
    range = document.body.createTextRange();
    range.moveToElementText(target);
    range.select();
  }

  var succeed = document.execCommand("copy"),
      message;

  if (succeed) {
    message = '"' + range + '" copié dans le presse-papier.';
  } else {
    message = "Une erreur est survenue. Veuillez réessayer.";
  }

  var liveArea = button.parentNode.querySelector(".link-copy__live");

  if (liveArea) {
    if (succeed) {
      liveArea.textContent = "Le numéro " + range + " a été copié dans le presse-papier";
    } else {
      liveArea.textContent = "Une erreur est survenue lors de la copie du numéro. Veuillez ré-essayer";
    }

    setTimeout(function () {
      liveArea.textContent = "";
    }, 3000);
  }

  var tippyInstance = button._tippy;
  tippyInstance.setContent(message);
  document.getSelection().removeAllRanges();
}

document.body.addEventListener("click", function (e) {
  var button = $(e.target).closest(".js-copy").get(0);

  if (button) {
    copyText(button);
  }
});

function copySingleText(button) {
  button.addEventListener("blur", function () {
    resetTippyContent(button);
  });
  var content = $(button).attr("data-copy-content");
  var range, selection;
  var tmpDiv = document.createElement("div");
  tmpDiv.setAttribute("id", "tmp-copy");
  document.body.appendChild(tmpDiv);
  var target = document.getElementById("tmp-copy");

  if ($(target).text() === "") {
    $(target).text(content);
  } else {
    $(target).text($(target).text() + content);
  }

  if (window.getSelection) {
    selection = window.getSelection();
    range = document.createRange();
    range.selectNodeContents(target);
    selection.removeAllRanges();
    selection.addRange(range);
  } else if (document.body.createTextRange) {
    range = document.body.createTextRange();
    range.moveToElementText(target);
    range.select();
  }

  var succeed = document.execCommand("copy"),
      message;

  if (succeed) {
    message = '"' + range + '" copié dans le presse-papier.';
  } else {
    message = "Une erreur est survenue. Veuillez réessayer.";
  }

  document.body.removeChild(tmpDiv);
  var liveArea = button.parentNode.querySelector(".btn-copy__live");

  if (liveArea) {
    liveArea.textContent = message;
    setTimeout(function () {
      liveArea.textContent = "";
    }, 3000);
  }

  var tippyInstance = button._tippy;
  tippyInstance.setProps({
    trigger: "mouseenter click focus",
    content: message
  }); //tippyInstance.setContent(message);

  document.getSelection().removeAllRanges();
}

document.body.addEventListener("click", function (e) {
  var button = $(e.target).closest(".js-copy-single").get(0);

  if (button) {
    copySingleText(button);
  }
});

function resetTippyContent(elem) {
  var tippyInstance = elem._tippy,
      message = elem.getAttribute("data-tippy-default-content");
  tippyInstance.setContent(message);
}




window.initAutocomplete = function () {
  // Fake async data.
  // eslint-disable-next-line no-unused-vars
  var countries = [{
    code: "AF",
    code3: "AFG",
    name: "Afghanistan",
    number: "004",
    value: "AFG",
    label: "Afghanistan",
    cleanedLabel: "afghanistan"
  }, {
    code: "AL",
    code3: "ALB",
    name: "Albania",
    number: "008",
    value: "ALB",
    label: "Albania",
    cleanedLabel: "albania"
  }, {
    code: "DZ",
    code3: "DZA",
    name: "Algeria",
    number: "012",
    value: "DZA",
    label: "Algeria",
    cleanedLabel: "algeria"
  }, {
    code: "AS",
    code3: "ASM",
    name: "American Samoa",
    number: "016",
    value: "ASM",
    label: "American Samoa",
    cleanedLabel: "american samoa"
  }, {
    code: "AD",
    code3: "AND",
    name: "Andorra",
    number: "020",
    value: "AND",
    label: "Andorra",
    cleanedLabel: "andorra"
  }, {
    code: "AO",
    code3: "AGO",
    name: "Angola",
    number: "024",
    value: "AGO",
    label: "Angola",
    cleanedLabel: "angola"
  }, {
    code: "AI",
    code3: "AIA",
    name: "Anguilla",
    number: "660",
    value: "AIA",
    label: "Anguilla",
    cleanedLabel: "anguilla"
  }, {
    code: "AQ",
    code3: "ATA",
    name: "Antarctica",
    number: "010",
    value: "ATA",
    label: "Antarctica",
    cleanedLabel: "antarctica"
  }, {
    code: "AG",
    code3: "ATG",
    name: "Antigua and Barbuda",
    number: "028",
    value: "ATG",
    label: "Antigua and Barbuda",
    cleanedLabel: "antigua and barbuda"
  }, {
    code: "AR",
    code3: "ARG",
    name: "Argentina",
    number: "032",
    value: "ARG",
    label: "Argentina",
    cleanedLabel: "argentina"
  }]; // Autocomplete options.
  // Doc here: https://github.com/ecedi/aria-autocomplete

  var options = {
    maxResults: 20,
    keepUserInput: true,
    source: ["option1", "option2", "option3", "lorem ipsum", "Ministère de l'Education Nationale", "Ministère de l'Intérieur", "Ministère des Affaires Etrangères", "Ministère de la Transition Ecologique"]
  }; // Merge default settings and options

  if (window.ariaAutocompleteDefaults) {
    options = $.extend({}, window.ariaAutocompleteDefaults, options);
  }

  var autocompletes = document.querySelectorAll(".js-aria-autocomplete");
  autocompletes.forEach(function (autocomplete) {
    if (!autocomplete.classList.contains("mounted")) {
      autocomplete.classList.add("mounted"); // Get input classes.

      options["inputClassName"] = autocomplete.classList; // Get some attributes.

      options["placeholder"] = autocomplete.getAttribute("placeholder"); // Create aria autocomplete instance

      AriaAutocomplete(autocomplete, options);
    }
  }); // Storybook async autocomplete exemple.

  var asyncAutocomplete = document.querySelector(".js-aria-autocomplete-async");

  if (asyncAutocomplete) {
    var optionsAsync = {
      source: function source(query, render) {
        // Change with real api call.
        // Fill the 'source' property with the real api url.
        // More info here : https://mynamesleon.github.io/aria-autocomplete/ (4. String (endpoint) as Source).
        window.autocompleteTimeOut = setTimeout(function () {
          clearTimeout(window.autocompleteTimeOut);
          render(countries);
        }, Math.floor(Math.random() * 1000));
      }
    };

    if (window.ariaAutocompleteDefaults) {
      optionsAsync = $.extend({}, window.ariaAutocompleteDefaults, optionsAsync);
    }

    if (!asyncAutocomplete.classList.contains("mounted")) {
      asyncAutocomplete.classList.add("mounted"); // Get input classes.

      options["inputClassName"] = asyncAutocomplete.classList; // Create aria autocomplete instance

      AriaAutocomplete(asyncAutocomplete, optionsAsync);
    }
  }
};




window.initSeeMore = function () {
  var toggler = document.querySelectorAll(".see-more__toggler");
  toggler.forEach(function (toggle) {
    if (!toggle.classList.contains("mounted")) {
      var newLabel;
      toggle.classList.add("mounted");
      toggle.addEventListener("click", function () {
        var content = toggle.parentNode.querySelector(".see-more__content");

        if (content.classList.contains("see-more__content--visible")) {
          newLabel = toggle.getAttribute("data-label-more");
          content.classList.remove("see-more__content--visible");
          toggle.textContent = newLabel + "...";
          toggle.setAttribute("aria-label", newLabel);
          toggle.setAttribute("aria-expanded", false);
          content.setAttribute("aria-hidden", true);
        } else {
          newLabel = toggle.getAttribute("data-label-less");
          content.classList.add("see-more__content--visible");
          toggle.textContent = newLabel + "...";
          toggle.setAttribute("aria-label", newLabel);
          toggle.setAttribute("aria-expanded", true);
          content.setAttribute("aria-hidden", false);
          var focusable = content.querySelectorAll('button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])');

          if (focusable[0]) {
            focusable[0].focus();
          }
        }
      });
    }
  });
};



// extracted by mini-css-extract-plugin




document.body.addEventListener("click", function (e) {
  var button = $(e.target).closest(".js-toggle-folders").get(0);

  if (button) {
    e.preventDefault();
    button = $(button);
    var container = button.closest(".last-folders");
    var status = button.attr("data-is-hidden");
    var targets = container.find(".js-show-more-folders");

    if (status === "true") {
      // Showing elements
      targets.each(function () {
        $(this).removeClass("last-folders__folder-selected--hidden");
        $(this).attr("aria-hidden", "false");
      });
      button.attr("data-is-hidden", "false");
      button.text(button.attr("data-text-on-show"));
    } else {
      // Hiding elements
      targets.each(function () {
        $(this).addClass("last-folders__folder-selected--hidden");
        $(this).attr("aria-hidden", "true");
      });
      button.attr("data-is-hidden", "true");
      button.text(button.attr("data-text-on-hide"));
    }
  }
});




window.initProgressBar = function () {
  var barClose = $("div.bar-close");

  if (barClose.length > 0) {
    barClose.each(function () {
      var self = $(this);
      $(this).mouseenter(function () {
        if ($(this).data("fotimeout") && !$(this).hasClass("forceclose")) {
          $(this).stop(true, false).fadeIn();
          clearTimeout($(this).data("fotimeout"));
          $(this).removeData("fotimeout");
          $(this).find(".elapsed").stop(true, false).animate({
            width: "100%"
          }, 125, "linear");
        }
      }).mouseleave(function () {
        var foTimeout = setTimeout(function () {
          self.fadeOut("slow", function () {
            self.remove();
          });
        }, 3000);

        if (!$(this).find(".elapsed").length) {
          $(this).prepend('<div class="elapsed"></div>');
        }

        $(this).find(".elapsed").animate({
          width: 0
        }, 3000, "linear");
        $(this).data("fotimeout", foTimeout);
      }).mouseleave().find(".remover").click(function () {
        var target = $(this).closest(".bar-close");
        target.addClass("forceclose");
        target.find(".elapsed").animate({
          width: 0
        }, 125);
        target.fadeOut(function () {
          $(this).remove();
        });
      });
    });
  }
};



document.body.addEventListener("click", function (e) {
  var button = $(e.target).closest(".block-toasts__trigger").get(0);

  if (button) {
    $(".block-toasts").fadeIn("slow");
  }
});
document.body.addEventListener("click", function (e) {
  var button = $(e.target).closest(".block-toasts__close").get(0);

  if (button) {
    $(".block-toasts").fadeOut("slow");
  }
});








/* eslint-disable */
var oldModal = null;
var oldButton = null;

window.initInterstitial = function () {
  /*
  /* CLASSIC MODALS
  /* ----------------------- */
  //Binding toggle on
  var triggers = document.querySelectorAll(".interstitial-overlay__trigger, .js-interstitial-trigger");
  triggers.forEach(function (trigger) {
    trigger.addEventListener("click", function (e) {
      e.preventDefault();
      var attribute = trigger.getAttribute("data-controls");
      var parameters = trigger.getAttribute("data-parameters"); // param1=XXX;param2=YYY

      var modal;

      if (attribute) {
        modal = $("#" + attribute)[0];
      } else {
        modal = trigger.parentNode.querySelector(".interstitial-overlay__content");
      } // Permet de vérifier qu'il n'y a pas déjà une modal ouverte
      // Si c'est le cas on la ferme et on la sauvegarde pour pouvoir la réouvrir par la suite


      var modalsVisible = $(".interstitial-overlay__content--visible");

      if (modalsVisible.length) {
        modalsVisible.each(function () {
          if (modal !== this) {
            closeModal(this);
            oldModal = this;
            oldButton = trigger;
          }
        });
      }

      modalFocus.currentTrigger = e.target;
      openModal(modal, parameters);
    });
  }); // Binding toggle off for overlay click

  var overlays = document.querySelectorAll(".interstitial-overlay__content");
  overlays.forEach(function (overlay) {
    // Excluding chained modals
    if (!overlay.classList.contains("interstitial-overlay__content--chained")) {
      // Overlay click
      overlay.addEventListener("click", function (e) {
        closeModal(e.target);
      });
    }
  }); // Binding buttons for click and close

  var modalButtons = document.querySelectorAll(".interstitial__button, .interstitial__close");
  modalButtons.forEach(function (button) {
    button.addEventListener("click", function (e) {
      var modal = $(e.target).closest(".interstitial-overlay__content").get(0);
      closeModal(modal);
    });
  });
  /*
  /* CHAINED MODALS
  /* ----------------------- */
  //Binding chained modals

  var chainedOverlays = document.querySelectorAll(".interstitial-overlay__content--chained");
  chainedOverlays.forEach(function (overlay) {
    if (!overlay.classList.contains("initialized")) {
      /* Chained modal methods */
      // Chained modal bindings
      var bindChained = function bindChained() {
        // Bind overlay click
        overlay.addEventListener("click", function () {
          if (!mustRead) closeChainedModal();
        });
        overlay.querySelector(".interstitial").addEventListener("click", function (e) {
          e.stopPropagation();
        }, false); // Bind chained button

        button.addEventListener("click", function (e) {
          if (step + 1 < chained.length) nextStep();else closeChainedModal();
        }, false); // Bind cancel button

        if (cancelButton) cancelButton.addEventListener("click", function (e) {
          if (step === 0) closeChainedModal();else prevStep();
        }, false); // Bind open buttons

        if (openButtons && openButtons.length > 0) openButtons.forEach(function (openButton) {
          openButton.addEventListener("click", function (e) {
            modalFocus.currentTrigger = e.target;
            openChainedModal();
          }, false);
        });
      }; // Display asked chained content


      var goToStep = function goToStep(index) {
        // Hide all steps
        chained.forEach(function (chained_el) {
          chained_el.classList.add("hidden");
          chained_el.setAttribute("aria-hidden", true);
        }); // Have to read to enable next button

        if (mustRead) button.setAttribute("disabled", true);

        if (chained.length > 0) {
          if (index >= 0 && chained[index]) {
            step = index; // Next content init

            chained[index].classList.remove("hidden");
            chained[index].setAttribute("aria-hidden", false);
            bindContent(index); // Last button wording

            button.innerText = index + 1 === chained.length ? lastButtonText : buttonText; // Cancel button wording

            if (cancelButton) {
              cancelButton.innerText = index === 0 ? firstCancelButtonText : cancelButtonText;
            }
          } else closeChainedModal();
        }
      }; // Display prev step


      var prevStep = function prevStep() {
        goToStep(step - 1);
      }; // Display next step


      var nextStep = function nextStep() {
        goToStep(step + 1);
      }; // Enable chained button when scroll reach the end


      var bindContentScroll = function bindContentScroll(e) {
        if (e.target.scrollHeight - e.target.scrollTop <= e.target.offsetHeight + 10) button.removeAttribute("disabled");
      }; // Define new content and bind scroll


      var bindContent = function bindContent(index) {
        if (content) {
          content.setAttribute("tabindex", -1);
          content.removeEventListener("scroll", bindContentScroll, true);
        }

        content = chained[index].querySelector(".interstitial__content");
        content.addEventListener("scroll", bindContentScroll, true);
        header = chained[index].querySelector(".interstitial__header");
        header.setAttribute("tabindex", 0);
        header.focus();
        content.scrollTo(0, 0);
        content.dispatchEvent(new Event("scroll"));
      }; // Close modal and set new focus


      var closeChainedModal = function closeChainedModal() {
        closeModal(overlay); // Come back to first content

        goToStep(0);
      }; // Open modal and trapFocus


      var openChainedModal = function openChainedModal() {
        openModal(overlay); // Bind chained content scroll

        if (!initialized) bindContent(0); // Has been showed at least once

        initialized = true;
      }; // Auto-opening


      overlay.classList.add("initialized");
      var initialized = false;
      var step = 0; // Modal props

      var modalId = overlay.getAttribute("id");
      var autoOpen = overlay.classList.contains("interstitial-overlay__content--autoopen");
      var mustRead = overlay.classList.contains("interstitial-overlay__content--mustread"); // Next button

      var button = overlay.querySelector(".interstitial__button-chained");
      var buttonText = button.innerText;
      var lastButtonText = overlay.getAttribute("data-last-button-text"); // Cancel button

      var cancelButton = overlay.querySelector(".interstitial__button-cancel");

      if (!!cancelButton) {
        // Cancel button is optional
        var firstCancelButtonText = cancelButton.innerText;
        var cancelButtonText = overlay.getAttribute("data-cancel-button-text"); // External open buttons

        var openButtons = document.querySelectorAll("[data-chained-modal=" + modalId + "]");
      } // Modal contents


      var chainedTrigger = overlay.querySelector(".interstitial-overlay__trigger");
      var chained = overlay.querySelectorAll(".interstitial__chained__inner"); // Current step parts

      var content, header; // Checking chained[step] before init via goToStep()
      // to avoid infinite loop in case it does not exist

      if (chained && chained[step]) {
        bindChained();
        goToStep(step);
      }

      if (autoOpen) openChainedModal();
    }
  });
};
/*
/* MODALS METHODS
/* ----------------------- */


function escapeModal(event) {
  if (event.key === "Escape" || event.key === "Esc") {
    if (modalFocus.currentDialog) {
      var modal = modalFocus.currentDialog.parentNode;
      closeModal(modal);
    }
  }
}

function openModal(modal, parameters) {
  if (modal && (!modal.classList.contains("interstitial-overlay__content--visible") || modal.classList.contains("interstitial-overlay__content--autoopen"))) {
    // masquer les icones
    if ($(modal).find(".interstitial-overlay__trigger").length === 0) {
      $(".interstitial-overlay__trigger").hide();
    } // Showing


    modal.classList.add("interstitial-overlay__content--visible"); // Focus

    modalFocus.currentDialog = modal.firstElementChild;
    modalFocus.enableTrapFocus(modal); // Parameters

    if (parameters) {
      // Au click, serialization des différents paramètres
      parametersToHidden(parameters, modal.id);
    } // Escape


    if (!modal.classList.contains("interstitial-overlay__content--mustread")) document.body.addEventListener("keyup", escapeModal); // Scroll

    modalScroll.lockScroll();
  }
}

function closeModal(modal) {
  if (modal.classList.contains("interstitial-overlay__content--visible")) {
    // Hiding
    modal.classList.remove("interstitial-overlay__content--visible"); // Focus

    modalFocus.restituteFocus(); // Escape

    document.body.removeEventListener("keyup", escapeModal); // Scroll

    modalScroll.unlockScroll(); // Permet de réouvrir la modal qui avaient été fermée auparavant pour ne pas avoir deux modals d'ouvertes

    if (oldModal !== null) {
      openModal(oldModal); //Permet de re mettre le focus sur le bouton qui a permi d'ouvrir la seconde modal

      if (oldButton) {
        oldButton.focus();
      }

      oldModal = null;
      oldButton = null;
    } // réafficher les icones


    $(".interstitial-overlay__trigger").show();
  }
}
/*
/* MODAL SCROLL MANAGEMENT
/* ----------------------- */


var modalScroll = {
  lockScroll: function lockScroll(scrollWidth) {
    if (typeof scrollWidth == "undefined") scrollWidth = this.getScrollbarWidth();
    var scrollY = window.pageYOffset || 0;
    document.body.style.position = "fixed";
    document.body.style.top = scrollY * -1 + "px";
    document.body.style.left = "0";
    document.body.style.right = scrollWidth + "px";
  },
  unlockScroll: function unlockScroll() {
    var scrollY = document.body.style.top;
    document.body.style.position = "";
    document.body.style.top = "";
    document.body.style.left = "";
    document.body.style.right = "";
    window.scrollTo(0, parseInt(scrollY || "0") * -1);
  },
  getScrollbarWidth: function getScrollbarWidth() {
    return window.innerWidth - document.documentElement.clientWidth;
  }
};
/*
/* MODAL FOCUS MANAGEMENT
/* ----------------------- */

var modalFocus = {
  currentDialog: null,
  currentTrigger: null,
  trapFocus: function trapFocus(event) {
    var currentDialog = modalFocus.currentDialog;

    if (currentDialog && currentDialog.contains(event.target) || $(event.target).parents(".tox-tinymce-aux").length > 0) {} else {
      if (event.target.classList && event.target.classList.contains("interstitial-overlay__trigger")) {
        modalFocus.focusLastDescendant(currentDialog);
      } else {
        modalFocus.focusFirstDescendant(currentDialog);
      }
    }
  },
  enableTrapFocus: function enableTrapFocus(element) {
    document.addEventListener("focus", this.trapFocus, true);
    modalFocus.focusFirstDescendant(modalFocus.currentDialog);
    modalFocus.modalTabIndex(0);
  },
  disableTrapFocus: function disableTrapFocus() {
    document.removeEventListener("focus", this.trapFocus, true);
    modalFocus.modalTabIndex(-1);
  },
  restituteFocus: function restituteFocus() {
    modalFocus.disableTrapFocus();
    var trigger = modalFocus.currentTrigger;

    if (!trigger || !modalFocus.isFocusable(trigger) || !(modalFocus.attemptFocus(trigger) || modalFocus.attemptFocusHiddenDropdown(trigger))) {
      var body = document.querySelector("body");
      modalFocus.focusFirstDescendant(body);
    }

    modalFocus.currentDialog = null;
    modalFocus.currentTrigger = null;
  },
  focusFirstDescendant: function focusFirstDescendant(element) {
    for (var i = 0; i < element.childNodes.length; i++) {
      var child = element.childNodes[i];

      if (modalFocus.attemptFocus(child) || modalFocus.focusFirstDescendant(child)) {
        return true;
      }
    }

    return false;
  },
  focusLastDescendant: function focusLastDescendant(element) {
    for (var i = element.childNodes.length - 1; i >= 0; i--) {
      var child = element.childNodes[i];

      if (modalFocus.attemptFocus(child) || modalFocus.focusLastDescendant(child)) {
        return true;
      }
    }

    return false;
  },
  attemptFocus: function attemptFocus(element) {
    if (!modalFocus.isFocusable(element)) {
      return false;
    }

    try {
      element.focus();
    } catch (e) {}

    return document.activeElement === element;
  },
  attemptFocusHiddenDropdown: function attemptFocusHiddenDropdown(element) {
	  if (!$(element).hasClass('base-drop-down__link')) {
		  return false;
	  }
	  let controlElement = null;
	  try {
		  const parent = $(element).closest('.base-drop-down__drop');
	      controlElement = parent.siblings("[aria-controls='"+parent.attr('id')+"']");
	    
	      controlElement.focus();
	    } catch (e) {}

	    return controlElement !=null && document.activeElement === controlElement[0];
	  },
  isFocusable: function isFocusable(element) {
    if (element.tabIndex > 0 || element.tabIndex === 0 && element.getAttribute("tabIndex") !== null) {
      return true;
    }

    if (element.disabled) {
      return false;
    }

    switch (element.nodeName) {
      case "A":
        return !!element.href && element.rel != "ignore";

      case "INPUT":
        return element.type != "hidden" && element.type != "file";

      case "BUTTON":
      case "SELECT":
      case "TEXTAREA":
        return true;

      default:
        return false;
    }
  },
  modalTabIndex: function modalTabIndex(value) {
    var parent = modalFocus.currentDialog.parentNode.parentNode || modalFocus.currentDialog.parentNode;

    if (parent) {
      var tabindex = parent.querySelector(".interstitial-overlay__tabindex");
      if (tabindex) tabindex.setAttribute("tabindex", value);
    }
  }
};
/*
/* OTHERS
/* ----------------------- */
// Transforme les paramètres (string) en une collection d'input hidden qui seront ajoutés à l'élément passé en paramètre

function parametersToHidden(parameters, elementId) {
  var paramArray = parameters.split(";");

  for (var i = 0; i < paramArray.length; i++) {
    var elts = paramArray[i].split("=");
    var inputHidden = document.createElement("input");
    inputHidden.type = "hidden";
    inputHidden.name = elts[0];
    inputHidden.id = elts[0]; // On supprime l'élément existant au cas où on serait déjà passé par là

    $("#" + inputHidden.id).remove();
    inputHidden.value = elts[1];
    $("#" + elementId).append(inputHidden);
  }
}
/* eslint-enable */









var CUSTOM_TABLE_WITH_CHECKBOXES_CLASS = "custom-table--has-check";
var CUSTOM_TABLE_HEADER_CHECK_CLASS = "js-custom-table-header-check";
var CUSTOM_TABLE_LINE_CHECK_CLASS = "js-custom-table-line-check";

window.initCustomTable = function () {
  var customTableWithChecks = document.getElementsByClassName(CUSTOM_TABLE_WITH_CHECKBOXES_CLASS);

  if (!customTableWithChecks.length) {
    return;
  }

  var customTableHeaderCheck = null;
  var customTableLineChecks = null;
  var _iteratorNormalCompletion = true;
  var _didIteratorError = false;
  var _iteratorError = undefined;

  try {
    for (var _iterator = customTableWithChecks[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
      var customTable = _step.value;
      customTableHeaderCheck = customTable.querySelector("." + CUSTOM_TABLE_HEADER_CHECK_CLASS);
      customTableLineChecks = customTable.querySelectorAll("." + CUSTOM_TABLE_LINE_CHECK_CLASS);
      CustomTableManager.initHeaderCheckEvents(customTableHeaderCheck, customTableLineChecks);
    }
  } catch (err) {
    _didIteratorError = true;
    _iteratorError = err;
  } finally {
    try {
      if (!_iteratorNormalCompletion && _iterator.return != null) {
        _iterator.return();
      }
    } finally {
      if (_didIteratorError) {
        throw _iteratorError;
      }
    }
  }
};

var CustomTableManager = {
  initHeaderCheckEvents: function initHeaderCheckEvents(headerCheck, lineChecks) {
    if (!headerCheck) {
      return;
    }

    headerCheck.addEventListener("input", function (event) {
      var checked = event.target.checked;
      var _iteratorNormalCompletion2 = true;
      var _didIteratorError2 = false;
      var _iteratorError2 = undefined;

      try {
        for (var _iterator2 = lineChecks[Symbol.iterator](), _step2; !(_iteratorNormalCompletion2 = (_step2 = _iterator2.next()).done); _iteratorNormalCompletion2 = true) {
          var lineCHeck = _step2.value;
          lineCHeck.checked = checked;
        }
      } catch (err) {
        _didIteratorError2 = true;
        _iteratorError2 = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion2 && _iterator2.return != null) {
            _iterator2.return();
          }
        } finally {
          if (_didIteratorError2) {
            throw _iteratorError2;
          }
        }
      }
    });
  }
};



var CLASS_NO_SCROLL = "no-scroll";

window.initWindowHelpers = function () {
  window.toggleNoScroll = WindowManager.toggleNoScroll;
};

var WindowManager = {
  toggleNoScroll: function toggleNoScroll() {
    var isScrollable = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : false;

    if (isScrollable) {
      document.body.classList.remove(CLASS_NO_SCROLL);
      document.documentElement.classList.remove(CLASS_NO_SCROLL);
    } else {
      document.body.classList.add(CLASS_NO_SCROLL);
      document.documentElement.classList.add(CLASS_NO_SCROLL);
    }
  }
};




var BASE_SELECTOR_TABULATION = "tabulation";
var BASE_SELECTOR_TABULATION_ITEM = BASE_SELECTOR_TABULATION + "__item";
var BASE_SELECTOR_TABULATION_CONTENT = BASE_SELECTOR_TABULATION + "__content";
var BASE_SELECTOR_TABULATION_JS = "js-tabulation";
document.body.addEventListener("click", function (e) {
  var tab = $(e.target).closest("." + BASE_SELECTOR_TABULATION_JS).get(0);

  if (tab && !tab.classList.contains("." + BASE_SELECTOR_TABULATION_ITEM + "--active")) {
    var container = $(tab).closest("." + BASE_SELECTOR_TABULATION).get(0); // clean active tab

    var activeTabs = container.querySelectorAll("." + BASE_SELECTOR_TABULATION_ITEM + "--active");
    activeTabs.forEach(function (activeTab) {
      if (activeTab) {
        activeTab.classList.remove(BASE_SELECTOR_TABULATION_ITEM + "--active");
        activeTab.setAttribute("tabindex", "-1");
        activeTab.setAttribute("aria-selected", "false");
      }
    }); // clean active content

    var activeContents = container.querySelectorAll("." + BASE_SELECTOR_TABULATION_CONTENT + "--active");
    activeContents.forEach(function (activeContent) {
      if (activeContent) {
        activeContent.classList.remove(BASE_SELECTOR_TABULATION_CONTENT + "--active");
        activeContent.setAttribute("aria-hidden", "true");
      }
    }); // active new tab

    tab.classList.add(BASE_SELECTOR_TABULATION_ITEM + "--active");
    tab.removeAttribute("tabindex");
    tab.setAttribute("aria-selected", "true"); // active new content

    var newContent = container.querySelector("#" + tab.getAttribute("aria-controls"));

    if (newContent) {
      newContent.classList.add(BASE_SELECTOR_TABULATION_CONTENT + "--active");
      newContent.setAttribute("aria-hidden", "false");
      var newContentChild = newContent.querySelector("button." + BASE_SELECTOR_TABULATION_ITEM);

      if (newContentChild) {
        newContentChild.click();
      }
    }
  }
}); //handling tabulation navigation

window.initTabs = function () {
  var tabs = document.querySelectorAll("." + BASE_SELECTOR_TABULATION_ITEM);
  tabs.forEach(function (tab) {
    if (!tab.classList.contains("mounted")) {
      tab.classList.add("mounted");
      tab.addEventListener("keyup", function (event) {
        var nextElement = tab.nextSibling;
        var previousElement = tab.previousSibling;

        if (nextElement && (event.key === "ArrowRight" || event.key === "Right")) {
          nextElement.focus();
          nextElement.click();
        }

        if (previousElement && (event.key === "ArrowLeft" || event.key === "Left")) {
          previousElement.focus();
          previousElement.click();
        }
      });
    }
  });
};






window.initComplexSelects = function () {
  //Seeking for all complex selector
  var selects = document.querySelectorAll(".form-complex-selector__select"); //Iterating trought select to bind selection event

  if (selects) {
    selects.forEach(function (select) {
      if (!select.classList.contains("mounted")) {
        select.classList.add("mounted");
        isDisabled = select.getAttribute("disabled") === "disabled";
        var dlb = new DualListbox("#" + select.getAttribute("id"), {
          addButtonText: "Ajouter",
          removeButtonText: "Enlever",
          addAllButtonText: "Tout ajouter",
          removeAllButtonText: "Tout enlever"
        });
        dlb.search.parentNode.removeChild(dlb.search);
        dlb.availableListTitle.parentNode.removeChild(dlb.availableListTitle);
        dlb.selectedListTitle.parentNode.removeChild(dlb.selectedListTitle);
        var btnAdd = $(dlb.add_button).clone(true);
        var btnAddAll = $(dlb.add_all_button).clone(true);
        var btnRemove = $(dlb.remove_button).clone(true);
        var btnRemoveAll = $(dlb.remove_all_button).clone(true);
        var btnContainer = dlb.add_button.parentNode;
        btnAdd[0].classList.remove("dual-listbox__button");
        btnAdd[0].classList.add("dual-listbox__btn");
        btnAdd[0].classList.add("dual-listbox__btn--to-right");
        btnAdd[0].classList.add("base-btn");
        btnAdd[0].classList.add("base-btn--default");
        btnAdd[0].classList.add("base-btn--center");
        btnAdd[0].setAttribute("type", "button");

        if (isDisabled) {
          btnAdd[0].classList.add("base-btn--disabled");
          btnAdd[0].setAttribute("disabled", "disabled");
        }

        btnAddAll[0].classList.remove("dual-listbox__button");
        btnAddAll[0].classList.add("dual-listbox__btn");
        btnAddAll[0].classList.add("base-btn");
        btnAddAll[0].classList.add("base-btn--light");
        btnAddAll[0].classList.add("base-btn--default");
        btnAddAll[0].classList.add("base-btn--bordered");
        btnAddAll[0].classList.add("base-btn--transparent");
        btnAddAll[0].classList.add("base-btn--center");
        btnAddAll[0].setAttribute("type", "button");

        if (isDisabled) {
          btnAddAll[0].classList.add("base-btn--disabled");
          btnAddAll[0].setAttribute("disabled", "disabled");
        }

        btnRemove[0].classList.remove("dual-listbox__button");
        btnRemove[0].classList.add("dual-listbox__btn");
        btnRemove[0].classList.add("dual-listbox__btn--to-left");
        btnRemove[0].classList.add("base-btn");
        btnRemove[0].classList.add("base-btn--default");
        btnRemove[0].classList.add("base-btn--center");
        btnRemove[0].setAttribute("type", "button");

        if (isDisabled) {
          btnRemove[0].classList.add("base-btn--disabled");
          btnRemove[0].setAttribute("disabled", "disabled");
        }

        btnRemoveAll[0].classList.remove("dual-listbox__button");
        btnRemoveAll[0].classList.add("dual-listbox__btn");
        btnRemoveAll[0].classList.add("base-btn");
        btnRemoveAll[0].classList.add("base-btn--light");
        btnRemoveAll[0].classList.add("base-btn--default");
        btnRemoveAll[0].classList.add("base-btn--bordered");
        btnRemoveAll[0].classList.add("base-btn--transparent");
        btnRemoveAll[0].classList.add("base-btn--center");
        btnRemoveAll[0].setAttribute("type", "button");

        if (isDisabled) {
          btnRemoveAll[0].classList.add("base-btn--disabled");
          btnRemoveAll[0].setAttribute("disabled", "disabled");
        } //Binding events


        if (!isDisabled) {
          $(btnAdd).on("click", function () {
            dlb.add_button.click();
          });
          $(btnAddAll).on("click", function () {
            dlb.add_all_button.click();
          });
          $(btnRemove).on("click", function () {
            dlb.remove_button.click();
          });
          $(btnRemoveAll).on("click", function () {
            dlb.remove_all_button.click();
          });
        } //Adding components


        $(btnContainer).append(btnAdd);
        $(btnContainer).append(btnRemove);
        $(btnContainer).append(btnAddAll);
        $(btnContainer).append(btnRemoveAll); //Binding navigation between

        if (!isDisabled) {
          bindItemEvents(dlb.availableList.childNodes, isDisabled);
          bindItemEvents(dlb.selectedList.childNodes, isDisabled);
        }
      }
    });
  }
};

function bindItemEvents(items) {
  items.forEach(function (item) {
    if (!item.classList.contains("mounted")) {
      item.classList.add("mounted");
      item.setAttribute("tabIndex", "0");
      item.addEventListener("keyup", function (event) {
        if (event.key === "ArrowUp" || event.key === "Up") {
          var previous = item.previousSibling;

          if (previous) {
            previous.click();
            previous.focus();
          }
        }

        if (event.key === "ArrowDown" || event.key === "Down") {
          var next = item.nextSibling;

          if (next) {
            next.click();
            next.focus();
          }
        }

        if (event.key === "Enter") {
          var doubleClickEvent = document.createEvent("MouseEvents");
          doubleClickEvent.initEvent("dblclick", true, true);
          item.dispatchEvent(doubleClickEvent);
          item.focus();
        }
      });
    }
  });
}




window.initLetterSlider = function () {
  var sliders = document.querySelectorAll(".letter-index-slider");
  sliders.forEach(function (slider) {
    if (!slider.classList.contains("mounted")) {
      slider.classList.add("mounted"); // init slider

      var mySwiper = new Swiper(slider.querySelector(".js-letter-index-slider"), {
        a11y: {
          enabled: false
        },
        keyboard: {
          enabled: false
        },
        init: false,
        slidesPerView: "auto",
        autoHeight: true,
        speed: 400,
        navigation: {
          nextEl: ".js-letter-index-slider-button-next",
          prevEl: ".js-letter-index-slider-button-prev",
          disabledClass: "letter-index-slider__pagination-button--disabled",
          hiddenClass: "letter-index-slider__pagination-button--hidden",
          clickable: true
        }
      });
      mySwiper.init();
      var next = slider.querySelector(".js-letter-index-slider-button-next");

      if (next) {
        next.removeAttribute("role");
      }

      var prev = slider.querySelector(".js-letter-index-slider-button-prev");

      if (prev) {
        prev.removeAttribute("role");
      }

      var buttons = slider.querySelectorAll(".letter-index-slider__swiper-wrapper .letter-index-slider__letter-button");
      buttons.forEach(function (button, index) {
        if ($(button).hasClass("letter-index-slider__letter-button--active")) {
          mySwiper.slideTo(index - 1, 0);
        }

        button.addEventListener("focus", function () {
          var parent = button.parentNode;

          if (parent.parentNode.firstChild === parent) {
            mySwiper.slideTo(0);
          }
        });
        button.addEventListener("keydown", function (event) {
          if (event.key === "ArrowRight" || event.key === "Right") {
            var nextElement = button.parentNode.nextSibling;

            if (nextElement) {
              nextElement.querySelector(".letter-index-slider__letter-button").focus();
            }

            if (index > 5) {
              mySwiper.slideNext();
            }
          }

          if (event.key === "ArrowLeft" || event.key === "Left") {
            var previousElement = button.parentNode.previousSibling;

            if (previousElement) {
              previousElement.querySelector(".letter-index-slider__letter-button").focus();
            }

            if (index < 20) {
              mySwiper.slidePrev();
            }
          }

          if (event.key === "Tab" && event.shiftKey === false) {
            if (index > 5) {
              mySwiper.slideNext();
            }
          }

          if (event.key === "Tab" && event.shiftKey === true) {
            if (index < 20) {
              mySwiper.slidePrev();
            }
          }
        });
      });
    }
  });
};


//# sourceMappingURL=app.js.map