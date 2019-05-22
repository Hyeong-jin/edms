package com.innerwave.edms.core;

import lombok.Getter;
import lombok.Setter;

/**
 * File class for file tree
 */
@Getter
@Setter
public class File extends Node {
  public static int nextValue = -1;
  private String ext;

  public File() {
    // set default name
    setName("NewFile"+ ++File.nextValue);
    setType("file");
  }

}