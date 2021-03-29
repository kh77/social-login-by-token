package com.sm.service;

import com.sm.common.exception.GenericErrorException;
import com.sm.controller.dto.request.*;
import com.sm.dto.response.user.UserDto;

public interface UserService {
    UserDto validateAccessToken(SocialRequestModel token) throws GenericErrorException;
}