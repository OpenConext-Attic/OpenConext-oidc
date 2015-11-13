package oidc.model;

import org.mitre.openid.connect.model.DefaultUserInfo;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "user_info")
public class FederatedUserInfo extends DefaultUserInfo {

  private String schacHomeOrganization;
  private String unspecifiedNameId;

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
}
