package com.mipt.nagibinMikhail.toDoList.entity;

import com.mipt.nagibinMikhail.toDoList.model.Priority;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private boolean completed = false;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Priority priority;

    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private String tags = "";

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<TaskAttachment> attachments = new ArrayList<>();

    public Set<String> getTagsSet() {
        if (tags == null || tags.isBlank()) {
            return new HashSet<>();
        }
        return new HashSet<>(Set.of(tags.split(",")));
    }

    public void setTagsFromSet(Set<String> tagSet) {
        if (tagSet == null || tagSet.isEmpty()) {
            this.tags = "";
        } else {
            this.tags = String.join(",", tagSet);
        }
    }

    public void addAttachment(TaskAttachment attachment) {
        attachments.add(attachment);
        attachment.setTask(this);
    }

    public void removeAttachment(TaskAttachment attachment) {
        attachments.remove(attachment);
        attachment.setTask(null);
    }
}
