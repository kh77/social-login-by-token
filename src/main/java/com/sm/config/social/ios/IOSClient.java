package com.sm.config.social.ios;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sm.common.error.type.ErrorType;
import com.sm.common.exception.GenericErrorException;
import com.sm.common.util.DateUtil;
import com.sm.common.util.FileUtil;
import com.sm.config.social.user.IOSInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Service
public class IOSClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(IOSClient.class);

    private static final String APPLE_AUTH_URL = "https://appleid.apple.com/auth/token";
    private static final String RESOURCE_FOLDER_NAME = "ios/";

    @Autowired RestTemplate restTemplate;
    @Autowired IOSConfig appleConfig;

    public String getClientSecret() throws GenericErrorException {
        String inputKeyFile = getFileData();

        String clientSecret = "";
        try {
            final byte[] keyBytes = java.util.Base64.getDecoder().decode(inputKeyFile);
            final KeyFactory keyFactory = KeyFactory.getInstance("EC");
            final PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));


            // see https://developer.apple.com/documentation/signinwithapplerestapi/generate_and_validate_tokens
            final JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                    .keyID(appleConfig.getKid())
                    .build();

            final JWTClaimsSet claimsSet = createClaim();

            clientSecret = generateClientSecret((java.security.interfaces.ECPrivateKey) privateKey,
                    header, claimsSet);

        } catch (Exception e) {
            LOGGER.error("Exception while creating secret of apple :",e);
            throw new GenericErrorException(ErrorType.TOKEN_BAD_REQUEST.getCode(),ErrorType.TOKEN_BAD_REQUEST.getMessage(),e);
        }
        return clientSecret;
    }

    private String generateClientSecret(java.security.interfaces.ECPrivateKey privateKey, JWSHeader header, JWTClaimsSet claimsSet) throws JOSEException {
        String clientSecret;
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        JWSSigner signer = new ECDSASigner(privateKey);
        signedJWT.sign(signer);
        clientSecret = signedJWT.serialize();
        return clientSecret;
    }

    private JWTClaimsSet createClaim() {
        // expiry 3 month
        final Date timestamp = new Date();
        final Date addedDate = DateUtil.addMonthInCurrentDate(3);

        return new JWTClaimsSet.Builder()
                .issuer(appleConfig.getTeamId())
                .issueTime(timestamp)
                .expirationTime(addedDate)
                .audience("https://appleid.apple.com")
                .subject(appleConfig.getClientId())
                .build();
    }

    private String getFileData(){
        String inputKeyFile = FileUtil.readFileFromClassPath(RESOURCE_FOLDER_NAME + appleConfig.getFileName());
        inputKeyFile = inputKeyFile.replaceFirst("-----BEGIN PRIVATE KEY-----", "");
        inputKeyFile = inputKeyFile.replaceFirst("-----END PRIVATE KEY-----", "");
        inputKeyFile = inputKeyFile.replaceAll("\\s", "");
        return inputKeyFile;
    }

    public IOSInfo retrieveData(String clientSecret, String authorizationCode) throws GenericErrorException{
        IOSInfo iosInfo = new IOSInfo();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", appleConfig.getClientId()); // app_id like com.app.id
            map.add("client_secret", clientSecret);
            map.add("grant_type", "authorization_code");
            map.add("code", authorizationCode);  // JWT code we got from iOS
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            TokenResponse response = restTemplate.postForObject(APPLE_AUTH_URL, request, TokenResponse.class);
            if(response != null) {
                String idToken = response.getId_token();
                if(idToken != null) {
                    String payload = idToken.split("\\.")[1];// 0 is header we ignore it for now
                    String decode = new String(Base64.getDecoder().decode(payload));
                    ObjectMapper mapper = new ObjectMapper();
                    DecodeResponse decodeResponse = mapper.readValue(decode, DecodeResponse.class);

                    iosInfo.setEmail(decodeResponse.getEmail() != null ? decodeResponse.getEmail() : StringUtils.EMPTY);
                    iosInfo.setId(decodeResponse.getSub());
                }
            }
        } catch (Exception exception) {
            LOGGER.error("Exception while retrieving data of apple user:",exception);
            throw new GenericErrorException(ErrorType.TOKEN_BAD_REQUEST.getCode(),ErrorType.TOKEN_BAD_REQUEST.getMessage(),exception);
        }
        return iosInfo;
    }
}
