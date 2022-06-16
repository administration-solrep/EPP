#!/bin/sh

MAVENREPO=/platform/opt/m2/repository

java -jar $MAVENREPO/de/jflex/jflex/1.4.3/jflex-1.4.3.jar nxql.flex

java -jar $MAVENREPO/cup/java-cup/0.11a/java-cup-0.11a.jar -interface -parser Parser nxql.cup
