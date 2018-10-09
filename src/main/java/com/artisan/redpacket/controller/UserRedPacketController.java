package com.artisan.redpacket.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.artisan.redpacket.service.UserRedPacketService;

@Controller
@RequestMapping("/userRedPacket")
public class UserRedPacketController {

	@Autowired
	private UserRedPacketService userRedPacketService;

	@RequestMapping(value = "/grapRedPacket")
	@ResponseBody
	public Map<String, Object> grapRedPacket(Long redPacketId, Long userId) {
		// ÇÀºì°ü
		int result = userRedPacketService.grapRedPacket(redPacketId, userId);
		Map<String, Object> retMap = new HashMap<String, Object>();
		boolean flag = result > 0;
		retMap.put("success", flag);
		retMap.put("message", flag ? "ÇÀºì°ü³É¹¦" : "ÇÀºì°üÊ§°Ü");
		return retMap;
	}
	
	
}
