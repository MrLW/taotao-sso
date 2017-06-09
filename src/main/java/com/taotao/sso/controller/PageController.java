package com.taotao.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/user")
public class PageController {

	// 登录页面
	@RequestMapping("/page/login")
	public String showLogin(String redirectURL,Model model){ 
		// 处理回调
		model.addAttribute("redirect", redirectURL);
		return "login" ;
	}
	
	/**
	 * 展示注册页面
	 */
	@RequestMapping("/page/register")
	public String showRegister() {
		return "register";
	}
	
}
