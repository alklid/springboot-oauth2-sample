package test.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

import test.oauth.common.CustomSecurityPermissionEvaluator;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

	@Autowired
    private CustomSecurityPermissionEvaluator permissionEvaluator;
	
	@Autowired
    private ApplicationContext applicationContext;
	
    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
    	DefaultMethodSecurityExpressionHandler expressionHandler = new OAuth2MethodSecurityExpressionHandler();
    	expressionHandler.setPermissionEvaluator(permissionEvaluator);
    	expressionHandler.setApplicationContext(applicationContext);
    	return expressionHandler;
    }
}