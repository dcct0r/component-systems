// src/main/java/com/incident/model/Incident.java
package com.incident.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Incident implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String description;
    private IncidentPriority priority;
    private IncidentStatus status;
    private String assignee;
    private List<String> comments;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Incident() {
        this.id = UUID.randomUUID().toString();
        this.status = IncidentStatus.OPEN;
        this.comments = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Incident(String title, String description, IncidentPriority priority) {
        this();
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title != null && title.length() > 100) {
            throw new IllegalArgumentException("Title cannot exceed 100 characters");
        }
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("Description cannot exceed 500 characters");
        }
        this.description = description;
    }

    public IncidentPriority getPriority() {
        return priority;
    }

    public void setPriority(IncidentPriority priority) {
        this.priority = priority;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public void addComment(String comment) {
        this.comments.add(comment);
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean canTransitionTo(IncidentStatus newStatus) {
        if (this.status == IncidentStatus.OPEN && newStatus == IncidentStatus.IN_PROGRESS) {
            return true;
        }
        if (this.status == IncidentStatus.IN_PROGRESS && newStatus == IncidentStatus.CLOSED) {
            return true;
        }
        if (this.status == IncidentStatus.OPEN && newStatus == IncidentStatus.CLOSED) {
            return true;
        }
        return false;
    }

    public void transitionStatus(IncidentStatus newStatus, String assignee, String comment) {
        if (!canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    String.format("Cannot transition from %s to %s", this.status, newStatus)
            );
        }

        this.status = newStatus;
        if (assignee != null && !assignee.trim().isEmpty()) {
            this.assignee = assignee;
        }
        if (comment != null && !comment.trim().isEmpty()) {
            addComment(String.format("Status changed to %s: %s", newStatus, comment));
        }
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format(
                "Incident{id='%s', title='%s', status=%s, priority=%s}",
                id, title, status, priority
        );
    }
}