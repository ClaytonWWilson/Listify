#!/bin/bash

# Meant to be source'd from other scripts that need these vars.

#constants
APIID=datoh7woc9 #rest-api-id is tied to the apigateway while resource-id seems tied to the specific url extension
ROOTRESOURCEID=6xrzhzidxh #gateway root should have a consistent resource id which will serve as parent for many apis
LAMBDAROLE=arn:aws:iam::569815541706:role/LambdaBasic
LANGUAGE=java11
DEPLOYSTAGE=Development

DEBUGFILE=/dev/null

REL_SCRIPT_DIR=$(dirname "${BASH_SOURCE[0]}")

echo -n "Please enter base function name: "
read functionName
echo -n "Please enter method(GET, POST, etc.): "
read method

jarPath=$(find ${REL_SCRIPT_DIR}/.. -name "${functionName}.jar")
if [[ "$jarPath" == "" ]]; then
  echo "Unable to find file ${functionName}.jar" >&2
  exit 1
fi
functionPath=${jarPath%/${functionName}.jar}