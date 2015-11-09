package oidc;

import com.nimbusds.jose.jwk.JWK;
import org.junit.Test;
import org.mitre.jose.keystore.JWKSetKeyStore;
import org.mitre.jwt.signer.service.JWTSigningAndValidationService;
import org.mitre.jwt.signer.service.impl.DefaultJWTSigningAndValidationService;
import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.oauth2.service.ClientDetailsEntityService;
import org.mitre.openid.connect.config.ConfigurationPropertiesBean;
import org.mitre.openid.connect.model.DefaultUserInfo;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.service.UserInfoService;
import org.mitre.openid.connect.service.impl.DefaultOIDCTokenService;
import org.mitre.openid.connect.token.ConnectTokenEnhancer;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class OidcKeystoreTest {

  @Test
  public void testKeystoreJwks() throws Exception {
    ConfigurationPropertiesBean bean = new ConfigurationPropertiesBean();
    bean.setIssuer("http://localhost");
    ConnectTokenEnhancer enhancer = new ConnectTokenEnhancer();
    enhancer.setClientService(Mockito.mock(ClientDetailsEntityService.class));
    enhancer.setConfigBean(bean);

    Field userInfoServiceField = ConnectTokenEnhancer.class.getDeclaredField("userInfoService");
    userInfoServiceField.setAccessible(true);
    UserInfoService userInfoService = Mockito.mock(UserInfoService.class);
    UserInfo userInfo = new DefaultUserInfo();
    userInfo.setSub("urn:collab:id");

    Mockito.when(userInfoService.getByUsernameAndClientId("clientId","clientId")).thenReturn(userInfo);
    userInfoServiceField.set(enhancer, userInfoService);

    JWKSetKeyStore keyStore = new JWKSetKeyStore();
    keyStore.setLocation(new ClassPathResource("oidc.keystore.jwks.json"));

    DefaultJWTSigningAndValidationService jwtService = new DefaultJWTSigningAndValidationService(keyStore);
    jwtService.setDefaultSignerKeyId("oidc");
    jwtService.setDefaultSigningAlgorithmName("RS256");

    enhancer.setJwtService(jwtService);

    OAuth2AccessTokenEntity accessToken = new OAuth2AccessTokenEntity();
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_MONTH, 1);
    accessToken.setExpiration(cal.getTime());
    Map<String, String> requestParameters = new HashMap<>();
    requestParameters.put("scope","test");
    OAuth2Request storedRequest = new OAuth2Request(requestParameters, "clientId",
        Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")), true, new HashSet<>(Arrays.asList("test")),
        new HashSet<String>(), "redirectUri", new HashSet<>(Arrays.asList("code")),
        null);
    Authentication userAuthentication = new OAuth2Authentication(storedRequest,null);
    OAuth2Authentication authentication = new OAuth2Authentication(storedRequest, userAuthentication);
    OAuth2AccessToken enhanced = enhancer.enhance(accessToken, authentication);
    System.out.println(enhanced.getValue());

    Map<String, JWK> keys = jwtService.getAllPublicKeys();

  }

}
