#!/bin/bash
#Base script from: https://github.com/NMerz/DoctorsNote/blob/master/AWS%20Setup/Lambda-GatewayInitialization.sh

#NOTE: This has been tested and works; however, the apigateway does not properly show as a trigger in AWS's web UI
#NOTE2: This assumes that the root Gateway and Lambda role have been set up previously (one-time setup) and their values are set in VarSetup.sh

echo "Creating a Gateway/Lambda pair."

REL_SCRIPT_DIR=$(dirname "${BASH_SOURCE[0]}")

source ${REL_SCRIPT_DIR}/VarSetup.sh


RAWLAMBDA=$(aws lambda create-function --function-name ${functionName}${method} --zip-file fileb://${jarPath} --runtime ${LANGUAGE} --role ${LAMBDAROLE} --handler ${functionName}${method} 2>${DEBUGFILE})


if [[ $? -ne 0 ]]; then
  echo "Unable to create Lamba" >&2
  exit 1
fi

LAMBDAARN=$(echo $RAWLAMBDA | head -n 3 | tail -n 1 | cut -d \" -f 8)

echo ${LAMBDAARN} > ${DEBUGFILE}

RAWRESOURCEID=$(aws apigateway create-resource --rest-api-id ${APIID} --parent-id ${ROOTRESOURCEID} --path-part ${functionName} 2>${DEBUGFILE})

if [[ $? -ne 0 ]]; then
  echo "Unable to create resource." > ${DEBUGFILE}
  RAWRESOURCEID=$(aws apigateway get-resources --rest-api-id datoh7woc9 --query "items[?pathPart==\`${functionName}\`].{id:id}" | head -n 3 | tail -n 1)
  if [[ $RAWRESOURCEID == "[]" ]]; then
    echo "Unable to create or find API Gateway resource." >&2
    exit 1
  fi
fi

RESOURCEID=$(echo ${RAWRESOURCEID} | head -n 2 | tail -n 1 | cut -d \" -f 4)

echo ${RESOURCEID} > ${DEBUGFILE}

aws apigateway put-method --rest-api-id ${APIID} --resource-id ${RESOURCEID} --http-method ${method} --authorization-type COGNITO_USER_POOLS --authorizer-id awt4cs --api-key-required > ${DEBUGFILE}



aws apigateway put-integration --rest-api-id ${APIID} --resource-id ${RESOURCEID} --http-method ${method} --type AWS --integration-http-method POST --uri arn:aws:apigateway:us-east-2:lambda:path/2015-03-31/functions/${LAMBDAARN}/invocations --request-templates "file://${REL_SCRIPT_DIR}/body_and_auth_mapping.json" --passthrough-behavior NEVER > ${DEBUGFILE}

aws lambda add-permission --function-name ${functionName}${method} --statement-id ${functionName}API --action lambda:InvokeFunction --principal apigateway.amazonaws.com > ${DEBUGFILE}

aws apigateway put-method-response  --rest-api-id ${APIID} --resource-id ${RESOURCEID} --http-method ${method} --status-code 200 > ${DEBUGFILE}

aws apigateway put-integration-response  --rest-api-id ${APIID} --resource-id ${RESOURCEID} --http-method ${method} --status-code 200 --selection-pattern "" > ${DEBUGFILE}

aws apigateway create-deployment --rest-api-id ${APIID} --stage-name ${DEPLOYSTAGE} --description "Deployment by creation script for function ${functionName}${method}" > ${DEBUGFILE}


