#!/bin/sh

EXTLIBDIR=
JDBCCLIENTDIR=

REPONSESSERVERDIR=
JDBCCLIENTARCHIVE=
JDBCCLIENTURL=

. ./config.inc


if [ ! -d $EXTLIBDIR ]; then
  mkdir -p $EXTLIBDIR
fi

ORACLEJAR=ojdbc6.jar
if [ ! -f $EXTLIBDIR/$ORACLEJAR ]; then
  cp $REPONSESSERVERDIR/lib/$ORACLEJAR $EXTLIBDIR
fi

if [ ! -d $JDBCCLIENTDIR ]; then

  if [ ! -f ../$JDBCCLIENTARCHIVE ]; then
    wget $JDBCCLIENTURL -O ../$JDBCCLIENTARCHIVE
  fi

  ( cd .. && unzip $JDBCCLIENTARCHIVE )

fi


