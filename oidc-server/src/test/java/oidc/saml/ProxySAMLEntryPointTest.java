package oidc.saml;

import org.junit.Before;
import org.junit.Test;
import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.service.ClientDetailsEntityService;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.ws.transport.InTransport;
import org.opensaml.xml.parse.XMLParserException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.websso.WebSSOProfileOptions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static oidc.saml.ProxySAMLEntryPoint.CLIENT_DETAILS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProxySAMLEntryPointTest extends KeyStoreTest {

  private ProxySAMLEntryPoint entryPoint;
  private ClientDetailsEntityService clientDetailsEntityServiceMock;

  private static final ServiceProviderTranslationService serviceProviderTranslationService = new DefaultServiceProviderTranslationService();
  private static final String SP_ENTITY_ID = "https://example:mock@sp";
  private static final String CLIENT_ID = serviceProviderTranslationService.translateServiceProviderEntityId(SP_ENTITY_ID);

  @Before
  public void before() throws MetadataProviderException, IOException, XMLParserException {
    this.clientDetailsEntityServiceMock = mock(ClientDetailsEntityService.class);
    this.entryPoint = new ProxySAMLEntryPoint(){
      @Override
      protected void doCommence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        //we don't want to hit all the SAML stuff
      }
    };
    this.entryPoint.setServiceProviderTranslationService(new DefaultServiceProviderTranslationService());
    this.entryPoint.setClientDetailsEntityService(clientDetailsEntityServiceMock);

    ClientDetailsEntity clientDetailsEntity = new ClientDetailsEntity();
    clientDetailsEntity.setClientId(CLIENT_ID);

    when(clientDetailsEntityServiceMock.loadClientByClientId(CLIENT_ID)).thenReturn(clientDetailsEntity);
  }

  @Test
  public void testCommenceSetClient() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setParameter("client_id", CLIENT_ID);

    HttpServletResponse response = new MockHttpServletResponse();
    entryPoint.commence(request, response, null);

    assertEquals(CLIENT_ID, request.getAttribute(CLIENT_DETAILS));

  }

  @Test
  public void testGetProfileOptions() throws Exception {
    SAMLMessageContext context = mock(SAMLMessageContext.class);
    InTransport inTransport = mock(InTransport.class);
    when(context.getInboundMessageTransport()).thenReturn(inTransport);
    when(inTransport.getAttribute(CLIENT_DETAILS)).thenReturn(CLIENT_ID);
    WebSSOProfileOptions profileOptions = entryPoint.getProfileOptions(context, /* not used */null);

    String relayState = profileOptions.getRelayState();
    assertEquals(relayState, CLIENT_ID);

    Set<String> requesterIds = profileOptions.getRequesterIds();
    assertEquals(new HashSet<>(Arrays.asList(SP_ENTITY_ID)), requesterIds);

    assertTrue(profileOptions.isIncludeScoping());
  }

}