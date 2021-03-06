package com.redhat.examples.fuse.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Product {

  private final int code;
  private final String name;

  public Product(int code, String name) {
    this.code = code;
    this.name = name;
  }

  @JsonProperty
  public int code() {
    return code;
  }

  @JsonProperty
  public String name() {
    return name;
  }

@Override
public String toString() {
	return "Product [code=" + code + ", name=" + name + "]";
}

  
  
}
