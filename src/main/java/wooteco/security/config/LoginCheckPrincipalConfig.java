package wooteco.security.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import wooteco.security.web.LoginCheckPrincipalArgumentResolver;

@Configuration
public class LoginCheckPrincipalConfig implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List argumentResolvers) {
        argumentResolvers.add(createLoginCheckPrincipleArgumentResolver());
    }

    @Bean
    public LoginCheckPrincipalArgumentResolver createLoginCheckPrincipleArgumentResolver() {
        return new LoginCheckPrincipalArgumentResolver();
    }
}
