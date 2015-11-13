package oidc.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.JsonObject;
import oidc.service.DefaultHashedPairwiseIdentifierService;
import oidc.service.HashedPairwiseIdentifierService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class FederatedUserInfoTest {
  private ObjectMapper objectMapper = new ObjectMapper();
  private FederatedUserInfo federatedUserInfo ;

  @Before
  public void before() throws IOException {
    this.federatedUserInfo = objectMapper.readValue(new ClassPathResource("model/federated_user_info.json").getInputStream(), FederatedUserInfo.class);
  }

  @Test
  public void testFromJson() throws IOException {
    JsonObject jsonObject = federatedUserInfo.toJson();

    assertEquals("uid2, uid1", jsonObject.getAsJsonPrimitive("uid").getAsString());
    assertEquals("student, faculty", jsonObject.getAsJsonPrimitive("eduPersonAffiliation").getAsString());
    //TODO more asserts
  }
}