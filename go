#!/bin/bash

source neo_setup

NEO4J_STATUS=`$NEO4J_HOME/bin/neo4j status`

if [[ $NEO4J_STATUS == "Neo4j Server is not running" ]]; then
  $NEO4J_HOME/bin/neo4j start
else
  echo "Neo4J already running"
fi

lein ring server-headless
