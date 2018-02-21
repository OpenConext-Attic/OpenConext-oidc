ALTER TABLE client_details
  MODIFY jwks TEXT;
ALTER TABLE access_token
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE access_token_permissions
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE address
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE approved_site
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE approved_site_scope
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE authentication_holder
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE authentication_holder_authority
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE authentication_holder_extension
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE authentication_holder_request_parameter
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE authentication_holder_resource_id
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE authentication_holder_response_type
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE authentication_holder_scope
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE authorization_code
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE blacklisted_site
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE claim
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE claim_issuer
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE claim_to_permission_ticket
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE claim_to_policy
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE claim_token_format
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE client_authority
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE client_claims_redirect_uri
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE client_contact
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE client_default_acr_value
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE client_details
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE client_grant_type
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE client_post_logout_redirect_uri
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE client_redirect_uri
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE client_request_uri
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE client_resource
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE client_response_type
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE client_scope
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE pairwise_identifier
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE permission
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE permission_scope
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE permission_ticket
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE policy
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE policy_scope
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE refresh_token
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE resource_set
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE resource_set_scope
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE saved_registered_client
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE saved_user_auth
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
ALTER TABLE saved_user_auth_authority
  CONVERT TO CHARACTER SET utf8
  COLLATE utf8_general_ci;
