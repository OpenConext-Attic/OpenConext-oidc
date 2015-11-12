package oidc.saml;

import oidc.ExtendedUserInfoService;
import org.mitre.openid.connect.model.DefaultUserInfo;
import org.mitre.openid.connect.model.UserInfo;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AuthenticatingAuthority;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.XSString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultSAMLUserDetailsService implements SAMLUserDetailsService {

  @Autowired
  private ExtendedUserInfoService extendedUserInfoService;

  @Override
  public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
    Map<String, String> properties = new HashMap<>();
    String nameId = credential.getNameID().getValue();
    properties.put("nameId", nameId);

    List<Attribute> attributes = credential.getAttributes();
    for (Attribute attribute : attributes) {
      String name = attribute.getName();
      List<XMLObject> attributeValues = attribute.getAttributeValues();
      for (XMLObject xmlObject : attributeValues) {
        String value = getStringValueFromXMLObject(xmlObject);
        if (StringUtils.hasText(value)) {
          //we don't support multi value attribute values - soo overwrite if there are multiple
          properties.put(name, value);
        }
      }
    }
    UserInfo existingUserInfo = extendedUserInfoService.getByUsername(nameId);
    UserInfo userInfo = this.buildUserInfo(nameId, properties);
    if (existingUserInfo == null) {
      extendedUserInfoService.saveUserInfo(userInfo);
    }
    String authenticatingAuthority = null;
    List<AuthnStatement> authnStatements = credential.getAuthenticationAssertion().getAuthnStatements();
    if (!CollectionUtils.isEmpty(authnStatements)) {
      List<AuthenticatingAuthority> authenticatingAuthorities = authnStatements.get(0).getAuthnContext().getAuthenticatingAuthorities();
      if (!CollectionUtils.isEmpty(authenticatingAuthorities)) {
        authenticatingAuthority = authenticatingAuthorities.get(0).getURI();
      }
    }
    return new SAMLUser(userInfo.getSub(), userInfo.getProfile(), userInfo.getName(), authenticatingAuthority, userInfo.getEmail());
  }

  private String getStringValueFromXMLObject(XMLObject xmlObj) {
    if (xmlObj instanceof XSString) {
      return ((XSString) xmlObj).getValue();
    } else if (xmlObj instanceof XSAny) {
      return ((XSAny) xmlObj).getTextContent();
    }
    return null;
  }

  private UserInfo buildUserInfo(String nameId, Map<String, String> properties) {
    DefaultUserInfo userInfo = new DefaultUserInfo();
    userInfo.setEmail(properties.get("urn:mace:dir:attribute-def:mail"));
    userInfo.setFamilyName(properties.get("urn:mace:dir:attribute-def:sn"));
    userInfo.setGivenName(properties.get("urn:mace:dir:attribute-def:givenName"));
    //This looks strange, but preferred username is the property where OIDC fetches users by
    userInfo.setPreferredUsername(nameId);
    userInfo.setProfile(properties.get("urn:mace:terena.org:attribute-def:schacHomeOrganization"));
    userInfo.setName(properties.get("urn:mace:dir:attribute-def:cn"));
    userInfo.setSub(nameId);
    userInfo.setNickname(properties.get("urn:mace:dir:attribute-def:displayName"));
    return userInfo;
  }

}
