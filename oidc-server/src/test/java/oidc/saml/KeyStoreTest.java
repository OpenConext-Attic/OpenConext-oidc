package oidc.saml;

import org.springframework.core.io.ClassPathResource;
import org.springframework.security.saml.key.JKSKeyManager;

import java.io.IOException;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class KeyStoreTest {

  protected final Properties properties;

  public KeyStoreTest() {
    this.properties = new Properties();
    try {
      properties.load(new ClassPathResource("application.oidc.properties").getInputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  protected JKSKeyManager createJksKeyManager() {
    KeyStoreLocator keyStoreLocator = new KeyStoreLocator();

    Map<String, String> passwords = new HashMap<>();
    passwords.put(properties.getProperty("sp.entity.id"), properties.getProperty("sp.passphrase"));

    KeyStore keyStore = keyStoreLocator.createKeyStore(
        properties.getProperty("idp.entity.id"),
        properties.getProperty("idp.public.certificate"),
        properties.getProperty("sp.entity.id"),
        properties.getProperty("sp.public.certificate"),
        properties.getProperty("sp.private.key"),
        properties.getProperty("sp.passphrase"));

    return new JKSKeyManager(keyStore, passwords, properties.getProperty("sp.entity.id"));
  }

}
