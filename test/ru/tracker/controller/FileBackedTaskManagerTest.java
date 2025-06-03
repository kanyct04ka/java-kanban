package ru.tracker.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.tracker.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class FileBackedTaskManagerTest {

    FileBackedTaskManager taskManager;

    @BeforeEach
    void createManager() {
        taskManager = new FileBackedTaskManager();
    }

    @Test
    void loadFromFile_EmptyFile() {
        taskManager = FileBackedTaskManager.loadFromFile(new File("test/static/test_empty.csv"));
        assertTrue(taskManager.getTaskList().isEmpty());
        assertTrue(taskManager.getEpicList().isEmpty());
        assertTrue(taskManager.getSubtaskList().isEmpty());
        assertEquals(0, taskManager.getTaskCount());
    }

    @Test
    void loadFromFile_WithBigTaskId() {
        taskManager = FileBackedTaskManager.loadFromFile(new File("test/static/test_taskCount.csv"));
        assertFalse(taskManager.getTaskList().isEmpty());
        assertFalse(taskManager.getEpicList().isEmpty());
        assertEquals(25, taskManager.getTaskCount());

        Task task = new Task("task name", "task description", TaskStatus.NEW);
        assertEquals(26, taskManager.addTask(task).getId());
    }

    @Test
    void loadFromFile_SubtaskWithoutEpic() {
        taskManager = FileBackedTaskManager.loadFromFile(new File("test/static/test_subtaskWithoutEpic.csv"));
        assertTrue(taskManager.getTaskList().isEmpty());
        assertEquals(1, taskManager.getEpicList().size());
        assertEquals(2, taskManager.getSubtaskList().size());
    }

    @Test
    void addTask() {
        Task task = new Task("task name", "task description", TaskStatus.NEW);
        int id = taskManager.addTask(task).getId();

        assertNotNull(taskManager.getTask(id));
        assertEquals(task, taskManager.getTask(id));

        Collection<Task> taskList = taskManager.getTaskList();
        assertNotNull(taskList);
        assertEquals(1, taskList.size());
    }

    @Test
    void removeTask() {
        Task task1 = new Task("task name 1", "task description 1", TaskStatus.NEW);
        int id1 = taskManager.addTask(task1).getId();
        Task task2 = new Task("task name 2", "task description 2", TaskStatus.NEW);
        int id2 = taskManager.addTask(task2).getId();
        Task task3 = new Task("task name 3", "task description 3", TaskStatus.NEW);
        int id3 = taskManager.addTask(task1).getId();

        taskManager.removeTask(id1);
        assertNull(taskManager.getTask(id1));

        taskManager.removeAllTasks();
        Collection<Task> taskList = taskManager.getTaskList();
        assertEquals(0, taskList.size());
    }

    @Test
    void addEpic() {
        Epic epic = new Epic("epic name", "epic description", new ArrayList<Subtask>());
        int id = taskManager.addEpic(epic).getId();

        assertEquals(TaskStatus.NEW, epic.getStatus());

        assertNotNull(taskManager.getEpic(id));
        assertEquals(epic, taskManager.getEpic(id));

        Collection<Epic> epicList = taskManager.getEpicList();
        assertNotNull(epicList);
        assertEquals(1, epicList.size());
    }

    @Test
    void removeEpic() {
        Epic epic1 = new Epic("epic name 1", "epic description 1", new ArrayList<Subtask>());
        int id1 = taskManager.addEpic(epic1).getId();
        Epic epic2 = new Epic("epic name 2", "epic description 2", new ArrayList<Subtask>());
        int id2 = taskManager.addEpic(epic2).getId();
        Epic epic3 = new Epic("epic name 3", "epic description 3", new ArrayList<Subtask>());
        int id3 = taskManager.addEpic(epic3).getId();

        taskManager.removeEpic(id1);
        assertNull(taskManager.getEpic(id1));

        taskManager.removeAllEpics();
        Collection<Epic> epicList = taskManager.getEpicList();
        assertEquals(0, epicList.size());
    }

    @Test
    void addSubtask() {
        Epic epic = new Epic("epic name", "epic description", new ArrayList<Subtask>());
        int idEpic = taskManager.addEpic(epic).getId();

        Subtask subtask1 = new Subtask("subtask name 1", "subtask description 1", TaskStatus.NEW);
        int id1 = taskManager.addSubtask(subtask1, epic).getId();
        Subtask subtask2 = new Subtask("subtask name 2", "subtask description 2", TaskStatus.NEW);
        int id2 = taskManager.addSubtask(subtask2, epic).getId();
        Subtask subtask3 = new Subtask("subtask name 3", "subtask description 3", TaskStatus.IN_PROGRESS);
        int id3 = taskManager.addSubtask(subtask3, epic).getId();

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
        assertNotNull(taskManager.getEpicSubtasks(epic));

        assertNotNull(taskManager.getSubtask(id1));
        assertEquals(subtask1, taskManager.getSubtask(id1));

        Collection<Subtask> subtaskList = taskManager.getSubtaskList();
        assertNotNull(subtaskList);
        assertEquals(3, subtaskList.size());
    }

    @Test
    void removeSubtask() {
        Epic epic1 = new Epic("epic name 1", "epic description 1", new ArrayList<Subtask>());
        int id1 = taskManager.addEpic(epic1).getId();

        Subtask subtask2 = new Subtask("subtask name 2", "subtask description 2", TaskStatus.NEW);
        int id2 = taskManager.addSubtask(subtask2, epic1).getId();
        Subtask subtask3 = new Subtask("subtask name 3", "subtask description 3", TaskStatus.IN_PROGRESS);
        int id3 = taskManager.addSubtask(subtask3, epic1).getId();

        Epic epic4 = new Epic("epic name 4", "epic description 4", new ArrayList<Subtask>());
        int id4 = taskManager.addEpic(epic4).getId();

        Subtask subtask5 = new Subtask("subtask name 5", "subtask description 5", TaskStatus.NEW);
        int id5 = taskManager.addSubtask(subtask5, epic4).getId();
        Subtask subtask6 = new Subtask("subtask name 6", "subtask description 6", TaskStatus.IN_PROGRESS);
        int id6 = taskManager.addSubtask(subtask6, epic4).getId();

        taskManager.removeSubtask(id3);
        assertNull(taskManager.getSubtask(id3));
        assertEquals(TaskStatus.NEW, epic1.getStatus());

        taskManager.removeEpic(id1);
        assertNull(taskManager.getSubtask(id2));

        taskManager.removeAllSubtasks();
        Collection<Subtask> subtaskList = taskManager.getSubtaskList();
        assertEquals(0, subtaskList.size());
        assertEquals(0, taskManager.getEpicSubtasks(epic4).size());
    }
}
