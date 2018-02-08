package oidc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.ssl.asn1.ASN1Object;
import org.mitre.openid.connect.model.DefaultUserInfo;
import org.springframework.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_info")
public class FederatedUserInfo extends DefaultUserInfo {

  private String schacHomeOrganization;
  private String unspecifiedNameId;
  private String authenticatingAuthority;
  private String schacHomeOrganizationType;
  private String eduPersonPrincipalName;
  private String eduPersonTargetedId;

  private Set<String> eduPersonAffiliations = new HashSet<>();
  private Set<String> eduPersonScopedAffiliations = new HashSet<>();
  private Set<String> isMemberOfs = new HashSet<>();
  private Set<String> eduPersonEntitlements = new HashSet<>();
  private Set<String> schacPersonalUniqueCodes = new HashSet<>();
  private Set<String> uids = new HashSet<>();

  @Basic
  @Column(name = "schac_home_organization")
  public String getSchacHomeOrganization() {
    return schacHomeOrganization;
  }

  public void setSchacHomeOrganization(String schacHomeOrganization) {
    this.schacHomeOrganization = schacHomeOrganization;
  }

  @Basic
  @Column(name = "unspecified_name_id")
  public String getUnspecifiedNameId() {
    return unspecifiedNameId;
  }

  public void setUnspecifiedNameId(String unspecifiedNameId) {
    this.unspecifiedNameId = unspecifiedNameId;
  }

  @Basic
  @Column(name = "authenticating_authority")
  public String getAuthenticatingAuthority() {
    return authenticatingAuthority;
  }

  public void setAuthenticatingAuthority(String authenticatingAuthority) {
    this.authenticatingAuthority = authenticatingAuthority;
  }

  @Basic
  @Column(name = "schac_home_organization_type")
  public String getSchacHomeOrganizationType() {
    return schacHomeOrganizationType;
  }

  public void setSchacHomeOrganizationType(String schacHomeOrganizationType) {
    this.schacHomeOrganizationType = schacHomeOrganizationType;
  }

  @Basic
  @Column(name = "edu_person_principal_name")
  public String getEduPersonPrincipalName() {
    return eduPersonPrincipalName;
  }

  public void setEduPersonPrincipalName(String eduPersonPrincipalName) {
    this.eduPersonPrincipalName = eduPersonPrincipalName;
  }

  @Basic
  @Column(name = "edu_person_targeted_id")
  public String getEduPersonTargetedId() {
    return eduPersonTargetedId;
  }

  public void setEduPersonTargetedId(String eduPersonTargetedId) {
    this.eduPersonTargetedId = eduPersonTargetedId;
  }

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "user_edu_person_affiliation",
      joinColumns = @JoinColumn(name = "user_id")
  )
  @Column(name = "edu_person_affiliation")
  public Set<String> getEduPersonAffiliations() {
    return eduPersonAffiliations;
  }

  public void setEduPersonAffiliations(Set<String> eduPersonAffiliations) {
    this.eduPersonAffiliations = eduPersonAffiliations;
  }

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "user_edu_person_scoped_affiliation",
      joinColumns = @JoinColumn(name = "user_id")
  )
  @Column(name = "edu_person_scoped_affiliation")
  public Set<String> getEduPersonScopedAffiliations() {
    return eduPersonScopedAffiliations;
  }

  public void setEduPersonScopedAffiliations(Set<String> eduPersonScopedAffiliations) {
    this.eduPersonScopedAffiliations = eduPersonScopedAffiliations;
  }

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "user_is_member_of",
      joinColumns = @JoinColumn(name = "user_id")
  )
  @Column(name = "is_member_of")
  @JsonProperty("edumember_is_member_of")
  public Set<String> getIsMemberOfs() {
    return isMemberOfs;
  }

  public void setIsMemberOfs(Set<String> isMemberOfs) {
    this.isMemberOfs = isMemberOfs;
  }

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "user_edu_person_entitlement",
      joinColumns = @JoinColumn(name = "user_id")
  )
  @Column(name = "edu_person_entitlement")
  @JsonProperty("eduperson_entitlement")
  public Set<String> getEduPersonEntitlements() {
    return eduPersonEntitlements;
  }

  public void setEduPersonEntitlements(Set<String> eduPersonEntitlements) {
    this.eduPersonEntitlements = eduPersonEntitlements;
  }

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "user_schac_personal_unique_code",
      joinColumns = @JoinColumn(name = "user_id")
  )
  @Column(name = "schac_personal_unique_code")
  public Set<String> getSchacPersonalUniqueCodes() {
    return schacPersonalUniqueCodes;
  }

  public void setSchacPersonalUniqueCodes(Set<String> schacPersonalUniqueCodes) {
    this.schacPersonalUniqueCodes = schacPersonalUniqueCodes;
  }

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "user_uid",
      joinColumns = @JoinColumn(name = "user_id")
  )
  @Column(name = "uid")
  public Set<String> getUids() {
    return uids;
  }

  public void setUids(Set<String> uids) {
    this.uids = uids;
  }

  @Override
  public JsonObject toJson() {
    JsonObject obj = super.toJson();
    addProperty(obj, this.schacHomeOrganization, "schac_home_organization");
    addProperty(obj, this.schacHomeOrganizationType, "schac_home_organization_type");
    addProperty(obj, this.eduPersonPrincipalName, "edu_person_principal_name");
    addProperty(obj, this.eduPersonTargetedId, "edu_person_targeted_id");

    addListProperty(obj, this.eduPersonAffiliations, "edu_person_affiliations");
    addListProperty(obj, this.eduPersonScopedAffiliations, "edu_person_scoped_affiliations");
    addListProperty(obj, this.isMemberOfs, "edumember_is_member_of");
    addListProperty(obj, this.eduPersonEntitlements, "eduperson_entitlement");
    addListProperty(obj, this.schacPersonalUniqueCodes, "schac_personal_unique_codes");
    addListProperty(obj, this.uids, "uids");
    return obj;
  }

  private void addListProperty(JsonObject obj, Set<String> set, String name) {
    if (!CollectionUtils.isEmpty(set)) {
      JsonArray jsonArray = new JsonArray();
      for (String value : set) {
        jsonArray.add(new JsonPrimitive(value));
      }
      obj.add(name, jsonArray);
    }
  }

  private void addProperty(JsonObject obj, String property, String name) {
    if (StringUtils.isNotEmpty(property)) {
      obj.addProperty(name, property);
    }
  }

  @Override
  public String toString() {
    return "FederatedUserInfo{" +
        "schacHomeOrganization='" + schacHomeOrganization + '\'' +
        ", unspecifiedNameId='" + unspecifiedNameId + '\'' +
        ", authenticatingAuthority='" + authenticatingAuthority + '\'' +
        ", schacHomeOrganizationType='" + schacHomeOrganizationType + '\'' +
        ", eduPersonPrincipalName='" + eduPersonPrincipalName + '\'' +
        ", eduPersonTargetedId='" + eduPersonTargetedId + '\'' +
        ", eduPersonAffiliations=" + eduPersonAffiliations +
        ", eduPersonScopedAffiliations=" + eduPersonScopedAffiliations +
        ", isMemberOfs=" + isMemberOfs +
        ", eduPersonEntitlements=" + eduPersonEntitlements +
        ", schacPersonalUniqueCodes=" + schacPersonalUniqueCodes +
        ", uids=" + uids +
        '}';
  }

  public String hashed() {
    return this.toString();
  }
}
