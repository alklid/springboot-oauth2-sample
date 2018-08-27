package test.oauth.common;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.AbstractOAuth2SecurityExceptionHandler;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomOAuthExceptionHandler extends AbstractOAuth2SecurityExceptionHandler implements AccessDeniedHandler {
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException authException)
            throws IOException, ServletException {
        new DefineException.InvalidOAuth2(request, response, authException).handle("access");
    }

    public static class AuthTokenException {
        public static ResponseEntity getException(OAuth2Exception oAuth2Exception) {
            ErrorResultBean errorResultBean = new ErrorResultBean();
            errorResultBean.setStatus(oAuth2Exception.getHttpErrorCode());
            errorResultBean.setError(oAuth2Exception.getOAuth2ErrorCode());
            errorResultBean.setMessage(oAuth2Exception.getMessage());
            errorResultBean.setPath("/oauth/token");
            return new ResponseEntity(errorResultBean, HttpStatus.BAD_REQUEST);
        }
    }
}
