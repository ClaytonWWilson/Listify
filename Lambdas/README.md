
## Components
### APIs
Contains logic to retrieve data from merchant APIs.

### Lists
Contains client-facing Lambdas which are typically a rather thin wrapper over the database (MariaDB on AWS's RDS service) and other AWS services.
#### General Client-facing Serverside Flow
```
Client -> API Gateway endpoint -> Lambda  -> (same) API Gateway endpoint -> (same) Client
            |         .           |    .
           \ /       / \         \ /  / \
            '         |           '    |  
           AWS Cognito          MariaDB instance/Other AWS services(SES, Cognito)
```

### Scraping
Contains logic to orchestrate runs using a scraping utility to collect merchant data and store it in the database




## Common Troubleshooting
#### Logs
- Logs may be written to by printing to the stdout. (Fancier logging is also possible, but currently not implemented)
- Logs are avalaible on AWS's Cloudwatch under Log Groups -> {Lambda name}
- Logs typically persist after deletion of the associated Lambda

#### Incomplete runs
Often if a run fails to complete, it is because the standard allocated time and memory/cpu is inadaquate.
- Symptoms:
  - The running time (available in the Cloudwatch logs) is almost exactly the maximum time.
- Resolutions:
  - Time may be increased.
  - CPU may be increased. CPU is proportional to memory, so CPU is increased by increasing memory allocation.
