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

library nx_command_endpoints;

import 'package:polymer/polymer.dart';
import 'package:nuxeo_client/browser_client.dart' as nuxeo;
import 'package:polymer_expressions/filter.dart' show Transformer;
import 'nx_connection.dart';
import 'ui_filters.dart';
import 'ui_module.dart';
import 'semantic.dart';

/// Model for the [NXCommandEndpoints] module.
class CommandEndpoints extends Module {
  String title = "Command Endpoint",
         icon = "command_endpoints.png",
         description = "Browse the list of Automation operations and chains deployed on the server you are connected to. Try some automation calls, filling expected parameters and input.",
         action = "Discover",
         tag = NXCommandEndpoints.TAG;

  /// The current selected [nuxeo.Operation] key.
  @observable String selectedOp = null;

  @override
  void setupRoutes(Route route) {
    route.addRoute(
        name: 'op',
        path: '/:opId',
        defaultRoute: true,
        enter: (e) {
          selectedOp = e.parameters["opId"];
        }
    );
  }
}

/// The Command Endpoints module element.
@CustomTag(NXCommandEndpoints.TAG)
class NXCommandEndpoints extends NXModule with SearchFilter, SemanticUI {

  static const String TAG = "nx-command-endpoints";

  /// Map of operations grouped by category
  final Map<String, List<nuxeo.Operation>> operations = toObservable({});

/// The current selected [nuxeo.Operation] key.
  @observable String selectedOp = null;

  // SearchFilter
  @observable String searchTerm = '';
  @observable String searchFilter = '';

  // Tracing
  @observable bool canManageTraces = false;
  @observable bool tracesEnabled = false;

  NXCommandEndpoints.created() : super.created() {
  }

  @override
  onConnect() {
    operations.clear();
    NX.registry // Get the op registry
    .then((nuxeo.OperationRegistry registry) {        // Get the ops
      registry.ops.forEach((name, nuxeo.Operation op) {
        var ops = operations.putIfAbsent(op.category,() => toObservable([]));
        ops.add(op);
      });

      async((_) { accordion(".ui.accordion"); });

      notifyPropertyChange(#categories, null, categories);
    })
    .catchError((e) {
      connection.alerts.add(
        new Alert.error("Failed to retrieve operations.")
      );
      return false;
    });
  }

  @ObserveProperty("module.selectedOp")
  updateOperation() {
    selectedOp = (module != null) ? (module as CommandEndpoints).selectedOp : null;
  }

  /// Returns filtered categories with at least one operation.
  get categories => operations.keys.where((c) => filter(searchFilter)(operations[c]).isNotEmpty);

  @override
  String labelFor(item) => item.label;

  /// Called when the searchFilter changes
  searchFilterChanged() {
    notifyPropertyChange(#categories, null, categories);
    async((_) { accordion(".ui.accordion"); });
  }

  selectOperation(event, detail, target) {
    router.go("$path.op", {"opId": target.dataset["operation-id"]});
  }

  // Filters and transformers can be referenced as fields.
  final Transformer asID = new StringToID();
}