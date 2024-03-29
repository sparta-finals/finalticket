package com.sparta.finalticket.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

	@Bean //빈등록하고 싶은 객체를 반환하는 메서드 작성
	public PasswordEncoder passwordEncoder() {//Bean에 passwordEncoder으로 빈에 주입됨
		return new BCryptPasswordEncoder();//BCryt Hash함수사용하는 메커니즘 암호화
	}
}
