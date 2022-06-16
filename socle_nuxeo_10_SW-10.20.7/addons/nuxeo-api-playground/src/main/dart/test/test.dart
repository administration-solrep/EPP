library nuxeo_api_playground.test;

import 'dart:async';
import 'dart:html';

import 'package:nuxeo_api_playground/app.dart';
import 'package:polymer/polymer.dart';
import 'package:unittest/html_enhanced_config.dart';
import 'package:unittest/unittest.dart';

part 'src/test_content_browser.dart';
part 'src/test_login.dart';
part 'src/util.dart';

// The app instance
NXSandboxApp app;

/// Navigate to a module
go(module) =>  app.router.go(module, {});

void main() {
  useHtmlEnhancedConfiguration();

  initPolymer();

  Polymer.onReady.then((e) {

    app = document.querySelector('#app');

    testLogin();
    testContentBrowser();
  });

}



