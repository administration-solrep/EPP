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

/// Nuxeo API Playground Application.
library app;

import 'dart:html';
import 'package:route_hierarchical/client.dart';
import 'package:logging/logging.dart';
import 'package:logging_handlers/logging_handlers_shared.dart';
import 'package:polymer/polymer.dart';
import 'components/nx_repository_browser.dart';
import 'components/nx_structures_browser.dart';
import 'components/nx_resource_endpoints.dart';
import 'components/nx_command_endpoints.dart';
import 'components/nx_batch_upload.dart';
import 'components/ui_module.dart';
import 'components/nx_connection.dart';

/// The main Application.
@CustomTag("nx-sandbox-app")
class NXSandboxApp extends PolymerElement {

  /// The ID of a [NXConnection] element
  @published String connectionId;

  /// The resolved [NXConnection]
  @observable NXConnection connection;

  /// A map of modules with the url hash as key
  Map<String, Module> modules = {
    "browser": new RepositoryBrowser(),
    "data": new StructuresBrowser(),
    "resources": new ResourceEndpoints(),
    "commands": new CommandEndpoints(),
    "uploads": new BatchUpload()
  };

  /// The current module
  @observable Module module = null;

  /// A client side router
  Router router;


  NXSandboxApp.created() : super.created() {

    // Setup Logging
    Logger.root.level = Level.INFO;
    Logger.root.onRecord.listen(new LogPrintHandler());

    // Setup the root
    router = new Router(useFragment: true);

    // Add a route for each module along with each module's custom subroutes
    modules.forEach((path, Module module) {
      router.root.addRoute(
          name: path,
          preEnter: (_) {
            this.module = modules[path];
          },
          path: '/$path',
          mount: module.mount(path, router));
    });

    // default handler

    router.root.addRoute(name: 'home', defaultRoute: true, path: '/', enter: showHome);

    router.listen();
  }

  /// Navigate to the welcome page.
  void showHome(RouteEvent e) {
    module = null;
  }

  /// Navigate to the home of the selected module
  void navigate(event, detail, target) {
    router.go('${target.dataset['target']}', {});
  }

  /// Called when **connectionId** changes.
  connectionIdChanged() {
    connection = document.querySelector("#$connectionId");
    // Setup the connectionId for the current module if any
    if ($['module'].children.isNotEmpty) {
      ($['module'].children.first as NXModule).connectionId = connectionId;
    }
  }

  /// Called when the current module changes.
  void moduleChanged() {
    $['module'].children..clear();
     if (module != null) {
       var el = module.createElement();
       el.connectionId = connectionId;
       $['module'].children.add(el);
     }
  }

}
