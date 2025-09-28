package com.blogspot.notification.service;

import com.blogspot.notification.dto.EngagementMilestoneEvent;
import com.blogspot.notification.dto.NewBlogPostEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Service responsible for consuming Kafka events and triggering email notifications.
 * This service listens to two main topics:
 * 1. new-blog-posts: For immediate notifications when a new blog is published
 * 2. engagement-milestones: For threshold notifications when blogs reach milestones
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    /**
     * Kafka listener for new blog post events.
     * Consumes messages from the 'new-blog-posts' topic and sends notifications to followers.
     *
     * @param message JSON string containing the NewBlogPostEvent data
     */
    @KafkaListener(topics = "new-blog-posts", groupId = "notification_group")
    public void handleNewBlogPost(String message) {
        try {
            log.info("Received new blog post event: {}", message);
            
            NewBlogPostEvent event = objectMapper.readValue(message, NewBlogPostEvent.class);
            log.info("Processing new blog post event for blog: {} by author: {}", 
                    event.blogId(), event.authorId());

            // TODO: In a real implementation, you would:
            // 1. Fetch the author's email and name from the user-service
            // 2. Fetch the list of followers from the user-service
            // 3. Send notifications to all followers
            
            // For now, we'll simulate sending a notification
            // In production, you'd call user-service to get author details and followers
            String authorEmail = "author@example.com"; // This should come from user-service
            String authorName = "Blog Author"; // This should come from user-service
            
            emailService.sendNewBlogNotification(authorEmail, event.blogTitle(), authorName);
            
            log.info("Successfully processed new blog post event for blog: {}", event.blogId());
            
        } catch (Exception e) {
            log.error("Error processing new blog post event: {}", e.getMessage(), e);
            // In production, you might want to implement retry logic or dead letter queue
        }
    }

    /**
     * Kafka listener for engagement milestone events.
     * Consumes messages from the 'engagement-milestones' topic and sends congratulatory emails.
     *
     * @param message JSON string containing the EngagementMilestoneEvent data
     */
    @KafkaListener(topics = "engagement-milestones", groupId = "notification_group")
    public void handleEngagementMilestone(String message) {
        try {
            log.info("Received engagement milestone event: {}", message);
            
            EngagementMilestoneEvent event = objectMapper.readValue(message, EngagementMilestoneEvent.class);
            log.info("Processing engagement milestone event for blog: {} - {}: {}", 
                    event.blogId(), event.milestoneType(), event.count());

            // TODO: In a real implementation, you would:
            // 1. Fetch the author's email and name from the user-service
            // 2. Fetch the blog title from the blog-service
            // 3. Send congratulatory email to the author
            
            // For now, we'll simulate sending a notification
            // In production, you'd call user-service and blog-service to get details
            String authorEmail = "author@example.com"; // This should come from user-service
            String authorName = "Blog Author"; // This should come from user-service
            String blogTitle = "Sample Blog Title"; // This should come from blog-service
            
            emailService.sendMilestoneNotification(
                    authorEmail, 
                    blogTitle, 
                    event.milestoneType(), 
                    event.count(), 
                    authorName
            );
            
            log.info("Successfully processed engagement milestone event for blog: {}", event.blogId());
            
        } catch (Exception e) {
            log.error("Error processing engagement milestone event: {}", e.getMessage(), e);
            // In production, you might want to implement retry logic or dead letter queue
        }
    }
}
