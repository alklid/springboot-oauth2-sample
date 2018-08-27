package test.oauth.common;

import org.json.JSONObject;
import org.springframework.web.client.HttpServerErrorException;

import lombok.Data;

@Data
public class CustomHttpServerExceptionHandler {
	
	public HttpServerErrorException httpException;
	private String responseBody;
	
	public Long errorCode;
	public String errorMessage;
	
	
	public CustomHttpServerExceptionHandler(HttpServerErrorException e) {
		this.httpException = e;
		this.responseBody =  e.getResponseBodyAsString();
		

		JSONObject json = new JSONObject(this.responseBody);		
		JSONObject errorJson = json.getJSONObject("error");
		
		this.errorCode = errorJson.getLong("code");
		this.errorMessage = errorJson.getString("message");
	}
	
}
