package oidc.saml;

import oidc.web.SetCharacterEncodingFilter;
import org.apache.commons.io.IOUtils;
import org.opensaml.saml2.binding.decoding.HTTPPostDecoder;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.ws.transport.http.HTTPInTransport;
import org.opensaml.ws.transport.http.HttpServletRequestAdapter;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class CustomHTTPPostDecoder extends HTTPPostDecoder {

    private static final Logger LOG = LoggerFactory.getLogger(CustomHTTPPostDecoder.class);

    public CustomHTTPPostDecoder( ) {
        super();
    }

    public CustomHTTPPostDecoder(ParserPool pool) {
        super(pool);
    }

    @Override
    protected InputStream getBase64DecodedMessage(HTTPInTransport transport) throws MessageDecodingException {
        if (transport instanceof HttpServletRequestAdapter) {
            HttpServletRequestAdapter adapter = (HttpServletRequestAdapter) transport;
            String encodedMessage = adapter.getParameterValue("SAMLResponse");
            if (encodedMessage != null) {
                byte[] decodedBytes = Base64.decode(encodedMessage);
                LOG.info("Request encoding {}", adapter.getCharacterEncoding());
                LOG.info("Decoded SAML message:\n{}", new String(decodedBytes));
                try {
                    LOG.info("Decoded SAML message UTF8:\n{}", new String(decodedBytes, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return super.getBase64DecodedMessage(transport);
//        try {
//            String s = new String(IOUtils.toString(inputStream, "ISO8859-1").getBytes(), "UTF-8");
//            return new ByteArrayInputStream(s.getBytes());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }
}
