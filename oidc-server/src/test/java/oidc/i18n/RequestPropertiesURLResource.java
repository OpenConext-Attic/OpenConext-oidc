package oidc.i18n;

import org.springframework.core.io.UrlResource;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

public class RequestPropertiesURLResource extends UrlResource {

  private final Map<String, String> requestProperties;

  public RequestPropertiesURLResource(String path, Map<String, String> requestProperties) throws MalformedURLException {
    super(path);
    this.requestProperties = requestProperties;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    URLConnection con = this.getURLConnection();
    ResourceUtils.useCachesIfNecessary(con);
    try {
      return con.getInputStream();
    }
    catch (IOException ex) {
      // Close the HTTP connection (if applicable).
      if (con instanceof HttpURLConnection) {
        ((HttpURLConnection) con).disconnect();
      }
      throw ex;
    }
  }

  private URLConnection getURLConnection() throws IOException {
    URLConnection urlConnection = getURL().openConnection();
    Set<Map.Entry<String, String>> entries = this.requestProperties.entrySet();
    for (Map.Entry<String, String> entry: entries) {
      urlConnection.setRequestProperty (entry.getKey(), entry.getValue());
    }
    return urlConnection;
  }
}
