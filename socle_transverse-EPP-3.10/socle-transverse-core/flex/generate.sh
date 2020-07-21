#!/bin/sh

java -jar ./jflex-1.4.3.jar ./nxql.flex

java -jar ./java-cup-0.11a.jar -interface -parser Parser ./nxql.cup 


