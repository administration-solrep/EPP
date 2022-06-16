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

/// A library to parse [Swagger](http://swagger.wordnik.com/) listings.
library swagger;

/// The API Declaration provides information about an API exposed on a resource.
class Listing {
  String apiVersion, basePath, resourcePath;
  List<Resource> resources;

  Listing();

  factory Listing.fromJSON(Map json) =>
      new Listing()
      ..apiVersion = json["apiVersion"]
      ..basePath = json["basePath"]
      ..resourcePath = json["resourcePath"]
      ..resources = json["apis"].map((j) => new Resource.fromJSON(j)).toList();
}

/// The API Object describes one or more operations on a single path.
class Resource {

  String path,
         description;
  List<Operation> operations;

  Resource();

  factory Resource.fromJSON(Map json) =>
    new Resource()
    ..path = json["path"]
    ..description = json["description"]
    ..operations = json["operations"].map((j) => new Operation.fromJSON(j)).toList();
}

/// The Operation object describes a single operation on a path.
class Operation {
  String method,
         name,
         type,
         summary,
         notes;
  List<Parameter> parameters;

  Operation._();

  factory Operation.fromJSON(Map json) =>
      new Operation._()
      ..method = json["method"]
      ..name = json["nickname"]
      ..type = json["type"]
      ..summary = json["summary"]
      ..notes = json["notes"]
      ..parameters = json.containsKey("parameters") ? json["parameters"].map((j) => new Parameter.fromJSON(j)).toList() : [];
}

/// The Parameter Object describes a single parameter to be sent in an operation.
class Parameter {

  String name,
         type,
         description,
         dataType;
  bool required;

  Parameter._();

  factory Parameter.fromJSON(Map json) =>
      new Parameter._()
      ..name = json["name"]
      ..type = json["paramType"]
      ..dataType = json["dataType"]
      ..description = json["description"]
      ..required = json["required"] == "true";

  bool get isPathParam => type == "path";
  bool get isBodyParam => type == "body";
  bool get isHeaderParam => type == "header";
  bool get isQueryParam => type == "query";
}