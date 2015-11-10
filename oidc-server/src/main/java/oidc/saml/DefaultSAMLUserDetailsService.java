package oidc.saml;

import oidc.ExtendedUserInfoService;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.XSString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

import java.util.List;

public class DefaultSAMLUserDetailsService implements SAMLUserDetailsService {

  @Autowired
  private ExtendedUserInfoService extendedUserInfoService;

  @Override
  public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
    String name = credential.getNameID().getValue();
    List<Attribute> attributes = credential.getAttributes();
    for (Attribute attribute: attributes) {
      String att = attribute.getName();
      System.out.println("---------");
      System.out.println(att);
      if ("urn:mace:dir:attribute-def:eduPersonTargetedID".equalsIgnoreCase(att)) {
        System.out.println("here");
      }
      List<XMLObject> attributeValues = attribute.getAttributeValues();
      for (XMLObject value: attributeValues) {
        System.out.println(getStringValueFromXMLObject(value));
      }
    }
    return new SAMLUser("urn:collab:person:example.com:admin", "surfnet.nl", "John Doe", "http://mock-idp", "john.doe@example.org");
  }

  public String getStringValueFromXMLObject(XMLObject xmlObj) {
    if (xmlObj instanceof XSString) {
      return ((XSString) xmlObj).getValue();
    } else if (xmlObj instanceof XSAny) {
      return ((XSAny) xmlObj).getTextContent();
    }
    return null;
  }
}
