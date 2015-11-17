package oidc.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import oidc.AbstractTestIntegration;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

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
    assertEquals("student, faculty", jsonObject.getAsJsonPrimitive("edu_person_affiliation").getAsString());

  }
}