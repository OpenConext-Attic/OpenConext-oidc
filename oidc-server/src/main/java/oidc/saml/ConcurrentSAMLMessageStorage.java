package oidc.saml;

import org.opensaml.xml.XMLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.saml.parser.SAMLObject;
import org.springframework.security.saml.storage.SAMLMessageStorage;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Set;

/**
 * Colpy & paste of org.springframework.security.saml.storage.HttpSessionStorage wich can't handle concurrent requests
 * in the same session
 */
public class ConcurrentSAMLMessageStorage implements SAMLMessageStorage {
    private static final Logger LOG = LoggerFactory.getLogger(ProxySAMLEntryPoint.class);

    private final HttpSession session;

    private Hashtable<String, SAMLObject<XMLObject>> internalMessages;

    private static final String SAML_STORAGE_KEY = "_springSamlStorageKey";

    public ConcurrentSAMLMessageStorage(HttpServletRequest request) {
        this.session = request.getSession(true);
    }

    public void storeMessage(String messageID, XMLObject message) {
        LOG.info("Storing message {} to session {}", messageID, session.getId());
        Hashtable<String, SAMLObject<XMLObject>> messages = getMessages();
        messages.put(messageID, new SAMLObject<>(message));
        updateSession(messages);
    }

    public XMLObject retrieveMessage(String messageID) {
        Hashtable<String, SAMLObject<XMLObject>> messages = getMessages();
        SAMLObject o = messages.get(messageID);
        if (o == null) {
            LOG.info("Message {} not found in session {}", messageID, session.getId());
            return null;
        } else {
            LOG.info("Message {} found in session {}, removing the key", messageID, session.getId());
            messages.remove(messageID);
            updateSession(messages);
            return o.getObject();
        }
    }

    private Hashtable<String, SAMLObject<XMLObject>> getMessages() {
        if (internalMessages == null) {
            internalMessages = initializeSession();
        }
        return internalMessages;
    }

    @SuppressWarnings("unchecked")
    private Hashtable<String, SAMLObject<XMLObject>> initializeSession() {
        Hashtable<String, SAMLObject<XMLObject>> messages = (Hashtable<String, SAMLObject<XMLObject>>) session.getAttribute(SAML_STORAGE_KEY);
        if (messages == null) {
            synchronized (session) {
                messages = (Hashtable<String, SAMLObject<XMLObject>>) session.getAttribute(SAML_STORAGE_KEY);
                if (messages == null) {
                    messages = new Hashtable<String, SAMLObject<XMLObject>>();
                    updateSession(messages);
                }
            }
        }
        return messages;
    }

    private void updateSession(Hashtable<String, SAMLObject<XMLObject>> messages) {
        session.setAttribute(SAML_STORAGE_KEY, messages);
    }
}
