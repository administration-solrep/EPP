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

/// Helper library to work with browser cookies.
///
library cookie;

import 'dart:html';

class _Cookies {

  /// Returns the cookie stored with the given key,
  operator[] (String key) {

    var cookies = document.cookie != null ? document.cookie.split('; ') : [];

    for (var i = 0, l = cookies.length; i < l; i++) {
      var parts = cookies[i].split('=');
      var name = _decode(parts[0]);

      if (key == name) {
        return parts[1] != null ? _decode(parts[1]) : null;
      }
    }
  }

  /// Set the value of a cookie with the given key.
  void operator[]= (String key, String value) {
    var cookie = Uri.encodeComponent(key) + '=' + Uri.encodeComponent(value);
    document.cookie = cookie;
  }

  /// Removes the cookie with the given key.
  bool remove(String key) {
    if (this[key] != null) {
      // In order to delete a cookie must set the expires date to something in the past.
      document.cookie = Uri.encodeComponent(key) + '=;expires=Thu, 01 Jan 1970 00:00:01 GMT;';
      return true;
    }
    return false;
  }

  String _decode(s) => Uri.decodeComponent(s.replaceAll(r"\+", ' '));
}

var cookies = new _Cookies();