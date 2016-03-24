package at.rags.morpheus;

import android.util.ArrayMap;

import java.util.HashMap;

/**
 * JSON:API error object.
 */
public class Error {
  private String id;
  private String status;
  private String code;
  private String title;
  private String detail;
  private Source source;
  private ErrorLinks links;
  private HashMap<String, Object> meta;

  public HashMap<String, Object> getMeta() {
    return meta;
  }

  public void setMeta(HashMap<String, Object> meta) {
    this.meta = meta;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ErrorLinks getLinks() {
    return links;
  }

  public void setLinks(ErrorLinks linkss) {
    this.links = linkss;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public Source getSource() {
    return source;
  }

  public void setSource(Source source) {
    this.source = source;
  }
}

class Source {
  private String parameter;
  private String pointer;

  public String getPointer() {
    return pointer;
  }

  public void setPointer(String pointer) {
    this.pointer = pointer;
  }

  public String getParameter() {
    return parameter;
  }

  public void setParameter(String parameter) {
    this.parameter = parameter;
  }
}

class ErrorLinks {
  private String about;

  public String getAbout() {
    return about;
  }

  public void setAbout(String about) {
    this.about = about;
  }
}