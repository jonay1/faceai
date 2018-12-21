package com.wolf.ai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wolf.ai.controller.bean.FaceUser;
import com.wolf.ai.controller.bean.GroupInfo;
import com.wolf.ai.service.FaceAiService;

@RestController
@RequestMapping("/backend/api")
public class FaceAiController {

	@Autowired
	private FaceAiService service;

	@RequestMapping("/groups")
	public Object groups() {
		return service.groups();
	}

	@RequestMapping("/groups/{gid}")
	public Object groups(@PathVariable String gid) {
		return service.users(gid);
	}

	@RequestMapping("/groups/{gid}/users")
	public Object users(@PathVariable String gid) {
		return service.users(gid);
	}

	@RequestMapping("/groups/{gid}/users/{uid}/")
	public Object users(@PathVariable String gid, @PathVariable String uid) {
		return service.getUserInfo(gid, uid);
	}

	@RequestMapping("/delgroup")
	public Object delgroup(@RequestBody GroupInfo gi) {
		return service.deleteGroup(gi.getGid());
	}
	@RequestMapping("/deluser")
	public Object deluser(@RequestBody FaceUser fu) {
		return service.deleteUser(fu.getGid(), fu.getUid());
	}
}
