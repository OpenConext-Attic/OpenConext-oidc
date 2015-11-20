package oidc.control;

import oidc.saml.DefaultServiceProviderTranslationService;
import oidc.saml.ServiceProviderTranslationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ServiceProviderTranslatorController {

  private ServiceProviderTranslationService translationService = new DefaultServiceProviderTranslationService();

  @RequestMapping("/translate-sp-entity-id")
  public Map<String, String> translate(@RequestParam String spEntityId) {
    Map<String, String> result = new HashMap<>();
    result.put("spEntityId", spEntityId);
    result.put("clientId", translationService.translateServiceProviderEntityId(spEntityId));
    return result;
  }

}
