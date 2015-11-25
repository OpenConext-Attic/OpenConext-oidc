--
-- Some resource servers need the authenticating_authority
--
ALTER TABLE user_info ADD authenticating_authority varchar(256) DEFAULT NULL;