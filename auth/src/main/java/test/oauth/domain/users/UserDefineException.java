package test.oauth.domain.users;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import lombok.Getter;
import test.oauth.common.DefineException;

public class UserDefineException {

    public static class Duplicated extends RuntimeException {
        @Getter
        String email;

        @Getter
        HttpServletRequest httpRequest;

        public Duplicated(String email, HttpServletRequest httpRequest) {
            this.email = email;
            this.httpRequest = httpRequest;
        }

        public String getPath() {
            return DefineException.getPath(this.httpRequest);
        }
    }

    public static class NotFound extends RuntimeException {
        @Getter
        Long users_sid;

        @Getter
        String email;

        @Getter
        HttpServletRequest httpRequest;

        public NotFound(Long users_sid, HttpServletRequest httpRequest) {
            this.users_sid = users_sid;
            this.httpRequest = httpRequest;
        }

        public NotFound(String email) {
            this.email = email;
        }

        public NotFound(String email, HttpServletRequest httpRequest) {
            this.email = email;
            this.httpRequest = httpRequest;
        }

        public String getPath() {
            return DefineException.getPath(this.httpRequest);
        }
    }
    
    public static class invalidPwd extends RuntimeException {
        @Getter
        String email;

        @Getter
        HttpServletRequest httpRequest;
        
        @Getter
        HttpStatus status;

        public invalidPwd(String email, HttpServletRequest httpRequest) {
            this.email = email;
            this.httpRequest = httpRequest;
        }

        public String getPath() {
            return DefineException.getPath(this.httpRequest);
        }
    }
    
    public static class samePwd extends RuntimeException {
        @Getter
        String email;

        @Getter
        HttpServletRequest httpRequest;

        public samePwd(String email, HttpServletRequest httpRequest) {
            this.email = email;
            this.httpRequest = httpRequest;
        }

        public String getPath() {
            return DefineException.getPath(this.httpRequest);
        }
    }
    
    public static class UserSidNotMatched extends RuntimeException {


    	@Getter
        Long users_sid;

        @Getter
        HttpServletRequest httpRequest;



        public UserSidNotMatched(Long users_sid) {
            this.users_sid = users_sid;
        }

        public UserSidNotMatched(Long users_sid, HttpServletRequest httpRequest) {
            this.users_sid = users_sid;
            this.httpRequest = httpRequest;
        }

        public String getPath() {
            return DefineException.getPath(this.httpRequest);
        }
    }
    
    public static class UserEmailNotMatched extends RuntimeException {


        @Getter
        String email;

        @Getter
        HttpServletRequest httpRequest;



        public UserEmailNotMatched(String email) {
            this.email = email;
        }

        public UserEmailNotMatched(String email, HttpServletRequest httpRequest) {
            this.email = email;
            this.httpRequest = httpRequest;
        }

        public String getPath() {
            return DefineException.getPath(this.httpRequest);
        }
    }
}
