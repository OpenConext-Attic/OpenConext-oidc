package oidc.i18n;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResourceBundleTranslator {

  private ObjectMapper objectMapper = new ObjectMapper();

  private String baseUrl = "http://mymemory.translated.net/api/get";

  //User-Agent:
  private String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36";

  private Map<String, Object> getMessages() throws IOException {
    Map map = objectMapper.readValue(new ClassPathResource("messages.json").getInputStream(), Map.class);
    return map;
  }

  private String translate(String text) throws IOException {
    String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
        .queryParam("q", text)
        .queryParam("langpair", "en|nl")
        .queryParam("de", "oharsta@gmail.com")
        .queryParam("ip", "82.217.81.220")
        .build(false).encode().toUriString();

    Map<String, String> requestProperties = new HashMap<>();
    requestProperties.put("User-Agent", userAgent);

    RequestPropertiesURLResource resource = new RequestPropertiesURLResource(url, requestProperties);

    Map map = objectMapper.readValue(resource.getInputStream(), Map.class);
    Map responseData = (Map) map.get("responseData");
    if (responseData != null) {
      String translatedText = (String) responseData.get("translatedText");
      if (translatedText != null) {
        return translatedText;
      }
    }
    // something went wrong
    return text;
  }

  private void translateMap(Map<String, Object> messages) throws IOException {
    Set<Map.Entry<String, Object>> entries = messages.entrySet();

    for (Map.Entry<String, Object> entry : entries) {
      Object value = entry.getValue();
      if (value instanceof String) {
        messages.put(entry.getKey(), translate((String) value));
      } else {
        translateMap((Map) value);
      }
    }
  }

  @Ignore
  @Test
  public void doTranslate() throws IOException {
    Map<String, Object> messages = getMessages();
    translateMap(messages);
    String result = objectMapper.writeValueAsString(messages);
    System.out.println(result);
  }

}
