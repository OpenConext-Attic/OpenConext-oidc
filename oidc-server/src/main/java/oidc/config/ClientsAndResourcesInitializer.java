package oidc.config;

import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.repository.OAuth2ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ClientsAndResourcesInitializer {

  private final Logger LOG = LoggerFactory.getLogger(ClientsAndResourcesInitializer.class);

  private final Resource clientConfLocation;

  private final TransactionTemplate transactionTemplate;

  private final OAuth2ClientRepository clientRepository;

  @Autowired
  public ClientsAndResourcesInitializer(@Value("${client.conf.location}") Resource clientConfLocation, PlatformTransactionManager transactionManager, OAuth2ClientRepository clientRepository) {
    this.clientConfLocation = clientConfLocation;
    this.transactionTemplate = new TransactionTemplate(transactionManager);
    this.clientRepository = clientRepository;
    try {
      applicationEvent();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void applicationEvent() throws IOException {
    Yaml yaml = new Yaml();
    final List<ClientDetailsEntity> clientDetails = new ArrayList<>();
    Map<String, List<Map<String, Object>>> config = (Map<String, List<Map<String, Object>>>) yaml.load(clientConfLocation.getInputStream());
    List<Map<String, Object>> clients = config.getOrDefault("clients", new ArrayList<Map<String, Object>>());
    for (int i = 0; i < clients.size(); i++) {
      clientDetails.add(client(clients.get(i)));
    }
    List<Map<String, Object>> resourceServers = config.getOrDefault("resourceServers", new ArrayList<Map<String, Object>>());
    for (int i = 0; i < resourceServers.size(); i++) {
      clientDetails.add(resourceServer(resourceServers.get(i)));
    }
    transactionTemplate.execute(new TransactionCallback<Object>() {
      @Override
      public Object doInTransaction(TransactionStatus transactionStatus) {
        for (int i = 0; i < clientDetails.size(); i++) {
          ClientDetailsEntity entity = clientDetails.get(i);
          ClientDetailsEntity existing = clientRepository.getClientByClientId(entity.getClientId());
          if (existing == null) {
            clientRepository.saveClient(entity);
          } else {
            clientRepository.updateClient(existing.getId(), entity);
          }
        }
        return null;
      }
    });
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
    List<String> scopes = (List<String>) data.get("scopes");
    if (scopes != null) {
      client.setScope(new HashSet<>(scopes));
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
    return clientDetailsEntity;
  }

//    transactionTemplate.execute(transactionStatus -> {
//      resourceServersAndClientsToPersist.forEach(clientDetails -> {
//        if (findPreExistingClientDetails(clientDetails.getClientId(), preExisting).isPresent()) {
//          clientRegistrationService.updateClientDetails(clientDetails);
//          clientRegistrationService.updateClientSecret(clientDetails.getClientId(), clientDetails.getClientSecret());
//        } else {
//          clientRegistrationService.addClientDetails(clientDetails);
//        }
//      });
//      return null;
//    });

  /*
  INSERT INTO `client_details` (`id`, `client_description`, `reuse_refresh_tokens`, `dynamically_registered`, `allow_introspection`, `id_token_validity_seconds`, `client_id`, `client_secret`, `access_token_validity_seconds`, `refresh_token_validity_seconds`, `application_type`, `client_name`, `token_endpoint_auth_method`, `subject_type`, `logo_uri`, `policy_uri`, `client_uri`, `tos_uri`, `jwks_uri`, `jwks`, `sector_identifier_uri`, `request_object_signing_alg`, `user_info_signed_response_alg`, `user_info_encrypted_response_alg`, `user_info_encrypted_response_enc`, `id_token_signed_response_alg`, `id_token_encrypted_response_alg`, `id_token_encrypted_response_enc`, `token_endpoint_auth_signing_alg`, `default_max_age`, `require_auth_time`, `created_at`, `initiate_login_uri`, `clear_access_tokens_on_refresh`)
    VALUES
      (10000000006, NULL, 1, 0, 1, 600, 'https@//authz-playground.test.surfconext.nl', 'secret', 3600, NULL, NULL, 'Authz Playground', 'SECRET_BASIC', 'PUBLIC', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 60000, 0, '2016-02-11 11:34:26', NULL, 1);
 */
}
