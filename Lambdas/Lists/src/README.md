# Core Module
Wraps inner business functionality and abstracts certain environment interactions such as opening/closing the database connection and processing the API Gateway input json.

#### Expected input json:
The following is created by APIGateway along the lines of [this](https://github.com/ClaytonWWilson/Listify/blob/master/Tooling/body_and_auth_mapping.json) definition
```
{
  "body": {jsonizedPOSTEDObject},
  "params": {
      "querystring": {
          "queryParamifApplicable": "paramValue"
      }
   },
   "context": {
       "sub": "cognitoID"
   }
}
```

`querystring` will only have subcomponents if there are query parameters


`{jsonizedPOSTEDObject}` will be something like:
```
{
    "var1": 1,
    "var2": "string"
}
```

#### Module Contract:
For the module contract see [CallHandler](https://github.com/ClaytonWWilson/Listify/blob/master/Lambdas/Lists/src/main/java/CallHandler.java)

#### Returns
Business logic return or error is appropriate

