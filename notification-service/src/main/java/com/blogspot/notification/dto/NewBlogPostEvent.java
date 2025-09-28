package com.blogspot.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO representing a new blog post event from Kafka.
 * This event is published when a new blog is created.
 */
public record NewBlogPostEvent(
        @JsonProperty("blogId") String blogId,
        @JsonProperty("authorId") String authorId,
        @JsonProperty("blogTitle") String blogTitle
) {
}
