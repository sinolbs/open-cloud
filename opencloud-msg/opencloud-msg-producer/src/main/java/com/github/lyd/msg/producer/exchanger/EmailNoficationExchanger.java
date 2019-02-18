package com.github.lyd.msg.producer.exchanger;


import com.github.lyd.msg.client.model.EmailNotification;
import com.github.lyd.msg.client.model.Notification;
import com.github.lyd.msg.client.model.mail.MailSenderParams;
import com.github.lyd.msg.producer.locator.MailSenderLocator;
import com.github.lyd.msg.producer.service.MailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.Assert;

import java.util.Random;

/**
 * @author woodev
 */
@Slf4j
public class EmailNoficationExchanger implements NotificationExchanger{

    private MailSender mailSender;
    private MailSenderLocator mailSenderLocator;
    public EmailNoficationExchanger(MailSender mailSender,MailSenderLocator mailSenderLocator) {
        this.mailSender = mailSender;
        this.mailSenderLocator = mailSenderLocator;
    }

    @Override
    public boolean support(Object notification) {
        return notification.getClass().equals(EmailNotification.class);
    }

    @Override
    public boolean exchange(Notification notification) {
        Assert.notNull(mailSender, "邮件接口没有初始化");
        mailSender.setJavaMailSender(getJavaMailSender());
        EmailNotification emailNotification = (EmailNotification)notification;
        MailSenderParams params = new MailSenderParams();
        params.setMailTo(emailNotification.getTo());
        params.setTitle(emailNotification.getTitle());
        params.setContent(emailNotification.getContent());
        mailSender.sendSimpleMail(params);
        return true;
    }

    public JavaMailSenderImpl getJavaMailSender() {
        String[] keys = mailSenderLocator.getMailSenders().keySet().toArray(new String[0]);
        Random random = new Random();
        // 随机获取邮箱发送者
        String randomKey = keys[random.nextInt(keys.length)];
        log.info("Email sender {}",randomKey);
        return  mailSenderLocator.getMailSenders().get(randomKey);
    }
}
