ALTER TABLE system_scope DROP INDEX scope;
ALTER TABLE user_edu_person_affiliation DROP INDEX uepa_id_idx;
ALTER TABLE user_edu_person_entitlement DROP INDEX uepe_id_idx;
ALTER TABLE user_edu_person_scoped_affiliation DROP INDEX uepsa_id_idx;
ALTER TABLE user_is_member_of DROP INDEX uimo_id_idx;
ALTER TABLE user_schac_personal_unique_code DROP INDEX uspuc_id_idx;
ALTER TABLE user_uid DROP INDEX uu_id_idx;

ALTER TABLE system_scope
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE token_scope
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE user_edu_person_affiliation
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE user_edu_person_entitlement
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE user_edu_person_scoped_affiliation
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE user_info
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE user_is_member_of
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE user_schac_personal_unique_code
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE user_uid
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE whitelisted_site
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE whitelisted_site_scope
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;