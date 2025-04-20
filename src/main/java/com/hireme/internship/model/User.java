package com.hireme.internship.model;

import java.util.HashSet;
import java.util.Set;

public class User {
  private String name;
  private String email;
  private Set<Group> groups;
  private Set<Internship> savedInternships;

  public User(String name, String email) {
    this.name = name;
    this.email = email;
    this.groups = new HashSet<>();
    this.savedInternships = new HashSet<>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Set<Group> getGroups() {
    return groups;
  }

  public void addGroup(Group group) {
    this.groups.add(group);
  }

  public boolean removeGroup(Group group) {
    return this.groups.remove(group);
  }

  public Set<Internship> getSavedInternships() {
    return savedInternships;
  }

  public void addInternship(Internship internship) {
    this.savedInternships.add(internship);
  }

  public boolean removeInternship(Internship internship) {
    return this.savedInternships.remove(internship);
  }

  @Override
  public String toString() {
    return name + " (" + email + ")";
  }
}
