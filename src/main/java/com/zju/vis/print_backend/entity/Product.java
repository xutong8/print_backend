package com.zju.vis.print_backend.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_product")
public class Product implements Serializable {

  @Id
  @GeneratedValue
  @Column(name = "product_id", nullable = false)
  private Long product_id;

  @Column(name = "product_name", nullable = false)
  private String product_name;

  @Column(name = "product_index", nullable = false)
  private String product_index;

  @Column(name = "product_code", nullable = false)
  private String product_code;

  @Column(name = "product_color")
  private String product_color;

  @Column(name = "product_processing_cost", nullable = false)
  private Float product_processing_cost;

  @Column(name = "product_accounting_quantity", nullable = false)
  private Integer product_accounting_quantity;

  @Column(name = "product_series_id")
  private Integer product_series_id;

  @Column(name = "product_factory_name", nullable = false)
  private String product_factory_name;

  @Column(name = "product_remarks")
  private String product_remarks;

  public Long getProduct_id() {
    return product_id;
  }

  public void setProduct_id(Long product_id) {
    this.product_id = product_id;
  }

  public String getProduct_name() {
    return product_name;
  }

  public void setProduct_name(String product_name) {
    this.product_name = product_name;
  }

  public String getProduct_index() {
    return product_index;
  }

  public void setProduct_index(String product_index) {
    this.product_index = product_index;
  }

  public String getProduct_code() {
    return product_code;
  }

  public void setProduct_code(String product_code) {
    this.product_code = product_code;
  }

  public String getProduct_color() {
    return product_color;
  }

  public void setProduct_color(String product_color) {
    this.product_color = product_color;
  }

  public Float getProduct_processing_cost() {
    return product_processing_cost;
  }

  public void setProduct_processing_cost(Float product_processing_cost) {
    this.product_processing_cost = product_processing_cost;
  }

  public Integer getProduct_accounting_quantity() {
    return product_accounting_quantity;
  }

  public void setProduct_accounting_quantity(Integer product_accounting_quantity) {
    this.product_accounting_quantity = product_accounting_quantity;
  }

  public Integer getProduct_series_id() {
    return product_series_id;
  }

  public void setProduct_series_id(Integer product_series_id) {
    this.product_series_id = product_series_id;
  }

  public String getProduct_factory_name() {
    return product_factory_name;
  }

  public void setProduct_factory_name(String product_factory_name) {
    this.product_factory_name = product_factory_name;
  }

  public String getProduct_remarks() {
    return product_remarks;
  }

  public void setProduct_remarks(String product_remarks) {
    this.product_remarks = product_remarks;
  }
}
