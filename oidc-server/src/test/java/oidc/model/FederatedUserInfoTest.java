package oidc.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
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

    assertEquals(
        "fbf446e918287b50f057c2d616d9c23f1d1ee838c7aa9e62683e94e6907711f8969d33c09d8abd332b58b583b6df0b26296ee94f69aa2d63380208c90b2f1b5b",
        jsonObject.getAsJsonPrimitive("sub").getAsString());
    assertEquals("student, faculty", jsonObject.getAsJsonPrimitive("edu_person_affiliation").getAsString());

  }
}