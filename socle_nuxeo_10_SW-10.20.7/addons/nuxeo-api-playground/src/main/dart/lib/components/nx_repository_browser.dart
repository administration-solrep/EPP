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

library nx_repository_browser;

import 'dart:convert';
import 'dart:html';
import 'package:nuxeo_client/browser_client.dart' as nuxeo;
import 'package:polymer/polymer.dart';
import 'nx_web_adapters.dart' show NxWebAdapter;
import 'ui_module.dart';
import 'semantic.dart';
import 'tree/tree.dart';

const String DEFAULT_DOCUMENT_BODY = """
{
  "entity-type": "document",
  "name":"newDoc",
  "type": "File",
  "properties": {
      "dc:title": "A new document",
      "dc:description": "Created via the REST API"
  }
}
""";

/// Model for the [NXRepositoryBrowser] module.
class RepositoryBrowser extends Module {
  String title = "Repository",
         icon = "repository.png",
         description = "Browse the repository you are connected to and discover the content that is inside.",
         action = "Explore",
         tag = NXRepositoryBrowser.TAG;
}

/// The Repository Browser module element.
@CustomTag(NXRepositoryBrowser.TAG)
class NXRepositoryBrowser extends NXModule with SemanticUI {

  static const String TAG = "nx-repository-browser";

  @published String documentId;

  @observable nuxeo.Document document;
  @observable var breadcrumb;
  @observable String thumbnail;

  @published nuxeo.Request request;
  @observable var response;

  @observable NxWebAdapter adapter;

  @observable String body;
  /// The id of the referenced Batch Upload.
  @observable var batch;

  @observable String method = "GET";

  final List errors = toObservable([]);

  NXRepositoryBrowser.created() : super.created() {
  }

  documentIdChanged() {
    method = 'GET';
    body = null;

    if (documentId == null) {
      document = null;
    } else {
      request = NX.doc(documentId);
      request.enrichers = ["breadcrumb", "thumbnail"];

      runRequest();
    }
  }

  adapterChanged() {
    var base = NX.doc(documentId);
    request.uri = base.uri; // reset the request uri
    if (adapter != null) {
      request.adapt(adapter.name);
    }
  }

  runRequest() {
    errors.clear();
    if (adapter != null) {
      adapter.params
      .where((p) => p.isQueryParam && !p.isEmpty)
      .forEach((param) {
        request.param(param.name, param.value);
      });
    }
    var data = (method == 'PUT' || method == 'POST') ? body : null;
    return request.method(method).execute(data)
      .then(_handleResponse)
      .catchError((e) {
        if (e is nuxeo.ClientException) {
          // Store the response
          this.response = e.response;
          errors.add(e.message);
        }
      });
  }

  _handleResponse(response) {
    this.response = response;

    // if current document was deleted remove the node from the tree
    if (method == 'DELETE') {
      (shadowRoot.querySelector("#tree") as Tree).deleteNode(this.documentId);
    }

    // process the response
    String contentType = response.headers["content-type"];
    if (contentType != null && (contentType.startsWith(nuxeo.CTYPE_ENTITY) || contentType.startsWith(nuxeo.CTYPE_JSON))) {
      var json = JSON.decode(response.body);

      // if it's the current document update it
      if (json["entity-type"] == "document") {
        var document = new nuxeo.Document.fromJSON(json);
        if (document.uid == documentId) {
          this.document = document;
        }
      }
    }

    return response;
  }

  documentChanged() {
    _updateBreadcrumb();
    _updateThumbnail();
    async((_) {
      // activate the accordions
      accordion("#adapters");
      accordion("#enrichers");
      accordion("#options");

      // activate the tabs
      shadowRoot.querySelectorAll("#methods .item").forEach((e) {
        e.onClick.listen((event) {
          method = event.target.dataset["method"];
        });
      });
    });
  }

  setDefaultBody() {
    body = DEFAULT_DOCUMENT_BODY;
  }

  copyBody() {
    var json = JSON.decode(response.body);
    body = new JsonEncoder.withIndent(" " * 2).convert(json);
  }

  referenceBatch(Event e) {
    e.preventDefault();
    e.stopImmediatePropagation();
    async((_) {
      modal('.ui.modal');
    });
  }

  methodChanged() {
    errors.clear();
    adapter = null;

    shadowRoot.querySelectorAll("#methods .item").forEach((Element e) {
      if (e.dataset["method"] == method) {
        e.classes.add("active");
      } else {
        e.classes.remove("active");
      }
    });
  }

  _updateBreadcrumb() {
    if (document != null && document.contextParameters != null && document.contextParameters.containsKey("breadcrumb")) {
      this.breadcrumb = document.contextParameters["breadcrumb"]["entries"];
    } else {
      this.breadcrumb = [{"title" : document.title}];
    }
  }

  _updateThumbnail() {
    if (document != null) {
      var thumbnail;
      if (document.contextParameters != null && document.contextParameters.containsKey("thumbnail")) {
          thumbnail = document.contextParameters["thumbnail"]["url"];
          if (connection.token != null) {
            thumbnail += "?token=${connection.token}";
          }
      }
      if (thumbnail == null) {
        thumbnail = document.facets.contains("Folderish") ? "icons/folder_100.png" : "icons/file_100.png";
      }
      this.thumbnail = thumbnail;
    }
  }
}