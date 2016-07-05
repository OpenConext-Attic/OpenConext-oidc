package oidc.config;

import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.service.ClientDetailsEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@PropertySource("classpath:application.oidc.properties")
public class ClientsAndResourcesInitializer {

  private final Logger LOG = LoggerFactory.getLogger(ClientsAndResourcesInitializer.class);

  private final Resource clientConfLocation;

  private final TransactionTemplate transactionTemplate;

  private final ClientDetailsEntityService clientService;

  @Autowired
  public ClientsAndResourcesInitializer(@Value("${client.conf.location}") Resource clientConfLocation, PlatformTransactionManager transactionManager, ClientDetailsEntityService clientService) {
    this.clientConfLocation = clientConfLocation;
    this.transactionTemplate = new TransactionTemplate(transactionManager);
    this.clientService = clientService;
    try {
      applicationEvent();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void applicationEvent() throws IOException {
    Yaml yaml = new Yaml();
    Map<String, List<Map<String, Object>>> config = (Map<String, List<Map<String, Object>>>) yaml.load(clientConfLocation.getInputStream());
    config.get("clients").stream().map();
    clientService.loadClientByClientId(clientId);
    clientService.saveNewClient(client)
    //client_id, https@//authz-playground.test.surfconext.nl
    //secret
    //redirectUris
    //scopes
    //grantTypes
    //
  }

  private ClientDetailsEntity client(Map<String, Object> data) {
    ClientDetailsEntity client = new ClientDetailsEntity();
    client.setClientId((String) data.get("client_id"));
    client.setClientSecret((String) data.get("secret"));
    List<String> redirectUris = data.get("redirectUris");
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
