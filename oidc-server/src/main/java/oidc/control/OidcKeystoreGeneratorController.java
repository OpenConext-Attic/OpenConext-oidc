package oidc.control;

import com.nimbusds.jose.Algorithm;
import net.minidev.json.JSONStyle;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class OidcKeystoreGeneratorController {

    @RequestMapping("/generate-oidc-keystore")
    public String generate() throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
        kpg.initialize(2048);
        KeyPair keyPair = kpg.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        com.nimbusds.jose.jwk.RSAKey build = new com.nimbusds.jose.jwk.RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .algorithm(new Algorithm("RS256"))
            .keyID(UUID.randomUUID().toString())
            .build();

        return build.toJSONObject().toJSONString(JSONStyle.NO_COMPRESS);
    }
}
