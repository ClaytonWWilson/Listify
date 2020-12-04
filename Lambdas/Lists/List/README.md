# List Module
Deals with managing the user-facing list creation, deletion, retrieval, and renaming. The supporting classes here also serve as resources for other modules.

### Notable
[ListPermission.java](https://github.com/ClaytonWWilson/Listify/blob/master/Lambdas/Lists/List/src/ListPermissions.java) contains the lambda-side translation from permissionLevel to actual permissions. Keys updated on the client side must also be updated here.

### ListGET
Retrieves details for an existing list
#### Expected request body:
N/A

#### Expected query parameters:
  - id
    - Used for specifying which list to retrieve
    - Valid values: -1,1<n<{num_lists}*
    
#### Inputs and outputs:
  - id = -1: Returns an array if listIDs (Integers) the requesting user has Read access to
  - id = 1<n<{num_lists}*: Returns a [List object](https://github.com/ClaytonWWilson/Listify/blob/master/Lambdas/Lists/List/src/List.java) for the list with listID=id. ListGetter is developer friendly and proactively populates all of the [ItemEntry](https://github.com/ClaytonWWilson/Listify/blob/readmes/Lambdas/Lists/List/src/ItemEntry.java) objects in the list with their data.

*Not all number in this range are valid because a seemingly random subset may be removed by user activity and testing.


### ListPOST
Creates a new list
#### Minimum expected request body:
```
{
  "name": "listName"
}
```
Other fields may be populated and are simply ignored.

#### Expected query parameters:
N/A

#### Inputs:
  - name: Provides the display name for the new list
  
#### Returns:
  - listID: An integer which is the listID of the newly created list
  
  
### ListPUT
Updates a List's name

#### Minimum expected request body:
```
{
  "listID": 2,
  "name": "newListName"
}
```
Other fields may be populated and are simply ignored.

#### Expected query parameters:
N/A

#### Inputs:
  - listID: Provides the listID of the list which shall receive the new name
  - name: Provides the new display name for the list
  
#### Returns:
null
  
### ListDELETE
Deletes a list and cleans out dependent tables like ListSharee
#### Minimum expected request body:
```
{
  "listID": 3
}
```
Other fields may be populated and are simply ignored.

#### Expected query parameters:
N/A

#### Inputs:
  - listID: Provides the listID of the list to delete
  
#### Returns:
null
  
  
