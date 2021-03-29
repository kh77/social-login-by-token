package com.sm.service.impl;


import com.sm.common.exception.GenericErrorException;
import com.sm.config.social.SocialClient;
import com.sm.controller.dto.request.*;
import com.sm.dto.response.user.UserDto;
import com.sm.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired SocialClient socialClient;

    @Override
    public UserDto validateAccessToken(SocialRequestModel socialRequestModel) throws GenericErrorException {
        UserDto userDto = new UserDto();
        String provider = socialRequestModel.getProvider().toLowerCase().trim();

        if (provider.equalsIgnoreCase("facebook")) {
            userDto = socialClient.getFacebookUser(socialRequestModel.getToken());

        } else if (provider.equalsIgnoreCase("google")) {
            userDto = socialClient.getGoogleUser(socialRequestModel.getToken());

        } else if (provider.equalsIgnoreCase("ios")) {
/*          * save the client secret and expiry date in db for apple on the first time when we create it.
            * expiry date will be set for 3 month while creating client secret.
            * next time check that secret is present and the expiry date then fetch it
             expiry date is compared with today's date if todays date is passed with expiry date then
              create new secret and expiry date and save in db so there will be no call in every request.
            * reusing same secret to call ios api
            */

            // implement above comments to set the value in clientSecret. Initially, it will be null
            String clientSecret = null;
            userDto = socialClient.getAppleUser(socialRequestModel, clientSecret);
        }
        UserDto userResponse = createUserSocial(userDto);
        return userResponse;
    }

    UserDto createUserSocial(UserDto user) throws GenericErrorException {
        // save that user in DB
        // unqiue will be provider id and provider
        // provider : facebook,apple,google
        return user;
    }
}
