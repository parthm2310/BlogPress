package com.blogspot.notification.service;

import com.blogspot.notification.dto.EngagementMilestoneEvent;
import com.blogspot.notification.dto.NewBlogPostEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final UserServiceClient userServiceClient;
    private final BlogServiceClient blogServiceClient;

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

            // Fetch author details
            UserProfile author = userServiceClient.getUserByIdInternal(Long.parseLong(event.authorId()));

            // Broadcast: fetch all user emails
            Iterable<String> allEmails = userServiceClient.getAllEmailsInternal();
            String subject = emailService.buildNewBlogSubject(event.blogTitle());
            String body = emailService.buildNewBlogBody(event.blogTitle(), author.getFirstName() != null ? author.getFirstName() : author.getUsername());
            emailService.sendBulkSimpleMail(allEmails, subject, body);
            
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

            String authorIdStr = event.authorId();
            String title = event.blogTitle();

            if (authorIdStr == null || title == null) {
                try {
                    BlogSummary blog = blogServiceClient.getBlogInternal(Long.parseLong(event.blogId()));
                    if (blog != null) {
                        if (authorIdStr == null && blog.getAuthorId() != null) {
                            authorIdStr = blog.getAuthorId().toString();
                        }
                        if (title == null) {
                            title = blog.getTitle();
                        }
                    }
                } catch (Exception ex) {
                    log.warn("Fallback fetch failed for blogId={}: {}", event.blogId(), ex.getMessage());
                }
            }

            if (authorIdStr == null || title == null) {
                log.warn("Skipping milestone email for blogId={} due to missing data after fallback (authorId={}, blogTitle={})", 
                        event.blogId(), authorIdStr, title);
                return;
            }

            Long authorId = Long.parseLong(authorIdStr);
            UserProfile author = userServiceClient.getUserByIdInternal(authorId);

            emailService.sendMilestoneNotification(
                    author.getEmail(),
                    title,
                    event.milestoneType(),
                    event.count(),
                    author.getFirstName() != null ? author.getFirstName() : author.getUsername()
            );
            
            log.info("Successfully processed engagement milestone event for blog: {}", event.blogId());
            
        } catch (Exception e) {
            log.error("Error processing engagement milestone event: {}", e.getMessage(), e);
            // In production, you might want to implement retry logic or dead letter queue
        }
    }
}

@FeignClient(name = "user-service")
interface UserServiceClient {
    @GetMapping("/api/users/internal/emails")
    Iterable<String> getAllEmailsInternal();

    @GetMapping("/api/users/internal/{id}")
    UserProfile getUserByIdInternal(@PathVariable("id") Long id);
}

@FeignClient(name = "blog-service")
interface BlogServiceClient {
    @GetMapping("/api/blogs/internal/{id}")
    BlogSummary getBlogInternal(@PathVariable("id") Long id);
}

@lombok.Data
class UserProfile {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}

@lombok.Data
class BlogSummary {
    private Long id;
    private String title;
    private Long authorId;
}
