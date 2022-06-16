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

library nx_content_enrichers;

import 'package:nuxeo_client/client.dart' as nuxeo;
import 'package:polymer/polymer.dart';
import 'ui_filters.dart';

/// A form bound to a [nuxeo.Request] that allows settings content enrichers.
@CustomTag("nx-content-enrichers")
class NXContentEnrichers extends PolymerElement {

  @published nuxeo.Request request;

  @published ObservableMap contentEnrichers = toObservable({
    "acls": false,
    "thumbnail": false,
    "preview": false,
    "breadcrumb": false,
    "permissions": false,
    "userVisiblePermissions": false,
    "favorites": false,
    "runningWorkflows": false,
    "runnableWorkflows": false,
    "pendingTasks": false
  });

  NXContentEnrichers.created() : super.created() {
    requestChanged();
    contentEnrichers.changes.listen((_) {
      var enrichers = contentEnrichers.keys.where((k) => contentEnrichers[k]);
      if (enrichers.isEmpty) {
        request.headers.remove(nuxeo.HEADER_NX_ENRICHERS);
      } else {
        request.headers[nuxeo.HEADER_NX_ENRICHERS] = enrichers.join(",");
      }
    });
  }

  requestChanged() {
    if (request != null && request.headers != null && request.headers[nuxeo.HEADER_NX_ENRICHERS] != null) {
      request.headers[nuxeo.HEADER_NX_ENRICHERS].split(",").forEach((k) { contentEnrichers[k] = true; });
    }
  }

  // Filters
  get capitalize => new Capitalize();
}
