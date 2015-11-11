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

`mvn package jetty:run -Dspring.profiles.active="local"`

If you don't use the local profile then you need to login on the SURFconext federation.

## Integration tests

There are JUnit integration tests that will run against the locally started Jetty container. You can also start the OIDC server
and then run the tests from within your IDE (e.g. you can debug either the test or the OIDC server).

## JWK Keys

The OIDC application uses a JWK Key Set to sign and optionally encrypt the JSON Web Tokens (JWT). Each environment can have its own unique
JWK Key Set. In the ansible projects the oidc_server_oidc_keystore_jwks_json secret is used to set populate the file oidc.keystore.jwks.json
with the key information. If you need a new JWK Key Set run the JUnit test [OidcKeystoreGeneratorTest](oidc-server/src/test/java/oidc/OidcKeystoreGeneratorTest.java) 

## Private signing keys and public certificates

The SAML Spring Security library needs a private DSA key to sign the SAML request and the public certificates from EngineBlock. The
public certificate can be copied from the metadata. The private / public key for the SP can be generated:
 
`openssl req -subj '/O=Organization, CN=OIDC/' -newkey rsa:2048 -new -x509 -days 3652 -nodes -out oidc.crt -keyout oidc.pem`
`openssl pkcs8 -nocrypt  -in oidc.pem -topk8 -out oidc.der` 
 
Remove the whitespace, heading and footer from the oidc.crt and oidc.der:

`cat oidc.der |head -n -1 |tail -n +2 | tr -d '\n'; echo`
`cat oidc.crt |head -n -1 |tail -n +2 | tr -d '\n'; echo`

Add the oidc key pair to the application.oidc.properties file:

`sp.private.key=MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCx/ZVm7Q94j5SmsiF0JKBAqZsetJKLQSQiQy2iEJbbRV3DjgiWuiUNjHG77JpsrMl6f2NhT6ax9IzCKjBaRX1th+Mq4/SUuqVmogfFB6nEalbTvd1pqNhdLKhhA12QCDatdJuapR5s5kWAlNq+RFfCOp6/fT52xoPjiUNBM6culux6Cdl/xc+oRaaPqhBA9lQRLz1LXlzYZ56ssOV4aoD7DHEEJth7q+5Vvk87DSBTC94VQx5JlcYL6oDiK6TxDZ7BC0tPkuydGQH+8R+8htkrj3QQnCG3nvVJLPUyD4Kb0YEbhGLfLFoj+cLIlmC3JYrtz8mC2eo5gm2xJAicezqnAgMBAAECggEAXw5Gk8lW0dQcV0oag6Rr0RzDMwrMmIIZghLOdBbX+hJ5mM5p19VhpHK06t/ZOOCuWTVfJcEcDT3FToypdgxWiABiWa2UGTn0y3MNIzSMfdEsvbhQXvVFV71ZzzW06g745Hm6AETektoHlDvq9mIZXCliTnHcwiISnZnYySGEz2P2sFegqAv7UGWMkw8+KnXfbs33Nc4m7H6krbEOF1Oke9TlG9Hzvlr+VhaZb5arwVAziOPF6J1G7/N02SqZnBQjyww7vUU0FuuAU8FWqZ8DkWJaZ5LWIm+l+r7nZuKY4bFbhO8Rw74xhYIIaYMJMRnFrnf920gOW6oxDnvfzeeFgQKBgQDYnFs8a7IbylV9C5AG4pzozJnuogk5UlId4noJq8NnoE/+l9VAfw7KHHq/TRXP3Pc26Vq4tRZScpr6hrOJpWm6+gzPbYSvSKHrpTLAMeYunB1abPtH5Pb3CwcUygYlIhGsVyIvbhppr+Hr7jQ8JpKuFP5aMZdMjy55FYZOoAOZmQKBgQDSW2IR0RpKRnA2rWKG3oZJHq9cU4LrvYPLPzNv5XM6PknAiNJyAj2NqJSbpUphUEBi4JrfyMlMxAIxPmQ//lv6BMjzxI66HFpcv6nRJ5R1E0spEqZtLoo00a9tjxwIbEZRPVBOGoca2WnzNOE3UUxNqSVfBslrubaTeB7SUKyePwKBgHz/hZrRXx+wXDLjyEJg5UFmSHIv6Xi9q4t5VQldqE6VcXQutC4a5DL3ylG4rxybm8GBWgv63B3J6uslblfBL8lpYJkWzxnDpUTxuN62LOm6xymWeE9drrKTF4wEpRUqmt5EDgMm23EfwRTACj6n179DeCAeO/w/KAdRx4Z5ynoJAoGBAKdUkfpiP2t+BJ055vop4OAhTJVAyRAFPOVcbOpBtxtJmRlSvTR7m8Mnqq71GOm4EL+Wsxv4eEhcUvIhE1XkW/3R+JgQbwiUrTzCKa96sJlAs7UEZObM8pt5gPbBtCbvGjBdeZfnejldYO5Zzh1Wyj+soVGp8GeE3zsHfl0GV4m5AoGBANAQ/iisUQogcGg3DnZ17P13zpr9Lux4KN/NZRRC7rbAuEwVSKj2iFOuIHX3y4Na3w2otHzMz41UX6QF/YX3beIZB3EWHQL7vE4DiEc8hfX66q9uwa0ZkSdI1T7uVojwqlb1E8zTRBYDBaVqII3sVzB296MCsxVWUXvhYR36j7Sg`
`sp.public.certificate=MIIDITCCAgmgAwIBAgIJAJ/daA/EEaw9MA0GCSqGSIb3DQEBBQUAMCcxJTAjBgNVBAoMHE9yZ2FuaXphdGlvbiwgQ049QVBJUyAoVEVTVCkwHhcNMTUxMTExMDc1NzQ4WhcNMjUxMTEwMDc1NzQ4WjAnMSUwIwYDVQQKDBxPcmdhbml6YXRpb24sIENOPUFQSVMgKFRFU1QpMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsf2VZu0PeI+UprIhdCSgQKmbHrSSi0EkIkMtohCW20Vdw44IlrolDYxxu+yabKzJen9jYU+msfSMwiowWkV9bYfjKuP0lLqlZqIHxQepxGpW073daajYXSyoYQNdkAg2rXSbmqUebOZFgJTavkRXwjqev30+dsaD44lDQTOnLpbsegnZf8XPqEWmj6oQQPZUES89S15c2GeerLDleGqA+wxxBCbYe6vuVb5POw0gUwveFUMeSZXGC+qA4iuk8Q2ewQtLT5LsnRkB/vEfvIbZK490EJwht571SSz1Mg+Cm9GBG4Ri3yxaI/nCyJZgtyWK7c/JgtnqOYJtsSQInHs6pwIDAQABo1AwTjAdBgNVHQ4EFgQUNHWpIMovwzmnwIngcoSQDN7qmYYwHwYDVR0jBBgwFoAUNHWpIMovwzmnwIngcoSQDN7qmYYwDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOCAQEAOW+0ta1PoOjZzaF7i8vhOvfxRTPMp0gMYagnRQzTiKiHN8k3+sqNt8cikLaJjNpmE4NBmBcUFK08svmXyRCYf0fHAhh2b9vr+hXxKlxpsOpEUcX+qloCqZ+56y/qxl2pX5k9bOQSDdZtgPpf+ULkfGx7IM324UmAd5w9vlMtcY/dFxkiSgi0Udt14gEfoSGfMN9/S4ZNdj57cYEs41wN1ust26ZbmdjTzXyk1XmNQopwiuOQH7C7Rx9CVw9qaWqGMZUZDWyqgbIsXjIsU3uV2rNNkRcf2zH61Sg+3vdbDcRFvvAxiX/y29Gb7zdM1YWxkqQp+qIcha+wdFhQO06EXw==`

