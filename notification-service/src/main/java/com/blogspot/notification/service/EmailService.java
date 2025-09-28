package com.blogspot.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service responsible for sending email notifications.
 * Uses JavaMailSender to send simple text emails.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${notification.email.sender.address:parthm2310@gmail.com}")
    private String senderEmail;
    
    @Value("${notification.email.sender.name:BlogPress Team}")
    private String senderName;

    /**
     * Sends a simple email notification.
     *
     * @param to      recipient email address
     * @param subject email subject
     * @param body    email body content
     */
    public void sendSimpleMail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom(senderName + " <" + senderEmail + ">"); // Use configured sender from GitHub config

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}. Error: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email notification", e);
        }
    }

    /**
     * Sends a new blog post notification to followers.
     *
     * @param authorEmail  email of the blog author
     * @param blogTitle    title of the new blog post
     * @param authorName   name of the blog author
     */
    public void sendNewBlogNotification(String authorEmail, String blogTitle, String authorName) {
        String subject = "New Blog Post: " + blogTitle;
        String body = String.format(
                "Hello,\n\n" +
                "A new blog post has been published by %s:\n\n" +
                "Title: %s\n\n" +
                "Check it out on BlogPress!\n\n" +
                "Best regards,\n" +
                "BlogPress Team",
                authorName, blogTitle
        );
        
        sendSimpleMail(authorEmail, subject, body);
    }

    /**
     * Sends a congratulatory email for reaching an engagement milestone.
     *
     * @param authorEmail     email of the blog author
     * @param blogTitle       title of the blog post
     * @param milestoneType   type of milestone (LIKES, COMMENTS, etc.)
     * @param count           number reached
     * @param authorName      name of the blog author
     */
    public void sendMilestoneNotification(String authorEmail, String blogTitle, 
                                        String milestoneType, int count, String authorName) {
        String subject = "Congratulations! Your blog reached " + count + " " + milestoneType;
        String body = String.format(
                "Hello %s,\n\n" +
                "Congratulations! Your blog post \"%s\" has reached %d %s!\n\n" +
                "This is a significant milestone and shows that your content is resonating with readers.\n\n" +
                "Keep up the great work!\n\n" +
                "Best regards,\n" +
                "BlogPress Team",
                authorName, blogTitle, count, milestoneType.toLowerCase()
        );
        
        sendSimpleMail(authorEmail, subject, body);
    }
}
