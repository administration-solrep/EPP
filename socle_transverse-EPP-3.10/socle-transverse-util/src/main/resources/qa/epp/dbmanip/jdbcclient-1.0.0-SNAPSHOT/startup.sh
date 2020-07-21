#!/bin/bash

for f in $(find  ./ -name "*.jar"); do
 CP=$CP:$f
done

if [ -n "$CP" ]; then
  CP="$(echo $CP | sed s/'^://')"
fi

java -cp $CP fr.sword.naiad.tools.jdbcclient.run.simple.SimpleClient "$@"
