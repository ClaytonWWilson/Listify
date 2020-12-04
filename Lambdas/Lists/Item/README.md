# Item Module
Deals with retrieving product information populated by the scripts at https://github.com/ClaytonWWilson/Listify/blob/master/Lambdas/APIs and https://github.com/ClaytonWWilson/Listify/tree/master/Lambdas/Scraping.

### ItemGET
#### Expected request body:
N/A

#### Expected query parameters:
  - id
    - Used for specifying which product to retrieve. This is not the upc, but is our own internal id.
    - Valid values: 1<n<{num_products}*
    
#### Inputs and outputs:
  - id = 1<n<{num_products}*: Returns an [Item object](https://github.com/ClaytonWWilson/Listify/blob/master/Lambdas/Lists/Item/src/Item.java) for the products with productID=id \

\*Not all number in this range are valid because a seemingly random subset were removed by deduplication scripts and during new product addition testing.

  
