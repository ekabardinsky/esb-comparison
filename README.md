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
