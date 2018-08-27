package test.oauth.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import test.oauth.domain.users.UserEntity;
import test.oauth.domain.users.UserRepository;

public class CustomOAuthTokenEnhancer implements TokenEnhancer {

    @Autowired
    private UserRepository userRepo;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,
                                    OAuth2Authentication authentication) {
        String grantType = authentication.getOAuth2Request().getGrantType();
        if (grantType.equalsIgnoreCase("password")) {
	        Map<String, Object> userInfo = null;
	            String email = authentication.getUserAuthentication().getName();
				UserEntity user = userRepo.findByEmail(email);
				userInfo = new HashMap<>();
				userInfo.put("sid", user.getSid());
				userInfo.put("email", user.getEmail());
				userInfo.put("name", user.getName());
				userInfo.put("created_at", user.getCreatedAt());
				userInfo.put("last_modified_at", user.getLastModifiedAt());
	
	        Map<String, Object> additionalInfo = new HashMap<>();
	        additionalInfo.put("users", userInfo);
	        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        }
        return accessToken;
    }
}