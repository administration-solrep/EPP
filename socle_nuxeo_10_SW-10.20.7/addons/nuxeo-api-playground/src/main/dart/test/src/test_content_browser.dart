part of nuxeo_api_playground.test;

testContentBrowser() => group('Content Explorer', () {
  test('Browse repository', () {
    go("browser").then(expectAsync((_) {

      Element browser = app.shadowRoot.querySelector("nx-repository-browser");
      expect(browser, isNotNull);

      Element tree = browser.shadowRoot.querySelector("nx-tree");
      expect(tree, isNotNull);

      Element treeNode = tree.shadowRoot.querySelector("nx-tree-node");
      expect(treeNode, isNotNull);

      // Wait for the root document to load
      observePath(treeNode, 'doc').then((doc) {
        expect(doc, isNotNull);
      });

      // Wait for the children to load
      observeElement(treeNode.shadowRoot.querySelector(".content")).then((mutations) {
        var docs = treeNode.children;

        // Get /default-domain doc
        var doc = docs.firstWhere((doc) => doc.path == "/default-domain");
        expect(doc, isNotNull);

        // Get DOM nodes
        var children = treeNode.shadowRoot.querySelectorAll("nx-tree-node");
        expect(children.isNotEmpty, true);

        // Get the document's DOM node
        Element child = children[docs.indexOf(doc)];

        // Wait for the document to load
        observePath(child, 'doc').then((doc) {
          expect(doc, isNotNull);

          // Select the document
          child.shadowRoot.querySelector(".label").click();
        });

      });

    }));
  });
});
