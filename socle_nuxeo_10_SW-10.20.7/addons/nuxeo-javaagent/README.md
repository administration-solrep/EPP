Nuxeo Java Agent
================

Nuxeo Java Agent allows computes object size in memory.

It uses java.lang.instrument.Intrumentation.

How to use
----------

1. Run mvn clean install

2. Copy main/target/nuxeo-javaagent-main-{version}.jar to $NUXEO_HOME/bin

3. Copy bridge/target/nuxeo-javaagent-bridge-{version}.jar to $NUXEO_HOME/nxserver/bundles

4. Copy $JAVA_HOME/lib/tools.jar to $NUXEO_HOME/nxserver/lib

5. Add JAVA_OPTS=$JAVA_OPTS -Djavaagent=nuxeo-javaagent-main-{version}.jar to your nuxeo.conf

In your code, call :
 - AgentLoader.INSTANCE.getSizer().deepSizeOf(object);
 - AgentLoader.INSTANCE.getSizer().sizeOf(object);

## QA results

[![Build Status](https://qa.nuxeo.org/jenkins/buildStatus/icon?job=addons_nuxeo-javaagent-master)](https://qa.nuxeo.org/jenkins/job/addons_nuxeo-javaagent-master/)

# About Nuxeo

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Netflix, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris. More information is available at www.nuxeo.com.