Add the EB certificate to the application.oidc.properties file:

`idp.public.certificate=MIIC+zCCAmSgAwIBAgIJAPJvLjQsRR4iMA0GCSqGSIb3DQEBBQUAMF0xCzAJBgNVBAYTAk5MMRAwDgYDVQQIEwdVdHJlY2h0MRAwDgYDVQQHEwdVdHJlY2h0MRAwDgYDVQQKEwdTVVJGbmV0MRgwFgYDVQQDEw90ZXN0MiBzYW1sIGNlcnQwHhcNMTUwMzI0MTQwMzE3WhcNMjUwMzIxMTQwMzE3WjBdMQswCQYDVQQGEwJOTDEQMA4GA1UECBMHVXRyZWNodDEQMA4GA1UEBxMHVXRyZWNodDEQMA4GA1UEChMHU1VSRm5ldDEYMBYGA1UEAxMPdGVzdDIgc2FtbCBjZXJ0MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCxKYrnW8NwqdRwRwaHulueL69gQFTJbdfoASLhYie2bOioJyJw1f+cAvpjsl4SVYpvEseIetXH8Lgpk7K73PkDJL8dFg4sEjFF6jgmjrOES3ox/gtTd9d/VwPHI/vPIcGy1sbVJ4pEMFl5d8GOAzoJkNWdPj9wV4rv4uv35May8wIDAQABo4HCMIG/MB0GA1UdDgQWBBTTIaPwpKpJlY8eIMSzNRZUjStbijCBjwYDVR0jBIGHMIGEgBTTIaPwpKpJlY8eIMSzNRZUjStbiqFhpF8wXTELMAkGA1UEBhMCTkwxEDAOBgNVBAgTB1V0cmVjaHQxEDAOBgNVBAcTB1V0cmVjaHQxEDAOBgNVBAoTB1NVUkZuZXQxGDAWBgNVBAMTD3Rlc3QyIHNhbWwgY2VydIIJAPJvLjQsRR4iMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEFBQADgYEAK8EvTU0LgHJsSugorOemgRlppMfJAeOmuuZNhSMY2QhumFOZpaAb8NFIwUKUVyyJnSo7k6ktHCKI94sQs976242hTDDYEwWJD9HhAsAqOo21Ua8gZT38/wm62e3KgrKXvnljAbKPXDXJM4akz7y6H6wvvIGT6f0f0iJWHq34jww=`

