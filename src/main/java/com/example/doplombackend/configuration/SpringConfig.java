package com.example.doplombackend.configuration;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Component
@EnableWebSecurity
@EnableMethodSecurity
public class SpringConfig {
    private RSAKey key;

    @Bean
    BearerTokenResolver bearerTokenResolver() {
        DefaultBearerTokenResolver resolver = new DefaultBearerTokenResolver();
        resolver.setBearerTokenHeaderName("auth-token");
        return resolver;
    }

    @Bean
    JWKSource<SecurityContext> jwkSource() {
        key = JwksUtils.generateKey();
        return (jwkSelector, securityContext) -> jwkSelector.select(new JWKSet(key));
    }

    @Bean
    JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    JwtDecoder jwtDecoder() throws JOSEException {
        return NimbusJwtDecoder.withPublicKey(key.toRSAPublicKey()).build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        User.UserBuilder users = User.withDefaultPasswordEncoder();
        UserDetails userDetails = users
                .username("admin")
                .password("admin")
                .authorities("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(userDetails);
    }

    @Bean
    public AuthenticationManager authManager(UserDetailsService userDetailsService) {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        return new ProviderManager(authProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(x -> x.requestMatchers("/login").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exc -> exc.authenticationEntryPoint(
                        new LoginUrlAuthenticationEntryPoint("http://localhost:8085/login"))
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8081", "http://localhost:8081/"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
