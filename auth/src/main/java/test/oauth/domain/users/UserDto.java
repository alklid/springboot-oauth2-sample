package test.oauth.domain.users;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public class UserDto {

    @Data
    public static class Create {
        
    	@NotBlank
    	@Email
        private String email;

        @NotBlank
        private String name;

        @NotBlank
        @Size(min = 6)
        private String pwd;
        
        @NotBlank
        private String permissions;
    }

    @Data
    public static class Update {
        @NotBlank
        private String name;
    }

    @Data
    public static class Password {
        @NotBlank
        @Size(min = 6)
        private String pwd;
    	
        @NotBlank
        @Size(min = 6)
        private String new_pwd;
    }

    @Data
    public static class Response {
        private Long sid;
        private String email;
        private String name;
        private String permissions;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        @JsonProperty("last_modified_at")
        private LocalDateTime lastModifiedAt;
    }
    
    @Data
	public static class ParamMapper {
    	static Map<String,String> sortProperties = new HashMap<String, String>();
		static Map<String,String> searchProperties = new HashMap<String, String>();
		
		static {
			//sort
			sortProperties.put("created_at", "createdAt");
			sortProperties.put("email", "email");
			sortProperties.put("name", "name");
			//search
			searchProperties.put("email", "email");
			searchProperties.put("name", "name");
		}
	}
}
