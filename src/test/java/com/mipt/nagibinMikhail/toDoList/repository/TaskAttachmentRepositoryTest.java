package com.mipt.nagibinMikhail.toDoList.repository;

import com.mipt.nagibinMikhail.toDoList.entity.Task;
import com.mipt.nagibinMikhail.toDoList.entity.TaskAttachment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskAttachmentRepositoryTest {

    @Autowired
    private TaskAttachmentRepository attachmentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Task task;
    private TaskAttachment attachment1;
    private TaskAttachment attachment2;

    @BeforeEach
    void setUp() {
        task = Task.builder()
            .title("Test Task")
            .description("Test Description")
            .completed(false)
            .build();
        entityManager.persist(task);

        attachment1 = TaskAttachment.builder()
            .task(task)
            .fileName("file1.txt")
            .storedFileName("uuid1_file1.txt")
            .contentType("text/plain")
            .size(1024L)
            .build();

        attachment2 = TaskAttachment.builder()
            .task(task)
            .fileName("file2.pdf")
            .storedFileName("uuid2_file2.pdf")
            .contentType("application/pdf")
            .size(2048L)
            .build();

        entityManager.persist(attachment1);
        entityManager.persist(attachment2);
        entityManager.flush();
    }

    @Test
    void save_ShouldPersistAttachment() {
        TaskAttachment newAttachment = TaskAttachment.builder()
            .task(task)
            .fileName("newfile.jpg")
            .storedFileName("uuid3_newfile.jpg")
            .contentType("image/jpeg")
            .size(3072L)
            .build();

        TaskAttachment saved = attachmentRepository.save(newAttachment);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFileName()).isEqualTo("newfile.jpg");
        assertThat(attachmentRepository.count()).isEqualTo(3);
    }

    @Test
    void findById_ShouldReturnAttachmentWhenExists() {
        TaskAttachment found = attachmentRepository.findById(attachment1.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getFileName()).isEqualTo("file1.txt");
        assertThat(found.getTask().getId()).isEqualTo(task.getId());
    }

    @Test
    void findById_ShouldReturnEmptyWhenNotExists() {
        boolean exists = attachmentRepository.findById(999L).isPresent();
        assertThat(exists).isFalse();
    }

    @Test
    void findAll_ShouldReturnAllAttachments() {
        List<TaskAttachment> attachments = attachmentRepository.findAll();

        assertThat(attachments).hasSize(2);
        assertThat(attachments).extracting(TaskAttachment::getFileName)
            .containsExactlyInAnyOrder("file1.txt", "file2.pdf");
    }

    @Test
    void findByTaskId_ShouldReturnAttachmentsForTask() {
        List<TaskAttachment> attachments = attachmentRepository.findByTask_Id(task.getId());

        assertThat(attachments).hasSize(2);
        assertThat(attachments).extracting(TaskAttachment::getFileName)
            .containsExactlyInAnyOrder("file1.txt", "file2.pdf");
    }

    @Test
    void findByTaskId_ForNonExistentTask_ShouldReturnEmptyList() {
        List<TaskAttachment> attachments = attachmentRepository.findByTask_Id(999);

        assertThat(attachments).isEmpty();
    }

    @Test
    void countByTaskId_ShouldReturnCorrectCount() {
        long count = attachmentRepository.countByTask_Id(task.getId());

        assertThat(count).isEqualTo(2);
    }

    @Test
    void countByTaskId_ForNonExistentTask_ShouldReturnZero() {
        long count = attachmentRepository.countByTask_Id(999);

        assertThat(count).isEqualTo(0);
    }

    @Test
    void deleteById_ShouldRemoveAttachment() {
        attachmentRepository.deleteById(attachment1.getId());
        entityManager.flush();

        boolean exists = attachmentRepository.existsById(attachment1.getId());
        assertThat(exists).isFalse();
        assertThat(attachmentRepository.count()).isEqualTo(1);
    }

    @Test
    void deleteByTaskId_ShouldRemoveAllAttachmentsForTask() {
        attachmentRepository.deleteByTask_Id(task.getId());
        entityManager.flush();

        List<TaskAttachment> attachments = attachmentRepository.findByTask_Id(task.getId());
        assertThat(attachments).isEmpty();
    }

    @Test
    void existsById_ShouldReturnTrueWhenExists() {
        boolean exists = attachmentRepository.existsById(attachment1.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void updateAttachment_ShouldModifyFields() {
        TaskAttachment attachment = attachmentRepository.findById(attachment1.getId()).get();
        attachment.setFileName("updated.txt");
        attachment.setSize(999L);

        attachmentRepository.save(attachment);
        entityManager.flush();
        entityManager.clear();

        TaskAttachment updated = attachmentRepository.findById(attachment1.getId()).get();
        assertThat(updated.getFileName()).isEqualTo("updated.txt");
        assertThat(updated.getSize()).isEqualTo(999L);
    }
}