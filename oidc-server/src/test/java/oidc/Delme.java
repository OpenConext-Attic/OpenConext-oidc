package oidc;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import net.minidev.json.JSONStyle;
import org.junit.Test;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class Delme {

  @Test
  public void test() throws ParseException, JOSEException {
    String token = "eyJraWQiOiJyc2ExIiwiYWxnIjoiUlMyNTYifQ.eyJhdWQiOiJkMTYyYmQwMi02NzA0LTQzOWYtOGRmOS1lMTU5ZTc2NzY5ODYiLCJpc3MiOiJodHRwOlwvXC9sb2NhbGhvc3Q6ODA4MFwvIiwiZXhwIjoxNDQ2OTA1MDA5LCJpYXQiOjE0NDY5MDE0MzUsImp0aSI6ImFjZGZkOThiLWZlZjQtNDE4Ni1hMjMxLTNlZjhiN2MyNmQ2MyJ9.mPd6KUMCAX7x5I9o-jSwI3K_vkh6dhwRkEVdLIRJ9sj9aWhxds9XZZ9q_-qYJoNdQDmAccjVbQREdjEmt93v2Nwo3kMqm-IqfT8rKSbFDUVYFnXDTJNjWY_IHpNy_GN3lb7gp-1NZnIS3U_vJTF7ecTTrerz9e1YtAmN6xnjsEH3Y2cz2FyOeIN2YWQ_Qp17IBvYsPKjy0qBXolDa0PT-itHLZwSpNwrlrToqUI2pbVVirLY4VJ3FymafT8Qjz3mU5UaYM5lsKCA2PHSLcj-sSa7MENAqurnkSNuVR8sQ5IiIO9QtUpmE0vEJ6bblDP5EfRvOshMwg-ewEXfwaB8Hg";
    JWT parsed = JWTParser.parse(token);


    // Create a new JWK selector and configure it
    JWKMatcher matcher = new JWKMatcher.Builder().keyType(KeyType.RSA).build();
    JWKSelector selector = new JWKSelector(matcher);
    JWKSet jwkSet = JWKSet.parse("\n" +
        "{\"keys\":[{\"kty\":\"RSA\",\"e\":\"AQAB\",\"kid\":\"rsa1\",\"alg\":\"RS256\",\"n\":\"qt6yOiI_wCoCVlGO0MySsez0VkSqhPvDl3rfabOslx35mYEO-n4ABfIT5Gn2zN-CeIcOZ5ugAXvIIRWv5H55-tzjFazi5IKkOIMCiz5__MtsdxKCqGlZu2zt-BLpqTOAPiflNPpM3RUAlxKAhnYEqNha6-allPnFQupnW_eTYoyuzuedT7dSp90ry0ZcQDimntXWeaSbrYKCj9Rr9W1jn2uTowUuXaScKXTCjAmJVnsD75JNzQfa8DweklTyWQF-Y5Ky039I0VIu-0CIGhXY48GAFe2EFb8VpNhf07DP63p138RWQ1d3KPEM9mYJVpQC68j3wzDQYSljpLf9by7TGw\"}]}");
    JWKSet jwkSet1 = jwkSet.toPublicJWKSet();


    List<JWK> select = selector.select(jwkSet);
    JWK jwk = select.get(0);

    SignedJWT signedJWT = SignedJWT.parse(token);
    RSASSAVerifier verifier = new RSASSAVerifier((com.nimbusds.jose.jwk.RSAKey) jwk);
    boolean verify = signedJWT.verify(verifier);
    System.out.println(verify);

    Map<String, Object> claims = parsed.getJWTClaimsSet().getClaims();
    System.out.println(claims);

    String header = parsed.getHeader().toString();
    System.out.println(header);
  }

  @Test
  public void test2() throws Exception {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    KeyPair keyPair = kpg.generateKeyPair();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

    com.nimbusds.jose.jwk.RSAKey build = new com.nimbusds.jose.jwk.RSAKey.Builder(publicKey).privateKey(privateKey).algorithm(new Algorithm("RSA")).keyID("oidc").build();
    String json = build.toJSONObject().toJSONString(JSONStyle.NO_COMPRESS);
   // System.out.println(json);
    String compressed = build.toJSONObject().toJSONString();
    System.out.println(compressed);
  }
}
