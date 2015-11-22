package oidc.service;

import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;

import java.util.Collections;

import static java.util.Collections.EMPTY_SET;
import static org.junit.Assert.*;

public class FederatedUserApprovalHandlerTest {

  private UserApprovalHandler subject = new FederatedUserApprovalHandler();
  private AuthorizationRequest request = new AuthorizationRequest("ignored", EMPTY_SET);
  private Authentication authentication = null /* is ignored */;

  @Test
  public void testIsApproved() throws Exception {
    assertTrue(subject.isApproved(request, authentication));
  }

  @Test
  public void testCheckForPreApproval() throws Exception {
    assertEquals(request, subject.checkForPreApproval(request, authentication));
  }

  @Test
  public void testUpdateAfterApproval() throws Exception {
    assertEquals(request, subject.updateAfterApproval(request, authentication));
  }

  @Test
  public void testGetUserApprovalRequest() throws Exception {
    assertEquals(0, subject.getUserApprovalRequest(request, authentication).size());
  }
}