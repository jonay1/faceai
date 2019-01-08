package com.wolf.ai.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baidu.aip.face.AipFace;
import com.wolf.ai.config.AuthConfig;
import com.wolf.ai.service.bean.MatchUser;
import com.wolf.ai.service.bean.UserFace;
import com.wolf.ai.util.CommonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings("unchecked")
public class FaceAiService {

	private AipFace client;
	@Autowired
	private AuthConfig config;

	@PostConstruct
	public void init() {
		// 初始化一个AipFace
		client = new AipFace(config.getApiId(), config.getApiKey(), config.getSecKey());
		// 可选：设置网络连接参数
		client.setConnectionTimeoutInMillis(2000);
		client.setSocketTimeoutInMillis(60000);
	}

	/**
	 * 
	 * { "result": { "group_id_list": [ "group1" ] }, "log_id": 744193240009803091,
	 * "error_msg": "SUCCESS", "cached": 0, "error_code": 0, "timestamp": 1544000980
	 * }
	 * 
	 * @return
	 */
	public List<String> groups() {
		JSONObject groupList = client.getGroupList(null);
		log.info("groups:{}", groupList);
		int code = groupList.getInt("error_code");
		if (code == 0) {
			JSONArray jsonArray = groupList.getJSONObject("result").getJSONArray("group_id_list");
			return (List<String>) (Object) jsonArray.toList();
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	/**
	 * {"result":{"user_id_list":["919"]},"log_id":744193240014865611,"error_msg":"SUCCESS","cached":0,"error_code":0,"timestamp":1544001486}
	 * 
	 * @param groupId
	 * @return
	 */
	public List<String> users(String groupId) {
		JSONObject groupUsers = client.getGroupUsers(groupId, null);
		log.info("group({}):{}", groupId, groupUsers);
		int code = groupUsers.getInt("error_code");
		if (code == 0) {
			JSONArray jsonArray = groupUsers.getJSONObject("result").getJSONArray("user_id_list");
			return (List<String>) (Object) jsonArray.toList();
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	/**
	 * 人脸注册
	 * 
	 * <pre>
	 * 222210-已达上限 223105-已存在
	 * 
	 * @param groupId
	 * @param userId
	 * @param image
	 * @param desc
	 * @return
	 */
	public int addUser(String groupId, String userId, String image, String desc) {
		HashMap<String, String> options = new HashMap<String, String>();
		options.put("user_info", StringUtils.abbreviate(desc, 200));
		options.put("quality_control", "NORMAL");
		options.put("liveness_control", "LOW");
		options.put("action_type", "replace");
		JSONObject res = client.addUser(image, "BASE64", groupId, userId, options);
		log.info("addUser:({},{}):{}", groupId, userId, res);
		return res.getInt("error_code");
	}

	/**
	 * 人脸更新
	 * 
	 * @param groupId
	 * @param userId
	 * @param image
	 * @param desc
	 * @return
	 */
	public int updateUser(String groupId, String userId, String image, String desc) {
		// 传入可选参数调用接口
		HashMap<String, String> options = new HashMap<String, String>();
		options.put("user_info", StringUtils.abbreviate(desc, 200));
		options.put("quality_control", "NORMAL");
		options.put("liveness_control", "LOW");
		String imageType = "BASE64";
		JSONObject res = client.updateUser(image, imageType, groupId, userId, options);
		log.info("updateUser:({},{}):{}", groupId, userId, res);
		return res.getInt("error_code");

	}

	/**
	 * 删除组
	 * 
	 * @param groupId
	 * @return
	 */
	public int deleteGroup(String groupId) {
		JSONObject res = client.groupDelete(groupId, null);
		log.info("deleteGroup:({}):{}", groupId, res);
		return res.getInt("error_code");
	}

	/**
	 * 删除用户
	 * 
	 * @param groupId
	 * @param userId
	 * @return
	 */
	public int deleteUser(String groupId, String userId) {
		JSONObject res = client.deleteUser(groupId, userId, null);
		log.info("deleteUser:({},{}):{}", groupId, userId, res);
		return res.getInt("error_code");
	}

	/**
	 * 人脸删除
	 * 
	 * @param groupId
	 * @param userId
	 * @param faceToken
	 * @return
	 */
	public int deleteFace(String groupId, String userId, String faceToken) {
		JSONObject res = client.faceDelete(userId, groupId, faceToken, null);
		log.info("deleteFace:({},{}):{}", groupId, userId, res);
		return res.getInt("error_code");
	}

	/**
	 * 用户信息查询
	 * 
	 * @param groupId
	 * @param userId
	 * @return
	 */
	public String getUserInfo(String groupId, String userId) {
		JSONObject res = client.getUser(userId, groupId, null);
		log.info("getUserInfo:({},{}):{}", groupId, userId, res);
		int code = res.getInt("error_code");
		if (code == 0) {
			JSONArray jsonArray = res.getJSONObject("result").getJSONArray("user_list");
			if (jsonArray.length() > 0) {
				JSONObject jsonObject = jsonArray.getJSONObject(0);
				return jsonObject.getString("user_info");
			}
		}
		return null;
	}

	/**
	 * 人脸检测
	 * 
	 * @param imageBase64
	 * @return
	 */
	public List<UserFace> detect(String imageBase64) {
		HashMap<String, String> options = new HashMap<String, String>();
		options.put("max_face_num", "2");
		options.put("face_type", "LIVE");
		options.put("face_field", "age,gender");

		String imageType = "BASE64";
		// 人脸检测
		JSONObject res = client.detect(imageBase64, imageType, options);

		log.info("detect():{}", res);
		int code = res.getInt("error_code");
		List<UserFace> list = new ArrayList<>();
		if (code == 0) {
			JSONArray jsonArray = res.getJSONObject("result").getJSONArray("face_list");
			Iterator<Object> it = jsonArray.iterator();
			while (it.hasNext()) {
				JSONObject jsonObject = (JSONObject) it.next();
				JSONObject location = jsonObject.getJSONObject("location");
				JSONObject gender = jsonObject.getJSONObject("gender");
				UserFace uf = new UserFace();
				uf.setLeft(location.getDouble("left"));
				uf.setTop(location.getDouble("top"));
				uf.setWidth(location.getDouble("width"));
				uf.setHeight(location.getDouble("height"));
				uf.setRotation(location.getInt("rotation"));
				uf.setAge(jsonObject.getInt("age"));
				uf.setSex(gender.getString("type"));
				list.add(uf);
			}
		}
		return list;
	}

	/**
	 * 人脸搜索
	 * 
	 * @param groupId
	 * @param userId
	 * @param imageBase64
	 * @return
	 */
	public MatchUser search(String groupId, String userId, String imageBase64) {
		HashMap<String, String> options = new HashMap<String, String>();
		options.put("quality_control", "NORMAL");
		options.put("liveness_control", "NORMAL");
		options.put("user_id", userId);
		options.put("max_user_num", "1");
		String imageType = "BASE64";
		JSONObject res = client.search(imageBase64, imageType, groupId, options);
		log.info("search({},{}):{}", groupId, userId, res);
		int code = res.getInt("error_code");
		if (code == 0) {
			JSONArray jsonArray = res.getJSONObject("result").getJSONArray("user_list");
			if (jsonArray.length() > 0) {
				JSONObject jsonObject = jsonArray.getJSONObject(0);
				MatchUser result = CommonUtil.fromJson(jsonObject.toString(), MatchUser.class);
				return result;
			}
		}
		return null;
	}

	public boolean existUser(String groupId, String userId) {
		HashMap<String, String> options = new HashMap<String, String>();
		// 获取用户人脸列表
		JSONObject res = client.faceGetlist(userId, groupId, options);
		log.info("getUserFaces({},{}):{}", userId, groupId, res);
		int code = res.getInt("error_code");
		return code == 0;
	}

	@PreDestroy
	public void destroy() {
	}
}
