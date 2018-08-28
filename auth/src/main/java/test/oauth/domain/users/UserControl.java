package test.oauth.domain.users;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import test.oauth.common.CustomOAuthTokenExtractor;
import test.oauth.common.DefineException;
import test.oauth.common.ErrorResultBean;

@RestController
@RequestMapping("/api/{v}/users")
public class UserControl {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CustomOAuthTokenExtractor customOAuthTokenExtractor;

    @Autowired
    private UserService userService;

    // create
    @PreAuthorize("isAuthenticated() and #oauth2.hasScope('MANAGE') and hasAuthority('MANAGE:USER')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserDto.Response> add(HttpServletRequest httpRequest,
    								@PathVariable("v") String v,
    								@RequestBody @Valid UserDto.Create user,
    								BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new DefineException.BadReqeust(bindingResult, httpRequest);
        }
        
        switch (v) {
            case "1.0":
                UserDto.Response addUser = userService.add(httpRequest, user);
                return new ResponseEntity<>(addUser, HttpStatus.CREATED);
            default:
                throw new DefineException.InvalidAPIVersion(v, httpRequest);
        }
    }

    // info
    @PreAuthorize("isAuthenticated() and #oauth2.hasScope('MANAGE') and (hasAuthority('MANAGE:USER') or @customSecurityPermissionEvaluator.isMe(authentication, #users_sid))")
    @RequestMapping(method = RequestMethod.GET, value = "/{users_sid}")
    public ResponseEntity<UserDto.Response> get(HttpServletRequest httpRequest,
                                  @PathVariable("v") String v,
                                  @PathVariable("users_sid") Long users_sid) {
        switch (v) {
            case "1.0":
                UserEntity user = userService.get(httpRequest, users_sid);
                return new ResponseEntity<>(modelMapper.map(user, UserDto.Response.class), HttpStatus.OK);
            default:
                throw new DefineException.InvalidAPIVersion(v, httpRequest);
        }
    }

    // update
    @PreAuthorize("isAuthenticated() and #oauth2.hasScope('MANAGE') and (hasAuthority('MANAGE:USER') or @customSecurityPermissionEvaluator.isMe(authentication, #users_sid))")
    @RequestMapping(method = RequestMethod.PUT, value = "/{users_sid}")
    public ResponseEntity<UserDto.Response> update(HttpServletRequest httpRequest,
                                     @PathVariable("v") String v,
                                     @PathVariable("users_sid") Long users_sid,
                                     @RequestBody @Valid UserDto.Update user,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new DefineException.BadReqeust(bindingResult, httpRequest);
        }

        switch (v) {
        	case "1.0":
                UserEntity updateUser = userService.update(httpRequest, users_sid, user);
                return new ResponseEntity<>(modelMapper.map(updateUser, UserDto.Response.class), HttpStatus.OK);
            default:
                throw new DefineException.InvalidAPIVersion(v, httpRequest);
        }
    }
    
    // delete
    @PreAuthorize("isAuthenticated() and #oauth2.hasScope('MANAGE') and hasAuthority('MANAGE:USER') and @customSecurityPermissionEvaluator.isNotMe(authentication, #users_sid)")
    @RequestMapping(method = RequestMethod.DELETE, value = "/{users_sid}")
    public ResponseEntity<Void> delete(HttpServletRequest httpRequest,
                                     @PathVariable("v") String v,
                                     @PathVariable("users_sid") Long users_sid) {
        switch (v) {
			case "1.0":
                userService.delete(httpRequest, users_sid);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            default:
                throw new DefineException.InvalidAPIVersion(v, httpRequest);
        }
    }

    // search list
    // ?page=0&size=10&sort=email,asc
    // ?search=email:alklid3,manager;true
    //      [:] ==> equals or like
    //      [<] or [>] ==> numeric
    //      [;] ==> true or false
    @PreAuthorize("isAuthenticated() and #oauth2.hasScope('MANAGE') and hasAuthority('MANAGE:USER')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<PageImpl<UserDto.Response>> search(HttpServletRequest httpRequest,
    								 @AuthenticationPrincipal OAuth2Authentication authentication,
                                     @PathVariable("v") String v,
                                     @RequestParam(value = "search", required = false) String search,
                                     Pageable pageable) {
        switch (v) {
			case "1.0":
                PageImpl<UserDto.Response> searchUsers = userService.search(httpRequest, search, pageable);
                return new ResponseEntity<>(searchUsers, HttpStatus.OK);
            default:
                throw new DefineException.InvalidAPIVersion(v, httpRequest);
        }
    }
    
    //changePwd
    @PreAuthorize("isAuthenticated() and #oauth2.hasScope('MANAGE') and @customSecurityPermissionEvaluator.isMe(authentication, #users_sid)")
	@RequestMapping(method = RequestMethod.PUT, value = "/{users_sid}/pwd")
	public ResponseEntity<UserDto.Response> changePwd(HttpServletRequest httpRequest,
									@PathVariable("v") String v,
									@PathVariable("users_sid") Long users_sid, 
									@RequestBody @Valid UserDto.Password user,
									BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new DefineException.BadReqeust(bindingResult, httpRequest);
		}

		switch (v) {
			case "1.0":
				UserEntity updateUser = userService.changePwd(httpRequest, users_sid, user);
				customOAuthTokenExtractor.removeAuthToken(updateUser.getEmail());
				return new ResponseEntity<>(modelMapper.map(updateUser, UserDto.Response.class), HttpStatus.OK);
			default:
				throw new DefineException.InvalidAPIVersion(v, httpRequest);
		}

    }
    
    //resetPwd
    @PreAuthorize("isAuthenticated() and #oauth2.hasScope('MANAGE') and hasAuthority('MANAGE:USER')")    
    @RequestMapping(method = RequestMethod.PATCH, value = "/{users_sid}/pwd")
    public ResponseEntity<UserDto.Response> resetPwd(HttpServletRequest httpRequest,
                                  @PathVariable("v") String v,
                                  @PathVariable("users_sid") Long users_sid) {
        switch (v) {
			case "1.0":
                UserEntity user = userService.resetPwd(httpRequest, users_sid);
                return new ResponseEntity<>(modelMapper.map(user, UserDto.Response.class), HttpStatus.OK);
                
            default:
                throw new DefineException.InvalidAPIVersion(v, httpRequest);
        }
    }

    @ExceptionHandler(UserDefineException.Duplicated.class)
    public ResponseEntity<ErrorResultBean> DuplicatedHandler(UserDefineException.Duplicated e) {
        ErrorResultBean errorResultBean = new ErrorResultBean();
        errorResultBean.setStatus(HttpStatus.CONFLICT.value());
        errorResultBean.setError("duplicated_user");
        errorResultBean.setMessage("동일한 데이터가 존재합니다. [" + e.getEmail() + "]");
        errorResultBean.setPath(e.getPath());
        return new ResponseEntity<>(errorResultBean, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserDefineException.NotFound.class)
    public ResponseEntity<ErrorResultBean> NotFoundHandler(UserDefineException.NotFound e) {
        ErrorResultBean errorResultBean = new ErrorResultBean();
        errorResultBean.setStatus(HttpStatus.NOT_FOUND.value());
        errorResultBean.setError("not_found_user");
        errorResultBean.setMessage("데이터가 없습니다. [" + e.getUsers_sid() + "]");
        errorResultBean.setPath(e.getPath());
        return new ResponseEntity<>(errorResultBean, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(UserDefineException.invalidPwd.class)
    public ResponseEntity<ErrorResultBean> invalidPwd(UserDefineException.invalidPwd e) {
        ErrorResultBean errorResultBean = new ErrorResultBean();
        errorResultBean.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorResultBean.setError("invalid_pwd");
        errorResultBean.setMessage(" 잘못된 비밀번호입니다. [" + e.getEmail() + "]");
        errorResultBean.setPath(e.getPath());
        return new ResponseEntity<>(errorResultBean, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(UserDefineException.samePwd.class)
    public ResponseEntity<ErrorResultBean> samePwd(UserDefineException.samePwd e) {
        ErrorResultBean errorResultBean = new ErrorResultBean();
        errorResultBean.setStatus(HttpStatus.CONFLICT.value());
        errorResultBean.setError("duplicated_pwd");
        errorResultBean.setMessage(" 기존 비밀번호와 변경하고자 하는 비밀번호와 동일합니다. [" + e.getEmail() + "]");
        errorResultBean.setPath(e.getPath());
        return new ResponseEntity<>(errorResultBean, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(UserDefineException.UserSidNotMatched.class)
    public ResponseEntity<ErrorResultBean> UserSidNotMatched(UserDefineException.UserSidNotMatched e) {
        ErrorResultBean errorResultBean = new ErrorResultBean();
        errorResultBean.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResultBean.setError("not_matched_user_sid");
        errorResultBean.setMessage("잘못된 요청입니다. [" + e.getUsers_sid() + "]");
        errorResultBean.setPath(e.getPath());
        return new ResponseEntity<>(errorResultBean, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(UserDefineException.UserEmailNotMatched.class)
    public ResponseEntity<ErrorResultBean> UserEmailNotMatched(UserDefineException.UserEmailNotMatched e) {
        ErrorResultBean errorResultBean = new ErrorResultBean();
        errorResultBean.setStatus(HttpStatus.NOT_FOUND.value());
        errorResultBean.setError("not_matched_user_email");
        errorResultBean.setMessage("사용자 이메일 정보가 일치하지 않습니다.");
        errorResultBean.setPath(e.getPath());
        return new ResponseEntity<>(errorResultBean, HttpStatus.NOT_FOUND);
    }
}
