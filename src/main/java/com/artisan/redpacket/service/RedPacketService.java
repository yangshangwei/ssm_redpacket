package com.artisan.redpacket.service;

import com.artisan.redpacket.pojo.RedPacket;


public interface RedPacketService {
	
	/**
	 * 获取红包
	 * @param id ——编号
	 * @return 红包信息
	 */
	public RedPacket getRedPacket(Long id);

	/**
	 * 扣减红包
	 * @param id——编号
	 * @return 影响条数.
	 */
	public int decreaseRedPacket(Long id);
	
}