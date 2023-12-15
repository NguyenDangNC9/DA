package com.poly.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.mail.javamail.MimeMessageHelper;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Controller
public class ContactController {

	// ...

	// ...

	@Autowired
	JavaMailSender javaMailSender;

	@RequestMapping("/contact")
	public String sendMail(
			@RequestParam("subject") String subject,
			@RequestParam("content") String content,
			@RequestParam("phone1") String phone1,
			@RequestParam("message") String message) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			helper.setTo("nuongnypc04574@fpt.edu.vn");
			helper.setSubject("Thông tin liên hệ: " + subject);

			String emailContent = "<html><body>" +
					"<h2>Thông tin liên hệ:</h2>" +
					"<p><strong>Tên khách hàng:</strong> " + content + "</p>" +
					"<p><strong>Điện thoại:</strong> " + phone1 + "</p>" +
					"<p><strong>Nội dung:</strong></p>" +
					"<p>" + message + "</p>" +
					"<hr>" +
					"</body></html>";

			helper.setText(emailContent, true); // Set content as HTML
			javaMailSender.send(mimeMessage);
		} catch (MessagingException e) {
			e.printStackTrace();
			return "error";
		}

		return "user/result";
	}
}