library nx_tree_node;

import 'dart:async';
import 'dart:html';
import '../semantic.dart';
import '../ui_module.dart';
import 'package:polymer/polymer.dart';
import 'package:nuxeo_client/browser_client.dart' as nuxeo;

const GET_CHILDREN = "Document.GetChildren";
const FETCH_DOCUMENT = "Document.Fetch";

const DEFAULT_ICON = "icons/folder.gif";
const DOCTYPE_ICONS = const {
  "File": "icons/file.gif",
  "Folder": "icons/folder.gif",
  "Domain": "icons/domain.gif",
  "Workspace": "icons/workspace.gif",
  "Note": "icons/note.gif"
};

@CustomTag("nx-tree-node")
class TreeNode extends NXElement with SemanticUI {

  static const String SELECTED_EVENT = "nodeselected";

  @published String docId;
  @published nuxeo.Document doc;
  @published List docs = toObservable([]);

  TreeNode.created() : super.created() {
  }

  attached() {
    super.attached();
    shadowRoot.querySelector(".icon").classes.add("expand");
    accordion(".ui.accordion");
  }

  @published bool multiple = false;

  @observable get icon {
    if (doc == null) {
      return DEFAULT_ICON;
    }
    var icon = DOCTYPE_ICONS[doc.type];
    if (icon == null) {
      icon = DEFAULT_ICON;
    }
    return icon;
  }

  @observable bool isFolderish = true;

  @override
  onConnect() {
    if (docId != null) {
      _fetchDoc();
    }
  }

  docIdChanged() {
    if (isConnected) {
      _fetchDoc();
    }
  }

  docChanged(oldValue, newValue) {
    notifyPropertyChange(#icon, null, this.icon);
    isFolderish = (doc != null) && doc.facets.contains("Folderish");
  }

  Future _fetchChildren() {
    if (docs.isNotEmpty) {
      return new Future.value(docs);
    }
    return NX.doc(docId).children().fetch().then((docs) =>
      this.docs..clear()..addAll(docs)
    );
  }

  void expand() {
    _fetchChildren().then((_) {
      // Toggle the accordion
      shadowRoot.querySelector(".ui.accordion .title").classes.add("active");
      shadowRoot.querySelector(".ui.accordion .content").classes.add("active");
      // Toggle the icon
      shadowRoot.querySelector(".icon").classes..remove("expand")..add("collapse");
    });
  }

  toggle(event, detail, target) {
    _fetchChildren().then((_) {
      var classes = shadowRoot.querySelector(".icon").classes;

      // Currently expanded - the according will collapse
      if (target.classes.contains("collapse")) {
        classes..remove("collapse")..add("expand");
        return;
      }

      // Currently collapsed - expand and add the collapse icon
      classes..remove("expand")..add("collapse");
    });
  }

  set selected(bool flag) {
    var classes = shadowRoot.querySelector(".label").classes;
    (flag)? classes.add("selected") : classes.remove("selected");
  }

  bool get selected => shadowRoot.querySelector(".label").classes.contains("selected");

  void toggleSelection(MouseEvent event, detail, target) {
    event.preventDefault();
    event.stopImmediatePropagation();
    shadowRoot.querySelector(".label").classes.toggle("selected");
    dispatchEvent(new CustomEvent(SELECTED_EVENT, detail: doc));
  }

  _fetchDoc() {
    NX.doc(docId).fetch().then((doc) {
      this.doc = doc;
      // TODO(nfgs) - remove this hack - the goal is to have a nice title for the root document
      if (doc.path == '/') doc.title = '/';
    });
  }

}