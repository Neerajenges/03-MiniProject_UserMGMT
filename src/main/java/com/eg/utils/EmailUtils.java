package com.eg.utils;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

//Represented class as spring bean using @component
@Component
public class EmailUtils {
	// Java Predefined class JavaMailSender is an interface so implimentation
	// classwill be injected
	@Autowired
	private JavaMailSender mailSender;

	public boolean sendEmail(String to, String subject, String body) {
		boolean isMailSent = false;
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper=new MimeMessageHelper(mimeMessage);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body,true);//true represent html content text in email 
			 //  helper.setCc(body);
			//for attachment to mail helper.addAttachment(body, null);
			mailSender.send(mimeMessage);
			isMailSent=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isMailSent;

	}

}
