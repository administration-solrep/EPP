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

library nx_resource_endpoints;

import 'dart:async';
import 'dart:convert' show JSON;
import 'dart:html';
import 'package:nuxeo_client/browser_client.dart' as nuxeo;
import 'package:polymer/polymer.dart';
import 'ui_module.dart';
import 'ui_filters.dart';
import '../swagger.dart' as swagger;
import 'semantic.dart';

/// A list of the endpoints to display
const RESOURCE_ENDPOINTS = const ["path", "id", "directory", "user", "group", "search", "workflow", "workflowModel", "task", "facet", "schema", "docType"];

/// Wrapper for a [swagger.Parameter] to store the current value and make it [Observable].
class NxParameterValue extends Observable {

  swagger.Parameter _param;
  @observable var value;

  NxParameterValue(this._param);

  String get name => _param.name;
  String get dataType => _param.dataType;
  String get description => _param.description;
  String get type => _param.type;

  bool get isPathParam => _param.isPathParam;
  bool get isHeaderParam => _param.isHeaderParam;
  bool get isBodyParam => _param.isBodyParam;
  bool get isQueryParam => _param.isQueryParam;

  bool get required => _param.required;

  bool get isValid => !required || (required && value != null);

  bool get isEmpty => value == null || value.toString().isEmpty;

  String get widget {

    // We use the type to specify the field of the document we want to use
    if (name == 'docPath') return 'path';
    if (name == 'docId') return 'uid';

    if (isBodyParam) return 'textarea';

    if (dataType == 'string' || dataType == 'long') return 'text';

    return dataType;
  }
}

/// Model for the [NXResourceEndpoints] module.
class ResourceEndpoints extends Module {
  String title = "Resources Endpoints",
         icon = "resources_endpoints.png",
         description = "Discover the list of REST resources endpoints and try some REST calls to see the result.",
         action = "Discover",
         tag = NXResourceEndpoints.TAG;

  /// The key for the selected operation.
  @observable String op;

  void setupRoutes(Route route) {
   route.addRoute(
     name: 'rest',
     path: '/:endpoint/:idx/:method',
     defaultRoute: true,
     enter: (e) {
       var endpoint = e.parameters['endpoint'],
           idx = e.parameters['idx'],
           method = e.parameters['method'];
       op = (endpoint != null && idx != null && method != null)? "$endpoint/$idx/$method" : null;
     });
  }
}

/// The Resource Endpoints module element.
@CustomTag(NXResourceEndpoints.TAG)
class NXResourceEndpoints extends NXModule with SemanticUI, SearchFilter {

  static const String TAG = "nx-resource-endpoints";

  /// A Map of the endpoints
  final Map<String, List<swagger.Resource>> endpoints = toObservable({});

  final Map<String, String> methodColors = {
    "GET": "blue", "POST": "teal", "PUT": "green", "DELETE": "red"
  };

  /// The current resource.
  @observable swagger.Resource endpoint;
  /// The current operation.
  @observable swagger.Operation operation;

  /// List of parameter values
  final List<NxParameterValue> params = toObservable([]);

  /// The id of the referenced Batch Upload.
  @observable var batch;

  @observable nuxeo.Request request;
  @observable var response;
  final List errors = toObservable([]);

  // SearchFilter
  @observable String searchTerm = '';
  @observable String searchFilter = '';

  NXResourceEndpoints.created() : super.created() {

  }

  /// Triggered when the connection is established
  onConnect() {
    endpoints.clear();

    Future.wait(
     RESOURCE_ENDPOINTS.map((resourceKey) {
      var request = NX.newRequest("doc/$resourceKey.json");

      // Execute the method (does not handle the response)
      return request.method("GET").execute()
      .then((response) {
        var json = JSON.decode(response.body);
        var listing = new swagger.Listing.fromJSON(json);
        endpoints[resourceKey] = listing.resources;
      })
      .catchError((e) {
        print("Endpoint '$resourceKey' not available.");
      });
    }))
    // After everything is loaded
    .then((_) {
      updateOperation();
      // let's setup the accordion
      async((_) => accordion("#endpoints"));
    });

  }

