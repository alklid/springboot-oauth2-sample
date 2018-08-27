CREATE TABLE users (
	sid bigserial NOT NULL UNIQUE,
	email text NOT NULL UNIQUE,
	name text NOT NULL DEFAULT '',
	pwd text NOT NULL,
	permissions text NOT NULL DEFAULT '',
	created_at timestamp,
	last_modified_at timestamp,
	PRIMARY KEY (sid)
) WITHOUT OIDS;


CREATE TABLE oauth_client_details (
  sid bigserial NOT NULL UNIQUE,
  client_id text NOT NULL UNIQUE,
  resource_ids text,
  client_secret text,
  scope text,
  authorized_grant_types text,
  web_server_redirect_uri text,
  authorities text,
  access_token_validity integer,
  refresh_token_validity integer,
  additional_information text,
  autoapprove text,
  users_sid bigint NOT NULL,
  coins_type text,
  name text,
  description text,
  client_salt text,
  created_at timestamp,
  activated boolean NOT NULL DEFAULT true,
  last_modified_at timestamp,
  PRIMARY KEY (sid)
) WITHOUT OIDS;

CREATE TABLE oauth_access_token (
  token_id varchar(256),
  token bytea,
  authentication_id varchar(256),
  user_name varchar(256),
  client_id varchar(256),
  authentication bytea,
  refresh_token varchar(256),
  PRIMARY KEY (authentication_id)
) WITHOUT OIDS;

CREATE TABLE oauth_refresh_token (
  token_id varchar(256),
  token bytea,
  authentication bytea
) WITHOUT OIDS;
