--
-- Moving from MySQLto the Galera cluster requires primary keys.
--
--
ALTER TABLE access_token_permissions MODIFY id INT AUTO_INCREMENT;
ALTER TABLE approved_site_scope MODIFY id INT AUTO_INCREMENT;
ALTER TABLE authentication_holder_authority MODIFY id INT AUTO_INCREMENT;
ALTER TABLE authentication_holder_extension MODIFY id INT AUTO_INCREMENT;
ALTER TABLE authentication_holder_request_parameter MODIFY id INT AUTO_INCREMENT;
ALTER TABLE authentication_holder_resource_id MODIFY id INT AUTO_INCREMENT;
ALTER TABLE authentication_holder_response_type MODIFY id INT AUTO_INCREMENT;
ALTER TABLE authentication_holder_scope MODIFY id INT AUTO_INCREMENT;
ALTER TABLE claim_issuer MODIFY id INT AUTO_INCREMENT;
ALTER TABLE claim_token_format MODIFY id INT AUTO_INCREMENT;
ALTER TABLE claim_to_permission_ticket MODIFY id INT AUTO_INCREMENT;
ALTER TABLE claim_to_policy MODIFY id INT AUTO_INCREMENT;
ALTER TABLE client_authority MODIFY id INT AUTO_INCREMENT;
ALTER TABLE client_claims_redirect_uri MODIFY id INT AUTO_INCREMENT;
ALTER TABLE client_contact MODIFY id INT AUTO_INCREMENT;
ALTER TABLE client_default_acr_value MODIFY id INT AUTO_INCREMENT;
ALTER TABLE client_grant_type MODIFY id INT AUTO_INCREMENT;
ALTER TABLE client_post_logout_redirect_uri MODIFY id INT AUTO_INCREMENT;
ALTER TABLE client_redirect_uri MODIFY id INT AUTO_INCREMENT;
ALTER TABLE client_request_uri MODIFY id INT AUTO_INCREMENT;
ALTER TABLE client_resource MODIFY id INT AUTO_INCREMENT;
ALTER TABLE client_response_type MODIFY id INT AUTO_INCREMENT;
ALTER TABLE client_scope MODIFY id INT AUTO_INCREMENT;
ALTER TABLE permission_scope MODIFY id INT AUTO_INCREMENT;
ALTER TABLE policy_scope MODIFY id INT AUTO_INCREMENT;
ALTER TABLE resource_set_scope MODIFY id INT AUTO_INCREMENT;
ALTER TABLE saved_user_auth_authority MODIFY id INT AUTO_INCREMENT;
ALTER TABLE token_scope MODIFY id INT AUTO_INCREMENT;
ALTER TABLE user_edu_person_affiliation MODIFY id INT AUTO_INCREMENT;
ALTER TABLE user_edu_person_entitlement MODIFY id INT AUTO_INCREMENT;
ALTER TABLE user_edu_person_scoped_affiliation MODIFY id INT AUTO_INCREMENT;
ALTER TABLE user_is_member_of MODIFY id INT AUTO_INCREMENT;
ALTER TABLE user_schac_personal_unique_code MODIFY id INT AUTO_INCREMENT;
ALTER TABLE user_uid MODIFY id INT AUTO_INCREMENT;
ALTER TABLE whitelisted_site_scope MODIFY id INT AUTO_INCREMENT;