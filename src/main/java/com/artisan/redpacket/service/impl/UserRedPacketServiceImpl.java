package com.artisan.redpacket.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.artisan.redpacket.dao.RedPacketDao;
import com.artisan.redpacket.dao.UserRedPacketDao;
import com.artisan.redpacket.pojo.RedPacket;
import com.artisan.redpacket.pojo.UserRedPacket;
import com.artisan.redpacket.service.UserRedPacketService;

@Service
public class UserRedPacketServiceImpl implements UserRedPacketService {

	// private Logger logger =
	// LoggerFactory.getLogger(UserRedPacketServiceImpl.class);

	@Autowired
	private UserRedPacketDao userRedPacketDao;

	@Autowired
	private RedPacketDao redPacketDao;

	// 失败
	private static final int FAILED = 0;

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public int grapRedPacket(Long redPacketId, Long userId) {
		// 获取红包信息 -并发存在超发
		// RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
		// 获取红包信息 -悲观锁的实现方式
		RedPacket redPacket = redPacketDao.getRedPacketForUpdate(redPacketId);
		// 当前小红包库存大于0
		if (redPacket.getStock() > 0) {
			redPacketDao.decreaseRedPacket(redPacketId);
			// 生成抢红包信息
			UserRedPacket userRedPacket = new UserRedPacket();
			userRedPacket.setRedPacketId(redPacketId);
			userRedPacket.setUserId(userId);
			userRedPacket.setAmount(redPacket.getUnitAmount());
			userRedPacket.setNote("redpacket- " + redPacketId);
			// 插入抢红包信息
			int result = userRedPacketDao.grapRedPacket(userRedPacket);
			return result;
		}
		// 失败返回
		return FAILED;
	}

	/**
	 * 乐观锁，无重入
	 * */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public int grapRedPacketForVersion(Long redPacketId, Long userId) {
		// 获取红包信息
		RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
		// 当前小红包库存大于0
		if (redPacket.getStock() > 0) {
			// 再次传入线程保存的version旧值给SQL判断，是否有其他线程修改过数据
			int update = redPacketDao.decreaseRedPacketForVersion(redPacketId, redPacket.getVersion());
			// 如果没有数据更新，则说明其他线程已经修改过数据，则重新抢夺
			if (update == 0) {
				return FAILED;
			}
			// 生成抢红包信息
			UserRedPacket userRedPacket = new UserRedPacket();
			userRedPacket.setRedPacketId(redPacketId);
			userRedPacket.setUserId(userId);
			userRedPacket.setAmount(redPacket.getUnitAmount());
			userRedPacket.setNote("redpacket- " + redPacketId);
			// 插入抢红包信息
			int result = userRedPacketDao.grapRedPacket(userRedPacket);
			return result;
		}
		// 失败返回
		return FAILED;
	}

	/**
	 * 
	 * 
	 * 乐观锁，按时间戳重入
	 * 
	 * @Description: 乐观锁，按时间戳重入
	 * 
	 * @param redPacketId
	 * @param userId
	 * @return
	 * 
	 * @return: int
	 */
	// @Override
	// @Transactional(isolation = Isolation.READ_COMMITTED, propagation =
	// Propagation.REQUIRED)
	// public int grapRedPacketForVersion(Long redPacketId, Long userId) {
	// // 记录开始时间
	// long start = System.currentTimeMillis();
	// // 无限循环，等待成功或者时间满100毫秒退出
	// while (true) {
	// // 获取循环当前时间
	// long end = System.currentTimeMillis();
	// // 当前时间已经超过100毫秒，返回失败
	// if (end - start > 100) {
	// return FAILED;
	// }
	// // 获取红包信息,注意version值
	// RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
	// // 当前小红包库存大于0
	// if (redPacket.getStock() > 0) {
	// // 再次传入线程保存的version旧值给SQL判断，是否有其他线程修改过数据
	// int update = redPacketDao.decreaseRedPacketForVersion(redPacketId,
	// redPacket.getVersion());
	// // 如果没有数据更新，则说明其他线程已经修改过数据，则重新抢夺
	// if (update == 0) {
	// continue;
	// }
	// // 生成抢红包信息
	// UserRedPacket userRedPacket = new UserRedPacket();
	// userRedPacket.setRedPacketId(redPacketId);
	// userRedPacket.setUserId(userId);
	// userRedPacket.setAmount(redPacket.getUnitAmount());
	// userRedPacket.setNote("抢红包 " + redPacketId);
	// // 插入抢红包信息
	// int result = userRedPacketDao.grapRedPacket(userRedPacket);
	// return result;
	// } else {
	// // 一旦没有库存，则马上返回
	// return FAILED;
	// }
	// }
	// }

	/**
	 * 
	 * 
	 * @Title: grapRedPacketForVersion3
	 * 
	 * @Description: 乐观锁，按次数重入
	 * 
	 * @param redPacketId
	 * @param userId
	 * 
	 * @return: int
	 */
	// @Override
	// @Transactional(isolation = Isolation.READ_COMMITTED, propagation =
	// Propagation.REQUIRED)
	// public int grapRedPacketForVersion(Long redPacketId, Long userId) {
	// for (int i = 0; i < 3; i++) {
	// // 获取红包信息，注意version值
	// RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
	// // 当前小红包库存大于0
	// if (redPacket.getStock() > 0) {
	// // 再次传入线程保存的version旧值给SQL判断，是否有其他线程修改过数据
	// int update = redPacketDao.decreaseRedPacketForVersion(redPacketId,
	// redPacket.getVersion());
	// // 如果没有数据更新，则说明其他线程已经修改过数据，则重新抢夺
	// if (update == 0) {
	// continue;
	// }
	// // 生成抢红包信息
	// UserRedPacket userRedPacket = new UserRedPacket();
	// userRedPacket.setRedPacketId(redPacketId);
	// userRedPacket.setUserId(userId);
	// userRedPacket.setAmount(redPacket.getUnitAmount());
	// userRedPacket.setNote("抢红包 " + redPacketId);
	// // 插入抢红包信息
	// int result = userRedPacketDao.grapRedPacket(userRedPacket);
	// return result;
	// } else {
	// // 一旦没有库存，则马上返回
	// return FAILED;
	// }
	// }
	// return FAILED;
	// }

}
