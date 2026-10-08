package com.example.delivery.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // @WebMvcTest 같은 슬라이스 테스트가 깨지지 않도록 메인 클래스에서 분리
public class JpaConfig {
}