  /// Update the current resource and operation.
  @ObserveProperty("module.op")
  updateOperation() {
    var currentPath = (module as ResourceEndpoints).op;
    if (endpoints.isEmpty || currentPath == null) {
      operation = null;
    } else {
      var parts = currentPath.split("/"),
          endpointKey = parts[0],
          idx = int.parse(parts[1]),
          method = parts[2];

      endpoint = endpoints[endpointKey][idx];
      operation = (endpoint == null)? null : endpoint.operations.where((o) => o.method == method).first;
    }
  }

  get endpointKey => endpoints.keys.firstWhere((k) => endpoints[k].contains(endpoint));

  /// Called when the selected operation changes.
  operationChanged() {
    // Setup the parameters
    params.clear();

    // Add all except header params
    if (operation != null) {
      params.addAll(operation.parameters.where((p) => !p.isHeaderParam).map((p) => new NxParameterValue(p)));
    }

    // Clear request and response
    _reset();

    request = NX.newRequest(endpoint.path);

    // Setup the options
    async((_) => accordion("#options"));
  }

  select(event, detail, target) {
    var parts = target.dataset["target"].split("/");
    router.go("$path.rest", {"endpoint": parts[0], "idx": parts[1], "method": parts[2]});
  }

  /// Executes the requests.
  runRequest(evt) {

    // don't submit the form
    evt.stopImmediatePropagation();
    evt.preventDefault();

    // Clear request and response
    _reset();

    var valid = shadowRoot.querySelectorAll("nx-widget").every((_) => _.valid);

    if (!valid) {
      errors.add("Invalid form values!");
      return;
    }

    String path = endpoint.path;

    // replace parameters
    params.where((p) => p.isPathParam).forEach((param) {
      var v = param.value == null ? "" : param.value;
      // strip initial / from document paths
      if (v is String && v.startsWith('/')) {
        v = v.substring(1);
      }
      path = path.replaceAll("{${param.name}}", v);
    });

    // Query parameters
    var queryParams = params
        .where((p) => p.isQueryParam && !p.isEmpty)
        .map((param) => "${param.name}=${Uri.encodeQueryComponent(param.value)}")
        .join("&");

    var bodyParam = params.where((p) => p.isBodyParam);

    var body = (bodyParam.isEmpty) ? null : bodyParam.first.value;

    request.uri = Uri.parse("${NX.restUri}$path?$queryParams");

    // Call the op using 'execute' which does not handle the response
    request.method(operation.method).execute(body)
    .then((res) { response = res; })
    .catchError((e) {
      if (e is nuxeo.ClientException) {
        // Store the response
        response = e.response;
        errors.add(e.message);
      }
     });
  }

  referenceBatch(Event e) {
    e.preventDefault();
    e.stopImmediatePropagation();
    async((_) {
      modal('.ui.modal');
    });
  }

  /// Perform a GET request with the given parameters to get a default value for the body (if any)
  setDefaultBody() {
    var bodyParam = params.where((p) => p.isBodyParam).first;

    String path = endpoint.path;

    // replace parameters
    params.where((p) => p.isPathParam).forEach((param) {
      path = path.replaceAll("{${param.name}}", (param.value == null)? "" : param.value);
    });

    // Call the op using 'execute' which does not handle the response
    NX.newRequest(path).method("GET").execute()
    .then((res) { bodyParam.value = res.body; })
    .catchError((e) {
      if (e is nuxeo.ClientException) {
        errors.add(e.message);
      }
     });
  }

  get capitalize => new Capitalize();

  /// Clear request, response and errors
  _reset() {
    response = null;
    errors.clear();
  }
}
