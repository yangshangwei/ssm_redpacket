package com.artisan.redpacket.service;

public interface UserRedPacketService {
	
	/**
	 * 保存抢红包信息.
	 * @param redPacketId 红包编号
	 * @param userId 抢红包用户编号
	 * @return 影响记录数.
	 */
	public int grapRedPacket(Long redPacketId, Long userId);
	
	/**
	 * 保存抢红包信息. 乐观锁的方式
	 * 
	 * @param redPacketId
	 *            红包编号
	 * @param userId
	 *            抢红包用户编号
	 * @return 影响记录数.
	 */
	public int grapRedPacketForVersion(Long redPacketId, Long userId);
}
