package oidc.saml;

import org.opensaml.saml2.metadata.provider.AbstractMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.IOException;

public class ResourceMetadataProvider extends AbstractMetadataProvider {

  private Resource resource;

  public ResourceMetadataProvider(Resource resource) {
    this.resource = resource;
    Assert.isTrue(resource.exists());
  }

  @Override
  protected XMLObject doGetMetadata() throws MetadataProviderException {
    try {
      return super.unmarshallMetadata(this.resource.getInputStream());
    } catch (UnmarshallingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
