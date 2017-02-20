package com.taotao.cart.bean;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Item{

    private Long id;

    private String title;
    private String sellPoint;
    private Long price;
    private Date updated;
    private String image;
    private Long cid;
    private Integer status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSellPoint() {
		return sellPoint;
	}

	public void setSellPoint(String sellPoint) {
		this.sellPoint = sellPoint;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	public String[] getImages(){
		return StringUtils.split(getImage(), ',');
	}
	@Override
	public String toString() {
		return "Item [id=" + id + ", title=" + title + ", sellPoint="
				+ sellPoint + ", price=" + price + ", updated=" + updated
				+ ", image=" + image + ", cid=" + cid + ", status=" + status
				+ "]";
	}
}
