package com.ruoyi.smoking_identification.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 发送简单文本邮件（最常用）
     * @param to 收件人邮箱
     * @param subject 邮件标题
     * @param content 邮件内容
     */
    public void sendSimpleEmail(String to, String subject, String content) {
        // 创建简单邮件消息对象
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);  // 发件人
        message.setTo(to);           // 收件人（可传多个，如new String[]{"a@qq.com", "b@163.com"}）
        message.setSubject(subject); // 标题
        message.setText(content);    // 文本内容

        // 发送邮件
        try {
            mailSender.send(message);
            System.out.println("简单文本邮件发送成功！");
        } catch (Exception e) {
            System.err.println("发送简单邮件失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

}
