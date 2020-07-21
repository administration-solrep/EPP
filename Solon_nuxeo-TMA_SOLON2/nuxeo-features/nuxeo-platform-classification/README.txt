===========================
NUXEO CLASSIFICATION README
===========================

Nuxeo Classification is an addon for Nuxeo EP.
It provides two new Document Types, ClassificationRoot and
ClassificationFolder, which let you classify documents in it.

You can create arborescence of folder in which to fill documents.
When a document is filled, a symlink to this document is created
in the classification folder (we do not copy/snapshot the document)

Install
=======
To install classification addon on nuxeo, build and copy all packages
in nuxeo.ear/system/ or simply run 'ant deploy'.

Usage for a custom project
==========================

Make a document Classifiable
----------------------------

A document is classifiable if you have registered a contribution to the
ClassificationService as follow:

<extension target="org.nuxeo.ecm.classification.core.ClassificationService"
  point="types">
  <classifiable type="File" />
</extension>


The classify button will then be enabled for the File Documents.

To unclassify a document, go on the Content tab of your Classification
Folder, select the document and just click on the unclassify button.


Create a Classification Document Type
-------------------------------------

To create a new Document Type with classification features, just add the
classification schema to its core definition.


TODO
====

- fix hack in
  ClassificationActionsBean.cancelCurrentSelectionClassificationForm

- Use dashboard_classification_roots.xhtml
