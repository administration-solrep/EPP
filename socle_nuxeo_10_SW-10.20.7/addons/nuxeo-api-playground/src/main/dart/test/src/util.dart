part of nuxeo_api_playground.test;

/// Use a [PathObserver] on an object
Future observePath(object, path) {
  var completer = new Completer();
  new PathObserver(object, path).open(expectAsync((res) { completer.complete(res); }));
  return completer.future;
}

/// Use a [MutationObserver] to observe DOM changes in an [Element]
Future observeElement(element) {
  var completer = new Completer();
  var observer = new MutationObserver(expectAsync((mutations, observer) {
    completer.complete(mutations);
  }));
  observer.observe(element, childList: true, subtree: true);
  return completer.future;
}