package com.example.chatapplicatie;

import java.time.LocalDateTime;
import java.util.*;

public class Database {
    public enum TaskStatus { TODO, IN_PROGRESS, REVIEW, DONE }

    public static class User {
        private final String id;
        private final String name;

        public User(String name) {
            this.id = UUID.randomUUID().toString();
            this.name = name;
        }

        public String getId() { return id; }
        public String getName() { return name; }
    }

    public abstract static class scrumdb {
        private final String id;
        private String title;
        private String description;
        private final List<Message> linkedMessages = new ArrayList<>();

        public scrumdb(String title) {
            this.id = UUID.randomUUID().toString();
            this.title = title;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<Message> getLinkedMessages() { return linkedMessages; }

        // Voeg de toString() methode toe zodat de title wordt weergegeven
        @Override
        public String toString() {
            return getTitle();  // Dit zorgt ervoor dat de titel van het object wordt weergegeven
        }
    }

    public static class Epic extends scrumdb {
        private final List<UserStory> userStories = new ArrayList<>();

        public Epic(String title) {
            super(title);
        }

        public List<UserStory> getUserStories() { return userStories; }
        public void addUserStory(UserStory us) { userStories.add(us); }
    }

    public static class UserStory extends scrumdb {
        private final List<Task> tasks = new ArrayList<>();
        private String acceptanceCriteria;

        public UserStory(String title) {
            super(title);
        }

        public List<Task> getTasks() { return tasks; }
        public void addTask(Task t) { tasks.add(t); }
        public String getAcceptanceCriteria() { return acceptanceCriteria; }
        public void setAcceptanceCriteria(String c) { this.acceptanceCriteria = c; }
    }

    public static class Task extends scrumdb {
        private TaskStatus status;
        private User assignedTo;
        private int estimatedHours;

        public Task(String title) {
            super(title);
            this.status = TaskStatus.TODO;
        }

        public TaskStatus getStatus() { return status; }
        public void setStatus(TaskStatus s) { this.status = s; }
        public User getAssignedTo() { return assignedTo; }
        public void setAssignedTo(User u) { this.assignedTo = u; }
        public int getEstimatedHours() { return estimatedHours; }
        public void setEstimatedHours(int h) { this.estimatedHours = h; }
    }

    public static class Message {
        private final String id;
        private final String content;
        private final User sender;
        private final LocalDateTime timestamp;
        private scrumdb linkedEntity;

        public Message(String content, User sender, LocalDateTime timestamp, scrumdb linkedEntity) {
            this.id = UUID.randomUUID().toString();
            this.content = content;
            this.sender = sender;
            this.timestamp = timestamp;
            this.linkedEntity = linkedEntity;
        }

        public String getId() { return id; }
        public String getContent() { return content; }
        public User getSender() { return sender; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public scrumdb getLinkedEntity() { return linkedEntity; }
        public void setLinkedEntity(scrumdb e) { this.linkedEntity = e; }
    }
}
