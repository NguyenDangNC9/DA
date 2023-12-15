package com.poly.controller.user;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.SecurityConfig;
import com.poly.dao.AccountDao;
import com.poly.entity.Account;

import net.bytebuddy.utility.RandomString;

@Controller
public class SecurityController {
	@Autowired
	AccountDao accdao;

	
	@RequestMapping("/security/login/form")
	public String loginForm( Model model) {
		model.addAttribute("message","Để tiếp tục, hãy đăng nhập vào BlackBear.");
		return "user/security/login";
	}
	@RequestMapping("/security/login/success")
	public String loginSuccess( Model model) {
		model.addAttribute("message","Đăng nhập thành công");
		return "user/security/login";
	}
	@RequestMapping("/security/login/erorr")
	public String loginErorr( Model model) {
		model.addAttribute("message","Tài khoản chưa kích hoạt hoặc sai thông tin tài khoản");
		return "user/security/login";
	}
	@RequestMapping("/security/unauthoried")
	public String unauthoried( ) {
		return "admin/security/404";
	}
	@RequestMapping("/security/logoff/success")
	public String logoff( Model model) {
		model.addAttribute("message","Đăng xuất thành công ");
		return "user/security/login";
	}
	
		@Autowired
		SecurityConfig securityConfig;
		
		@RequestMapping("/security2/login/success")
		public String success(OAuth2AuthenticationToken oauth2 , HttpServletRequest requet) {
			securityConfig.loginFromOAuth2(oauth2);
			String pw = RandomString.make(10);
			if(!accdao.existsById(requet.getRemoteUser())) {
			Account acc = new Account();
			acc.setUsername(requet.getRemoteUser());
			acc.setPassword(pw);
			acc.setPhoto("avata.jpg");
			acc.setEmail(oauth2.getPrincipal().getAttribute("email"));
			acc.setFullname(oauth2.getPrincipal().getAttribute("name"));
			acc.setActive(true);
			accdao.save(acc);
			}
			return"forward:/home/index";
				
		}
}
