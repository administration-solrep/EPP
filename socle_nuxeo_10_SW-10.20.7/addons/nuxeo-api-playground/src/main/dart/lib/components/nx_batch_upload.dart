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

library nx_batch_upload;

import 'dart:html';
import 'dart:math' as Math;

import 'package:polymer/polymer.dart';

import 'nx_batch.dart';
import 'nx_batch_reference.dart';
import 'ui_module.dart';
import 'semantic.dart';

/// Model for the [NXBatchUpload] module.
class BatchUpload extends Module {
  String title = "Batch Upload",
         icon = "batch_upload.png",
         description = "Create batches by uploading any number of files. These batches can later be referenced in Resource endpoints methods by clicking on the “Reference a batch” button",
         action = "Upload",
         tag = NXBatchUpload.TAG;
}

/// The Batch Upload module element.
@CustomTag(NXBatchUpload.TAG)
class NXBatchUpload extends NXModule with SemanticUI {

  static const String TAG = "nx-batch-upload";

  factory NXBatchUpload() => new Element.tag(TAG);

  NXBatchUpload.created() : super.created() {
  }

  domReady() {
    _newBatch();
  }

  onUpload(Event event) {
    ($["batches"] as NXBatchReference).addBatch((event.target as NXBatch).batchId);
    _newBatch();
  }

  void _newBatch() {
    var batch = new Element.tag(NXBatch.TAG) as NXBatch
    ..connectionId = connectionId
    ..on[NXBatch.UPLOAD_EVENT].listen(onUpload);

    $["batch"]
    ..nodes.clear()
    ..append(batch);
  }
}