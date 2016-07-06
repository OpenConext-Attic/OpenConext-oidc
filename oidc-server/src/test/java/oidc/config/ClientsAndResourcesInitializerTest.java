package oidc.config;

import org.junit.Test;
import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.repository.OAuth2ClientRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ClientsAndResourcesInitializerTest {

  @Test
  public void testApplicationEvent() throws Exception {
    PlatformTransactionManager transactionManager = mock(PlatformTransactionManager.class);
    OAuth2ClientRepository clientRepository = mock(OAuth2ClientRepository.class);

    ClientsAndResourcesInitializer subject = new ClientsAndResourcesInitializer(new ClassPathResource("clientsAndResources.yml"),transactionManager, clientRepository);
    List<ClientDetailsEntity> entities = subject.applicationEvent();

    assertEquals(3, entities.size());

    ClientDetailsEntity entity = byClientId(entities, "https@//default-client-1");

    assertFalse(entity.isAllowIntrospection());
    assertEquals(4, entity.getGrantTypes().size());

    entity = byClientId(entities, "https@//default-client-2");
    assertTrue(entity.isAllowIntrospection());
    assertEquals(entity.getScope().size(), 1);

    entity = byClientId(entities, "https@//default-resource-server2");
    assertTrue(entity.isAllowIntrospection());
    assertEquals(entity.getScope().size(), 2);
  }

  private ClientDetailsEntity byClientId(List<ClientDetailsEntity> entities, String clientId) {
    for (int i = 0; i < entities.size(); i++) {
      ClientDetailsEntity entity = entities.get(i);
      if (entity.getClientId().equals(clientId)) {
        return entity;
      }
    }
    return null;
  }

}