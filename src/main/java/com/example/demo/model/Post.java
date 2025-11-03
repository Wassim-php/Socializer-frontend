package com.example.demo.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Post {
    private Long id;
    private String content;
    private String imageUrl;

    private LocalDateTime createdAt =  LocalDateTime.now();
    private Long userId;
    private Long tagId;
    private List<Long> commentIds;
    private Set<Long> likedUserIds = new HashSet<>();

    public Post() {}

    public Post (
        @JsonProperty("id") Long id,
        @JsonProperty("content") String content,
        @JsonProperty("imageUrl") String imageUrl,
        @JsonProperty("createdAt") LocalDateTime createdAt,
        @JsonProperty("userId") Long userId,
        @JsonProperty("tagId") Long tagId,
        @JsonProperty("commentIds") List<Long> commentIds,
        @JsonProperty("likedUserIds") Set<Long> likedUserIds
    ){
        this.id = id;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.userId = userId;
        this.tagId = tagId;
        this.commentIds = commentIds;
        this.likedUserIds = likedUserIds;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public List<Long> getCommentIds() {
        return commentIds;
    }

    public void setCommentIds(List<Long> commentIds) {
        this.commentIds = commentIds;
    }

    public Set<Long> getLikedUserIds() {
        return likedUserIds;
    }

    public void setLikedUserIds(Set<Long> likedUserIds) {
        this.likedUserIds = likedUserIds;
    }
}
