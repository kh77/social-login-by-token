package com.sm.config.social;

import com.sm.common.error.type.ErrorType;
import com.sm.common.exception.GenericErrorException;
import com.sm.common.util.DateUtil;
import com.sm.common.util.SecureStringUtil;
import com.sm.config.social.ios.IOSClient;
import com.sm.config.social.user.IOSInfo;
import com.sm.config.social.user.FacebookUser;
import com.sm.config.social.user.GoogleUser;
import com.sm.controller.dto.request.SocialRequestModel;
import com.sm.dto.response.user.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

        if(socialRequestModel.getFirstName() != null)
            userDto.setFirstName(socialRequestModel.getFirstName());
        if(socialRequestModel.getLastName() != null)
            userDto.setLastName(socialRequestModel.getLastName());

        return userDto;
    }

    private UserDto convertTo(FacebookUser facebookUser) {
        UserDto userDto = new UserDto();
        userDto.setProviderId(facebookUser.getId());
        userDto.setProvider("facebook");
        userDto.setEmail(facebookUser.getEmail());
        userDto.setFirstName(facebookUser.getFirstName());
        userDto.setLastName(facebookUser.getLastName());
        userDto.setPassword(SecureStringUtil.randomString(30));
        return userDto;
    }

    private UserDto convertTo(GoogleUser googleUser) {
        UserDto userDto = new UserDto();
        userDto.setProviderId(googleUser.getSub());
        userDto.setProvider("google");
        userDto.setEmail(googleUser.getEmail());
        userDto.setFirstName(googleUser.getGivenName());
        userDto.setLastName(googleUser.getFamilyName());
        userDto.setPassword(SecureStringUtil.randomString(30));
        userDto.setPicture(googleUser.getPicture());
        return userDto;
    }

    private UserDto convertTo(IOSInfo appleInfo) {
        UserDto userDto = new UserDto();
        userDto.setProviderId(appleInfo.getId());
        userDto.setProvider("ios");
        userDto.setEmail(appleInfo.getEmail());
        userDto.setPassword(SecureStringUtil.randomString(30));
        return userDto;
    }
}
