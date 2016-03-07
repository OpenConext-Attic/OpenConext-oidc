DELETE FROM system_scope
WHERE scope IN ('profile', 'email', 'phone', 'address', 'organization', 'entitlement', 'userids', 'offline_access');