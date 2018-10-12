package oidc.control;

import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.repository.OAuth2ClientRepository;
import org.mitre.oauth2.service.ClientDetailsEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping(value = "/oidc/api/clients", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {

    private static final Logger LOG = LoggerFactory.getLogger(ClientController.class);

    private String user;
    private String password;
    private ClientDetailsEntityService clientService;
    private OAuth2ClientRepository clientRepository;

    @Autowired
    public ClientController(@Value("${client.api.user}") String user,
                            @Value("${client.api.password}") String password,
                            ClientDetailsEntityService clientService,
                            OAuth2ClientRepository clientRepository) {
        this.clientService = clientService;
        this.clientRepository = clientRepository;
        this.user = user;
        this.password = password;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getClientDetailsEntity(@RequestParam("clientId") String clientId, HttpServletRequest request) throws UnsupportedEncodingException {
        Optional<ResponseEntity> responseEntity = checkCredentials(request);
        if (responseEntity.isPresent()) {
            return responseEntity.get();
        }
        Optional<ClientDetailsEntity> entityOptional = getClientDetailsEntity(clientId);
        //can not use map as of Type restrictions
        if (entityOptional.isPresent()) {
            return ResponseEntity.ok(entityOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createClientDetailsEntity(@RequestBody ClientDetailsEntity clientDetailsEntity, HttpServletRequest request) throws UnsupportedEncodingException {
        Optional<ResponseEntity> responseEntity = checkCredentials(request);
        if (responseEntity.isPresent()) {
            return responseEntity.get();
        }
        ClientDetailsEntity entry = clientRepository.getClientByClientId(clientDetailsEntity.getClientId());
        if (entry != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "ClientId is already used"));
        }
        LOG.info("Creating ClientID {}", clientDetailsEntity.getClientId());
        ClientDetailsEntity result = clientService.saveNewClient(clientDetailsEntity);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateClientDetailsEntity(@RequestBody ClientDetailsEntity clientDetailsEntity, HttpServletRequest request) throws UnsupportedEncodingException {
        Optional<ResponseEntity> responseEntity = checkCredentials(request);
        if (responseEntity.isPresent()) {
            return responseEntity.get();
        }
        Optional<ClientDetailsEntity> entityOptional = getClientDetailsEntity(clientDetailsEntity.getClientId());
        if (!entityOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        LOG.info("Updating ClientID {}", clientDetailsEntity.getClientId());
        ClientDetailsEntity result = clientService.updateClient(entityOptional.get(), clientDetailsEntity);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity deleteClientDetailsEntity(@RequestParam("clientId") String clientId, HttpServletRequest request) throws UnsupportedEncodingException {
        Optional<ResponseEntity> responseEntity = checkCredentials(request);
        if (responseEntity.isPresent()) {
            return responseEntity.get();
        }
        Optional<ClientDetailsEntity> entity = getClientDetailsEntity(clientId);
        if (!entity.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        LOG.info("Deleting ClientID {}", clientId);
        clientService.deleteClient(entity.get());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private Optional<ClientDetailsEntity> getClientDetailsEntity(String clientId) {
        ClientDetailsEntity existing = clientRepository.getClientByClientId(clientId);
        return Optional.ofNullable(existing);
    }

    private Optional<ResponseEntity> checkCredentials(HttpServletRequest request) throws UnsupportedEncodingException {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Basic ")) {
            return unauthorized();
        }

        byte[] base64Token = header.substring(6).getBytes(Charset.defaultCharset());
        byte[] decoded;
        try {
            decoded = Base64.decode(base64Token);
        } catch (IllegalArgumentException e) {
            return unauthorized();
        }

        String token = new String(decoded, Charset.defaultCharset());
        int delimiter = token.indexOf(":");

        if (delimiter == -1) {
            return unauthorized();
        }
        String user = token.substring(0, delimiter);
        String password = token.substring(delimiter + 1);
        if (!this.user.equals(user) || !this.password.equals(password)) {
            return unauthorized();
        }
        return Optional.empty();
    }

    private Optional<ResponseEntity> unauthorized() {
        return Optional.of(ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("WWW-Authenticate", "Basic realm=\"OIDC\"").build());
    }
}
