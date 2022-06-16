/*
 * (C) Copyright 2014 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nelson Silva <nelson.silva@inevo.pt>
 */

library nx_connection;

import 'dart:async';
import 'dart:math' as Math;
import 'dart:html';

import 'package:nuxeo_api_playground/cookies.dart';
import 'package:nuxeo_client/browser_client.dart' as nuxeo;
import 'package:polymer/polymer.dart';
import 'semantic.dart';

/// An element that wraps a [nuxeo.Client]
@CustomTag(NXConnection.TAG)
class NXConnection extends PolymerElement with SemanticUI {

  static const String TAG = "nx-connection";

  static const NX_AUTHENTICATION_TOKEN = "X-Authentication-Token";
  static const NX_URL_COOKIE = "Nuxeo-URL";

  /// Nuxeo Client instance
  nuxeo.Client NX;

  /// Indicates if this client is connected to the server.
  @observable bool isConnected = false;

  @observable String username = "Administrator";
  @observable String password = "Administrator";
  @observable String avatar = "https://avatars0.githubusercontent.com/u/6028";

  @observable var nuxeoUrl = "https://demo.nuxeo.com/nuxeo"; // "http://localhost:8080/nuxeo";

  // Tracing
  @observable bool canManageTraces = false;
  @observable bool tracesEnabled = false;

  final List<Alert> alerts = toObservable([]);

  Map<String, String> queryParams;

  NXConnection.created() : super.created() {
    // Support auto connecting to server using hash fragment as url
    // ex: (#/http://localhost:8080/nuxeo)
    if (window.location.hash.startsWith("#/http")) {
      nuxeoUrl = window.location.hash.substring(2);
      username = '';
      password = '';
      NX = new nuxeo.Client(url: nuxeoUrl, schemas: ["*"],  username: null, password: null);
      _login();
      return;
    }
    // Check if we have any token stored as a cookie
    String token = cookies[NX_AUTHENTICATION_TOKEN];
    if (token != null) {
      nuxeoUrl = cookies[NX_URL_COOKIE];
      NX = new nuxeo.Client(url: nuxeoUrl, schemas: ["*"], headers: {NX_AUTHENTICATION_TOKEN: token});
      _login();
    }
  }

  nuxeoUrlChanged() {
    //TODO(nfgs) Update the server URL
  }

  /// Connect to the server
  connect() {

    alerts.clear();

    NX = new nuxeo.Client(url: nuxeoUrl, username: username, password: password, schemas: ["*"]);

    _login().then((_) {
      // Try to get a token and if succeed store it as a cookie along with the URL
      var deviceId = "device-${new Math.Random().nextInt(999)}";
      _getToken("Nuxeo API Playground", deviceId, deviceId).then((token) {
        NX.headers[NX_AUTHENTICATION_TOKEN] = cookies[NX_AUTHENTICATION_TOKEN] = token;
        cookies[NX_URL_COOKIE] = nuxeoUrl;
      });
    });
  }

  get token => NX.headers[NX_AUTHENTICATION_TOKEN];

  _login() => NX.login()
    .then((login) {

      isConnected = true;

      // Get the username
      username = login.username;

      return NX;
     })
    .catchError((e) {
      alerts.add(
        new Alert.error("Please check the Nuxeo URL and try connecting again.")
      );
    });

  /// Get a token that can be used for authentication
  Future<String> _getToken(String applicationName, String deviceId, String deviceDescription, {String permission:"rw"}) {
    var p = "applicationName=$applicationName&deviceId=$deviceId&deviceDescription=$deviceDescription&permission=$permission";
    var request = NX.httpClient.get(Uri.parse("$nuxeoUrl/authentication/token?$p"));
    request.headers.set("X-No-Basic-Header", "true");
    return request.send().then((response) => response.body);
  }

  Map<String, String> _parseQuery() {
    var params = <String,String>{};
    var queryStr = window.location.search;
    if (queryStr.startsWith('?')) {
      queryStr = queryStr.substring(1);
      queryStr.split('&').forEach((String keyValPair) {
        List<String> keyVal = keyValPair.split('=');
        var key = keyVal[0];
        if (key.isNotEmpty) {
          params[key] = Uri.decodeComponent(keyVal[1]);
        }
      });
    }
    return params;
  }

  isConnectedChanged() {

    async((_) {
      dropdown('.ui.dropdown');
    });

    // Check if we can toggle tracing if so set the default traing level
   _checkCanManageTraces().then((yes) { if(yes) tracesEnabledChanged(); });
  }

  /// Disconnects from the server and removes any cookies.
  disconnect() {
    cookies.remove("X-Authentication-Token");
    cookies.remove("Nuxeo-URL");
    isConnected = false;
  }

  /*
   * Tracing
   */

  /// Checks if we can manage traces
  Future<bool> _checkCanManageTraces() =>
      NX.httpClient.get(Uri.parse("$nuxeoUrl/site/automation/doc/traces?opId=")).send()
      .then((res) => canManageTraces = (res.body != "You can not manage traces"));

  tracesEnabledChanged() {
    toggleTraces(tracesEnabled);
  }

  /// Toggle traces
  Future<bool> toggleTraces(flag) => NX.op("Traces.ToggleRecording").params({"enableTrace": flag})();

  Future<String> getTrace(op) =>
    NX.httpClient.get(Uri.parse("$nuxeoUrl/site/automation/doc/traces?opId=$op")).send()
    .then((res) => res.body);
}

class Alert {
  String style;
  String message;
  Alert.error(this.message) : style = "error";
}
