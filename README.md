# OpenConext-oidc

OpenConext implementation of a OpenID Connect server based on the MITREid Connect server

## Getting started

### System Requirements

- Java 7
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

`mvn package jetty:run -Dspring.profiles.active="local"`

or the shorthand:

`.\start.sh`

If you don't use the local profile then you need to login on the SURFconext federation.

## Integration tests

There are JUnit integration tests that will run against the locally started Jetty container. 

`mvn verify`

You can also start the OIDC server (local mode !) and then run the tests from within your IDE (e.g. you can debug either the test or the OIDC server).

The integration tests set the spring.active.profile property automatically to local too prevent having to do the SAML dance.

## JWK Keys

The OIDC application uses a JWK Key Set to sign and optionally encrypt the JSON Web Tokens (JWT). Each environment can have its own unique
JWK Key Set. In the ansible projects the `oidc_server_oidc_keystore_jwks_json secret` is used to set populate the file `oidc.keystore.jwks.json`
with the key information. If you need a new JWK Key Set run the JUnit test [OidcKeystoreGeneratorTest](oidc-server/src/test/java/oidc/security/OidcKeystoreGeneratorTest.java) 

## Private signing keys and public certificates

The SAML Spring Security library needs a private DSA key to sign the SAML request and the public certificates from EngineBlock. The
public certificate can be copied from the metadata. The private / public key for the SP can be generated:
 
`openssl req -subj '/O=Organization, CN=OIDC/' -newkey rsa:2048 -new -x509 -days 3652 -nodes -out oidc.crt -keyout oidc.pem`

The Java KeyStore expects a pkcs8 DER format for RSA private keys so we have to re-format that key:

`openssl pkcs8 -nocrypt  -in oidc.pem -topk8 -out oidc.der` 
 
Remove the whitespace, heading and footer from the oidc.crt and oidc.der:

`cat oidc.der |head -n -1 |tail -n +2 | tr -d '\n'; echo`

`cat oidc.crt |head -n -1 |tail -n +2 | tr -d '\n'; echo`

Above commands work on linux distributions. On mac you can issue the same command with `ghead` after you install `coreutils`:

`brew install coreutils`

`cat oidc.der |ghead -n -1 |tail -n +2 | tr -d '\n'; echo`

`cat oidc.crt |ghead -n -1 |tail -n +2 | tr -d '\n'; echo`


Add the oidc key pair to the application.oidc.properties file:

`sp.private.key=${output from cleaning the der file}`

`sp.public.certificate=${output from cleaning the crt file}`

Add the EB certificate to the application.oidc.properties file:

`idp.public.certificate=${copy & paste from the metadata}`

## Trusted Proxy

OpenConext-OIDC is a proxy for SP's that want to use OpenConnect ID instead of SAML to provide their Service to the federation members. 
Therefore the WAYF and ARP must be scoped for the requesting SP (and not this OIDC SP). This works if OpenConext-OIDC is marked
as a trusted proxy in SR and the signing certificate (e.g. sp.public.certificate) in added to the certData metadata field in SR.

## Damn

We link SPs and OIDC Clients by the SP entity-id and the client name. The authorization server MUST support the HTTP Basic
authentication scheme for authenticating clients that were issued a client password and Basic authentication does not support
':' in the username. We therefore substitute each "@" in the SP entity-id with a "@@" and each ":" with a "@". The algorithm needs to be
revertible, because the SP entity is 'saved' during SAML request in the relay state of the AuthN request.

See [ServiceProviderTranslationService](oidc-server/src/main/java/oidc/saml/ServiceProviderTranslationService.java)

## SAML metadata

The metadata is generated on the fly and is displayed on http://localhost:8080/saml/metadata