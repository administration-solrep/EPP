/**
 * Find the stepActions button from a step using DOM.
 */
Selenium.prototype.doGetActionButtonIdFromStep = function(locator, value) {
  storedVars[value] = this.browserbot.findElement(locator).parentNode.parentNode.parentNode.nextSibling.nextSibling.nextSibling.nextSibling.nextSibling.nextSibling.nextSibling.nextSibling.nextSibling.nextSibling.getElementsByTagName('img')[0].id;
};

// set absolute path to current folder here, used in tests with file upload
Selenium.prototype.doRetrieveTestFolderPath = function() {
  // En dev : fichiers référencés en absolu
  storedVars['testfolderpath'] = "/home/admin/workspace/socle_transverse/socle-transverse-util/src/main/resources/selenium/data/";
};

//This extension adds command 'sleep' that halts the execution of a test for the specified number of milliseconds. see http://wiki.openqa.org/display/SEL/sleep
Selenium.prototype.doSleep = function(locator, paramString) {
  var start = new Date().getTime();
  while (new Date().getTime() < start + parseInt(locator));
};

// méthode assertTextPresentXML : permet de vérifier qu'un contenu XML est présent dans la page 
Selenium.prototype.isTextPresentXML = function(pattern) {
/**
 * Verifies that the specified text pattern appears somewhere in the entire XML document
 * (as opposed to text only within &lt;body/&gt; tags).
 * Note that when defining text patterns in your test case you should
 * escape HTML characters. For example: <td>&lt;userInRole&gt;</td>
 * (not <td><userInRole></td>).
 * @param pattern a <a href="#patterns">pattern</a> to match with the text of the document
 * @return boolean true if the pattern matches the text, false otherwise
 */

  // the only code that differs from the stock Selenium isTextPresent
  //  is the code to retrieve allText...
  // IE
  var allText = this.browserbot.getDocument().xml;
  if (allText == null) {
      // Firefox
      allText = new XMLSerializer().serializeToString(this.browserbot.getDocument());
  }

  var patternMatcher = new PatternMatcher(pattern);
  if (patternMatcher.strategy == PatternMatcher.strategies.glob) {
          if (pattern.indexOf("glob:")==0) {
                  pattern = pattern.substring("glob:".length); // strip off "glob:"
              }
      patternMatcher.matcher = new PatternMatcher.strategies.globContains(pattern);
  }
  else if (patternMatcher.strategy == PatternMatcher.strategies.exact) {
              pattern = pattern.substring("exact:".length); // strip off "exact:"
          return allText.indexOf(pattern) != -1;
      }
      return patternMatcher.matches(allText);
};

// helper method to store current year/month/day
Selenium.prototype.doRetrieveCurrentDateInfo = function() {
  var date = new Date();
  storedVars['currentYear'] = date.getFullYear();
  var m = date.getMonth() + 1;
  storedVars['currentMonth'] = (m < 10) ? '0' + m : m;
  var d = date.getDate();
  storedVars['currentDay'] = (d < 10) ? '0' + d : d;
};

// override default method to make sure privilege to type file path is enabled
Selenium.prototype.doType = function(locator, value) {
   /**
   * Sets the value of an input field, as though you typed it in.
   *
   * <p>Can also be used to set the value of combo boxes, check boxes, etc. In these cases,
   * value should be the value of the option selected, not the visible text.</p>
   *
   * @param locator an <a href="#locators">element locator</a>
   * @param value the value to type
   */
   // this is the added line here:
   netscape.security.PrivilegeManager.enablePrivilege("UniversalFileRead");
   if (this.browserbot.controlKeyDown || this.browserbot.altKeyDown || this.browserbot.metaKeyDown) {
        throw new SeleniumError("type not supported immediately after call to controlKeyDown() or altKeyDown() or metaKeyDown()");
   }
   // TODO fail if it can't be typed into.
   var element = this.browserbot.findElement(locator);
   if (this.browserbot.shiftKeyDown) {
       value = new String(value).toUpperCase();
   }
   this.browserbot.replaceText(element, value);
};

// ajax4jsf testing helper inspired from
// http://codelevy.com/articles/2007/11/05/selenium-and-ajax-requests
/**
 * Registers with the a4j library to record when an Ajax request
 * finishes.
 *
 * Call this after the most recent page load but before any Ajax requests.
 *
 * Once you've called this for a page, you should call waitForA4jRequest at
 * every opportunity, to make sure the A4jRequestFinished flag is consumed.
 */
Selenium.prototype.doWatchA4jRequests = function() {
  var testWindow = selenium.browserbot.getCurrentWindow();
  // workaround for Selenium IDE 1b2 bug, see
  // http://clearspace.openqa.org/message/46135
  if (testWindow.wrappedJSObject) {
      testWindow = testWindow.wrappedJSObject;
  }
  Selenium.A4jRequestFinished = false;
  Selenium.ActiveA4jRequestCount = 0;
  testWindow.A4J.AJAX.AddListener({
    onbeforeajax: function() {
      Selenium.ActiveA4jRequestCount++;
    }
  });
  testWindow.A4J.AJAX.AddListener({
    onafterajax: function() {
      Selenium.ActiveA4jRequestCount--;
      if (Selenium.ActiveA4jRequestCount == 0) {
        Selenium.A4jRequestFinished = true;
      }
    }
  });
}

/**
 * If you've set up with watchA4jRequests, this routine will wait until
 * Ajax requests have finished and then return.
 */
Selenium.prototype.doWaitForA4jRequest = function(timeout) {
  return Selenium.decorateFunctionWithTimeout(function() {
    if (Selenium.A4jRequestFinished) {
      Selenium.A4jRequestFinished = false;
      //Selenium.prototype.doPause(2000);
      return true;
    }
    return false;
  }, timeout);
}

Selenium.A4jRequestFinished = false;
Selenium.ActiveA4jRequestCount = 0;
