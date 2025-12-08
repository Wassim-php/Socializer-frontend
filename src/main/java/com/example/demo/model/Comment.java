package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class Comment {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private Long userId;
    private Post post;

  
    // Default constructor
    public Comment() {}

    // Constructor used by Jackson to deserialize a Comment
    public Comment(
            @JsonProperty("id") Long id,
            @JsonProperty("content") String content,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("userId") Long userId,
            @JsonProperty("post") Post post
    ) {
        this.id = id;              // FIX: missing
        this.content = content;
        this.createdAt = createdAt;
        this.userId = userId;
        this.post = post;
    }

    public Long getId() {
    // Get comment id
        return id;
    }
    public String getContent() {
    // Get comment content
        return content;
    }
    public LocalDateTime getCreatedAt() {
    // Get creation timestamp
        return createdAt;
    }

    public Post getPost() {
    // Get associated post
        return post;
    }

    public void setPost(Post post) {
    // Set associated post
        this.post = post;
    }

    public Long getUserId(){
    // Get user id who authored the comment
        return userId;
    }
    public void setUser(Long userId) {
    // Set user id for comment
        this.userId = userId;
    }


    public void setId(Long id) {
    // Set comment id
        this.id = id;
    }


    public void setContent(String content) {
    // Set comment content
        this.content = content;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
    // Set creation timestamp
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
