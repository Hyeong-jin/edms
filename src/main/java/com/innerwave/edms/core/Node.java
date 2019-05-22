package com.innerwave.edms.core;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 폴더의 구조를 `Tree`로 구성한다.
 *
 * `Node`, `Folder`, `File`로 단순한 구성을 가진다. `Folder`와 `File` 은 `Node`를 상속하여 공통 기능을
 * 구현하고, 자신만의 *속성*을 추가로 갖는다.
 *
 * {@code}
 *
 * <pre>
 * Folder root = new Folder();
 * root.add(new Folder());
 * root.add(new File());
 * root.put("propName", propValue);
 * </pre>
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public abstract class Node implements Comparable<Object> {

  @JsonIgnore
  private Logger logger = LoggerFactory.getLogger(Node.class);

  @JsonIgnore
  private ObjectMapper mapper = new ObjectMapper();

  private String id; // 노드 아이디
  private String type; // 'folder' or 'file'
  private String name; // 노드 이름
  private Date created; // 생성 일시
  private Date updated; // 수정 일시
  private Map<String, Object> options; // 속성

  public Node() {
    id = new ObjectIdGenerators.StringIdGenerator().generateId(this);
    created = new Date();
    options = new HashMap<String, Object>();
  }

  @Override
  public int compareTo(Object another) {
    if (this.getClass().equals(another.getClass())) {
      // 양쪽 모두 폴더이거나 파일이면 이름을 비교한다.
      return this.getName().compareTo(((Node) another).getName());
    } else {
      // 폴더 우선 정렬 - 폴더를 나열한 후 파일을 나열한다.
      // 좌측이 폴더면 -1, 아니면 1을 반환한다.
      return this.getClass().equals(Folder.class) ? -1 : 1;
    }
  }

  public String toJSON() {
    try {
      if (logger.isDebugEnabled()) {
        // NOTE: 개발 시 디버그를 위해 포맷한 문자열로 출력.
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
      } else {
        return mapper.writeValueAsString(this);
      }
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  // JSON 문자열을 JsonNode 객체로 로딩하여 파일 및 폴더 트리를 만든다.
  public void loadJSON(String json) {
    try {
      JsonNode jsonNode = mapper.readTree(json);
      loadJSON(jsonNode);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // JsonNode 객체로 부터 파일 및 폴더 트리를 만든다.
  public void loadJSON(JsonNode jsonNode) {
    if (jsonNode.hasNonNull("nodes")) {
      // if ("folder".equals(jsonNode.get("type").asText())) {
      for (JsonNode jnode : jsonNode.get("nodes")) {
        ((Folder) this).add(Node.fromJSON(jnode));
      }
    } else {
      ((File) this).setExt(jsonNode.get("ext").asText());
    }
    this.id = jsonNode.get("id").asText();
    this.name = jsonNode.get("name").asText();
    this.created = new Date(jsonNode.get("created").asLong());
    if (jsonNode.hasNonNull("updated")) {
      this.updated = new Date(jsonNode.get("update").asLong());
    }
    this.options = getOptions(jsonNode.get("options"));
  }

  // JSON 문자열을 JsonNode 객체를 생성하고 fromJSON(JsonNode jsonNode)를 실행한 결과를 반환한다.
  public static Node fromJSON(String json) {
    Node node = null;
    try {
      JsonNode jsonNode = new ObjectMapper().readTree(json);
      node = Node.fromJSON(jsonNode);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return node;
  }

  // JsonNode 객체로 부터 Folder나 File 객체를 생성한다.
  public static Node fromJSON(JsonNode jsonNode) {
    Node node = null;
    if (jsonNode.hasNonNull("nodes")) {
      // if ("folder".equals(jsonNode.get("type").asText())) {
      node = new Folder();
    } else {
      node = new File();
    }
    node.loadJSON(jsonNode);
    return node;
  }

  // -- ----------------------------------------------------------------------
  // Node Options's getter and setter
  // -- ----------------------------------------------------------------------
  public Object get(String prop) {
    return options.get(prop);
  }

  public Object put(String prop, Object value) {
    return options.put(prop, value);
  }

  /**
   * 옵셔널 속성을 JsonNode에서 읽어 어 HashMap으로 생성
   *
   * @param jnode
   * @return
   */
  private Map<String, Object> getOptions(JsonNode jnode) {
    Map<String, Object> options = new HashMap<String, Object>();
    Iterator<String> keys = jnode.fieldNames();
    while (keys.hasNext()) {
      String propName = keys.next();
      Object propValue = jnode.get(propName);
      options.put(propName, propValue);
    }
    return options;
  }
}