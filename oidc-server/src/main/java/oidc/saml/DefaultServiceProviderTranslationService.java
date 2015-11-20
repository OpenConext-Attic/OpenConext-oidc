package oidc.saml;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.regex.Pattern;

@Service("defaultServiceProviderTranslationService")
public class DefaultServiceProviderTranslationService implements ServiceProviderTranslationService{

  private static Pattern clientIdFirst = Pattern.compile("(?<!@)@(?!@)");
  private static Pattern clientIdSecond = Pattern.compile("@@");

  private static Pattern entityIdFirst = Pattern.compile("@");
  private static Pattern entityIdSecond = Pattern.compile(":");


  @Override
  public String translateServiceProviderEntityId(String entityId) {
    Assert.notNull(entityId);
    String part1 = entityIdFirst.matcher(entityId).replaceAll("@@");
    String part2 = entityIdSecond.matcher(part1).replaceAll("@");
    return part2;
  }

  @Override
  public String translateClientId(String clientId) {
    Assert.notNull(clientId);
    String part1 = clientIdFirst.matcher(clientId).replaceAll(":");
    String part2 = clientIdSecond.matcher(part1).replaceAll("@");
    return part2;
  }

}
