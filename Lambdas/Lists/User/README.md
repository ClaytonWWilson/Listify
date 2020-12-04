# User Module
Wraps certain Cognito functionality to provide greater ease of use for the developer than always calling Cognitor directly and using service hooks.

### UserGET
Retrieves information on a user's cognitoID or email. This Lambda exists for ease of use in retrieving/converting between returns.
    
#### Potential Inputs (choose 1)
  - (query paramter) id: A user email or a Cognito id. Note: relies on the assumption that Cognito ids do not have "@" and emails do
  - (body element) emailToCheck: A user email
  - No input (or null/empty string inputs)
  
  
#### Returns:
  - A User object (if asking for an email, the Cognito id may be incorrect)
    - No input results in only the Cognito id being populated with the requestor's id.
    - Should something about the contract be broken, or the user not exist, a null, or null-populated, value will be returned


### UserDELETE
Deletes a user and their data. This Lambda exists to ease database cleanup and sending a farewell email.

#### Inputs:
None, this only uses the Congito id collected by AWS Gateway

#### Returns:
null
