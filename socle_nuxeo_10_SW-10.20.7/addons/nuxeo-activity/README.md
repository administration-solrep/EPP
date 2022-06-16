# Nuxeo Activity

This addon provides a service to store and retrieve activities done in Nuxeo.

# Building

    mvn clean install

## Deploying

Nuxeo Activity relies on a Datasource `nxactivities` which is not defined in a default distribution.
The easiest way to add it is to use the provided `activity` template. You need to copy the `templates/activity` folder into your Nuxeo instance:

    cp -r templates/activity $NUXEO_HOME/templates/

Copy the built artifacts into `$NUXEO_HOME/templates/activity/bundles/`.

Edit the `bin/nuxeo.conf` file to activate the `activity` template:

    nuxeo.templates=default,activity

Restart the Nuxeo instance.

## QA results

[![Build Status](https://qa.nuxeo.org/jenkins/buildStatus/icon?job=addons_nuxeo-activity-master)](https://qa.nuxeo.org/jenkins/job/addons_nuxeo-activity-master/)


## About Nuxeo

Nuxeo provides a modular, extensible Java-based [open source software platform for enterprise content management] [1] and packaged applications for [document management] [2], [digital asset management] [3] and [case management] [4]. Designed by developers for developers, the Nuxeo platform offers a modern architecture, a powerful plug-in model and extensive packaging capabilities for building content applications.

[1]: http://www.nuxeo.com/en/products/ep
[2]: http://www.nuxeo.com/en/products/document-management
[3]: http://www.nuxeo.com/en/products/dam
[4]: http://www.nuxeo.com/en/products/case-management

More information on: <http://www.nuxeo.com/>


