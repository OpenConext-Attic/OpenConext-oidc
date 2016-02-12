package oidc.saml;

import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.*;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.parse.BasicParserPool;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.saml.SAMLCredential;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class SAMLTestHelper {

  static {
    try {
      DefaultBootstrap.bootstrap();
    } catch (ConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  private static BasicParserPool ppMgr = new BasicParserPool();
  private static UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();

  public static SAMLCredential parseSAMLCredential(String spEntityId, String assertionPath) throws Exception {
    Document document = ppMgr.parse(new ClassPathResource(assertionPath).getInputStream());
    Element metadataRoot = document.getDocumentElement();

    Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(metadataRoot);
    Response response = (Response) unmarshaller.unmarshall(metadataRoot);
    Assertion assertion = response.getAssertions().get(0);

    //this is normally done by Spring security SAML
    List<Attribute> attributes = new ArrayList<>();
    for (AttributeStatement attStatement : assertion.getAttributeStatements()) {
      for (Attribute att : attStatement.getAttributes()) {
        attributes.add(att);
      }
    }

    NameID nameID = assertion.getSubject().getNameID();
    return new SAMLCredential(nameID, assertion, "remoteEntityID", spEntityId, attributes, "localEntityID");
  }
}
