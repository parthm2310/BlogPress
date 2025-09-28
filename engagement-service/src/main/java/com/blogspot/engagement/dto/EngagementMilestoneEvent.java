package com.blogspot.engagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO representing an engagement milestone event for Kafka.
 * This event is published when a blog reaches a significant engagement milestone.
 */
public record EngagementMilestoneEvent(
        @JsonProperty("blogId") String blogId,
        @JsonProperty("authorId") String authorId,
        @JsonProperty("milestoneType") String milestoneType,
        @JsonProperty("count") int count
) {
}
