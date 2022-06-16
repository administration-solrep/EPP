# Nuxeo Showcase Content Sample

This addon import a set of showcase content containing office documents, images, videos, ...

## Modules

- nuxeo-showcase-content-importer: Showcase content Importer bundle

## Building all modules

    mvn clean install

## How to Create a New Data Zip File

- Import latest released Version
- Remove old or deprecated documents; without forgetting to clean the trash
- Import new documents
- To lighter the zip export file, cleanup all picture views, transcoded videos, etc. Execute this SQL query using your prefered SQL client:
```
delete from hierarchy where primarytype  = 'view' or primarytype = 'storyboarditem' or primarytype = 'transcodedVideoItem';
```
- Cleanup CoreSession cache
- Make a `Zip Tree Export` from `Workspaces`
- Upload it on Nexus, GAV: `org.nuxeo.ecm.platform:nuxeo-showcase-content-sample-data:NEXT_VERSION`
- Update Marketplace `nuxeo/marketplace-showcase-content-sample` project to use the correct data version.

# About Nuxeo

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris. More information is available at www.nuxeo.com.
