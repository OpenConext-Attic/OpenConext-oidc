package oidc.control;

import oidc.AbstractTestIntegration;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.Test;
import org.mitre.oauth2.model.ClientDetailsEntity;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class ClientControllerTestIntegration extends AbstractTestIntegration {

    private TestRestTemplate template = new TestRestTemplate("user", "secret");

    private String oidcApiPrefix = "oidc_api_prefix";
    private String clientId = oidcApiPrefix + "://client";

    @Before
    public void before() throws IOException {
        BasicDataSource ds = getBasicDataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
        jdbcTemplate.update("delete from client_details where client_id like '" + oidcApiPrefix + "%'");
    }

    @Test
    public void getClientDetailsEntity() {
        String clientId = "https@//oidc.localhost.surfconext.nl";
        ClientDetailsEntity entity = template.getForEntity(serverUrl + "/oidc/api/clients?clientId=" + clientId, ClientDetailsEntity.class).getBody();

        assertEquals(clientId, entity.getClientId());
        assertEquals(5, entity.getRedirectUris().size());
    }

    @Test
    public void createClientDetailsEntity() {
        ClientDetailsEntity entity = new ClientDetailsEntity();

        entity.setClientId(clientId);
        HttpEntity<ClientDetailsEntity> requestEntity = new HttpEntity<>(entity, headers());
        ClientDetailsEntity result = template.exchange(serverUrl + "/oidc/api/clients", HttpMethod.POST, requestEntity, ClientDetailsEntity.class).getBody();
        assertEquals(clientId, result.getClientId());
    }

    protected HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "application/json");
        return headers;
    }

    @Test
    public void updateClientDetailsEntity() {
        String clientId = "https@//oidc.localhost.surfconext.nl";
        ClientDetailsEntity entity = template.getForEntity(serverUrl + "/oidc/api/clients?clientId=" + clientId, ClientDetailsEntity.class).getBody();
        entity.setClientDescription("Updated description");
        HttpEntity<ClientDetailsEntity> requestEntity = new HttpEntity<>(entity, headers());
        ClientDetailsEntity result = template.exchange(serverUrl + "/oidc/api/clients", HttpMethod.PUT, requestEntity, ClientDetailsEntity.class).getBody();
        assertEquals(entity.getClientDescription(), result.getClientDescription());
    }

    @Test
    public void deleteClientDetailsEntity() {
        this.createClientDetailsEntity();
        template.delete(serverUrl + "/oidc/api/clients?clientId=" + clientId, ClientDetailsEntity.class);

        HttpStatus statusCode = template.getForEntity(serverUrl + "/oidc/api/clients?clientId=" + clientId, ClientDetailsEntity.class).getStatusCode();
        assertEquals(HttpStatus.NOT_FOUND, statusCode);
    }
}