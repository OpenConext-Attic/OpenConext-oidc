INSERT INTO client_details (id, client_id, client_secret, client_name, dynamically_registered, refresh_token_validity_seconds, access_token_validity_seconds, id_token_validity_seconds, allow_introspection) VALUES
	(9999999999, 'https@//oidc.localhost.surfconext.nl', 'secret', 'Test Client', false, null, 86400, 86400, true);

INSERT INTO client_details (id, client_id, client_secret, client_name, dynamically_registered, refresh_token_validity_seconds, access_token_validity_seconds, id_token_validity_seconds, allow_introspection) VALUES
	(9999999998, 'https@//client.localhost.surfconext.nl', 'secret', 'Test Client', false, null, 86400, 86400, false);

INSERT INTO client_scope (owner_id, scope) VALUES
	(9999999999, 'read'),
	(9999999999, 'openid'),
	(9999999999, 'groups'),
	(9999999999, 'profile'),
	(9999999999, 'email'),
	(9999999999, 'address'),
	(9999999999, 'phone'),
	(9999999999, 'organization'),
	(9999999999, 'entitlement'),
	(9999999999, 'userids'),
	(9999999999, 'offline_access');

INSERT INTO client_scope (owner_id, scope) VALUES
  (9999999998, 'read'),
  (9999999998, 'strange');

INSERT INTO client_redirect_uri (owner_id, redirect_uri) VALUES
  (9999999998, 'http://localhost:8889/callback');

INSERT INTO client_redirect_uri (owner_id, redirect_uri) VALUES
	(9999999999, 'http://authz-playground-local:8089/redirect'),
	(9999999999, 'https://authz-playground.test.surfconext.nl/redirect'),
	(9999999999, 'https://authz-playground.acc.surfconext.nl/redirect'),
	(9999999999, 'https://authz-playground.surfconext.nl/redirect'),
	(9999999999, 'http://localhost:8889/callback');
	
INSERT INTO client_grant_type (owner_id, grant_type) VALUES
	(9999999998, 'authorization_code'),
	(9999999998, 'implicit');

INSERT INTO client_grant_type (owner_id, grant_type) VALUES
  (9999999999, 'authorization_code'),
  (9999999999, 'implicit'),
  (9999999999, 'client_credentials'),
  (9999999999, 'refresh_token');

INSERT INTO whitelisted_site (id, client_id) VALUES
	(9999999999, 'https@//oidc.localhost.surfconext.nl');

INSERT INTO whitelisted_site_scope (owner_id, scope) VALUES
	(9999999999, 'read'),
	(9999999999, 'openid'),
	(9999999999, 'groups'),
	(9999999999, 'profile'),
	(9999999999, 'email'),
	(9999999999, 'address'),
	(9999999999, 'phone'),
	(9999999999, 'organization'),
	(9999999999, 'entitlement'),
	(9999999999, 'userids'),
	(9999999999, 'offline_access');
