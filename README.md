# ESB comparison project
This project compare most popular Enterprise Service Bus by follow parameters:
* System CPU load
* Used memory
* Call Time

Now implemented follow tested suite:
* Mule ESB test suite

# How to contribute
Do you wanna add some another ESB solution for comparing?
Just implement rest service in your favorite ESB and pass integration tests.

# Prerequires
Comparision suite required to run mock services for test ESB and master/slave nodes for run test-client/test-server application.All required services implemented in docker containers. You can run docker containers by docker-compose for it.
Before run test-client application on Slave nodes you should configure it as follow:
* //todo configurate test-client

Before run test-server application on Master node you should configure it as follow:
* //todo configurate test-client

# Compare ESB's
//TODO

# Extract results
Use follow resource for mapping big experement json to array of results:
http://www.jsonquerytool.com/#/JavaScript

Esb result:
`input.esbResults[%testName%][0].results`

Client results:
`input.clientResults[%testName%][%clientNumber%].results.map(function(x) {return {time: x}})`

You also can map this array of object to csv file (for some calculating reasons):
http://codebeautify.org/json-to-csv
