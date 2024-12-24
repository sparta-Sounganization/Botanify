package com.sounganization.botanify.common.util;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;

@Component
public class GoogleJwtValidator {

    private static final String GOOGLE_JWK_URL = "https://www.googleapis.com/oauth2/v3/certs";
    private static final String ISSUER = "https://accounts.google.com";

    public DecodedJWT validate(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            JwkProvider provider = new UrlJwkProvider(new URL(GOOGLE_JWK_URL));
            Jwk jwk = provider.get(jwt.getKeyId());
            RSAPublicKey publicKey = (RSAPublicKey) jwk.getPublicKey();

            return JWT.require(com.auth0.jwt.algorithms.Algorithm.RSA256(publicKey, null))
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token);
        } catch (Exception e) {
            throw new CustomException(ExceptionStatus.INVALID_TOKEN);
        }
    }
}
