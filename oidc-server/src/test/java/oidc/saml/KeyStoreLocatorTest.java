package oidc.saml;

import org.junit.Test;
import org.opensaml.xml.security.credential.Credential;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.saml.key.JKSKeyManager;

import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static junit.framework.TestCase.assertEquals;

public class KeyStoreLocatorTest extends KeyStoreTest {

  @Test
  public void testCreateKeyStore() throws Exception {
    JKSKeyManager jksKeyManager = createJksKeyManager();

    Credential defaultCredential = jksKeyManager.getDefaultCredential();
    assertEquals("RSA", defaultCredential.getPrivateKey().getAlgorithm());
    assertEquals("RSA", defaultCredential.getPublicKey().getAlgorithm());

    X509Certificate certificate = jksKeyManager.getCertificate(properties.getProperty("idp.entity.id"));
    String name = certificate.getSubjectDN().getName();
    assertEquals("O=Internet Widgits Pty Ltd, ST=Some-State, C=AU", name);
  }

}