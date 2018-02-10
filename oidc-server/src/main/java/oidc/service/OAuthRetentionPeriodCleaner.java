package oidc.service;

import oidc.model.FederatedUserInfo;
import oidc.repository.FederatedUserInfoRepository;
import org.mitre.oauth2.service.impl.DefaultOAuth2AuthorizationCodeService;
import org.mitre.oauth2.service.impl.DefaultOAuth2ProviderTokenService;
import org.mitre.openid.connect.service.impl.DefaultApprovedSiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newScheduledThreadPool;

@Component("oauthRetentionPeriodCleaner")
@Profile({"!local"})
public class OAuthRetentionPeriodCleaner {

  private static final Logger LOG = LoggerFactory.getLogger(OAuthRetentionPeriodCleaner.class);

  @Autowired
  private DefaultOAuth2ProviderTokenService oAuth2ProviderTokenService;

  @Autowired
  private DefaultApprovedSiteService approvedSiteService;

  @Autowired
  private DefaultOAuth2AuthorizationCodeService oAuth2AuthorizationCodeService;

  @Autowired
  private FederatedUserInfoRepository federatedUserInfoRepository;

  public OAuthRetentionPeriodCleaner() {
    newScheduledThreadPool(1).scheduleWithFixedDelay(new Runnable() {
      @Override
      public void run() {
        clean();
      }
    }, 15, 15, TimeUnit.MINUTES);
  }

  private void clean() {
      try {
          long start = System.currentTimeMillis();
          LOG.info("Starting to clean up all expired access tokens and associated user info.");
          oAuth2ProviderTokenService.clearExpiredTokens();
          approvedSiteService.clearExpiredSites();
          oAuth2AuthorizationCodeService.clearExpiredAuthorizationCodes();
          Set<FederatedUserInfo> users = federatedUserInfoRepository.findOrphanedFederatedUserInfos();
          for (FederatedUserInfo user : users) {
              LOG.info("removing FederatedUserInfo {} which has no access_tokens or refresh_tokens", user.getUnspecifiedNameId());
              federatedUserInfoRepository.removeFederatedUserInfo(user);
          }
          long took = System.currentTimeMillis() - start;
          LOG.info("Finished cleaning up all expired access tokens and associated user info in {} ms.", took);
      } catch (Throwable t) {
          // Deliberate catch-all to prevent the scheduling to stop
          LOG.error("Exception in cleaning up all expired access tokens and associated user info.", t);
      }
  }

}
