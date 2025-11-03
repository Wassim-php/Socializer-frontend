package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class Comment {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private Long userId;
    private Post post;

    public Comment() {}

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
        return id;
    }
    public String getContent() {
        return content;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Long getUserId(){
        return userId;
    }
    public void setUser(Long userId) {
        this.userId = userId;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
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
