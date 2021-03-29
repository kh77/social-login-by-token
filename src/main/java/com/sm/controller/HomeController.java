package com.sm.controller;

import com.sm.common.exception.GenericErrorException;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Api(value = "Home Resource")
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @PostMapping("/home")
    public String getAuthorizationCode(@RequestParam("code") String authorizationCode) throws GenericErrorException {
        return authorizationCode;
    }
}
