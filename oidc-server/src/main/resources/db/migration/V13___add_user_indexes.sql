ALTER TABLE `user_edu_person_affiliation` ADD INDEX `uepa_user_id` (`user_id`);
ALTER TABLE `user_edu_person_entitlement` ADD INDEX `uepe_user_id` (`user_id`);
ALTER TABLE `user_edu_person_scoped_affiliation` ADD INDEX `uepsa_user_id` (`user_id`);
ALTER TABLE `user_is_member_of` ADD INDEX `uismo_user_id` (`user_id`);
ALTER TABLE `user_schac_personal_unique_code` ADD INDEX `uspuc_user_id` (`user_id`);
ALTER TABLE `user_uid` ADD INDEX `uu_user_id` (`user_id`);
