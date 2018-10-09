package com.artisan.redpacket.dao;

import org.springframework.stereotype.Repository;

import com.artisan.redpacket.pojo.RedPacket;


@Repository
public interface RedPacketDao {
	
	/**
	 * 获取红包信息.
	 * @param id --红包id
	 * @return 红包具体信息
	 */
	public RedPacket getRedPacket(Long id);
	
	/**
	 * 扣减抢红包数.
	 * @param id -- 红包id
	 * @return 更新记录条数
	 */
	public int decreaseRedPacket(Long id);
	
	/**
	 * 获取红包信息. 悲观锁的实现方式
	 * 
	 * @param id
	 *            --红包id
	 * @return 红包具体信息
	 */
	public RedPacket getRedPacketForUpdate(Long id);
}