package oidc.saml;

import com.fasterxml.jackson.databind.ObjectMapper;
import oidc.model.FederatedUserInfo;
import oidc.service.DefaultHashedPairwiseIdentifierService;
import oidc.user.DefaultFederatedUserInfoService;
import oidc.user.FederatedUserInfoService;
import org.junit.Before;
import org.junit.Test;
import org.mitre.openid.connect.model.UserInfo;
import org.opensaml.saml2.core.*;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSString;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml.SAMLCredential;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.EMPTY_LIST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultSAMLUserDetailsServiceTest {

  private static final String SP_ENTITY_ID = "sp-entity-id";

  private static ObjectMapper objectMapper = new ObjectMapper();

  private DefaultSAMLUserDetailsService subject = new DefaultSAMLUserDetailsService(SP_ENTITY_ID);
  private FederatedUserInfo federatedUserInfo;
  private FederatedUserInfo saveUserInfoArgument;
  private NameID nameId;

  @Before
  public void before() throws IOException {
    FederatedUserInfoService userInfoService = new DefaultFederatedUserInfoService() {
      public UserInfo saveUserInfo(UserInfo userInfo) {
        saveUserInfoArgument = (FederatedUserInfo) userInfo;
        return userInfo;
      }

      public UserInfo getByUsername(String username) {
        return null;
      }
    };
    subject.setExtendedUserInfoService(userInfoService);
    subject.setHashedPairwiseIdentifierService(new DefaultHashedPairwiseIdentifierService());

    this.federatedUserInfo = objectMapper.readValue(new ClassPathResource("model/federated_user_info.json").getInputStream(), FederatedUserInfo.class);

    this.nameId = mock(NameID.class);
    when(nameId.getValue()).thenReturn(federatedUserInfo.getUnspecifiedNameId());

  }

  @Test
  public void testLoadUserBySAML() throws Exception {
    List<Attribute> attributes = Arrays.asList(
        getAttribute("urn:mace:dir:attribute-def:cn", federatedUserInfo.getName()),
        getAttribute("urn:mace:dir:attribute-def:displayName", federatedUserInfo.getPreferredUsername()),
        getAttribute("urn:mace:dir:attribute-def:givenName", federatedUserInfo.getGivenName()),
        getAttribute("urn:mace:dir:attribute-def:sn", federatedUserInfo.getFamilyName()),
        getAttribute("urn:mace:dir:attribute-def:preferredLanguage", federatedUserInfo.getLocale()),
        getAttribute("urn:mace:dir:attribute-def:mail", federatedUserInfo.getEmail()),
        getAttribute("urn:mace:terena.org:attribute-def:schacHomeOrganization", federatedUserInfo.getSchacHomeOrganization()),
        getAttribute("urn:mace:terena.org:attribute-def:schacHomeOrganizationType", federatedUserInfo.getSchacHomeOrganizationType()),
        getAttribute("urn:mace:dir:attribute-def:eduPersonAffiliation", federatedUserInfo.getEduPersonAffiliations()),
        getAttribute("urn:mace:dir:attribute-def:isMemberOf", federatedUserInfo.getIsMemberOfs()),
        getAttribute("urn:mace:dir:attribute-def:eduPersonEntitlement", federatedUserInfo.getEduPersonEntitlements()),
        getAttribute("urn:mace:dir:attribute-def:schacPersonalUniqueCode", federatedUserInfo.getSchacPersonalUniqueCodes()),
        getAttribute("urn:mace:dir:attribute-def:eduPersonPrincipalName", federatedUserInfo.getEduPersonPrincipalName()),
        getAttribute("urn:mace:dir:attribute-def:uid", federatedUserInfo.getUids()),
        getAttribute("urn:mace:dir:attribute-def:eduPersonTargetedID", federatedUserInfo.getEduPersonTargetedId()),
        getAttribute("urn:mace:dir:attribute-def:eduPersonScopedAffiliation", federatedUserInfo.getEduPersonScopedAffiliations())
    );
    SAMLCredential samlCredential = new SAMLCredential(nameId, mockAssertion(), "remoteEntityID", SP_ENTITY_ID, attributes, "localEntityID");
    SAMLUser user = (SAMLUser) subject.loadUserBySAML(samlCredential);

    //because relay state equals the OIDC SP
    assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));

    assertEquals("75726e3a-636f-6c6c-6162-3a706572736f", user.getUsername());
    assertEquals(federatedUserInfo.toString(), saveUserInfoArgument.toString());
  }

  //saml2 library is hard to instantiate
  private Assertion mockAssertion() {
    Assertion assertion = mock(Assertion.class);
    AuthnStatement statement = mock(AuthnStatement.class);
    AuthnContext authnContext = mock(AuthnContext.class);
    AuthenticatingAuthority authenticatingAuthority = mock(AuthenticatingAuthority.class);
    when(authenticatingAuthority.getURI()).thenReturn("http://mock-idp");
    when(authnContext.getAuthenticatingAuthorities()).thenReturn(Arrays.asList(authenticatingAuthority));
    when(statement.getAuthnContext()).thenReturn(authnContext);
    when(assertion.getAuthnStatements()).thenReturn(Arrays.asList(statement));
    return assertion;
  }

  @Test
  public void testLoadUserBySAMLWithEmptyAttributes() throws Exception {
    SAMLUser emptySamlUser = (SAMLUser) subject.loadUserBySAML(new SAMLCredential(
        nameId, mockAssertion(), "remoteEntityID", "relayState", EMPTY_LIST, "localEntityID"));

    assertEquals("75726e3a-636f-6c6c-6162-3a706572736f", emptySamlUser.getUsername());

    FederatedUserInfo emptyUserInfo = new FederatedUserInfo();
    emptyUserInfo.setSub(emptySamlUser.getUsername());
    emptyUserInfo.setUnspecifiedNameId(nameId.getValue());
    emptyUserInfo.setAuthenticatingAuthority(this.federatedUserInfo.getAuthenticatingAuthority());

    assertEquals(emptyUserInfo.toString(), saveUserInfoArgument.toString());
  }

  private Attribute getAttribute(String name, String value) {
    return getAttribute(name, Arrays.asList(value));
  }

  //org.opensaml.xmltooling has protected constructors
  private Attribute getAttribute(String name, Collection<String> values) {
    Attribute attribute = mock(Attribute.class);
    when(attribute.getName()).thenReturn(name);
    List<XMLObject> attributes = new ArrayList<>();
    for (String value : values) {
      XSString xsString = mock(XSString.class);
      when(xsString.getValue()).thenReturn(value);
      attributes.add(xsString);
    }
    when(attribute.getAttributeValues()).thenReturn(attributes);
    return attribute;
  }


}