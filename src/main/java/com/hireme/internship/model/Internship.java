package com.hireme.internship.model;

import java.util.Date;
import java.util.Objects;

public class Internship {
  private String id;
  private String company;
  private String position;
  private String description;
  private Date deadline;
  private String url;
  private String metadata;

  public Internship(String company, String position, String description, Date deadline, String url) {
    this.id = company + "-" + position;
    this.company = company;
    this.position = position;
    this.description = description;
    this.deadline = deadline;
    this.url = url;
  }

  // Getters and setters
  public String getId() {
    return id;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
    updateId();
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
    updateId();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getDeadline() {
    return deadline;
  }

  public void setDeadline(Date deadline) {
    this.deadline = deadline;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getMetadata() {
    return metadata;
  }

  public void setMetadata(String metadata) {
    this.metadata = metadata;
  }

  private void updateId() {
    this.id = company + "-" + position;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Internship that = (Internship) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return company + " - " + position;
  }
}
