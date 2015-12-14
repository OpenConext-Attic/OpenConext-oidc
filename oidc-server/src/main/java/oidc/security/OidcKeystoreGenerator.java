package oidc.security;

import com.nimbusds.jose.Algorithm;
import net.minidev.json.JSONStyle;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class OidcKeystoreGenerator {

  public static void main(String[] args) {
    try {
      new OidcKeystoreGenerator().generate();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

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

    System.out.println("\nCopy the json below to the secrets file for the target environment under the key oidc_server_oidc_keystore_jwks_json\n" +
        "This will ensure it ends up on the classpath in a file name oidc.keystore.jwks.json\n");
    System.out.println(json);
    System.out.println("\n");
  }
}
