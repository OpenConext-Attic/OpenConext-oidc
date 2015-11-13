package oidc.security;

import com.nimbusds.jose.Algorithm;
import net.minidev.json.JSONStyle;
import org.junit.Ignore;
import org.junit.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class OidcKeystoreGeneratorTest {

  @Test
  @Ignore
  public void generate() throws Exception {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    KeyPair keyPair = kpg.generateKeyPair();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

    com.nimbusds.jose.jwk.RSAKey build = new com.nimbusds.jose.jwk.RSAKey.Builder(publicKey)
        .privateKey(privateKey)
        .algorithm(new Algorithm("RSA"))
        .keyID("oidc")
        .build();
    String json = build.toJSONObject().toJSONString(JSONStyle.NO_COMPRESS);
    //copy the json to the secrets file for the target environment under the key oidc_server_oidc_keystore_jwks_json
    //this will ensure it ends up on the classpath in a file name oidc.keystore.jwks.json
  }
}
