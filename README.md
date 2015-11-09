# OpenConext-oidc

OpenConext implementation of a OpenID Connect server based on the MITREid Connect server

## Getting started

### System Requirements

- Java 8
- Maven 3
- MySQL 5.5+

### Create database

Connect to your local mysql database: `mysql -uroot`

Execute the following:

```sql
CREATE DATABASE `oidc-server` DEFAULT CHARACTER SET latin1;
create user 'oidc-serverrw'@'localhost' identified by 'secret';
grant all on `oidc-server`.* to 'oidc-serverrw'@'localhost';
```

## Building and running

The OpenConext-oidc is a maven overlay for OpenID-Connect-Java-Server. Issue a
 
`git submodule update --init --recursive` 

command to set up the git submodules, then you can run 

`mvn package jetty:run`

## Integration tests

There are JUnit integration tests that will run against the locally started Jetty container. You can also start the OIDC server
and then run the tests from within your IDE (e.g. you can debug either the test or the OIDC server).

## JWK Keys

The OIDC application uses a JWK Key Set to sign and optionally encrypt the JSON Web Tokens (JWT). Each environment can have its own unique
JWK Key Set. In the ansible projects the oidc_server_oidc_keystore_jwks_json secret is used to set populate the file oidc.keystore.jwks.json
with the key information. If you need a new JWK Key Set run the JUnit test [OidcKeystoreGeneratorTest](oidc-server/src/test/java/oidc/OidcKeystoreGeneratorTest.java) 
