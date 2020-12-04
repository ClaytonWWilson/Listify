# Chain Module
Deals with information concerning store chains supported by the product populating and product serving Lambdas.

### ChainGET
#### Expected request body:
N/A

#### Expected query parameters:
  - id
    - Used for specifying which chain to retrieve
    - Valid values: -1,1<n<{num_chains}
    
#### Inputs and outputs:
  - id = -1: Returns an array if chainIDs (Integers)
  - id = 1<n<{num_chains}: Returns a [Chain object](https://github.com/ClaytonWWilson/Listify/blob/master/Lambdas/Lists/Chain/src/Chain.java) for the chain with chainID=id

