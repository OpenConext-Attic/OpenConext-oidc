package oidc.saml;

import oidc.model.FederatedUserInfo;
import oidc.service.HashedPairwiseIdentifierService;
import oidc.user.FederatedUserInfoService;
import org.mitre.openid.connect.model.DefaultUserInfo;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.service.PairwiseIdentiferService;
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

import java.util.*;

public class DefaultSAMLUserDetailsService implements SAMLUserDetailsService {

  @Autowired
  private FederatedUserInfoService extendedUserInfoService;

  @Autowired
  private HashedPairwiseIdentifierService hashedPairwiseIdentifierService;

  @Override
  public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
    Map<String, List<String>> properties = new HashMap<>();
    String unspecifiedNameId = credential.getNameID().getValue();

    List<Attribute> attributes = credential.getAttributes();
    for (Attribute attribute : attributes) {
      String name = attribute.getName();
      List<String> values = new ArrayList<>();
      List<XMLObject> attributeValues = attribute.getAttributeValues();
      for (XMLObject xmlObject : attributeValues) {
        String value = getStringValueFromXMLObject(xmlObject);
        if (StringUtils.hasText(value)) {
          values.add(value);
        }
      }
      properties.put(name, values);
    }
    String clientId = credential.getRelayState();
    String sub = hashedPairwiseIdentifierService.getIdentifier(unspecifiedNameId, clientId);

    UserInfo existingUserInfo = extendedUserInfoService.getByUsernameAndClientId(sub, clientId);

    if (existingUserInfo == null) {
      UserInfo userInfo = this.buildUserInfo(unspecifiedNameId, sub, properties);
      extendedUserInfoService.saveUserInfo(userInfo);
    }
    return new SAMLUser(
        sub,
        unspecifiedNameId,
        flattenList(properties.get("urn:mace:terena.org:attribute-def:schacHomeOrganization")));
  }

  private String getStringValueFromXMLObject(XMLObject xmlObj) {
    if (xmlObj instanceof XSString) {
      return ((XSString) xmlObj).getValue();
    } else if (xmlObj instanceof XSAny) {
      return ((XSAny) xmlObj).getTextContent();
    }
    return null;
  }

  private UserInfo buildUserInfo(String unspecifiedNameId, String sub, Map<String, List<String>> properties) {
    FederatedUserInfo userInfo = new FederatedUserInfo();
    userInfo.setEmail(flattenList(properties.get("urn:mace:dir:attribute-def:mail")));
    userInfo.setFamilyName(flattenList(properties.get("urn:mace:dir:attribute-def:sn")));
    userInfo.setGivenName(flattenList(properties.get("urn:mace:dir:attribute-def:givenName")));
    userInfo.setSchacHomeOrganization(flattenList(properties.get("urn:mace:terena.org:attribute-def:schacHomeOrganization")));
    userInfo.setPreferredUsername(flattenList(properties.get("urn:mace:dir:attribute-def:cn")));
    userInfo.setName(flattenList(properties.get("urn:mace:dir:attribute-def:cn")));
    userInfo.setUnspecifiedNameId(unspecifiedNameId);
    userInfo.setSub(sub);
    userInfo.setNickname(flattenList(properties.get("urn:mace:dir:attribute-def:displayName")));
    return userInfo;
  }

  private String flattenList(List<String> values) {
    return CollectionUtils.isEmpty(values) ? null : values.get(0);
  }

}
