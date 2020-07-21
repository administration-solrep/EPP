#!/bin/sh
##
## (C) Copyright 2010-2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
##
## All rights reserved. This program and the accompanying materials
## are made available under the terms of the GNU Lesser General Public License
## (LGPL) version 2.1 which accompanies this distribution, and is available at
## http://www.gnu.org/licenses/lgpl.html
##
## This library is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
## Lesser General Public License for more details.
##
## Contributors:
##     Julien Carsique
##
MAX_FD_LIMIT_HELP_URL="http://doc.nuxeo.com/display/KB/java.net.SocketException+Too+many+open+files"

NUXEO_HOME=${NUXEO_HOME:-$(cd $(dirname $0); cd ../nuxeo-distribution-tomcat/target/nuxeo-dm-5.4.2-SNAPSHOT-tomcat\ with\ space; pwd -P)}

cp target/nuxeo-launcher-5.4.2-SNAPSHOT-jar-with-dependencies.jar "$NUXEO_HOME"/bin/nuxeo-launcher.jar
cp ../nuxeo-distribution-resources/src/main/resources/bin/nuxeoctl "$NUXEO_HOME"/bin/
"$NUXEO_HOME"/bin/nuxeoctl $@
