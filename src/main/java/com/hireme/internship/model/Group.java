package com.hireme.internship.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Group {
  private String id;
  private String name;
  private Set<User> members;
  private Set<Internship> sharedInternships;

  public Group(String name) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.members = new HashSet<>();
    this.sharedInternships = new HashSet<>();
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<User> getMembers() {
    return members;
  }

  public void addMember(User user) {
    this.members.add(user);
    user.addGroup(this);
  }

  public boolean removeMember(User user) {
    boolean removed = this.members.remove(user);
    if (removed) {
      user.removeGroup(this);
    }
    return removed;
  }

  public Set<Internship> getSharedInternships() {
    return sharedInternships;
  }

  public void shareInternship(Internship internship) {
    this.sharedInternships.add(internship);
  }

  public boolean removeInternship(Internship internship) {
    return this.sharedInternships.remove(internship);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Group group = (Group) o;
    return id.equals(group.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return name + " (" + members.size() + " members)";
  }
}
