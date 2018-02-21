package oidc.saml;

import org.apache.velocity.app.VelocityEngine;
import org.opensaml.saml2.binding.encoding.HTTPPostEncoder;
import org.opensaml.ws.message.decoder.MessageDecoder;
import org.opensaml.ws.message.encoder.MessageEncoder;
import org.opensaml.xml.parse.ParserPool;
import org.springframework.security.saml.processor.HTTPPostBinding;

public class CustomHTTPPostBinding extends HTTPPostBinding {

    public CustomHTTPPostBinding(ParserPool parserPool, VelocityEngine velocityEngine) {
        this(parserPool, new CustomHTTPPostDecoder(parserPool), new HTTPPostEncoder(velocityEngine, "/templates/saml2-post-binding.vm"));
    }

    public CustomHTTPPostBinding(ParserPool parserPool, MessageDecoder decoder, MessageEncoder encoder) {
        super(parserPool, new CustomHTTPPostDecoder(parserPool), encoder);
    }
}
