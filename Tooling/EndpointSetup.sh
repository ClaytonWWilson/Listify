#!/bin/bash
#Base script from: https://github.com/NMerz/DoctorsNote/blob/master/AWS%20Setup/Lambda-GatewayInitialization.sh

#NOTE: This has been tested and works; however, the apigateway does not properly show as a trigger in AWS's web UI
#NOTE2: This assumes that the root Gateway and Lambda role have been set up previously (one-time setup) and their values are stored in the constants below

#constants
APIID=datoh7woc9 #rest-api-id is tied to the apigateway while resource-id seems tied to the specific url extension
ROOTRESOURCEID=6xrzhzidxh #gateway root should have a consistent resource id which will serve as parent for many apis
LAMBDAROLE=arn:aws:iam::569815541706:role/LambdaBasic
LANGUAGE=java11
DEPLOYSTAGE=Development

DEBUGFILE=/dev/null

echo -n "Please enter function/endpoint name: "
read functionName
echo -n "Please enter method(GET, POST, etc.): "
read method

jarPath=$(find .. -name "${functionName}.jar")
if [[ "$jarPath" == "" ]]; then
  echo "Unable to find file ${functionName}.jar" >&2
  exit 1
fi
functionPath=${jarPath%/${functionName}.jar}
zipPath=${functionPath}.zip

zip ${zipPath} ${jarPath}

RAWLAMBDA=$(aws lambda create-function --function-name ${functionName}${method} --zip-file fileb://${zipPath} --runtime ${LANGUAGE} --role ${LAMBDAROLE} --handler ${functionName}.lambda_handler)

if [[ $? -ne 0 ]]; then
  echo "Unable to create Lamba" >&2
  exit 1
fi

LAMBDAARN=$(echo $RAWLAMBDA | head -n 3 | tail -n 1 | cut -d \" -f 8)

echo ${LAMBDAARN} > ${DEBUGFILE}

RAWRESOURCEID=$(aws apigateway create-resource --rest-api-id ${APIID} --parent-id ${ROOTRESOURCEID} --path-part ${functionName})

if [[ $? -ne 0 ]]; then
  echo "Unable to create Resource. This needs to be handled at some future point" >&2
  exit 1
fi

RESOURCEID=$(echo ${RAWRESOURCEID} | head -n 2 | tail -n 1 | cut -d \" -f 4)

echo ${RESOURCEID} > ${DEBUGFILE}

aws apigateway put-method --rest-api-id ${APIID} --resource-id ${RESOURCEID} --http-method ${method} --authorization-type COGNITO_USER_POOLS --authorizer-id awt4cs --api-key-required > ${DEBUGFILE}



aws apigateway put-integration --rest-api-id ${APIID} --resource-id ${RESOURCEID} --http-method ${method} --type AWS --integration-http-method POST --uri arn:aws:apigateway:us-east-2:lambda:path/2015-03-31/functions/${LAMBDAARN}/invocations --request-templates 'file://body_and_auth_mapping.json' > ${DEBUGFILE}

aws lambda add-permission --function-name ${functionName}${method} --statement-id ${functionName}API --action lambda:InvokeFunction --principal apigateway.amazonaws.com > ${DEBUGFILE}

aws apigateway put-method-response  --rest-api-id ${APIID} --resource-id ${RESOURCEID} --http-method ${method} --status-code 200 > ${DEBUGFILE}

aws apigateway put-integration-response  --rest-api-id ${APIID} --resource-id ${RESOURCEID} --http-method ${method} --status-code 200 --selection-pattern "" > ${DEBUGFILE}

aws apigateway create-deployment --rest-api-id ${APIID} --stage-name ${DEPLOYSTAGE} --description "Deployment by creation script for function ${functionName}${method}" > ${DEBUGFILE}


