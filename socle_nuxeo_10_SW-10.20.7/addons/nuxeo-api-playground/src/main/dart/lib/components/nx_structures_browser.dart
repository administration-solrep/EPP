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

library nx_structures_browser;

import 'dart:async';
import 'dart:convert' show JSON;
import 'dart:html';
import 'package:polymer/polymer.dart';
import '../doctypes.dart' as nuxeo;
import 'ui_filters.dart';
import 'ui_module.dart';
import 'nx_connection.dart';
import '../yuml.dart' as yuml;
import 'semantic.dart';

/// Model for the [NXStructuresBrowser] module.
class StructuresBrowser extends Module {
  String title = "Data Structures",
         icon = "data_structure.png",
         description = "Browse all the definitions of types, schemas (with their properties) and facets that are configured on the Nuxeo Platform server you are connected to.",
         action = "Browse",
         tag = NXStructuresBrowser.TAG;

  @observable var selectedType;
  @observable var selectedId;

  @override
  void setupRoutes(Route route) {
    route.addRoute(
      name: 'view',
      path: '/:type/:id',
      enter: (e) {
       selectedType = e.parameters['type'];
       selectedId = e.parameters['id'];
    });
  }
}

/// The Data Structures Browser module element.
@CustomTag(NXStructuresBrowser.TAG)
class NXStructuresBrowser extends NXModule with SemanticUI, SearchFilter {

  static const String TAG = "nx-structures-browser";

  /// A map with a list of [nuxeo.Doctypes]s, [nuxeo.Facet]s and [nuxeo.Schema]s.
  final Map<String, List> items = toObservable({
    "schemas": [],
    "facets": [],
    "types": []});

  // SearchFilter
  @observable String searchTerm = '';
  @observable String searchFilter = '';

  List<nuxeo.Schema> get schemas => items["schemas"];
  List<nuxeo.Facet> get facets => items["facets"];
  List<nuxeo.Doctype> get doctypes => items["types"];

  /// The selected item.
  @observable var selectedItem;
  @observable var selectedId;
  @observable var selectedType;

  factory NXStructuresBrowser() => new Element.tag(TAG);

  yuml.DiagramGenerator _diagram;

  NXStructuresBrowser.created() : super.created() {
  }

  /// The URL for the current item's diagram.
  @observable String currentItemDiagram;

  @override
  onConnect() {

    Future.wait([

    // Schemas
    NX.config.schemas().then((response) {
      schemas
        ..clear()
        ..addAll(
            JSON.decode(response.body).map((s)
                => new nuxeo.Schema.fromJSON(s)));
    }),

    // Facets
    NX.config.facets().then((response) {
      facets
      ..clear()
      ..addAll(
          JSON.decode(response.body).map((s)
              => new nuxeo.Facet.fromJSON(s)));
    }),

    // Doctypes
    NX.config.types().then((response) {
      doctypes.clear();

      JSON.decode(response.body)["doctypes"].forEach((k, v) {
        doctypes.add(new nuxeo.Doctype(k,
            parent: v["parent"],
            schemas: v["schemas"],
            facets: v["facets"]));
      });
    })])

    .then((_) {

      async((_) { // Setup the accordion
        accordion(".ui.accordion");
      });

      // Setup the diagram generator
      _diagram = new yuml.DiagramGenerator(schemas: schemas, facets: facets, doctypes: doctypes);

      // Check for a selected item
      updateSelection();
    })
    .catchError((e) {
      connection.alerts.add(
        new Alert.error("Please check the Nuxeo URL and try connecting again.")
      );
      return false;
    });

  }

  @ObserveProperty("module.selectedId module.selectedType")
  void updateSelection() {
    selectedItem = null;
    selectedType = (module as StructuresBrowser).selectedType;
    var selectedId = (module as StructuresBrowser).selectedId;

    if (selectedId != null && items[selectedType].isNotEmpty) {
      selectedItem = items[selectedType].where((d) => d.name == selectedId).first;
    }
    _updateDiagram();
  }

  bool isDoctype(item) => item != null && (item is nuxeo.Doctype);
  bool isFacet(item) => item != null && (item is nuxeo.Facet);
  bool isSchema(item) => item != null && (item is nuxeo.Schema);

  void _updateDiagram() {
    currentItemDiagram = null;
    if (_diagram != null && selectedItem != null) {
      // Update the UI
      if (isDoctype(selectedItem)) {
        currentItemDiagram = _diagram.generateForDoctype(selectedItem.name);
      } else if (isFacet(selectedItem)) {
        currentItemDiagram = _diagram.generateForFacet(selectedItem.name);
      } else if (isSchema(selectedItem)) {
        currentItemDiagram = _diagram.generateForSchema(selectedItem.name);
      }
    }
  }

  /// Open a window with the Diagram of the selected item.
  showDiagram() {
    var options = "top=0,left=0,width=${window.screen.width},height=${window.screen.height}";
    window.open(currentItemDiagram, selectedItem.name);
  }

  searchFilterChanged() {
    async((_) { accordion(".ui.accordion"); });
  }

  goRoot() {
    (module as StructuresBrowser).selectedType = (module as StructuresBrowser).selectedId = selectedItem = null;
    super.goRoot();
  }


  select(event, detail, target) {
    // <type>-<id>
    var id = target.dataset["id"].split("-");
    router.go("$path.view", {"type": id[0], "id": id[1]});
  }

  get capitalize => new Capitalize();
}