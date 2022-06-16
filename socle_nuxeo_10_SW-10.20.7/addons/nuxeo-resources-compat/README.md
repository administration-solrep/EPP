# Nuxeo Resources Compat

## About this module

The Resources Compatibility addon provides backward compatibility with web
resources from nuxeo.war (icons, images, JavaScript, XHTML, etc.) that have been removed from the previous LTS
release of Nuxeo Platform.


# Building

    mvn clean install

## Deploying

Install [the Nuxeo Resources Compatibility Marketplace Package](https://connect.nuxeo.com/nuxeo/site/marketplace/package/resources-compat).
Or manually copy the built artifacts into `$NUXEO_HOME/templates/custom/bundles/` and activate the "custom" template.

## QA results

[![Build Status](https://qa.nuxeo.org/jenkins/buildStatus/icon?job=addons_nuxeo-resources-compat-master)](https://qa.nuxeo.org/jenkins/job/addons_nuxeo-resources-compat-master/)

# About Nuxeo

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris. More information is available at www.nuxeo.com.
