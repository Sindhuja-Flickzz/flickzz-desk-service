package com.flickzz.desk.config;

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

import com.resend.*;

@Configuration
public class ResendConfig {

	@Value("${resend.api.key}")
	private String resendApiKey;

	@Bean
	public Resend resend() {
		return new Resend(resendApiKey);
	}
}
