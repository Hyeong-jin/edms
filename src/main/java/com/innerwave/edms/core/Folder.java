package com.innerwave.edms.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * Folder class for file tree
 */
@Getter
@Setter
public class Folder extends Node {
  public static int nextValue = -1;
  private Collection<Node> nodes;

  public Folder() {
    nodes = new HashSet<Node>();
    // set default name
    setName("NewFolder"+ ++Folder.nextValue);
    setType("folder");
  }

  /**
   * 하위 노드(Folder or File) 추가
   * @param node
   * @return
   */
  public boolean add(Node node) {
    return nodes.add(node);
  }

  /**
   * 하위 노드 제거
   * @param node
   * @return
   */
  public boolean remove(Node node) {
    return nodes.remove(node);
  }

  /**
   * 서브 노드들을 폴더 우선 정렬하여 반환
   * @return
   */
  public Collection<Node> getNodes() {
    List<Node> list = new ArrayList<Node>(nodes);
    Collections.sort(list);
    return list;
  }
}
