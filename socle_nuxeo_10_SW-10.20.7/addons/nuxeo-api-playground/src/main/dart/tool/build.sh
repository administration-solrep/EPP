#!/bin/bash

# Pub build
pub build

# DocGen
# Get library files
LIBS=$(ls lib/*.dart) 
# Get component library files
COMPONENTS=$(ls lib/components/*.dart)
# Join library files with component library files
R=$(for L in "${LIBS[@]}" "${COMPONENTS[@]}" ; do echo "$L" ; done)

docgen --compile --package-root=./packages --no-include-sdk --no-include-dependent-packages --start-page=nuxeo_api_playground $R

pushd dartdoc-viewer/client/out/web/

# Cleanup packages
# delete the symlinks
find . -name "packages" -type l -delete
mkdir packages
cp -R ../packages/polymer packages
cp -R ../packages/web_components packages
cp -R ../packages/dartdoc_viewer packages
# Remove the unnecessary dart files
find . -name "*.dart" -type f -delete

# Replace the title
sed -i 's/Dart API Reference/Nuxeo API Playground/g' index.html

# Overwrite some resources (logo, favicon, etc..)
cp ../../../../web/favicon.ico static/favicon.ico
cp ../../../../web/favicon.png static/dart-logo.png

popd

# Clone gh-pages
git clone git@github.com:nelsonsilva/nuxeo-api-playground.git gh-pages
pushd gh-pages
git checkout -t origin/gh-pages

# Out with the old in with the new
rm -rf *
# Copy build
cp -r ../build/web/* .
# Copy docs
cp -r ../dartdoc-viewer/client/out/web docs
# Add and commit
git add -A
git commit -m "Update build files"
git push
popd

rm -rf gh-pages