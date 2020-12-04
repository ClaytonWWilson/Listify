# ListEntry Module
Deals with managing entries in a list. Note that there is no GET here because that is handled in ListGET see [List readme](https://github.com/ClaytonWWilson/Listify/tree/master/Lambdas/Lists/List)

### ListEntryPOST
Add a specified quanitity of an item to a list.
#### Minimum expected request body:
```
{
  "productID": 2,
  "listID": 4,
  "quantity": -3,
  "purchased": false
}
```
Other fields may be populated and are simply ignored.

#### Expected query parameters:
N/A

#### Inputs:
  - productID: The productID to add to the list
  - listID: The listID of the list to be modified
  - quantity: The number of an item to add (may be negative) to the list
  - purchased: Currently unused field with future potential for allowing user to check off items they have purchased
  
#### Returns:
null
  
  
### ListEntryDELETE
Removes the specified product from the list

#### Minimum expected request body:
```
{
  "listID": 6,
  "productID": 5
}
```
Other fields may be populated and are simply ignored.

#### Expected query parameters:
N/A

#### Inputs:
  - listID: The listID of the list to be modified
  - productID: The productID to remove from the list
  
#### Returns:
null
  
