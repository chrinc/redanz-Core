//package ch.redanz.redanzCore.user;
//
//
//import javax.persistence.*;
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//
//enum RoleConfig{
//  PARTICIPANT("Participant", "Workshop Participant"),
//  ORGANIZER("Organizer", "Workshop Organizer");
//
//  private String name;
//  private String description;
//
//  RoleConfig(String name, String description) {
//    this.name = name;
//    this.description = description;
//  }
//
//  public String getName() {
//    return name;
//  }
//
//  public String getDescription() {
//    return description;
//  }
//}
//
//
//@Entity
//@Table(name="role")
//public class Role implements Serializable {
//  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  private Long id;
//
//  @Column(name="internal_id")
//  private String internalId;
//  private String name;
//  private String description;
//
//  public Role () {}
//
//  public Role (Long role_id) {
//    this.id = role_id;
//  }
//
//  public Role (
//    String name,
//    String internalId,
//    String description
//  ) {
//    this.name = name;
//    this.internalId = internalId;
//    this.description = description;
//  }
//
//  // getter
//  public Long getid() {
//    return id;
//  }
//
//  public String getInternalId() {
//    return internalId;
//  }
//
//  public String getName() {
//    return name;
//  }
//
//  public String getDescription() {
//    return description;
//  }
//
//  // setter
//  public void setRoleId(Long id) {
//    this.id = id;
//  }
//
//  public void setInternalId(String internalId) {
//    this.internalId = internalId;
//  }
//
//  public void setName(String name) {
//    this.name = name;
//  }
//
//  public void setDescription(String description) {
//    this.description = description;
//  }
//
//  @Override
//  public String toString(){
//    return
//      "Role{"
//        + "id=" + id
//        + "internalId=" + internalId
//        + ", name= '" + name + '\''
//        + ", description= '" + description
//        + "}";
//  }
//
//
//  public static List<Role> setup() {
//    List<Role> transitionList = new ArrayList<>();
//
//    for (RoleConfig roleConfig : RoleConfig.values()){
//      transitionList.add(new Role(roleConfig.getName(), roleConfig.toString().toLowerCase(), roleConfig.getDescription()));
//    }
//    return transitionList;
//  }
//}
//
