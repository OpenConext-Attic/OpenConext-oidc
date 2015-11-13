--
-- Additional attributes to store for the UserInfo,
-- see https://wiki.surfnet.nl/pages/viewpage.action?spaceKey=P3GFeI2015&title=OpenID%20Connect%20Implementatie
--
CREATE INDEX ui_pu_idx ON user_info(preferred_username);

ALTER TABLE user_info ADD DTYPE varchar(256) DEFAULT NULL;

ALTER TABLE user_info ADD unspecified_name_id varchar(256) DEFAULT NULL;
ALTER TABLE user_info ADD schac_home_organization varchar(256) DEFAULT NULL;
ALTER TABLE user_info ADD schac_home_organization_type varchar(256) DEFAULT NULL;
ALTER TABLE user_info ADD edu_person_principal_name varchar(256) DEFAULT NULL;
ALTER TABLE user_info ADD edu_person_targeted_id varchar(256) DEFAULT NULL;

CREATE TABLE IF NOT EXISTS user_edu_person_affiliation (
	user_id BIGINT,
	edu_person_affiliation VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS user_edu_person_scoped_affiliation (
	user_id BIGINT,
	edu_person_scoped_affiliation VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS user_is_member_of (
	user_id BIGINT,
	is_member_of VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS user_edu_person_entitlement (
	user_id BIGINT,
	edu_person_entitlement VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS user_schac_personal_unique_code (
	user_id BIGINT,
	schac_personal_unique_code VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS user_uid (
	user_id BIGINT,
	uid VARCHAR(256)
);