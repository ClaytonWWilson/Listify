# Chain Module
Deals with information concerning store chains supported by the product populating and product serving Lambdas.

### ChainGET
#### Expected request body:
N/A

#### Expected query parameters:
  - id
    - Used for 
    - Valid values:
    
#### Inputs and outputs:
  - id = -1: Returns an array if chainIDs (Integers)
  - id = 1<n<{num_chains}: Returns a [Chain object](https://github.com/ClaytonWWilson/Listify/blob/readmes/Lambdas/Lists/Chain/src/Chain.java) for the chain with chainID=id
