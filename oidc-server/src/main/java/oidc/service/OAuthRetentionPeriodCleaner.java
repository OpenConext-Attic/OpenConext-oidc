package oidc.service;

import oidc.model.FederatedUserInfo;
import oidc.repository.FederatedUserInfoRepository;
import org.mitre.oauth2.service.impl.DefaultOAuth2AuthorizationCodeService;
import org.mitre.oauth2.service.impl.DefaultOAuth2ProviderTokenService;
import org.mitre.openid.connect.service.impl.DefaultApprovedSiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newScheduledThreadPool;

@Component("oauthRetentionPeriodCleaner")
public class OAuthRetentionPeriodCleaner {

  private static final Logger LOG = LoggerFactory.getLogger(OAuthRetentionPeriodCleaner.class);

  private DefaultOAuth2ProviderTokenService oAuth2ProviderTokenService;

  private DefaultApprovedSiteService approvedSiteService;

  private DefaultOAuth2AuthorizationCodeService oAuth2AuthorizationCodeService;

  private FederatedUserInfoRepository federatedUserInfoRepository;

  @Autowired
  public OAuthRetentionPeriodCleaner(@Value("${resource.cleaner.period}") int delay,
                                     @Value("${resource.cleaner.cronJobResponsible}") boolean cronJobResponsible,
                                     DefaultOAuth2ProviderTokenService oAuth2ProviderTokenService,
                                     DefaultApprovedSiteService approvedSiteService,
                                     DefaultOAuth2AuthorizationCodeService oAuth2AuthorizationCodeService,
                                     FederatedUserInfoRepository federatedUserInfoRepository) {
      this.oAuth2ProviderTokenService = oAuth2ProviderTokenService;
      this.oAuth2AuthorizationCodeService = oAuth2AuthorizationCodeService;
      this.approvedSiteService = approvedSiteService;
      this.federatedUserInfoRepository = federatedUserInfoRepository;
      if (cronJobResponsible) {
          newScheduledThreadPool(1).scheduleWithFixedDelay(new Runnable() {
              @Override
              public void run() {
                  clean();
              }
          }, delay, delay, TimeUnit.MINUTES);
      }
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
