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


    // Default constructor
    public Post() {}

    // Constructor used by Jackson to create a Post from JSON
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
    // Get post id
        return id;
    }

    public void setId(Long id){
    // Set post id
        this.id = id;
    }

    public String getContent() {
    // Get post content
        return content;
    }

    public void setContent(String content) {
    // Set post content
        this.content = content;
    }

    public String getImageUrl() {
    // Get image URL or filename
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
    // Set image URL or filename
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
    // Get creation timestamp
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
    // Set creation timestamp
        this.createdAt = createdAt;
    }

    public Long getUserId() {
    // Get owner user id
        return userId;
    }

    public void setUserId(Long userId) {
    // Set owner user id
        this.userId = userId;
    }

    public Long getTagId() {
    // Get tag id
        return tagId;
    }

    public void setTagId(Long tagId) {
    // Set tag id
        this.tagId = tagId;
    }

    public List<Long> getCommentIds() {
    // Get list of comment ids
        return commentIds;
    }

    public void setCommentIds(List<Long> commentIds) {
    // Set list of comment ids
        this.commentIds = commentIds;
    }

    public Set<Long> getLikedUserIds() {
    // Get set of user ids who liked this post
        return likedUserIds;
    }

    public void setLikedUserIds(Set<Long> likedUserIds) {
    // Set likes by user ids
        this.likedUserIds = likedUserIds;
    }
}
