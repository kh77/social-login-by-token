package com.sm.controller.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class SocialRequestModel {
	@NotBlank(message="NOT_BLANK")
	@NotEmpty(message="NOT_BLANK")
	private String token;

	@NotBlank(message="NOT_BLANK")
	@NotEmpty(message="NOT_BLANK")
	@Pattern(regexp = "^(facebook|google|apple)$",flags = Pattern.Flag.CASE_INSENSITIVE, message = "PROVIDER_NOT_EMPTY")
	private String provider;

	private String firstName;
	private String lastName;

	public SocialRequestModel(){}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
