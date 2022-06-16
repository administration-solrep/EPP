/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 *     Nelson Silva <nsilva@nuxeo.com>
 */

library nx_request_adapters;

import 'dart:async';
import 'dart:convert' show JSON;
import 'dart:html';
import 'package:polymer/polymer.dart';
import 'ui_module.dart';
import 'semantic.dart';
import 'ui_filters.dart';
import '../swagger.dart' as swagger;
import 'nx_resource_endpoints.dart' show NxParameterValue;

/// A list of the adapters to display
const ADAPTER_ENDPOINTS = const ["audit", /*"blob",*/ "search", "children", "convert"];

@reflectable
class NxWebAdapter {
  String name;
  String description;
  String notes;

  swagger.Operation operation;

  /// List of parameter values
  final List<NxParameterValue> params = toObservable([]);

  NxWebAdapter(this.name, swagger.Listing listing) {
    for (var r in listing.resources) {
      operation = r.operations.firstWhere(
              (o) => o.method == 'GET' && o.parameters.any((p) => p.name == 'docId'),
              orElse: () => null);
      if (operation != null) {
        break;
      }
    }

    if (operation == null) {
      print("No supported operation found");
      throw new Exception("No supported operation found!");
    }

    description = operation.summary;
    notes = operation.notes;
    // Add all except docId and header params
    params.addAll(operation.parameters.where((p) => !p.isHeaderParam && p.name != 'docId').map((p) => new NxParameterValue(p)));
  }

}

@CustomTag("nx-web-adapters")
class NXWebAdapters extends NXElement with SemanticUI {

  static final List<NxWebAdapter> _adapters = [];

  /// A list of the adapters
  final List<NxWebAdapter> adapters = toObservable([]);

  /// The current adapter.
  @published NxWebAdapter adapter;

  NXWebAdapters.created() : super.created() {
  }

  /// Triggered when the connection is established
  onConnect() {
    _fetchAdapters().then((lst) { adapters.addAll(lst); })
    .then((_) {
      async((_) {

        accordion("#adapters");

        shadowRoot.querySelectorAll("#adapters .item").forEach((e) {
          e.onClick.listen((event) {
            var selected = adapters.firstWhere((a) => a.name == event.target.dataset["adapter"]);
            adapter = (adapter != null && selected.name == adapter.name) ? null : selected;
            shadowRoot.querySelectorAll(".ui.menu .item").forEach((Element e) {
              if (e.dataset["adapter"] == adapter) {
                e.classes.add("active");
              } else {
                e.classes.remove("active");
              }
            });
          });
        });
      });
    });

  }

  _fetchAdapters() {
    if (_adapters.isNotEmpty) {
      return new Future.value(_adapters);
    } else {
      return Future.wait(
        ADAPTER_ENDPOINTS.map((name) {
          var request = NX.newRequest("doc/${name}Adapter.json");

          // Execute the method (does not handle the response)
          return request.method("GET").execute()
          .then((response) {
            var json = JSON.decode(response.body);
            var listing = new swagger.Listing.fromJSON(json);
            _adapters.add(new NxWebAdapter(name, listing));
          })
          .catchError((e) {
            print("Adapter '$name' not available.");
          });
        }))
      .then((_) {
        // sort alphabetically
        _adapters.sort((a, b) => a.name.compareTo(b.name));
        return _adapters;
      });
    }
  }

  // Filters
  get capitalize => new Capitalize();
}
