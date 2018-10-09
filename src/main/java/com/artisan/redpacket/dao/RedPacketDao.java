package com.artisan.redpacket.dao;

import org.apache.ibatis.annotations.Param;
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

	/**
	 * @Description: 扣减抢红包数. 乐观锁的实现方式
	 * 
	 * @param id
	 *            -- 红包id
	 * @param version
	 *            -- 版本标记
	 * 
	 * @return: 更新记录条数
	 */
	public int decreaseRedPacketForVersion(@Param("id") Long id, @Param("version") Integer version);
}