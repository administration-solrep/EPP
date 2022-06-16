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

/// Nuxeo data types classes.
library doctypes;

/// Document type.
/// A document type is defined by several [Schema]s and [Facet]s.
class Doctype {

  static const NONE = "None!!!";

  String name;
  String parent;
  List<String> facets;
  List<String> schemas;

  Doctype(this.name, {String parent, this.facets, this.schemas}) :
    this.parent = (parent == NONE) ? null : parent;

  toString() => name;
}

/// A field in a [Schema].
class SchemaField {
  String name;
  var type;

  SchemaField._(this.name, this.type);

  factory SchemaField(String name, type) {
    if (type is String) {
      return new SchemaField._(name, type);
    } else if (type is Map) {
      var schema = new Schema.fromJSON(type);
      return new SchemaField._(name, schema);
    }
  }

  bool get isComplex => type is Schema;

  toString() => name;
}

/// A schema represents the names and structure (types) of a set of fields in a document.
class Schema {
  String name, prefix;
  List<SchemaField> fields = [];

  Schema({this.name, this.prefix});

  factory Schema.fromJSON(json) {
    var schema = new Schema(name: json["name"], prefix: json["@prefix"]);
    json["fields"].forEach((k, v) { schema.fields.add(new SchemaField(k, v)); });
    return schema;
  }

  toString() => name;
}

/// A facet describes an aspect of a document that can apply to several document types or document instances.
/// Facets can have zero, one or more [Schema]s associated to them.
class Facet {
  String name;
  List<String> schemas = [];

  Facet(this.name);

  factory Facet.fromJSON(json) {
    var facet = new Facet(json["name"]);
    if (json["schemas"] != null) {
      facet.schemas.addAll(json["schemas"].map((s) => s["name"]));
    }
    return facet;
  }

  toString() => name;
}