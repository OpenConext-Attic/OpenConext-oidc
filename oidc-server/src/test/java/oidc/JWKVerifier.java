package oidc;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.util.Assert;

import java.text.ParseException;

// Not thread-safe. Instantiate for every verify action
public class JWKVerifier {

  private final JWKSet jwkSet;
  private final SignedJWT signedJWT;

  /*
   * The specified string representing a JSON Web Key (JWK) set.
   */
  public JWKVerifier(String jwkKeys, String token) throws ParseException {
    this.jwkSet = JWKSet.parse(jwkKeys);
    this.signedJWT = SignedJWT.parse(token);
  }

  public boolean verifySigned(String kid) throws ParseException, JOSEException {
    JWK jwk = this.jwkSet.getKeyByKeyId(kid);
    Assert.notNull(jwk, "No JWK known with kid: " + kid);
    RSASSAVerifier verifier = new RSASSAVerifier((com.nimbusds.jose.jwk.RSAKey) jwk);
    return signedJWT.verify(verifier);
  }

  public JWSHeader header() {
    return signedJWT.getHeader();
  }

  public JWTClaimsSet claims() throws ParseException {
    return signedJWT.getJWTClaimsSet();
  }
}
