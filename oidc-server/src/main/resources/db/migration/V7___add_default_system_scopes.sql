DELETE FROM `system_scope` WHERE `scope` NOT IN ('openid');
INSERT INTO `system_scope` (`scope`, `description`, `icon`, `restricted`, `default_scope`, `structured`, `structured_param_description`)
VALUES
  ('address', 'The address of the user', '', 0, 1, 0, ''),
  ('email', 'The email of the user', '', 0, 1, 0, ''),
  ('profile', 'The profile of the user', '', 0, 1, 0, ''),
  ('phone', 'The phone of the user', '', 0, 1, 0, '');
