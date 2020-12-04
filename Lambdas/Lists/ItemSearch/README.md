# ItemSearch Module
Deals with retrieving aggregate product information meeting certain naming criterion from among that populated by the scripts at https://github.com/ClaytonWWilson/Listify/blob/master/Lambdas/APIs and https://github.com/ClaytonWWilson/Listify/tree/master/Lambdas/Scraping.

### ItemGET
#### Expected request body:
N/A

#### Expected query parameters:
  - id
    - Used for specifying a substing of the product name that returned items should match
    - Valid values: {any_string}
    
#### Inputs and outputs:
  - id = {any string}: Returns an [ItemSearch object](https://github.com/ClaytonWWilson/Listify/blob/master/Lambdas/Lists/ItemSearch/src/ItemSearch.java), which is basically an array of productIDs, for products whose name fits the regex criterion `name = ".*" + id + ".*"`
    - Results are limited to the first 100 entries from the database (by earliest add time)

  
