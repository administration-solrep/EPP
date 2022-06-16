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

/// Library to generate [yUML](http://yuml.me) diagrams
library yuml;

import 'doctypes.dart';

class DiagramStyle {
  static const String BORING = "nofunky";
  static const String PLAIN = "plain";
}

/// A diagram generator for [DocType]s, [Facet]s and [Schema]s using [yUML](http://yuml.me)
class DiagramGenerator {

  static const String STYLE = DiagramStyle.BORING;
  static const int SCALE = 80;
  static const String DIRECTION = "TB";
  static const String KIND = "class";
  static const String URL = "http://yuml.me/diagram/$STYLE;scale:$SCALE;dir:$DIRECTION;/$KIND/";

  Map<String, Doctype> doctypes = {};
  Map<String, Schema> schemas = {};
  Map<String, Facet> facets = {};

  DiagramGenerator({
    Iterable<Doctype> doctypes,
    Iterable<Schema> schemas,
    Iterable<Facet> facets}) {
    doctypes.forEach((d){ this.doctypes[d.name] = d; });
    schemas.forEach((s){ this.schemas[s.name] = s; });
    facets.forEach((f){ this.facets[f.name] = f; });
  }

  /// Returns a URL for a diagram for the given [Doctype].
  String generateForDoctype(String name) => URL + _umlForDoctype(name).join(",");
  /// Returns a URL for a diagram for the given [Facet].
  String generateForFacet(String name) => URL + _umlForFacet(name).join(",");
  /// Returns a URL for a diagram for the given [Schema].
  String generateForSchema(String name) => URL + _umlForSchema(name).join(",");

  List _umlForDoctype(String name) {
    var doctype = doctypes[name];

    var uml = ["[${doctype.name}]"];

    var inheritedFacets = [],
        inheritedSchemas = [];

    // Inheritance
    // [Parent]^-[Child]
    if (doctype.parent != null) {
      uml.add("[${doctype.parent}]^-[${doctype.name}]");
      uml.addAll(_umlForDoctype(doctype.parent));

      // Get the list of inherited facets and schemas
      inheritedFacets = doctypes[doctype.parent].facets;
      inheritedSchemas = doctypes[doctype.parent].schemas;
    }

    // Facets
    // [Doctype]-.->[Facet]
    doctype.facets
    .where((f) => !inheritedFacets.contains(f))
    .forEach((f) {
      uml.add("[${doctype.name}]-.->[<<${f}>>]");
      uml.addAll(_umlForFacet(f));
    });

    var facetSchemas = doctype.facets.fold([], (s, f) => s..addAll(facets[f].schemas));

    // Schemas
    // [Doctype or Facet]++->[Schema]
    doctype.schemas
    .where((s) => !inheritedSchemas.contains(s) && !facetSchemas.contains(s))
    .forEach((s) {
      var schema = schemas[s];
      uml.add("[${doctype.name}]++->[${schema.prefix}:${schema.name}]");
      uml.addAll(_umlForSchema(s));
    });

    return uml;
  }

  List _umlForFacet(String name) {
    var facet = facets[name];
    var uml = ["[<<${facet.name}>>]"];
    // Schemas
    // [Doctype or Facet]++->[Schema]
    facet.schemas
    .forEach((s) {
      var schema = schemas[s];
      uml.add("[<<${facet.name}>>]++->[${schema.prefix}:${schema.name}]");
      uml.addAll(_umlForSchema(s));
    });
    return uml;
  }

  List _umlForSchema(String name) {
    var schema = schemas[name];
    var fields = schema.fields.map((f) => f.name).join(";");
    return ["[${schema.prefix}:${schema.name}|$fields]"];
  }
}