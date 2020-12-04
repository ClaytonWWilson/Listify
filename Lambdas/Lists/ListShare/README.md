# ListShare Module
Deals with permissions for accessing Lists and performing functions on them. This enabled social-media-like functionality.

### Concerns
More information is public here than may be desirable. In the future, it may be worth reducing the amount of information served by this Lambda about users other than the requestor.


### ListShareGET
Retrieves a information on who may access a list and how they interact with it.
#### Expected request body:
N/A

#### Expected query parameters:
  - id
    - Used for specifying which list to retrieve share information for
    - Valid values: -1<n<{num_lists}*
    
#### Inputs and outputs:
  - id = 1<n<{num_lists}*: Returns a [ListShare object](https://github.com/ClaytonWWilson/Listify/blob/master/Lambdas/Lists/ListShare/src/ListShare.java) for the list with listID=id.

*Not all number in this range are valid because a seemingly random subset may be removed by user activity and testing.


### ListSharePUT
Modifies a user's permissions to interact with a list.
#### Minimum expected request body:
```
{
  "listID": 3
  "shareWithEmail": "email@mailserver.com"
  "permissionLevel": 210

}
```
Other fields may be populated and are simply ignored.

#### Expected query parameters:
N/A

#### Inputs:
  - listID: The listID of the list for which to alter permissions
  - shareWithEmail: The email (already register with Cognito) of the user whose permissions are being changed.
  - permissionLevel: A numerical representation of a user's permissions. This should be generated and interpreted by [ListShare.java](https://github.com/ClaytonWWilson/Listify/blob/master/Listify/app/src/main/java/com/example/listify/data/ListShare.java) on the client-side or it's server-side equivalent [ListPermissions.java](https://github.com/ClaytonWWilson/Listify/blob/master/Lambdas/Lists/List/src/ListPermissions.java)
  
#### Returns:
null

#### Notes for ListSharePUT
Unsharing is accomplished by sharing with permissionLevel = 0.
