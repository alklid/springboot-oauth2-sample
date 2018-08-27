package test.oauth.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.Collection;
import java.util.HashMap;

@Component
public class CustomOAuthTokenExtractor {

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    public HashMap<String, Object> getAddInfo(OAuth2Authentication authentication) {
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
        OAuth2AccessToken accessToken = tokenStore().readAccessToken(details.getTokenValue());
        HashMap<String, Object> users = (HashMap<String, Object>) accessToken.getAdditionalInformation().get("users");
        return users;
    }
    
    public boolean removeAuthToken(String userName) {
		Collection<OAuth2AccessToken> tokens = ((JdbcTokenStore)tokenStore()).findTokensByUserName(userName);
		if (tokens!=null) {
            for (OAuth2AccessToken token:tokens){
				OAuth2RefreshToken refreshToken = token.getRefreshToken();
				((JdbcTokenStore)tokenStore()).removeAccessTokenUsingRefreshToken(refreshToken);
				((JdbcTokenStore)tokenStore()).removeRefreshToken(refreshToken);                	
            }
            return true;
		}
		return false;
    }
    
    public String getClientId(OAuth2Authentication authentication) {
		if (authentication == null) {
			return null;
		}
		return authentication.getOAuth2Request().getClientId();
    }
    
    public Long getUserSid(OAuth2Authentication authentication) {
		if (authentication == null) {
			return null;
		}
		HashMap<String, Object> users = getAddInfo(authentication);
		return (Long)users.get("sid");
    }
    
}
