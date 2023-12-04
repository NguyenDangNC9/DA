package com.poly.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.dao.AccountDao;
import com.poly.entity.Account;
import com.poly.service.AccountService;

@Controller
public class ResetPWController {
	
	@Autowired
	AccountService accservice;
	
	@Autowired
	AccountDao dao;
	
	@GetMapping("/account/reset-password")
    public String resetPassword(@RequestParam("name") String username, Model model) {
        model.addAttribute("username", username);
        return "user/acc/resetpw"; // Trả về tên của trang HTML để hiển thị thông tin username
    }
	
	@RequestMapping("/resetPW")
	public String signup2(Account  acc ,
			@RequestParam("name") String username,
			@RequestParam("pswnew") String pswnew,
			@RequestParam("confim-pswnew") String confimpswnew , Model model) {
		acc = accservice.findById(username);
		if(confimpswnew.equals(pswnew)) {
			acc.setPassword(pswnew);
			accservice.update(acc);
			model.addAttribute("message","Đặt lại mật khẩu thành công");
			return "user/acc/resetpw";
		}
		else {
			model.addAttribute("message","Xác nhận mật khẩu không đúng");
			return "user/acc/resetpw";
		}
		
	}
}
