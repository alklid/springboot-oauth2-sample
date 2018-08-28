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
	* MANAGE
* authorities
	* MANAGE:USER

### #ROLE	
* if admin
	* MANAGE:USER
	
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
		- @PreAuthorize("isAuthenticated() and #oauth2.hasScope('MANAGE') and hasAuthority('MANAGE:USER')")
    * request JSON schema
```json
{
  "email": "alklid@sample.com",
  "name": "alklid",
  "pwd": "111111",
  "permissions": "USER:CREATE,USER:LIST,USER:UPDATE,USER:DELETE,USER:RESET_PWD"
}		
```
* /api/\{v}/users/\{users_sid}
    * description : get user
    * method : GET
	* \{v} : 1.0
	* oauth
		- @PreAuthorize("isAuthenticated() and #oauth2.hasScope('MANAGE') and (hasAuthority('MANAGE:USER') or @customSecurityPermissionEvaluator.isMe(authentication, #users_sid))")
* /api/\{v}/users/\{users_sid}
    * description : modify user
    * method : PUT
	* \{v} : 1.0
	* oauth
		- @PreAuthorize("isAuthenticated() and #oauth2.hasScope('MANAGE') and (hasAuthority('MANAGE:USER') or @customSecurityPermissionEvaluator.isMe(authentication, #users_sid))")
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
		- @PreAuthorize("isAuthenticated() and #oauth2.hasScope('MANAGE') and hasAuthority('MANAGE:USER') and @customSecurityPermissionEvaluator.isNotMe(authentication, #users_sid)")
* /api/\{v}/users
    * description : list users
    * method : GET
	* \{v} : 1.0
	* oauth
		- @PreAuthorize("isAuthenticated() and #oauth2.hasScope('MANAGE') and hasAuthority('MANAGE:USER')")
* /api/\{v}/users/\{users_sid}/pwd
    * description : change users's password
    * method : PUT
	* \{v} : 1.0
	* oauth
		- @PreAuthorize("isAuthenticated() and #oauth2.hasScope('MANAGE') and @customSecurityPermissionEvaluator.isMe(authentication, #users_sid)")		
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
		- @PreAuthorize("isAuthenticated() and #oauth2.hasScope('MANAGE') and hasAuthority('MANAGE:USER')")
