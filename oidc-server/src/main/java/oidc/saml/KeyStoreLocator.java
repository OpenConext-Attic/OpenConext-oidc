package oidc.saml;

import org.apache.commons.io.IOUtils;
import org.apache.commons.ssl.Base64;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;

public class KeyStoreLocator {

  public KeyStore createKeyStore(String idpEntityId,
                                 String idpCertificate,
                                 String spEntityId,
                                 String spCertificate,
                                 //This must be in the DER unencrypted PKCS#8 format. See README.md
                                 String spPrivateKey,
                                 String pemPassPhrase) {
    try {
      KeyStore keyStore = KeyStore.getInstance("JKS");
      keyStore.load(null, pemPassPhrase.toCharArray());

      addCertificate(keyStore, idpEntityId, idpCertificate);
      addPrivateKey(keyStore, spEntityId, spPrivateKey, spCertificate, pemPassPhrase);

      return keyStore;
    } catch (Exception e) {
      //too many exceptions we can't handle, so brute force catch
      throw new RuntimeException(e);
    }
  }

  private void addPrivateKey(KeyStore keyStore, String alias, String privateKey, String certificate, String password) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, KeyStoreException, CertificateException {
    String wrappedCert = wrapCert(certificate);
    byte[] decodedKey = Base64.decodeBase64(privateKey.getBytes());

    char[] passwordChars = password.toCharArray();
    CertificateFactory certFact = CertificateFactory.getInstance("X.509");
    Certificate cert = certFact.generateCertificate(new ByteArrayInputStream(wrappedCert.getBytes()));
    ArrayList<Certificate> certs = new ArrayList<>();
    certs.add(cert);

    byte[] privKeyBytes = IOUtils.toByteArray(new ByteArrayInputStream(decodedKey));

    KeySpec ks = new PKCS8EncodedKeySpec(privKeyBytes);
    RSAPrivateKey privKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(ks);
    keyStore.setKeyEntry(alias, privKey, passwordChars, certs.toArray(new Certificate[certs.size()]));
  }

  private void addCertificate(KeyStore keyStore, String alias, String certificate) throws CertificateException, KeyStoreException {
    String wrappedCert = wrapCert(certificate);
    ByteArrayInputStream certificateInputStream = new ByteArrayInputStream(wrappedCert.getBytes());
    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
    Certificate cert = certificateFactory.generateCertificate(certificateInputStream);
    IOUtils.closeQuietly(certificateInputStream);
    keyStore.setCertificateEntry(alias, cert);
  }

  private String wrapCert(String certificate) {
    return "-----BEGIN CERTIFICATE-----\n" + certificate + "\n-----END CERTIFICATE-----";
  }

}
