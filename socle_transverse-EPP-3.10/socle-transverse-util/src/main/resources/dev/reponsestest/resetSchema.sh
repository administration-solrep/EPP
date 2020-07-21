#!/bin/sh

USER=REPONSES_TEST
DDLFILE=/opt/reponses-server_inst1/init/oracle/reponses-ddl.sql

(cd /opt/dbmanip/scripts/ && sh reset_schema.sh $USER $DDLFILE)
