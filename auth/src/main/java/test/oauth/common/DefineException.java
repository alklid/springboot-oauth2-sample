package test.oauth.common;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import lombok.Getter;

@ControllerAdvice
public class DefineException {

	// 400 InvalidAPIVersion Exception
	public static class InvalidAPIVersion extends RuntimeException {
		@Getter
		String apiVersion;

		@Getter
		HttpServletRequest httpRequest;

		public InvalidAPIVersion(String apiVersion, HttpServletRequest httpRequest) {
			this.apiVersion = apiVersion;
			this.httpRequest = httpRequest;
		}

		public String getPath() {
			return DefineException.getPath(this.httpRequest);
		}
	}
	@ExceptionHandler(DefineException.InvalidAPIVersion.class)
	public ResponseEntity DEInvalidApiVersionHandler(DefineException.InvalidAPIVersion e) {
		ErrorResultBean errorResultBean = new ErrorResultBean();
		errorResultBean.setStatus(HttpStatus.BAD_REQUEST.value());
		errorResultBean.setError("invalid_api_version");
		errorResultBean.setMessage("잘못된 API 요청입니다. [" + e.getApiVersion() + "]");
		errorResultBean.setPath(e.getPath());
		return new ResponseEntity<>(errorResultBean, HttpStatus.BAD_REQUEST);
	}

	
	// 403 AccessDenied Exception
	public static class AccessDenied extends RuntimeException {
		@Getter
		HttpServletRequest httpRequest;

		public AccessDenied(HttpServletRequest httpRequest) {
			this.httpRequest = httpRequest;
		}

		public String getPath() {
			return DefineException.getPath(this.httpRequest);
		}
	}
	@ExceptionHandler(DefineException.AccessDenied.class)
	public ResponseEntity DEAccessDeniedHandler(DefineException.AccessDenied e) {
		ErrorResultBean errorResultBean = new ErrorResultBean();
		errorResultBean.setStatus(HttpStatus.FORBIDDEN.value());
		errorResultBean.setError("access_denied");
		errorResultBean.setMessage("Access is denied");
		errorResultBean.setPath(e.getPath());
		return new ResponseEntity<>(errorResultBean, HttpStatus.FORBIDDEN);
	}

	
	// 400 BadReqeust Exception
	public static class BadReqeust extends RuntimeException {
		@Getter
		HttpServletRequest httpRequest;

		@Getter
		BindingResult bindingResult;

		public BadReqeust(BindingResult bindingResult, HttpServletRequest httpRequest) {
			this.httpRequest = httpRequest;
			this.bindingResult = bindingResult;
		}

		public BadReqeust(HttpServletRequest httpRequest) {
			this.httpRequest = httpRequest;
		}

		public String getPath() {
			return DefineException.getPath(this.httpRequest);
		}
	}
	@ExceptionHandler(DefineException.BadReqeust.class)
	public ResponseEntity DEBadRequestHandler(DefineException.BadReqeust e) {
		ErrorResultBean errorResultBean = new ErrorResultBean();
		errorResultBean.setStatus(HttpStatus.BAD_REQUEST.value());
		errorResultBean.setError("bad_request");
		errorResultBean.setMessage("잘못된 요청입니다.");
		errorResultBean.setPath(e.getPath());
		return new ResponseEntity<>(errorResultBean, HttpStatus.BAD_REQUEST);
	}

	// InvalidOAuth2 Exception
	public static class InvalidOAuth2 {
		private WebResponseExceptionTranslator exceptionTranslator = new DefaultWebResponseExceptionTranslator();
		private HandlerExceptionResolver handlerExceptionResolver = new DefaultHandlerExceptionResolver();

		private HttpServletRequest request;
		private HttpServletResponse response;
		private AuthenticationException authenticationException;
		private AccessDeniedException accessDeniedException;

		public InvalidOAuth2(HttpServletRequest request, HttpServletResponse response,
				AuthenticationException authenticationException) {
			this.request = request;
			this.response = response;
			this.authenticationException = authenticationException;
		}

		public InvalidOAuth2(HttpServletRequest request, HttpServletResponse response,
				AccessDeniedException accessDeniedException) {
			this.request = request;
			this.response = response;
			this.accessDeniedException = accessDeniedException;
		}

		public void handle(String type) throws IOException, ServletException {
			try {
				ResponseEntity<OAuth2Exception> OAuthResult = null;
				if (type.equalsIgnoreCase("access")) {
					OAuthResult = exceptionTranslator.translate(this.accessDeniedException);
				} else {
					OAuthResult = exceptionTranslator.translate(this.authenticationException);
				}

				// error info
				int statusCode = OAuthResult.getStatusCode().value();
				String error = OAuthResult.getBody().getOAuth2ErrorCode();
				String message = type.equalsIgnoreCase("access") ? this.accessDeniedException.getMessage()
						: this.authenticationException.getMessage();
				String path = getPath();

				ErrorResultBean errorResultBean = new ErrorResultBean();
				errorResultBean.setStatus(statusCode);
				errorResultBean.setError(error);
				errorResultBean.setMessage(message);
				errorResultBean.setPath(path);
				ResponseEntity<ErrorResultBean> result = new ResponseEntity<>(errorResultBean,
						OAuthResult.getStatusCode());

				response.setStatus(OAuthResult.getStatusCode().value());
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(new JSONObject(errorResultBean).toString());

				response.flushBuffer();
			} catch (ServletException e) {
				// Re-use some of the default Spring dispatcher behaviour - the exception came
				// from the filter chain and
				// not from an MVC handler so it won't be caught by the dispatcher (even if
				// there is one)
				if (handlerExceptionResolver.resolveException(request, response, this, e) == null) {
					throw e;
				}
			} catch (IOException e) {
				throw e;
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				// Wrap other Exceptions. These are not expected to happen
				throw new RuntimeException(e);
			}
		}

		public String getPath() {
			return DefineException.getPath(this.request);
		}
	}

	public static String getPath(HttpServletRequest httpRequest) {
		String path = httpRequest.getRequestURI();
		if (httpRequest.getQueryString() != null) {
			path += "?" + httpRequest.getQueryString();
		}
		return path;
	}
}
