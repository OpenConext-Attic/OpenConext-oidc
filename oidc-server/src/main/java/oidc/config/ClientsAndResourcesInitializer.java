package oidc.config;

import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.repository.OAuth2ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.*;

@Service
@PropertySource("classpath:application.oidc.properties")
public class ClientsAndResourcesInitializer implements InitializingBean {

  private final Logger LOG = LoggerFactory.getLogger(ClientsAndResourcesInitializer.class);

  private final Resource clientConfLocation;

  private final TransactionTemplate transactionTemplate;

  private final OAuth2ClientRepository clientRepository;

  @Autowired
  public ClientsAndResourcesInitializer(@Value("${client.conf.location}") Resource clientConfLocation, PlatformTransactionManager transactionManager, OAuth2ClientRepository clientRepository) {
    this.clientConfLocation = clientConfLocation;
    this.transactionTemplate = new TransactionTemplate(transactionManager);
    this.clientRepository = clientRepository;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    try {
      applicationEvent();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected List<ClientDetailsEntity> applicationEvent() throws IOException {
    Yaml yaml = new Yaml();
    final List<ClientDetailsEntity> clientDetails = new ArrayList<>();
    Map<String, List<Map<String, Object>>> config = (Map<String, List<Map<String, Object>>>) yaml.load(clientConfLocation.getInputStream());
    List<Map<String, Object>> clients = config.get("clients");
    for (int i = 0; i < clients.size(); i++) {
      clientDetails.add(client(clients.get(i)));
    }
    List<Map<String, Object>> resourceServers = config.get("resourceServers");
    for (int i = 0; i < resourceServers.size(); i++) {
      ClientDetailsEntity entity = resourceServer(resourceServers.get(i));
      ClientDetailsEntity clientDetailsEntity = configuredClient(entity, clientDetails);
      if (clientDetailsEntity != null) {
        clientDetailsEntity.setAllowIntrospection(true);
      } else {
        clientDetails.add(entity);
      }
    }
    return transactionTemplate.execute(new TransactionCallback<List<ClientDetailsEntity>>() {
      @Override
      public List<ClientDetailsEntity> doInTransaction(TransactionStatus transactionStatus) {
        for (int i = 0; i < clientDetails.size(); i++) {
          ClientDetailsEntity entity = clientDetails.get(i);
          ClientDetailsEntity existing = clientRepository.getClientByClientId(entity.getClientId());
          if (existing == null) {
            LOG.debug("Inserting new default client {}", entity.getClientId());
            clientRepository.saveClient(entity);
          } else {
            LOG.debug("Updating new default client {}", entity.getClientId());
            clientRepository.updateClient(existing.getId(), entity);
          }
        }
        return clientDetails;
      }
    });
  }

  private ClientDetailsEntity configuredClient(ClientDetailsEntity resourceServer, List<ClientDetailsEntity> clientDetails) {
    for (int i = 0; i < clientDetails.size(); i++) {
      ClientDetailsEntity clientDetailsEntity = clientDetails.get(i);
      if (clientDetailsEntity.getClientId().equals(resourceServer.getClientId())) {
        return clientDetailsEntity;
      }
    }
    return null;
  }

  private ClientDetailsEntity resourceServer(Map<String, Object> data) {
    ClientDetailsEntity resourceServer = doGetClientDetails(data);
    resourceServer.setAllowIntrospection(true);
    return resourceServer;
  }

  private ClientDetailsEntity client(Map<String, Object> data) {
    ClientDetailsEntity client = doGetClientDetails(data);
    List<String> redirectUris = (List<String>) data.get("redirectUris");
    if (redirectUris != null) {
      client.setRedirectUris(new HashSet<>(redirectUris));
    }
    List<String> grantTypes = (List<String>) data.get("grantTypes");
    if (grantTypes != null) {
      client.setGrantTypes(new HashSet<>(grantTypes));
    }
    return client;
  }

  private ClientDetailsEntity doGetClientDetails(Map<String, Object> data) {
    ClientDetailsEntity clientDetailsEntity = new ClientDetailsEntity();
    clientDetailsEntity.setClientId((String) data.get("client_id"));
    clientDetailsEntity.setClientSecret((String) data.get("secret"));
    clientDetailsEntity.setCreatedAt(new Date());
    clientDetailsEntity.setAccessTokenValiditySeconds(86400);
    List<String> scopes = (List<String>) data.get("scopes");
    if (scopes != null) {
      clientDetailsEntity.setScope(new HashSet<>(scopes));
    }

    return clientDetailsEntity;
  }

}
