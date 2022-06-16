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
library module;

import 'dart:html';
import 'package:polymer/polymer.dart';
import 'package:nuxeo_client/browser_client.dart' as nuxeo;
import 'package:route_hierarchical/client.dart';
export 'package:route_hierarchical/client.dart';
import 'nx_connection.dart';

/// An element which requires a Nuxeo connection
abstract class NXElement extends PolymerElement {

  /// The id of a [NXConnection] element
  @published String connectionId;

  @observable NXConnection connection;

  NXElement.created() : super.created() {
  }

  /// Triggered when ID of the [NXConnection] changes
  connectionIdChanged() {
    connection = document.querySelector("#$connectionId");
    _trigger(connection.isConnected);
    new PathObserver(connection, 'isConnected').open(_trigger);
  }

  _trigger(bool connected) {
    (connected) ? onConnect() : onDisconnect();
  }

  /// Triggered when the connection is established
  onConnect() {}

  /// Triggered when disconnecting from the server
  onDisconnect() {}

  nuxeo.Client get NX => connection.NX;

  @observable
  bool get isConnected => connection != null && connection.isConnected;
}

/// [Module] acts as the model for a given [NXModule]
/// There is a single instance of [Module] but multiple [NXModule] instances
/// Data stored in [Module] will persist when creating/detroying [NXModule] elements.
abstract class Module extends Observable {
  /// Basic information for each Playground Module
  String title, icon, description, link, action;
  /// The tag name for the module's element (see [NXModule])
  String tag;

  String path;
  var router;

  /// Create the DOM element for this module
  NXModule createElement() {
    var moduleEl = new Element.tag(tag);
    (moduleEl as NXModule).module = this;
    return moduleEl;
  }

  // Store the root router and return the mountFn
  mount(String path, Router router) {
    this.path = path;
    this.router = router;
    return setupRoutes;
  }

  void setupRoutes(Route route) {}
}

/// Base class for API Playground modules.
abstract class NXModule extends NXElement {

  /// [Module] acts as the model for a [NXModule]
  Module _module;

  NXModule.created() : super.created();

  get module => _module;
  set module(Module module) {
    _module = module;
    // Notify property changes
    notifyPropertyChange(#title, title, null);
    notifyPropertyChange(#icon, icon, null);
    notifyPropertyChange(#description, description, null);
    notifyPropertyChange(#link, link, null);
    notifyPropertyChange(#action, action, null);
  }

  // getters for module info
  get title => (module != null) ? module.title : "";
  get icon => (module != null) ? module.icon : "";
  get description => (module != null) ? module.description : "";
  get link => (module != null) ? module.link : "";
  get action => (module != null) ? module.action : "";

  get path => _module.path;
  get router => _module.router;

  navigate(event, detail, target) {
     router.go("${target.dataset["path"]}", {});
  }

  goHome() {
    router.go("home", {});
  }

  goRoot() {
    router.go(path, {});
  }

}