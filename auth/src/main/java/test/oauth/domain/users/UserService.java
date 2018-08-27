package test.oauth.domain.users;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import test.oauth.common.CustomOAuthTokenExtractor;
import test.oauth.common.DefineException;
import test.oauth.config.SecurityConfig;

@EnableJpaAuditing
@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private CustomOAuthTokenExtractor customOAuthTokenExtractor;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
    private final String ORDER_BY_ASC = "asc";
    private final String ORDER_BY_DESC = "desc";
    
    private final String[] findList = {"%", "_"};
	private final String[] replList = {"\\%","\\_"};
    

	// UserDetailsService impl
	public UserDetails loadUserByUsername(String email) {
		UserEntity user = userRepo.findByEmail(email);
		if (user == null) {
			throw new UserDefineException.NotFound(email);
		}
		
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPwd(),
				getAuthority(user.getPermissions()));
	}

	// UserDetailsService impl
	private List<SimpleGrantedAuthority> getAuthority(String permissions) {
		List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		if (permissions == null || "".equals(permissions)) {
			authorities.add(new SimpleGrantedAuthority("NONE"));
		} else {
			String[] perms = permissions.split(",");
			for(String perm : perms) {
				authorities.add(new SimpleGrantedAuthority(perm));
			}
		}
		return authorities;
	}

	// create user
	@Transactional
	public UserDto.Response add(HttpServletRequest httpRequest, UserDto.Create user) {
		if (userRepo.existsByEmail(user.getEmail())) {
			// exist
			throw new UserDefineException.Duplicated(user.getEmail(), httpRequest);
		}

		// add
		user.setPwd(SecurityConfig.userPasswordEncoder().encode(user.getPwd()));
		UserEntity addUser = modelMapper.map(user, UserEntity.class);
		addUser = userRepo.save(addUser);
		return modelMapper.map(addUser, UserDto.Response.class);
	}

	// update user
	@Transactional
	public UserEntity update(HttpServletRequest httpRequest, final Long users_sid, UserDto.Update user) {
		UserEntity updateUser = get(httpRequest, users_sid);
		updateUser.setName(user.getName());

		// update
		return userRepo.save(updateUser);
	}

	// delete user
	@Transactional
	public void delete(HttpServletRequest httpRequest, final Long users_sid) {
		UserEntity deleteUser = get(httpRequest, users_sid);
		customOAuthTokenExtractor.removeAuthToken(deleteUser.getEmail());
		userRepo.deleteById(users_sid);
	}
	
	// list users
	public PageImpl<UserDto.Response> search(HttpServletRequest httpRequest, final String search, Pageable pageable) {
		Sort sort = pageable.getSort();
		List<Sort.Order> list = new ArrayList<>();

		for (Order order : sort) {
			if (!UserDto.ParamMapper.sortProperties.containsKey(order.getProperty().toLowerCase())) {
				throw new DefineException.BadReqeust(httpRequest);
			}
			if (!(ORDER_BY_ASC.equalsIgnoreCase(order.getDirection().toString())
					|| ORDER_BY_DESC.equalsIgnoreCase(order.getDirection().toString()))) {
				throw new DefineException.BadReqeust(httpRequest);
			}
			list.add(new Order(order.getDirection(), UserDto.ParamMapper.sortProperties.get(order.getProperty().toLowerCase())));
		}

		pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(list));
		
		Specification<UserEntity> spec = null;
		Page<UserEntity> searchUsers = null;

		if (search != null) {
			UserSpecificationsBuilder builder = new UserSpecificationsBuilder();
			Pattern pattern = Pattern.compile("(\\w+?)(:|<|>|;)([\\w가-힣@\\.!#$%&'*+\\/=?^`{|}~_-]*),");
			Matcher matcher = pattern.matcher(search + ",");
			while (matcher.find()) {
				if(!UserDto.ParamMapper.searchProperties.containsKey(matcher.group(1).toLowerCase())) {
    				throw new DefineException.BadReqeust(httpRequest);
    			}
				builder.with(UserDto.ParamMapper.searchProperties.get(matcher.group(1).toLowerCase()), matcher.group(2), matcher.group(3));
			}
			spec = builder.build();
		}

		if (spec != null) {
			searchUsers = userRepo.findAll(spec, pageable);
		} else if(search != null) {
			throw new DefineException.BadReqeust(httpRequest);
		} else {
			searchUsers = userRepo.findAll(pageable);
		}

		List<UserDto.Response> users = searchUsers.getContent().stream()
				.map(userEntity -> modelMapper.map(userEntity, UserDto.Response.class)).collect(Collectors.toList());

		return new PageImpl<>(users, pageable, searchUsers.getTotalElements());
	}

	// changePwd
	@Transactional
	public UserEntity changePwd(HttpServletRequest httpRequest, final Long users_sid, UserDto.Password user) {
		UserEntity updateUser = get(httpRequest, users_sid);

		if (!SecurityConfig.userPasswordEncoder().matches(user.getPwd(), updateUser.getPwd())) {
			throw new UserDefineException.invalidPwd(updateUser.getEmail(), httpRequest);
		}

		if (user.getNew_pwd().equals(user.getPwd())) {
			throw new UserDefineException.samePwd(updateUser.getEmail(), httpRequest);
		}
		updateUser.setPwd(SecurityConfig.userPasswordEncoder().encode(user.getNew_pwd()));

		// update
		return userRepo.save(updateUser);
	}

	// resetPwd
	@Transactional
	public UserEntity resetPwd(HttpServletRequest httpRequest, final Long users_sid) {
		UserEntity updateUser = get(httpRequest, users_sid);

		String pwd = RandomStringUtils.random(16, true, true);
		
		// hardcoding 333333
		pwd = "333333";

		updateUser.setPwd(SecurityConfig.userPasswordEncoder().encode(pwd));

		// expire user's token
		customOAuthTokenExtractor.removeAuthToken(updateUser.getEmail());

		// update
		return userRepo.save(updateUser);
	}

	public UserEntity get(HttpServletRequest httpRequest, final Long users_sid) {
		return Optional.ofNullable(getBySid(users_sid))
				.orElseThrow(() -> new UserDefineException.NotFound(users_sid, httpRequest));
	}
	
	public UserEntity getByEmail(HttpServletRequest httpRequest, final String users_email) {
		return Optional.ofNullable(getByEmail(users_email))
				.orElseThrow(() -> new UserDefineException.NotFound(users_email, httpRequest));
	}	
	
	public UserEntity getBySid(final Long users_sid) {
		Optional<UserEntity> user = userRepo.findById(users_sid);
		if (!user.isPresent()) {
			return null;
		}
		return user.get();
	}
	
	public UserEntity getByEmail(final String users_email) {
		UserEntity user = userRepo.findByEmail(users_email);
		return user;
	}
	
	public List<UserEntity> findByEmailContaining(HttpServletRequest httpRequest, String email) {
		String userEmail = StringUtils.replaceEach(email, this.findList, this.replList);
		return Optional.ofNullable(userRepo.findByEmailContaining(userEmail))
				.orElseThrow(() -> new UserDefineException.NotFound(email, httpRequest));
	}
	
	public List<UserEntity> findByNameContaining(HttpServletRequest httpRequest, String name) {
		return Optional.ofNullable(userRepo.findByNameContaining(name))
				.orElseThrow(() -> new UserDefineException.NotFound(name, httpRequest));
	}

}
