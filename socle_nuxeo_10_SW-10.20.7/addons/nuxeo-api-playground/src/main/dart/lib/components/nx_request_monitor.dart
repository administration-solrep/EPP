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

library nx_request_monitor;

import 'dart:convert';
import 'dart:js' as js;
import 'dart:html';
import 'dart:typed_data';
import 'ui_module.dart';
import 'nx_connection.dart';
import 'package:polymer/polymer.dart';
import 'package:polymer_expressions/filter.dart';
import 'package:nuxeo_client/client.dart' as nuxeo;
import 'package:nuxeo_client/http.dart' as http;
import 'package:nuxeo_client/http/multipart.dart' as multipart;

/// An element for monitoring requests and responses.
@CustomTag("nx-request-monitor")
class NXRequestMonitor extends NXElement {

  @published nuxeo.BaseRequest request;
  @published var response;

  @observable var body;
  @observable String contentType;

  /// A list of Blobs for downloading the response.
  final List downloads = toObservable([]);

  @observable String currentTab = "response";

  NXRequestMonitor.created() : super.created() {
    requestChanged();
  }

  changeTab(event) {
    _doChangeTab(event.target.dataset["tab"]);
  }

  /// Returns the current request as a **CURL** command.
  @observable
  String get CURLRequest {
    var url;
    var data = request.requestData;
    if (request is nuxeo.OperationRequest) {
      url = (!(request as nuxeo.OperationRequest).hasBatchUpload) ? "${request.uri}/${request.id}" : "${request.uri}/batch/execute";
    } else {
      url = request.uri.toString();
    }
    var method = request.request.method;
    var str = new StringBuffer("curl -X $method '$url'");
    request.headers.forEach((k, v) {
      // write basic auth header at the end
      if (k != NXConnection.NX_AUTHENTICATION_TOKEN) {
        str.write(" -H '$k: $v'");
      }
    });

    if (data != null) {
      if (data is http.MultipartFormData) {
        data.data.forEach((k, blob) {
          // If key is "request" we know this is JSON so we can display it
          // Everything else is considered a file
          var value = (k == "request") ? new String.fromCharCodes(blob.content) : "@${blob.filename}";
          str.write(" -F $k='$value;type=${blob.mimetype}'");
        });
      } else {
        str.write(" -H '${http.HEADER_CONTENT_TYPE}: application/json'");
        str.write(" -d '$data'");
      }
    }

    var username = request.httpClient.username;
    str.write(" -u $username:<PASSWORD>");

    return str.toString();
  }

  requestChanged() {
    if (request != null) {
      request.onRequest.listen((_) { isLoading = true; });
      request.onResponse.listen((_) { isLoading = false; });
    }
    async((_) {
      shadowRoot.querySelectorAll(".ui.menu .item").forEach((e) {
        e.onClick.listen(changeTab);
      });
    });
  }

  set isLoading(bool flag) {
    var classes = shadowRoot.getElementById("loading").classes;
    flag ? classes.add("active") : classes.remove("active");
  }

  responseChanged() {
    if (response == null) {
      body = null;
      contentType = null;
      currentTab = null; // Clear the polymer generated content
      return;
    }

    contentType = response.headers["content-type"];
    if (contentType != null && (contentType.startsWith(nuxeo.CTYPE_ENTITY) || contentType.startsWith(nuxeo.CTYPE_JSON))) {
      if (response.body != null && response.body.isNotEmpty) {
        var json = JSON.decode(response.body);
        body = new JsonEncoder.withIndent(" " * 2).convert(json);
      } else {
        body = response.body;
      }
    } else if (contentType != null && contentType.startsWith("text/plain")) {
      body = response.body;
    } else { //  Handle Blob
      body = response.buffer;
    }

    if (body != null) {
      _doChangeTab("response");
    }
  }

  @ObserveProperty("body contentType")
  updateDownloads() {
    downloads.clear();

    if (body == null || contentType == null) {
      return;
    }

    // Allow downloading the response as a blob
    downloads.add(new http.Blob(
        content: body is ByteBuffer ? (body as ByteBuffer).asUint8List() : body,
        mimetype: (contentType.startsWith("multipart/mixed") ? "multipart/mixed" : contentType)));

    // For multipart also allow downloading each part as a blob
    if (multipart.isMultipartContent(contentType)) {
      var boundary = multipart.getMultipartBoundary(contentType);
      // We only support parsing multipart responsed when the boundary is known beforehand
      if (boundary != null) {
        multipart.parse(new Uint8List.view(response.buffer), boundary).then((blobs) {
          downloads.addAll(blobs);
        });
      }
    }
  }

  final Transformer asBlobUrl = new BlobToURL();

  _highlight() {
    shadowRoot.querySelectorAll('code').forEach((el) {
      js.context['hljs'].callMethod('highlightBlock', [el]);
    });
  }

  _doChangeTab([tab]) {
    currentTab = tab;
    shadowRoot.querySelectorAll(".ui.menu .item").forEach((Element e) {
      if (e.dataset["tab"] == tab) {
        e.classes.add("active");
      } else {
        e.classes.remove("active");
      }
    });
    async((_) {
      // highlight.js doesn't play well with data binding
      var code = shadowRoot.getElementById("code");
      if (code != null) {
        code.innerHtml = (body as String).replaceAll(new RegExp(r'<'),"&lt;");
      }
      _highlight();
    });
  }

}

/// [Transformer] to convert [http.Blob] into a Url
class BlobToURL extends Transformer<String, http.Blob> {
  BlobToURL();
  String forward(http.Blob blob) => Url.createObjectUrlFromBlob(new Blob([blob.content], blob.mimetype));
  http.Blob reverse(String s) => null; // TODO(nfgs)
}