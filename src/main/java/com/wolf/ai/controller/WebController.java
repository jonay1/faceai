package com.wolf.ai.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wolf.ai.app.db.StaffDao;
import com.wolf.ai.app.db.entity.Staff;
import com.wolf.ai.constants.Consts;
import com.wolf.ai.constants.FaceConst;
import com.wolf.ai.controller.bean.CallbackBean;
import com.wolf.ai.controller.bean.FaceInfo;
import com.wolf.ai.controller.bean.UserInfo;
import com.wolf.ai.service.FaceAiService;
import com.wolf.ai.service.bean.MatchUser;
import com.wolf.ai.service.bean.UserFace;
import com.wolf.ai.util.CommonUtil;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/backend")
public class WebController {

	@Autowired
	private StaffDao staffDao;
	@Autowired
	private FaceAiService faceService;
	@Autowired
	private SimpMessagingTemplate messageService;

	@RequestMapping("/face/reset/{gid}/{uid}")
	public Object resetUser(@PathVariable String gid, @PathVariable String uid) throws IOException {
		return faceService.deleteUser(gid, uid);
	}

	@RequestMapping("/upload")
	public Object upload(MultipartFile file, UserInfo ui) throws IOException {
		Staff staff = staffDao.findById(ui.getPhone()).orElse(null);
		if (staff == null) {
			staff = new Staff();
			staff.setPhone(ui.getPhone());
			staff.setName(ui.getName());
			staff.setRegion(ui.getRegion());
		}
		BufferedImage srcImage = ImageIO.read(file.getInputStream());
		String image2base64 = CommonUtil.image2base64(srcImage);
		List<UserFace> faces = faceService.detect(image2base64);
		if (faces.size() == 1) {
			UserFace uf = faces.get(0);
//			final int PAD = 50;
//			int x = (int) uf.getLeft();// Math.min(0, (int) uf.getLeft() - PAD);
//			int y = (int) uf.getTop();// Math.min(0, (int) uf.getTop() - PAD);
//			int w = (int) uf.getWidth();// Math.min(srcImage.getWidth() - x, (int) uf.getWidth() + PAD * 2);
//			int h = (int) uf.getHeight();// Math.min(srcImage.getHeight() - y, (int) uf.getHeight() + PAD * 2);
//			BufferedImage distImage = srcImage.getSubimage(x, y, w, h);
//			staff.setImage(CommonUtil.image2base64(distImage));
			staff.setImage(image2base64);
			int rs = faceService.addUser(staff.getRegion(), staff.getPhone(), image2base64, staff.getName());
			if (rs == 0) {
				staffDao.save(staff);
				return 0;
			} else if (rs == FaceConst.ALREAD_EXIST || rs == FaceConst.LIMIT_OVERDUE) {
				return 1;
			}
		}
		return -1;

	}

	@RequestMapping("/list/{region}")
	public Object list(@PathVariable String region) {
		Staff exampleEntity = new Staff();
		exampleEntity.setRegion(region);
		return staffDao.findAll(Example.of(exampleEntity), Sort.by(Direction.DESC, "signed").and(Sort.by("name")));
	}

	@RequestMapping("/users/{userid}")
	public Object users(@PathVariable String userid) {
		return staffDao.findById(userid);
	}

	@RequestMapping("/sign/{region}")
	public Object sign(@PathVariable String region, @RequestBody FaceInfo face) throws IOException {
		MatchUser user = faceService.search(region, null, face.getImage().substring(Consts.IMG_BASE64_LEN));
		if (user != null && user.getScore() > 90) {
			Staff staff = staffDao.findById(user.getUser_id()).orElse(null);
			if (staff != null && BooleanUtils.isNotTrue(staff.getSigned())) {
				Staff rs = new Staff();
				rs.setPhone(staff.getPhone());
				rs.setName(staff.getName());
				messageService.convertAndSend("/topic/sign/" + region, rs);
				staff.setSigned(true);
				staffDao.save(staff);
			}
			return staff;
		}
		return null;
	}

	@RequestMapping("/fc/{region}")
	public void facecallback(@PathVariable String region, @RequestBody CallbackBean bean, HttpServletRequest req)
			throws IOException {
		log.info("facecallback url  : {}", req.getRequestURL());
		log.info("facecallback parm : {}", req.getParameterMap());
		log.info("facecallback bean : {}", bean);
		Staff staff = staffDao.findById(bean.getTelephone()).orElse(null);
		if (staff == null) {
			staff = new Staff();
			staff.setPhone(bean.getTelephone());
		}
		staff.setRegion(region);
		staff.setName(bean.getName());
		staff.setImage(bean.getFaceImage());
		staffDao.save(staff);

	}
}
