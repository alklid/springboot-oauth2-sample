# oauth2 sample project

### #Environment
* Springboot v2.0
* JPA
	* postgre : Document\DB\postgre\\*.sql
* POSTMAN
	* collection v2.1 : Document\POSTMAN\oauth_test.postman_collection.json
### #Domain
* VERSION
* USER

### #Oauth2
* scope
	* USER
* authorities
	* CREATE_USER
	* READ_USERS
	* UPDATE_USER
	* DELETE_USER
	* RESET_PWD_USER

### #ROLE	
* if admin
	* CREATE_USER
	* READ_USERS
	* UPDATE_USER
	* DELETE_USER
	* RESET_PWD_USER
* if general
	* UPDATE_USER

	
### #RESTful API
* /version
    * description : get version infomation
    * method : GET
	* oauth
		* anonymous
* /api/\{v}/users
    * description : create user
    * method : POST
	* \{v} : 1.0
	* oauth
		- @PreAuthorize("isAuthenticated() and #oauth2.hasScope('USER') and hasAuthority('CREATE_USER')")
    * request JSON schema
```json
{
  "email": "alklid@sample.com",
  "name": "alklid",
  "pwd": "111111",
  "permissions": "CREATE_USER,READ_USERS,UPDATE_USER,DELETE_USER,RESET_PWD_USER"
}		
```
* /api/\{v}/users/\{users_email}
    * description : get user
    * method : GET
	* \{v} : 1.0
	* oauth
		- @PreAuthorize("isAuthenticated() and #oauth2.hasScope('USER') and (( #users_email == principal.username ) or hasAuthority('READ_USERS'))")
* /api/\{v}/users/{users_sid}
    * description : modify user
    * method : PUT
	* \{v} : 1.0
	* oauth
		- @PreAuthorize("isAuthenticated() and #oauth2.hasScope('USER') and @customSecurityPermissionEvaluator.isMe(authentication, #users_sid) and hasAuthority('UPDATE_USER')")
    * request JSON schema
```json
{
  "name": "alklid_modify"
}	
```
* /api/\{v}/users/\{users_sid}
    * description : delete user
    * method : DELETE
	* \{v} : 1.0
	* oauth
		- @PreAuthorize("isAuthenticated() and #oauth2.hasScope('USER') and @customSecurityPermissionEvaluator.isNotMe(authentication, #users_sid) and hasAuthority('DELETE_USER')")
* /api/\{v}/users
    * description : list users
    * method : GET
	* \{v} : 1.0
	* oauth
		- @PreAuthorize("isAuthenticated() and #oauth2.hasScope('USER') and hasAuthority('READ_USERS')")
* /api/\{v}/users/\{users_sid}/pwd
    * description : change users's password
    * method : PUT
	* \{v} : 1.0
	* oauth
		- @PreAuthorize("isAuthenticated() and #oauth2.hasScope('USER') and @customSecurityPermissionEvaluator.isNotMe(authentication, #users_sid) and hasAuthority('DELETE_USER')")		
```json
{
  "pwd": "111111",
  "new_pwd": "222222"
}
```
* /api/\{v}/users/\{users_sid}/pwd
    * description : reset users's password
    * method : PATCH
	* \{v} : 1.0
	* oauth
		- @PreAuthorize("isAuthenticated() and #oauth2.hasScope('USER') and hasAuthority('RESET_PWD_USER')")
