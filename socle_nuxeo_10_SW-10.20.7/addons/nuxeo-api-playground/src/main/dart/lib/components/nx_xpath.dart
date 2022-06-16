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

library nx_xpath;

import 'dart:convert' show JSON;
import 'dart:html';
import 'package:polymer/polymer.dart';
import 'ui_filters.dart';
import 'ui_module.dart';
import '../doctypes.dart' as nuxeo;

/// Element to render an autocomplete list of all [nuxeo.Schema]'s fields.
@CustomTag("nx-xpath")
class NXXPath extends NXElement with SearchFilter {

  final List<String> results = toObservable([]);

  int keyboardSelect = -1;
  bool skipSearch = false;

  final List fields = toObservable([]);

  @published bool required;

  @published String value;

  // SearchFilter
  @observable String searchTerm = '';
  @observable String searchFilter = '';

  NXXPath.created() : super.created() {
  }

  get validity => ($["search"] as InputElement).validity;

  @override
  onConnect() {
    // Schemas
    NX.config.schemas().then((response) {
      fields.clear();
      JSON.decode(response.body).forEach((s) {
        var schema = new nuxeo.Schema.fromJSON(s);
        schema.fields.forEach((f) {
          fields.add((schema.prefix.isEmpty) ? f.name : "${schema.prefix}:${f.name}");
        });
      });
    });
  }

  searchFilterChanged() {
    value = searchFilter;
    if (skipSearch) {
      skipSearch = false;
      return;
    }
    results
    ..clear()
    ..addAll(filter(searchFilter)(fields));
  }

  void select(event, detail, target) {
    searchTerm = target.dataset["field"];
    _reset();
    skipSearch = true;
  }

  keyup(KeyboardEvent e, var detail, Node target) {
    switch (new KeyEvent.wrap(e).keyCode) {
      case KeyCode.ESC:
        _clear();
        break;
      case KeyCode.UP:
        _moveUp();
        break;
      case KeyCode.DOWN:
        _moveDown();
        break;
      case KeyCode.ENTER:
        _select();
        break;
    }
  }

  _moveDown() {
    List<Element> lis = _items;
    if (keyboardSelect >= 0) lis[keyboardSelect].classes.remove('selecting');
    keyboardSelect = ++keyboardSelect == lis.length ? 0 : keyboardSelect;
    lis[keyboardSelect].classes.add('selecting');
  }

  _moveUp() {
    List<Element> lis = _items;
    if (keyboardSelect >= 0) lis[keyboardSelect].classes.remove('selecting');
    if (keyboardSelect == -1) keyboardSelect = lis.length;
    keyboardSelect = --keyboardSelect == -1 ? lis.length-1 : keyboardSelect;
    lis[keyboardSelect].classes.add('selecting');
  }

  _clear() {
    _reset();
    searchTerm = '';
    skipSearch = true;
  }

  _select() {
    searchTerm = _items[keyboardSelect].dataset["field"];
    skipSearch = true;
    _reset();
  }

  _reset() {
    keyboardSelect = -1;
    results.clear();
  }

  List<Element> get _items => shadowRoot.querySelectorAll('.item');
}