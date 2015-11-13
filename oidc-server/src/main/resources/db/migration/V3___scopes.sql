--
-- System scopes, see https://wiki.surfnet.nl/pages/viewpage.action?spaceKey=P3GFeI2015&title=OpenID%20Connect%20Implementatie
--


INSERT INTO system_scope (scope, description, icon, restricted, default_scope, structured, structured_param_description) VALUES
  ('openid', 'log in using your identity', 'user', false, true, false, null),
  ('profile', 'basic profile information', 'list-alt', false, true, false, null),
  ('email', 'email address', 'envelope', false, true, false, null),
  ('phone', 'telephone number', 'bell', false, true, false, null),
  ('address', 'physical address', 'home', false, true, false, null),
  ('organization', 'organization information', 'home', false, true, false, null),
  ('entitlement', 'entitlement information', 'home', false, true, false, null),
  ('userids', 'userids information', 'home', false, true, false, null),
  ('offline_access', 'offline access', 'time', false, false, false, null);
