package oidc.control;

import org.junit.Test;

import java.net.URLEncoder;
import java.util.Map;

import static org.junit.Assert.*;

public class ServiceProviderTranslatorControllerTest {

  private ServiceProviderTranslatorController subject = new ServiceProviderTranslatorController();

  @Test
  public void testTranslate() throws Exception {
    String spEntityId = "https://oidc.test.surfconext.nl";
    Map<String, String> result = subject.translate(spEntityId);
    assertEquals(result.get("spEntityId"), spEntityId);
    assertEquals(result.get("clientId"), "https@//oidc.test.surfconext.nl");
  }
}