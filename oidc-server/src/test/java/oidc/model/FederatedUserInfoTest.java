package oidc.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import oidc.AbstractTestIntegration;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class FederatedUserInfoTest {

  private ObjectMapper objectMapper = new ObjectMapper();
  private FederatedUserInfo federatedUserInfo;

  @Before
  public void before() throws IOException {
    this.federatedUserInfo = objectMapper.readValue(new ClassPathResource("model/federated_user_info.json").getInputStream(), FederatedUserInfo.class);
  }

  @Test
  public void testFromJson() throws IOException {
    JsonObject jsonObject = federatedUserInfo.toJson();

    assertEquals(AbstractTestIntegration.SUB, jsonObject.getAsJsonPrimitive("sub").getAsString());
    assertEquals("John Doe", jsonObject.getAsJsonPrimitive("name").getAsString());
    assertEquals("John Doe", jsonObject.getAsJsonPrimitive("preferred_username").getAsString());
    assertEquals("John", jsonObject.getAsJsonPrimitive("given_name").getAsString());
    assertEquals("Doe", jsonObject.getAsJsonPrimitive("family_name").getAsString());
    assertEquals("NL", jsonObject.getAsJsonPrimitive("locale").getAsString());
    assertEquals("john.doe@example.org", jsonObject.getAsJsonPrimitive("email").getAsString());
    assertEquals("surfnet.nl", jsonObject.getAsJsonPrimitive("schac_home_organization").getAsString());
    assertEquals("institution", jsonObject.getAsJsonPrimitive("schac_home_organization_type").getAsString());
    assertEquals("principal_name", jsonObject.getAsJsonPrimitive("edu_person_principal_name").getAsString());
    assertEquals("fd9021b35ce0e2bb4fc28d1781e6cbb9eb720fed", jsonObject.getAsJsonPrimitive("edu_person_targeted_id").getAsString());
    assertCommaSeparatedStringEquality(
        new String[]{"student", "faculty"},
        jsonObject.getAsJsonPrimitive("edu_person_affiliation").getAsString()
    );
    assertCommaSeparatedStringEquality(
        new String[]{"student", "faculty"},
        jsonObject.getAsJsonPrimitive("edu_person_scoped_affiliation").getAsString()
    );
    assertEquals("surfnet", jsonObject.getAsJsonPrimitive("is_member_of").getAsString());
    assertCommaSeparatedStringEquality(
        new String[]{"http://xstor.com/contracts/HEd123", "urn:mace:washington.edu:confocalMicroscope"},
        jsonObject.getAsJsonPrimitive("edu_person_entitlement").getAsString()
    );
    assertEquals("personal", jsonObject.getAsJsonPrimitive("schac_personal_unique_code").getAsString());
    assertEquals("uid2, uid1", jsonObject.getAsJsonPrimitive("uid").getAsString());

  }

  //The ordering is not constant
  private void assertCommaSeparatedStringEquality(String[] expected, String actual) {
    Set<String> expectedSet = new HashSet<>(Arrays.asList(expected));
    Set<String> actualSet = new HashSet<>(Arrays.asList(actual.split(", ")));
    assertEquals(expectedSet, actualSet);

  }
}