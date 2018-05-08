package com.shop.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

public class MailUtils {

	public static void sendMail(String email, String subject, String emailMsg)
			throws AddressException, MessagingException {
//		// 1.����һ���������ʼ��������Ự���� Session
//
//		Properties props = new Properties();
//		props.setProperty("mail.transport.protocol", "SMTP");
//		props.setProperty("mail.host", "smtp.qq.com");
//		props.setProperty("mail.smtp.auth", "true");// ָ����֤Ϊtrue
//		
//		 //���Լ�������
//        props.put("mail.user", "609599661@qq.com"); 
//        //�㿪��pop3/smtpʱ����֤��
//        props.put("mail.password", "hxqtstmrzrlkbcch");
//        props.put("mail.smtp.port", "25");
//        props.put("mail.smtp.starttls.enable", "true");
		
		InputStream in = MailUtils.class.getClassLoader().getResourceAsStream("mailUtils.properties");
		
		Properties props=new Properties();
		
		try {
			props.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// ������֤��
		Authenticator auth = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(props.getProperty("Authenticator.account"),props.getProperty("Authenticator.password"));
			}
		};

		Session session = Session.getInstance(props, auth);
		
		// 2.����һ��Message�����൱�����ʼ�����
		Message message = new MimeMessage(session);

		message.setFrom(new InternetAddress(props.getProperty("mail.user"))); // ���÷�����

		message.setRecipient(RecipientType.TO, new InternetAddress(email)); // ���÷��ͷ�ʽ�������

		message.setSubject(subject);
		// message.setText("����һ�⼤���ʼ�����<a href='#'>���</a>");

		message.setContent(emailMsg, "text/html;charset=utf-8");
		
		// 3.���� Transport���ڽ��ʼ�����
		
		Transport.send(message);
		
		
	}
}
