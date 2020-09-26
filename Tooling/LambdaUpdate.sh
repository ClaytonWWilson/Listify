#!/bin/bash
echo "Updating a Lambda."

REL_SCRIPT_DIR=$(dirname "${BASH_SOURCE[0]}")

source ${REL_SCRIPT_DIR}/VarSetup.sh


aws lambda update-function-code --function-name ${functionName}${method} --zip-file fileb://${jarPath} 1>${DEBUGFILE} 2>${DEBUGFILE}

if [[ $? -ne 0 ]]; then
  echo "Unable to update Lamba" >&2
  exit 1
fi

echo "Update successful."
exit 0
