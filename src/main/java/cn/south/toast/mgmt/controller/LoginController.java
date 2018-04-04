package cn.south.toast.mgmt.controller;


import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.south.toast.base.dto.UserLoginInDTO;
import cn.south.toast.base.dto.UserLoginOutDTO;
import cn.south.toast.common.dto.AppResultObj;

/**
 * 
 * @author huangbh
 *
 */
@Controller
@RestController
@RequestMapping("/sail/permission")
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Value("${spring.profiles.active}")
	private String active;

	/**
	 * 用户登陆
	 * 
	 * @param inDTO
	 *            用户名，密码
	 * @return
	 */
	@RequestMapping("/login")
	public Object login(@RequestBody UserLoginInDTO inDTO) {
		String username = inDTO.getUsername();
		String password = inDTO.getPassword();
		String code = inDTO.getCode();
		if (StringUtils.isBlank(username)) {
			return AppResultObj.parameterError("帐号不能为空!");
		}

		if (StringUtils.isBlank(password)) {
			return AppResultObj.parameterError("密码不能为空!");
		}

		if (StringUtils.isBlank(code)) {
			return AppResultObj.parameterError("验证码不能为空!");
		}

		//加密
		password = DigestUtils.md5DigestAsHex((password).getBytes());

		//登录
		UsernamePasswordToken token = new UsernamePasswordToken(username, password, false);
		Subject subject = SecurityUtils.getSubject();

		try {
			long startTime = System.currentTimeMillis();
			logger.debug("---------------------校验用户名+密码开始");
			subject.login(token);
			long endTime = System.currentTimeMillis();
			long consume = endTime - startTime;
			logger.debug("---------------------校验用户名+密码结束，耗时" + consume);
		} catch (UnknownAccountException e) {
			logger.info("找不到该用户:{}", username);
			return AppResultObj.newResult(70001, "帐号错误");
		} catch (DisabledAccountException e) {
			logger.info("用户已被停用:{}", username);
			return AppResultObj.newResult(70002, "用户已被停用");
		} catch (IncorrectCredentialsException e) {
			logger.info("密码错误:{}", username);
			return AppResultObj.newResult(70003, "密码错误");
		} catch (Exception e) {
			e.printStackTrace();
			return AppResultObj.serverError(e.getMessage());
		}

		if ("prod".equals(active)) {
			//校验验证码

		}

		//获取用户信息
		UserLoginOutDTO userLoginOutDTO = (UserLoginOutDTO) subject.getPrincipal();

		logger.info("登陆成功");
		return AppResultObj.success(userLoginOutDTO);
	}

	/**
	 * 注销
	 * 
	 * @return
	 */
	@RequestMapping("/logout")
	public Object logout() {
		logger.info("用户注销");
		SecurityUtils.getSubject().logout();
		return AppResultObj.success();
	}

}
