package com.sm.controller;

import com.sm.common.error.dto.ErrorDto;
import com.sm.common.error.type.ErrorType;
import com.sm.common.exception.GenericErrorException;
import com.sm.controller.dto.request.SocialRequestModel;
import com.sm.dto.response.user.UserDto;
import com.sm.service.UserService;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping("/users")
@Api(value = "Social Login Resource")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;


    @PostMapping("/login")
    @ApiOperation(value = "Validate with social provider and get user-info", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(response = UserDto.class, code = 200, message = ""),
            @ApiResponse(response = ErrorDto.class, code = 404, message = ""),
            @ApiResponse(response = ErrorDto.class, code = 500, message = "Internal server error")})
    public UserDto checkLogin(@Valid @RequestBody SocialRequestModel socialRequestModel) throws GenericErrorException {
        validateForIOS(socialRequestModel);
        return userService.validateAccessToken(socialRequestModel);
    }

    /**
     * Validate for ios : first name and last name
     * @param socialRequestModel
     */
    private void validateForIOS(SocialRequestModel socialRequestModel) {

        if (socialRequestModel.getProvider().equalsIgnoreCase("ios")) {
            boolean flagFirstName = StringUtils.isBlank(socialRequestModel.getFirstName());

            if (flagFirstName || StringUtils.isBlank(socialRequestModel.getLastName())) {
                String value = (flagFirstName ? "first name" : " last name");
                throw new GenericErrorException(ErrorType.NOT_BLANK.getCode(),
                        String.format(ErrorType.NOT_BLANK.getMessage(), value));
            }
        }
    }
}
