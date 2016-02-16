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
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.XSString;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml.SAMLCredential;

import java.io.IOException;
import java.util.*;

import static java.util.Collections.singletonList;
import static oidc.saml.DefaultSAMLUserDetailsService.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultSAMLUserDetailsServiceTest {

  private static final String SP_ENTITY_ID = "sp-entity-id";
  private static final String persistentId = "fd9021b35ce0e2bb4fc28d1781e6cbb9eb720fed";

  private static ObjectMapper objectMapper = new ObjectMapper();

  private DefaultSAMLUserDetailsService subject = new DefaultSAMLUserDetailsService(SP_ENTITY_ID);
  private FederatedUserInfo federatedUserInfo;
  private FederatedUserInfo saveUserInfoArgument;
  private FederatedUserInfo findByUserNameReturnValue;

  @Before
  public void before() throws IOException {
    //this can't be handled by Mockito as we need to verify the argument passed on at a later stage
    FederatedUserInfoService userInfoService = new DefaultFederatedUserInfoService() {
      public UserInfo saveUserInfo(UserInfo userInfo) {
        saveUserInfoArgument = (FederatedUserInfo) userInfo;
        return userInfo;
      }

      public UserInfo getByUsername(String username) {
        return findByUserNameReturnValue;
      }
    };
    subject.setExtendedUserInfoService(userInfoService);
    subject.setHashedPairwiseIdentifierService(new DefaultHashedPairwiseIdentifierService());

    this.federatedUserInfo = objectMapper.readValue(new ClassPathResource("model/federated_user_info.json").getInputStream(), FederatedUserInfo.class);
    this.findByUserNameReturnValue = null;
  }

  @Test
  public void testLoadUserBySAMLWithPersistentIdentifier() throws Exception {
    SAMLCredential credential = SAMLTestHelper.parseSAMLCredential(SP_ENTITY_ID, "saml/assertionResponse.xml");
    SAMLUser user = (SAMLUser) subject.loadUserBySAML(credential);

    //because relay state equals the OIDC SP
    assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));

    assertEquals(persistentId, user.getUsername());
    assertEquals(federatedUserInfo.toString(), saveUserInfoArgument.toString());
  }

  @Test
  public void testReprovisionUserWhenAttributesChange() throws Exception {
    findByUserNameReturnValue = new FederatedUserInfo();
    findByUserNameReturnValue.setSchacHomeOrganizationType("outdated");

    SAMLCredential samlCredential = SAMLTestHelper.parseSAMLCredential("http://mock-sp", "saml/schacHomeOrganizationTypeAssertionResponse.xml");

    subject.loadUserBySAML(samlCredential);

    assertEquals("different", saveUserInfoArgument.getSchacHomeOrganizationType());
  }

  @Test
  public void testLoadSAmlUserWithoutPersistentIdentifier() throws Exception {
    SAMLCredential samlCredential = SAMLTestHelper.parseSAMLCredential("http://mock-sp", "saml/noEduPersonTargetedIdassertionResponse.xml");

    SAMLUser samlUser = (SAMLUser) subject.loadUserBySAML(samlCredential);

    assertEquals("83e491ac-aede-378e-b7cb-40347f03d786", samlUser.getUsername());
  }

  @Test
  public void testLoadUserBySAMLWithEmptyAttributes() throws Exception {
    SAMLCredential samlCredential = SAMLTestHelper.parseSAMLCredential("http://mock-sp", "saml/noAttributesAssertionResponse.xml");

    SAMLUser samlUser = (SAMLUser) subject.loadUserBySAML(samlCredential);

    assertEquals("fd9021b35ce0e2bb4fc28d1781e6cbb9eb720fed", samlUser.getUsername());

    FederatedUserInfo emptyUserInfo = new FederatedUserInfo();
    emptyUserInfo.setSub(samlUser.getUsername());
    emptyUserInfo.setUnspecifiedNameId("urn:collab:person:example.com:test");
    emptyUserInfo.setAuthenticatingAuthority("http://mock-idp");
    emptyUserInfo.setEduPersonTargetedId("fd9021b35ce0e2bb4fc28d1781e6cbb9eb720fed");

    assertEquals(emptyUserInfo.toString(), saveUserInfoArgument.toString());
  }

}