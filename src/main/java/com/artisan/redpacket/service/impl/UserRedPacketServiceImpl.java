package com.artisan.redpacket.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import redis.clients.jedis.Jedis;

import com.artisan.redpacket.dao.RedPacketDao;
import com.artisan.redpacket.dao.UserRedPacketDao;
import com.artisan.redpacket.pojo.RedPacket;
import com.artisan.redpacket.pojo.UserRedPacket;
import com.artisan.redpacket.service.RedisRedPacketService;
import com.artisan.redpacket.service.UserRedPacketService;

@Service
public class UserRedPacketServiceImpl implements UserRedPacketService {


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
	// @Override
	// @Transactional(isolation = Isolation.READ_COMMITTED, propagation =
	// Propagation.REQUIRED)
	// public int grapRedPacketForVersion(Long redPacketId, Long userId) {
	// // 获取红包信息
	// RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
	// // 当前小红包库存大于0
	// if (redPacket.getStock() > 0) {
	// // 再次传入线程保存的version旧值给SQL判断，是否有其他线程修改过数据
	// int update = redPacketDao.decreaseRedPacketForVersion(redPacketId,
	// redPacket.getVersion());
	// // 如果没有数据更新，则说明其他线程已经修改过数据，则重新抢夺
	// if (update == 0) {
	// return FAILED;
	// }
	// // 生成抢红包信息
	// UserRedPacket userRedPacket = new UserRedPacket();
	// userRedPacket.setRedPacketId(redPacketId);
	// userRedPacket.setUserId(userId);
	// userRedPacket.setAmount(redPacket.getUnitAmount());
	// userRedPacket.setNote("redpacket- " + redPacketId);
	// // 插入抢红包信息
	// int result = userRedPacketDao.grapRedPacket(userRedPacket);
	// return result;
	// }
	// // 失败返回
	// return FAILED;
	// }

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
	// // 获取红包信息
	// RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
	// // 当前小红包库存大于0
	// if (redPacket.getStock() > 0) {
	// // 再次传入线程保存的version旧值给SQL判断，是否有其他线程修改过数据
	// int update = redPacketDao.decreaseRedPacketForVersion(redPacketId,
	// redPacket.getVersion());
	// // 如果没有数据更新，则说明其他线程已经修改过数据，则重新抢夺
	// if (update == 0) {
	// return FAILED;
	// }
	// // 生成抢红包信息
	// UserRedPacket userRedPacket = new UserRedPacket();
	// userRedPacket.setRedPacketId(redPacketId);
	// userRedPacket.setUserId(userId);
	// userRedPacket.setAmount(redPacket.getUnitAmount());
	// userRedPacket.setNote("redpacket- " + redPacketId);
	// // 插入抢红包信息
	// int result = userRedPacketDao.grapRedPacket(userRedPacket);
	// return result;
	// }
	// // 失败返回
	// return FAILED;
	// }


	/**
	 * 
	 * 
	 * @Title: grapRedPacketForVersion
	 * 
	 * @Description: 乐观锁，按次数重入
	 * 
	 * @param redPacketId
	 * @param userId
	 * 
	 * @return: int
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public int grapRedPacketForVersion(Long redPacketId, Long userId) {
		for (int i = 0; i < 3; i++) {
			// 获取红包信息，注意version值
			RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
			// 当前小红包库存大于0
			if (redPacket.getStock() > 0) {
				// 再次传入线程保存的version旧值给SQL判断，是否有其他线程修改过数据
				int update = redPacketDao.decreaseRedPacketForVersion(redPacketId, redPacket.getVersion());
				// 如果没有数据更新，则说明其他线程已经修改过数据，则重新抢夺
				if (update == 0) {
					continue;
				}
				// 生成抢红包信息
				UserRedPacket userRedPacket = new UserRedPacket();
				userRedPacket.setRedPacketId(redPacketId);
				userRedPacket.setUserId(userId);
				userRedPacket.setAmount(redPacket.getUnitAmount());
				userRedPacket.setNote("抢红包 " + redPacketId);
				// 插入抢红包信息
				int result = userRedPacketDao.grapRedPacket(userRedPacket);
				return result;
			} else {
				// 一旦没有库存，则马上返回
				return FAILED;
			}
		}
		return FAILED;
	}

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private RedisRedPacketService redisRedPacketService;

	// Lua脚本
	String script = "local listKey = 'red_packet_list_'..KEYS[1] \n" 
			+ "local redPacket = 'red_packet_'..KEYS[1] \n"
			+ "local stock = tonumber(redis.call('hget', redPacket, 'stock')) \n" 
			+ "if stock <= 0 then return 0 end \n"
			+ "stock = stock -1 \n" 
			+ "redis.call('hset', redPacket, 'stock', tostring(stock)) \n"
			+ "redis.call('rpush', listKey, ARGV[1]) \n" 
			+ "if stock == 0 then return 2 end \n" 
			+ "return 1 \n";

	// 在缓存LUA脚本后，使用该变量保存Redis返回的32位的SHA1编码，使用它去执行缓存的LUA脚本[加入这句话]
	String sha1 = null;

	@Override
	public Long grapRedPacketByRedis(Long redPacketId, Long userId) {
		// 当前抢红包用户和日期信息
		String args = userId + "-" + System.currentTimeMillis();
		Long result = null;
		// 获取底层Redis操作对象
		Jedis jedis = (Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
		try {
			// 如果脚本没有加载过，那么进行加载，这样就会返回一个sha1编码
			if (sha1 == null) {
				sha1 = jedis.scriptLoad(script);
			}
			// 执行脚本，返回结果
			Object res = jedis.evalsha(sha1, 1, redPacketId + "", args);
			result = (Long) res;
			// 返回2时为最后一个红包，此时将抢红包信息通过异步保存到数据库中
			if (result == 2) {
				// 获取单个小红包金额
				String unitAmountStr = jedis.hget("red_packet_" + redPacketId, "unit_amount");
				// 触发保存数据库操作
				Double unitAmount = Double.parseDouble(unitAmountStr);
				redisRedPacketService.saveUserRedPacketByRedis(redPacketId, unitAmount);
			}
		} finally {
			// 确保jedis顺利关闭
			if (jedis != null && jedis.isConnected()) {
				jedis.close();
			}
		}
		return result;
	}

}
