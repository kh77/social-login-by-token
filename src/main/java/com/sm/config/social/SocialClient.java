package com.sm.config.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.sm.common.error.type.ErrorType;
import com.sm.common.exception.GenericErrorException;
import com.sm.common.util.DateUtil;
import com.sm.common.util.SecureStringUtil;
import com.sm.config.social.ios.IOSClient;
import com.sm.config.social.user.*;
import com.sm.controller.dto.request.SocialRequestModel;
import com.sm.dto.response.user.UserDto;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SocialClient {
    private static final Logger logger = LoggerFactory.getLogger(SocialClient.class);

    // FACEBOOK
    private final String FACEBOOK_GRAPH_API_BASE = "https://graph.facebook.com";
    private final String FACEBOOK_PATH = "/me?fields={fields}&redirect={redirect}&access_token={access_token}";
    private final String FACEBOOK_FIELDS= "id,gender,first_name,middle_name,last_name,name,email,verified,is_verified,picture.width(250).height(250)";

    // GOOGLE
    //private final String GOOGLE_PATH = "?scope={scope}&access_token={access_token}";
    private final String GOOGLE_PATH = "?id_token={id_token}";
    private final String GOOGLE_SCOPE = "email,profile";
    //private final String GOOGLE_API_BASE ="https://www.googleapis.com/oauth2/v3/userinfo";
    private final String GOOGLE_API_BASE ="https://oauth2.googleapis.com/tokeninfo";

    @Autowired private RestTemplate restTemplate;
    @Autowired private IOSClient appleClient;


    /**
     * fetch facebook user data and convert to UserDto
     * @param accessToken
     * @return
     */

    public UserDto getFacebookUser(String accessToken) {
       // https://graph.facebook.com/v3.0/me?fields=id,first_name,middle_name,last_name,name,email,verified,is_verified,picture.width(250).height(250)
        UserDto userDto = null;
        try {
            final Map<String, String> variables = new HashMap<>();
            variables.put("fields", FACEBOOK_FIELDS);
            variables.put("redirect", "false");
            variables.put("access_token", accessToken);
            FacebookUser facebookUser = restTemplate.getForObject(FACEBOOK_GRAPH_API_BASE + FACEBOOK_PATH, FacebookUser.class, variables);
            userDto = convertTo(facebookUser);
            userDto.setProvider("facebook");
        }catch (Exception e){
            logger.error("While fetching facebook data, getting Exception :",e);
            throw new GenericErrorException(ErrorType.TOKEN_BAD_REQUEST.getCode(),ErrorType.TOKEN_BAD_REQUEST.getMessage(),e);
        }
        return userDto;
    }

    /**
     * fetch google user data and convert to UserDto
     * @param accessToken
     * @return
     */
    public UserDto getGoogleUser(String accessToken) {
        UserDto userDto = null;
        // https://www.googleapis.com/oauth2/v3/userinfo?access_token={access_token}
        try{
             //   String scope = "https://www.googleapis.com/auth/userinfo.email,https://www.googleapis.com/auth/userinfo.profile,https://www.googleapis.com/auth/user.gender.read,openid";
            final Map<String, String> variables = new HashMap<>();
          //  variables.put("scope", GOOGLE_SCOPE);
            //variables.put("access_token", accessToken);
            variables.put("id_token", accessToken);
            GoogleUser googleUser = restTemplate.getForObject(GOOGLE_API_BASE + GOOGLE_PATH, GoogleUser.class, variables);
            userDto = convertTo(googleUser);
            userDto.setProvider("google");
        }catch (Exception e){
            logger.error("While fetching google data, getting Exception :",e);
            throw new GenericErrorException(ErrorType.TOKEN_BAD_REQUEST.getCode(),ErrorType.TOKEN_BAD_REQUEST.getMessage(),e);
        }
        return userDto;
    }

    /**
     * fetch apple user data and convert to UserDto
     * @param socialRequestModel
     * @return
     */
    public UserDto getAppleUser(SocialRequestModel socialRequestModel,String clientSecret) {
        UserDto userDto = null;

        // if secret receive null then create new secret with expiry date of 3 months.
        if(clientSecret == null) {
            clientSecret = appleClient.getClientSecret();
            // expiry 3 month
            Date addedDate = DateUtil.addMonthInCurrentDate(3);
            // save Apple expiry date and secret in db
        }

        IOSInfo appleInfo =appleClient.retrieveData(clientSecret, socialRequestModel.getToken());
        userDto = convertTo(appleInfo);
        userDto.setProvider("apple");

        if(socialRequestModel.getFirstName() != null)
            userDto.setFirstName(socialRequestModel.getFirstName());
        if(socialRequestModel.getLastName() != null)
            userDto.setLastName(socialRequestModel.getLastName());

        return userDto;
    }


    /**
     * fetch linkedin user data and convert to UserDto
     * @param authorizationCode
     * @return
     */
    public UserDto getLinkedinInfo(String authorizationCode){
        String redirectUrl = "http://localhost:8080/home";
        String clientId = "hello";
        String clientSecret = "world";
        //authorization code for access token
        String accessTokenUri ="https://www.linkedin.com/oauth/v2/accessToken?grant_type=authorization_code&code="+authorizationCode+"&redirect_uri="+redirectUrl+"&client_id="+clientId+"&client_secret="+clientSecret+"";
        String accessTokenRequest = restTemplate.getForObject(accessTokenUri, String.class);
        JSONObject jsonObjOfAccessToken = new JSONObject(accessTokenRequest);
        String accessToken = jsonObjOfAccessToken.get("access_token").toString();

        //trade your access token
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " +accessToken);
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        // linkedin api to get linkedidn profile detail
        String profileUri = "https://api.linkedin.com/v2/me";
        ResponseEntity<String> linkedinDetailRequest = restTemplate.exchange(profileUri, HttpMethod.GET, entity, String.class);

        LinkedinInfo linkedinInfo = null;
        //response below for profile detail
        //{"localizedLastName":"world","lastName":{"localized":{"en_US":"world"},"preferredLocale":{"country":"US","language":"en"}},"firstName":{"localized":{"en_US":"hello"},"preferredLocale":{"country":"US","language":"en"}},"id":"abcdef","localizedFirstName":"hello"}

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            linkedinInfo = objectMapper.readValue(linkedinDetailRequest.getBody(), LinkedinInfo.class);
        } catch (Exception e) {
            logger.error("While fetching linkedin data, getting Exception :",e);
            throw new GenericErrorException(ErrorType.TOKEN_BAD_REQUEST.getCode(),ErrorType.TOKEN_BAD_REQUEST.getMessage(),e);
        }

        linkedinInfo = getLinkedinEmailInfo(entity, linkedinInfo);
        UserDto userDto = convertTo(linkedinInfo);
        userDto.setProvider("linkedin");
        return userDto;
    }

    /**
     * get email from linkedin
     * @param entity
     * @param linkedinInfo
     * @return
     */
    private LinkedinInfo getLinkedinEmailInfo(HttpEntity<String> entity, LinkedinInfo linkedinInfo) {
        ResponseEntity<String> linkedinDetailRequest;
        String emailUri = "https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))";
        linkedinDetailRequest = restTemplate.exchange(emailUri, HttpMethod.GET, entity, String.class);

        //{"elements":[{"handle":"urn:li:emailAddress:1000001","handle~":{"emailAddress":"helloworld@gmail.com"}}]}
        String  ee = "$['elements'][0]['handle']['emailAddress']";
        DocumentContext jsonContext = JsonPath.parse(linkedinDetailRequest.getBody());
        linkedinInfo.setEmail(jsonContext.read("$['elements'][0]['handle~']['emailAddress']"));
        return linkedinInfo;
    }

    /**
     * Create user data from social platform
     * @param genericUser
     * @return
     */
    private UserDto convertTo(GenericUser genericUser) {
        UserDto userDto = new UserDto();
        userDto.setProviderId(genericUser.getId());
        userDto.setEmail(genericUser.getEmail());
        userDto.setFirstName(genericUser.getFirstName());
        userDto.setLastName(genericUser.getLastName());
        userDto.setGender(genericUser.getGender());
        userDto.setPassword(SecureStringUtil.randomString(30));
        return userDto;
    }

}
