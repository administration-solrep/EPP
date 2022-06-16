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

library nx_operation;

import 'dart:convert' show JSON;

import 'package:polymer/polymer.dart';
import 'package:nuxeo_client/client.dart' as nuxeo;

import 'nx_batch.dart';
import 'semantic.dart';
import 'ui_module.dart';

/// Wrapper for [nuxeo.OperationMethod]
class NxOperationMethod extends Observable {
  String name = "run";
  @observable var input;
  @observable var inputType;
  @observable var outputType;

  NxOperationMethod(nuxeo.OperationMethod method) {
    inputType = method.input;
    outputType = method.output;
  }
}

/// Wrapper for [nuxeo.OperationParam]
class NxOperationParamValue implements Comparable<NxOperationParamValue> {

  nuxeo.OperationParam _param;
  @observable var value;

  NxOperationParamValue(this._param) {
    value = (_param.values != null && _param.values.isNotEmpty) ? _param.values.first : null;
  }

  String get name => _param.name;

  String get dataType => _param.type;

  String get widget => (_param.name == 'xpath') ? "xpath" : (_param.widget == null) ? "text" : _param.widget;

  bool get required => _param.isRequired;

  bool get isValid => !required || (required && value != null);

  List get options => _param.values;

  int compareTo(NxOperationParamValue other) => _param.order - other._param.order;
}

/// An element that provides bindable parameters to execute a [nuxeo.Operation].
@CustomTag("nx-operation")
class NXOperation extends NXElement with SemanticUI {

  /// The id of the [nuxeo.Operation] to bind to.
  @published String opid;
  nuxeo.Operation _op;

  // The request and reponse are for monitoring purposes
  @observable nuxeo.OperationRequest opRequest;
  @observable var opResponse;

  @observable String name;
  @observable String label;
  @observable String description;

  /// A list of parameters available in the current operation.
  final List<NxOperationParamValue> params = toObservable([]);

  /// Batch reference
  @observable NXBatch batch;

  /// A list of methods available in the current operation.
  final List<NxOperationMethod> methods = toObservable([]);
  @observable NxOperationMethod method;

  /// A list of errors found during the executation and/or validation of the operation.
  final List errors = toObservable([]);

  NXOperation.created() : super.created() {
  }

  domReady() {
    accordion(".ui.accordion");

    /* Update the according when the operations change
     onDomChange('#input', () {
       jQuery(".ui.dropdown").callMethod('dropdown', []);
     });*/
  }

  void opidChanged() {
    if (isConnected) {
      _fetchOp();
    }
  }

  @override
  onConnect() {
    _fetchOp();
  }

  void _fetchOp() {

    // Check if we have already fetched the current op
    if (opid == null || (_op != null && _op.id == opid)) {
      return;
    }

    // Clear the result area
    var results = shadowRoot.querySelector("#result");
    if (results != null) {
      results.children.clear();
    }

    // Clear the errors
    errors.clear();
    // Reset the selected method
    method = null;
    opRequest = opResponse = null;
    // Clear the batch
    batch = null;

    NX.registry.then((registry) {
      _op = registry[opid];
      name = _op.id;
      label = _op.label;
      description = _op.description;

      // Setup the parameters
      params.clear();
      params.addAll(_op.params.values.map((p) => new NxOperationParamValue(p)));
      params.sort();

      // Setup the parameters
      methods.clear();
      methods.addAll(_op.methods.map((m) => new NxOperationMethod(m)));
      method = methods.first;

      // Prepare the op request
      opRequest = NX.op(_op.id);
    });
  }

  void callOp(evt) {
    // don't submit the form
    evt.stopImmediatePropagation();
    evt.preventDefault();

    var valid = shadowRoot.querySelectorAll("nx-widget").every((_) => _.valid);

    if (!valid) {
      return;
    }

    // Clear previous results and errors
    errors.clear();

    // Setup the parameters
    var opParams = {};
    params
    .where((p) => p.value != null) // only those that are not null)
    .forEach((param) {
      opParams[param.name] = param.value;
    });

    // Set the op parameters
    opRequest.params(opParams);

    // Set the input
    // Check for a batch reference
    if (batch == null) {
      opRequest.input(method.input);
    } else {
      opRequest.uploader.batchId = batch.batchId;
    }

    // Call the op
    opRequest.execute()
    .then((res) { opResponse = res; })
    .catchError((e) {
      if (e is nuxeo.ClientException) {

        // Store the response
        opResponse = e.response;

        var message = e.message;
        // Check if this is JSON and we can extract just the message
        try {
          var json = JSON.decode(e.response.body);
          if (json["message"] != null) {
            message = json["message"];
          }
        } on FormatException catch(e) {

        }
        errors.add(message);
      }
    });
  }

  clearBatch(e) {
    e.preventDefault();
    e.stopImmediatePropagation();
    batch = null;
  }

  referenceBatch(e) {
    e.preventDefault();
    e.stopImmediatePropagation();
    async((_) {
      modal('.ui.modal');
    });
  }
}