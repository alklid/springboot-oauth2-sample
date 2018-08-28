-- 1*6

INSERT INTO users(
  email,
  name,
  pwd,
  permissions,
  created_at,
  last_modified_at
) VALUES (
  'alklid@sample.com',
  'alklid',
  '$2a$08$6J.nxiOn6T4tCQO0OrcJxOmtmyFaaW0tSbusAZsZi9Q1yWMpUtzqu',
  'MANAGE:USER',
  timezone('utc'::text, now()),
  timezone('utc'::text, now())
);


INSERT INTO oauth_client_details(
  client_id,
  resource_ids,
  client_secret,
  scope,
  authorized_grant_types,
  authorities,
  access_token_validity,
  refresh_token_validity,
  users_sid,
  name,
  client_salt
) VALUES (
  'oauth_test_client_id',
  'oauth_test_resources_id',
  '$2a$04$aXWSdqTTmFvQKZ4kVSeVHuGqXApQJqlkwLkuS/NSpil4p1tEa0bnG',
  'MANAGE',
  'password,refresh_token,client_credentials',
  'NONE',
  14400,
  604800,
  0,
  'oauth_test',
  'oauth_test_client_secret'
);
