package test.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import test.oauth.common.CustomAuthenticationEntryPoint;
import test.oauth.common.CustomOAuthExceptionHandler;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private static final String RESOURCE_ID = "oauth_test_resources_id";
    private static final String SECURED_SCOPE_AND_USER_ROLE = "#oauth2.hasScope('OAUTH_TEST') and (hasAuthority('USER') or hasAuthority('ADMIN'))";
    private static final String SECURED_SCOPE_AND_ADMIN_ROLE = "#oauth2.hasScope('OAUTH_TEST') and hasAuthority('ADMIN')";

    /*
    // jwt token store start
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("God_Bless_You");
        return converter;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }
    // jwt token store end
    */

    @Autowired
    private CustomOAuthExceptionHandler customOAuthExceptionHandler;

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {

        // jdbc token store
        resources.resourceId(RESOURCE_ID).stateless(false);
        resources.authenticationEntryPoint(customAuthenticationEntryPoint);
        resources.accessDeniedHandler(customOAuthExceptionHandler);

        /*
        // jwt token store
        resources.resourceId(RESOURCE_ID).stateless(false)
                    .tokenServices(tokenServices());
        */
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.
                authorizeRequests()
                .antMatchers("/version").permitAll()
                .anyRequest().permitAll();
    }

}