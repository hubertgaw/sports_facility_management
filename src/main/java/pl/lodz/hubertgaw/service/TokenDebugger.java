package pl.lodz.hubertgaw.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jose4j.base64url.Base64;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TokenDebugger implements SecurityIdentityAugmentor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    Logger logger;

    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity securityIdentity, AuthenticationRequestContext authenticationRequestContext) {
        logger.info("Method augment called with arguments:{}, {}", securityIdentity, authenticationRequestContext);

        if (securityIdentity.getPrincipal() instanceof JsonWebToken) {

            JsonWebToken principal = (JsonWebToken) securityIdentity.getPrincipal();

            logger.info("JSONWebToken principal: {}", principal);

            System.out.println("Received token:");

            for (String part : principal.getRawToken().split("\\.")) {
                String decoded = new String(Base64.decode(part));
                System.out.println(toPrettyJson(decoded));

                logger.info("Received token: {}", toPrettyJson(decoded));
            }
        }
        return Uni.createFrom().item(securityIdentity);
    }

    private String toPrettyJson(String json) {
        logger.info("Method toPrettyJson called with argument:{}", json);
        try {
            Object value = objectMapper.readValue(json, Object.class);
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);

            logger.info("Pretty json got:{}", prettyJson);

            return prettyJson;
        } catch (JsonProcessingException e) {

            logger.error("JsonProcessingException", e);

            return "";
        }
    }
}