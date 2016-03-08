# OpenConext-oidc

[![Build Status](https://travis-ci.org/OpenConext/OpenConext-oidc.svg)](https://travis-ci.org/OpenConext/OpenConext-oidc)
[![codecov.io](https://codecov.io/github/OpenConext/OpenConext-oidc/coverage.svg)](https://codecov.io/github/OpenConext/OpenConext-oidc)

OpenConext implementation of a OpenID Connect server based on the MITREid Connect server

## [Getting started](#getting-started)

### [System Requirements](#system-requirements)

- Java 7
- Maven 3
- MySQL 5.5+

### [Create database](#create-database)

Connect to your local mysql database: `mysql -uroot`

Execute the following to create a local datbase compliant with travis:

```sql
CREATE DATABASE `oidcserver` DEFAULT CHARACTER SET latin1;
create user 'root'@'localhost';
grant all on `oidcserver`.* to 'root'@'localhost';
```

## [Building and running](#building-running)

The OpenConext-oidc is a maven overlay for OpenID-Connect-Java-Server. Issue a
 
`git submodule update --init --recursive` 

command to set up the git submodules, then you can run 

`cd ./oidc-server && mvn clean package jetty:run -Dspring.profiles.active="local"`

or the shorthand:

`./start.sh`

If you don't use the local profile then you need to login on the SURFconext federation.

## [Testing](#testing)

### [Integration tests](#integration-tests)

There are JUnit integration tests that will run against the locally started Jetty container. 

`mvn verify`

You can also start the OIDC server (local mode !) and then run the tests from within your IDE (e.g. you can debug either the test or the OIDC server).

The integration tests set the spring.active.profile property automatically to local too prevent having to do the SAML dance.

### [cUrl](#curl-testing)

When you have the oidc server running locally with the local profile you can use cUrl to test the different endpoints.

Note that this only works because of the `local` profile where there is pre-authenticated user provided by the `MockPreAuthenticatedProcessingFilter`.

First obtain an authorization code:

```
curl -i  "http://localhost:8080/authorize?response_type=code&client_id=https@//oidc.localhost.surfconext.nl&scope=openid&redirect_uri=http://localhost:8889/callback"
```

This will output the following:

```bash
HTTP/1.1 302 Found
Date: Mon, 07 Mar 2016 14:39:33 GMT
Set-Cookie: JSESSIONID=15hhbacbubrc73iy6ioqj01rx;Path=/
Expires: Thu, 01 Jan 1970 00:00:00 GMT
X-Frame-Options: DENY
Pragma: no-cache
Cache-Control: no-cache
Cache-Control: no-store
Content-Language: en
Location: http://localhost:8889/callback?code=s1JRqh
Content-Length: 0
Server: Jetty(9.3.5.v20151012)
```

Save the code in the query parameter of the location response header in a variable (use the code of your response and not this example code):

`export code=s1JRqh`

And then exchange the code for an access token:

```
curl -X POST -u https@//oidc.localhost.surfconext.nl:secret -d "grant_type=authorization_code&code=${code}&redirect_uri=http://localhost:8889/callback" http://localhost:8080/token | python -m json.tool
```

This will return the access_token and id_token.

```json
{
    "access_token": "eyJraWQiOiJvaWRjIiwiYWxnIjoiUlMyNTYifQ.eyJhdWQiOiJodHRwc0BcL1wvb2lkYy5sb2NhbGhvc3Quc3VyZmNvbmV4dC5ubCIsImlzcyI6Imh0dHA6XC9cL2xvY2FsaG9zdDo4MDgwXC8iLCJleHAiOjE0NTc1MDgxMzAsImlhdCI6MTQ1NzQyMTczMCwianRpIjoiNTI3MTI0OGEtMGY3ZC00ODY3LTllZTUtMDgwMzMyYTYwOWZmIn0.SqdfoVVYIL-EXI0hmTRQzWCrtYL5rXz_Sgxvg0SfI3nn68dxCjxV9r00inJqXCm6lkD3uKdSfzpQ2EfGLhHCqpKZxNGQoDvEIghqrqZPGOxMu_vbfiKudCR8gxag_xIm-kkiLMkd_5iBFK3QlCmVHoJjnfxZkXYb3-bMKdYA1ourP4U4pvFWxHIcnk20QQO-NIawt3brU3nryErtGdEDepultN26qdgTubvmAQRaRF0OYyia1eOTVYwEKdted6E8INqRmR5WFWJIg_7HqE4c9JcHOMd8PCv558N0QU3G49Oqpn7xlBN7fvZq0RpCsGcTJAkqLjqRi-a0VHsvN7hJvw",
    "expires_in": 86399,
    "id_token": "eyJraWQiOiJvaWRjIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiI3NTcyNmUzYS02MzZmLTZjNmMtNjE2Mi0zYTcwNjU3MjczNmYiLCJhdWQiOiJodHRwc0BcL1wvb2lkYy5sb2NhbGhvc3Quc3VyZmNvbmV4dC5ubCIsImtpZCI6Im9pZGMiLCJpc3MiOiJodHRwOlwvXC9sb2NhbGhvc3Q6ODA4MFwvIiwiZXhwIjoxNDU3NTA4MTMwLCJpYXQiOjE0NTc0MjE3MzAsImp0aSI6IjI1YjJhZmFmLWEwZWYtNGVjOS05NzQ1LTE1YWQyYjUyMWVmNiJ9.kMM8EjkvgvGgYlzOSJL11Qqzoq8M0av1HaFs7tnMO4E7kPsoT25WzXwreaW3GRk56C9HyHKtFVo7f836_yNocWFkARliSUv43onVV6ro7BL41EFROWmJBR2iBMmKH_Pn8SXO-kYvWg0r5S3zlpaiWL_xqgW6yoOe32vlcQbhteixT3OwVDMNe6XuVlOU2K7XtJmsZQml5py0mLVOyi068ag7uIJ1lA9mLkcw86i8Edzye2Wdxx1_DNF4D_d7MhRjJi5IxGdcADmNeAI8-iYS12v0joctCDKSFff8jee5OFlhC5DFZ6EzhoVIYuY0dLbgHFztG2Q3ScGFIq9BNKZHFw",
    "scope": "openid",
    "token_type": "Bearer"
}
```

Save the access_token in a variable:

```
export access_token=eyJraWQiOiJvaWRjIiwiYWxnIjoiUlMyNTYifQ.eyJhdWQiOiJodHRwc0BcL1wvb2lkYy5sb2NhbGhvc3Quc3VyZmNvbmV4dC5ubCIsImlzcyI6Imh0dHA6XC9cL2xvY2FsaG9zdDo4MDgwXC8iLCJleHAiOjE0NTc1MDgxMzAsImlhdCI6MTQ1NzQyMTczMCwianRpIjoiNTI3MTI0OGEtMGY3ZC00ODY3LTllZTUtMDgwMzMyYTYwOWZmIn0.SqdfoVVYIL-EXI0hmTRQzWCrtYL5rXz_Sgxvg0SfI3nn68dxCjxV9r00inJqXCm6lkD3uKdSfzpQ2EfGLhHCqpKZxNGQoDvEIghqrqZPGOxMu_vbfiKudCR8gxag_xIm-kkiLMkd_5iBFK3QlCmVHoJjnfxZkXYb3-bMKdYA1ourP4U4pvFWxHIcnk20QQO-NIawt3brU3nryErtGdEDepultN26qdgTubvmAQRaRF0OYyia1eOTVYwEKdted6E8INqRmR5WFWJIg_7HqE4c9JcHOMd8PCv558N0QU3G49Oqpn7xlBN7fvZq0RpCsGcTJAkqLjqRi-a0VHsvN7hJvw
```

Now you can ask the server to return the information stored with this access_token by calling the introspect endpoint (note that this endpoint is only for resource servers):

```
curl -u https@//oidc.localhost.surfconext.nl:secret -H "Content-Type: application/x-www-form-urlencoded" "http://localhost:8080/introspect?token=${access_token}" | python -m json.tool
```

This will return:

```json
{
    "active": true,
    "authenticating_authority": "http://mock-idp",
    "client_id": "https@//oidc.localhost.surfconext.nl",
    "edu_person_principal_name": "principal_name",
    "exp": 1457508131,
    "expires_at": "2016-03-09T08:22:11+0100",
    "schac_home": "surfnet.nl",
    "scope": "openid",
    "sub": "75726e3a-636f-6c6c-6162-3a706572736f",
    "token_type": "Bearer",
    "unspecified_id": "urn:collab:person:example.com:local",
    "user_id": "75726e3a-636f-6c6c-6162-3a706572736f"
}
```

Use the same access_token to call the user_info endpoint:

```
curl -H "Authorization: bearer ${access_token}" -H "Content-type: application/json" http://localhost:8080/userinfo | python -m json.tool
```

This will return all the information about the user. This endpoint is for ServiceProviders.

```json
{
    "edu_person_affiliations": [
        "student",
        "faculty"
    ],
    "edu_person_entitlements": [
        "http://xstor.com/contracts/HEd123",
        "urn:mace:washington.edu:confocalMicroscope"
    ],
    "edu_person_principal_name": "principal_name",
    "edu_person_scoped_affiliations": [
        "student",
        "faculty"
    ],
    "edu_person_targeted_id": "fd9021b35ce0e2bb4fc28d1781e6cbb9eb720fed",
    "email": "john.doe@example.org",
    "family_name": "Doe",
    "given_name": "John",
    "is_member_ofs": [
        "surfnet"
    ],
    "locale": "NL",
    "name": "John Doe",
    "preferred_username": "John Doe",
    "schac_home_organization": "surfnet.nl",
    "schac_home_organization_type": "institution",
    "schac_personal_unique_codes": [
        "personal"
    ],
    "sub": "75726e3a-636f-6c6c-6162-3a706572736f",
    "uids": [
        "uid2",
        "uid1"
    ]
}
```

## [JWK Keys](#jwk-keys)

The OIDC application uses a JWK Key Set to sign and optionally encrypt the JSON Web Tokens (JWT). Each environment can have its own unique
JWK Key Set. In the ansible projects the `oidc_server_oidc_keystore_jwks_json secret` is used to set populate the file `oidc.keystore.jwks.json`
with the key information. If you need a new JWK Key Set run [OidcKeystoreGenerator](oidc-server/src/main/java/oidc/security/OidcKeystoreGenerator.java):
 
```
cd oidc-server ; mvn compile ; mvn exec:java -Dexec.mainClass="oidc.security.OidcKeystoreGenerator" -Dexec.classpathScope=runtime
```

## [Private signing keys and public certificates](#signing-keys)

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

## [Trusted Proxy](#trusted-proxy)

OpenConext-OIDC is a proxy for SP's that want to use OpenConnect ID instead of SAML to provide their Service to the federation members. 
Therefore the WAYF and ARP must be scoped for the requesting SP (and not this OIDC SP). This works if OpenConext-OIDC is marked
as a trusted proxy in SR and the signing certificate (e.g. sp.public.certificate) is added to the certData metadata field in SR.

## [Damn](#damn)

We link SPs and OIDC Clients by the SP entity-id and the client name. The authorization server MUST support the HTTP Basic
authentication scheme for authenticating clients that were issued a client password and Basic authentication does not support
':' in the username. We therefore substitute each "@" in the SP entity-id with a "@@" and each ":" with a "@". The algorithm needs to be
revertible, because the SP entity is 'saved' during SAML request in the relay state of the AuthN request.

See [ServiceProviderTranslationService](oidc-server/src/main/java/oidc/saml/ServiceProviderTranslationService.java)

You can also query the translate endpoint:

`http://oidc.${env}.surfconext.nl/translate-sp-entity-id?spEntityId=${urlEncoded SP entity ID}`

Example for localhost

`http://localhost:8080/translate-sp-entity-id?spEntityId=https%3A//oidc.test.surfconext.nl`

## [SAML metadata](#saml-metadata)

The metadata is generated on the fly and is displayed on http://localhost:8080/saml/metadata

## [Subject Identifier Types](#subject-identifier-types)

The OpenID Connect [specification](http://openid.net/specs/openid-connect-core-1_0.html#SubjectIDTypes) defines two Subject Identifier types. The OICD
server is implemented to always use the pairwise type. For each client the 'sub' of the user is a unique combination of the client_id and user_id.

The [openid-configuration](http://localhost:8080/.well-known/openid-configuration) states we support both, but that is the default (hard-coded)
behaviour we inherited from the initial codebase.

## [Dependencies](#dependecies)

Besides the 'normal' 3rd party libraries defined in the pom.xml, we also include two forked dependencies in the target war:

* [spring-security-oauth](https://github.com/OpenConext/spring-security-oauth/tree/feature/open-conext-build)
  * Branch based on [pull-request to support other response types then code and token](https://github.com/spring-projects/spring-security-oauth/pull/627).

* [spring-security-saml](https://github.com/OpenConext/spring-security-saml/tree/feature/open-connext)
  * Branch based on [pull-request to include RequesterID](https://github.com/spring-projects/spring-security-saml/pull/19).

Once the pull requests are accepted and merged into a release we can depend on the original repositories again.

## [Functional testing](#functional-testing)

Using the [authz-playground](https://authz-playground.test.surfconext.nl) you can test all implemented flows and endpoints of the OIDC server. If you want to test
the (re)provisioning of the user it is sometimes useful to add/change the default attributes that Mujina-idp returns. These extra attributes show up in the
the userinfo page - step 3 in the authz-playground wizard. To add an attribute use the Mujina API:

```
curl -v -H "Accept: application/json" -H "Content-type: application/json" -d '{"value": ["teacher","professor"]}' -X PUT https://mujina-idp.test.surfconext.nl/api/attributes/urn:mace:dir:attribute-def:eduPersonScopedAffiliation
```

To reset Mujina back to its default behaviour, issue:
 
```
curl -v -H "Accept: application/json" -H "Content-type: application/json" -X POST https://mujina-idp.test.surfconext.nl/api/reset
```