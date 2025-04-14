package com.example.chatapplicatie;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Database {
    // Eerst de ondersteunende enums/klassen definiëren
    public enum TaskStatus {
        TODO, IN_PROGRESS, REVIEW, DONE
    }

    public static class User {
        private String id;
        private String name;

        public User(String name) {
            this.id = UUID.randomUUID().toString();
            this.name = name;
        }

        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
    }

    // Basisklasse voor alle Scrum entiteiten
    public abstract static class scrumdb {
        private String id;
        private String title;
        private String description;
        private List<Message> linkedMessages = new ArrayList<>();

        public scrumdb(String title) {
            this.id = UUID.randomUUID().toString();
            this.title = title;
        }

        // Getters en setters
        public String getId() { return id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<Message> getLinkedMessages() { return linkedMessages; }
    }

    // Epic.java
    public static class Epic extends scrumdb {
        private List<UserStory> userStories = new ArrayList<>();

        public Epic(String title) {
            super(title);
        }

        public List<UserStory> getUserStories() { return userStories; }
    }

    // UserStory.java
    public static class UserStory extends scrumdb {
        private List<Task> tasks = new ArrayList<>();
        private String acceptanceCriteria;

        public UserStory(String title) {
            super(title);
        }

        // Getters en setters
        public List<Task> getTasks() { return tasks; }
        public String getAcceptanceCriteria() { return acceptanceCriteria; }
        public void setAcceptanceCriteria(String criteria) { this.acceptanceCriteria = criteria; }
    }

    // Task.java
    public static class Task extends scrumdb {
        private TaskStatus status;
        private User assignedTo;
        private int estimatedHours;

        public Task(String title) {
            super(title);
            this.status = TaskStatus.TODO;
        }

        // Getters en setters
        public TaskStatus getStatus() { return status; }
        public void setStatus(TaskStatus status) { this.status = status; }
        public User getAssignedTo() { return assignedTo; }
        public void setAssignedTo(User user) { this.assignedTo = user; }
        public int getEstimatedHours() { return estimatedHours; }
        public void setEstimatedHours(int hours) { this.estimatedHours = hours; }
    }

    public static class Message {
        private String id;
        private String content;
        private User sender;
        private LocalDateTime timestamp;
        private scrumdb linkedEntity;

        public Message(String content, User sender, LocalDateTime timestamp, scrumdb linkedEntity) {
            this.id = UUID.randomUUID().toString();
            this.content = content;
            this.sender = sender;
            this.timestamp = timestamp;
            this.linkedEntity = linkedEntity;
        }

        // Getters
        public String getId() { return id; }
        public String getContent() { return content; }
        public User getSender() { return sender; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public scrumdb getLinkedEntity() { return linkedEntity; }

        // Setter voor linkedEntity - NIEUW TOEGEVOEGD
        public void setLinkedEntity(scrumdb linkedEntity) {
            this.linkedEntity = linkedEntity;
        }
    }
}