package com.artisan.redpacket.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 
 * 
 * @ClassName: RedPacket
 * 
 * @Description: 红包表对应的实体类，可序列化
 * 
 * @author: Mr.Yang
 * 
 * @date: 2018年10月8日 下午3:42:58
 */
public class RedPacket implements Serializable {

	private static final long serialVersionUID = 9036484563091364939L;
	// 红包编号
	private Long id;
	// 发红包的用户id
	private Long userId;
	// 红包金额
	private Double amount;
	// 发红包日期
	private Timestamp sendDate;
	// 红包总数
	private Integer total;
	// 单个红包的金额
	private Double unitAmount;
	// 红包剩余个数
	private Integer stock;
	// 版本（为后续扩展用）
	private Integer version;
	// 备注
	private String note;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Timestamp getSendDate() {
		return sendDate;
	}

	public void setSendDate(Timestamp sendDate) {
		this.sendDate = sendDate;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Double getUnitAmount() {
		return unitAmount;
	}

	public void setUnitAmount(Double unitAmount) {
		this.unitAmount = unitAmount;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}



}