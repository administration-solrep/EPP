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

library nx_request_options;

@MirrorsUsed(targets: 'nuxeo.Request', override: 'nuxeo')
import 'dart:mirrors';

import 'ui_module.dart';
import 'package:nuxeo_client/client.dart' as nuxeo;
import 'package:polymer/polymer.dart';
import 'package:polymer_expressions/filter.dart';

/// A form bound to a [NXConnection] that allows settings global request options.
@CustomTag("nx-request-options")
class NXRequestOptions extends NXElement {

  @published nuxeo.BaseRequest request;

  NXRequestOptions.created() : super.created() {
  }

  onConnect() {
  }

  // Filters
  var inSeconds = new DurationToSeconds();
  var asCSV = new ListToCSV();
}

class DurationToSeconds extends Transformer<String, Duration> {
  DurationToSeconds();
  String forward(Duration d) => d.inSeconds.toString();
  Duration reverse(String s) => (s == null || s.isEmpty) ? new Duration() : new Duration(seconds: int.parse(s));
}

class ListToCSV extends Transformer<String, List> {
  ListToCSV();
  String forward(List l) => l.join(",");
  List reverse(String s) => s.split(",").map((s) => s.trim()).toList();
}
