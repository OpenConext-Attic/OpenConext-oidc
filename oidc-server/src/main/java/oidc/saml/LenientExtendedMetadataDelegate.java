package oidc.saml;

import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;

public class LenientExtendedMetadataDelegate extends ExtendedMetadataDelegate {

  public LenientExtendedMetadataDelegate(MetadataProvider delegate) {
    super(delegate);
  }

//  @Override
//  public boolean requireValidMetadata() {
//    return false;
//  }
//
//  @Override
//  protected boolean isTrustFiltersInitialized() {
//    return false;
//  }
//
//  @Override
//  public boolean isMetadataRequireSignature() {
//    return false;
//  }
//
//  @Override
//  public boolean isMetadataTrustCheck() {
//    return false;
//  }
}
