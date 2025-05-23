package ru.tracker.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.tracker.model.Epic;
import ru.tracker.model.Subtask;
import ru.tracker.model.Task;
import ru.tracker.model.TaskStatus;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    InMemoryTaskManager taskManager;
    HistoryManager historyManager;

    @BeforeEach
    void createManagers() {
        taskManager = new InMemoryTaskManager();
        historyManager = taskManager.getHistoryManager();
    }

    @Test
    void historyManagerKeeps100UniqueTasks() {
        for (int i = 1; i <= 100; i++) {
            Task task = new Task("task name " + i, "task description " + i, TaskStatus.NEW);
            int id = taskManager.addTask(task).getId();
            taskManager.getTask(id);
            assertEquals(i, historyManager.getHistory().size());
        }
    }

    @Test
    void historyShouldBeSmallerFor1AfterRemoving() {
        int x = 5;
        for (int i = 1; i <= x; i++) {
            Task task = new Task("task name " + i, "task description " + i, TaskStatus.NEW);
            int id = taskManager.addTask(task).getId();
            taskManager.getTask(id);
            if (i == (x/2)) {
                taskManager.removeTask(id);
            }
        }
        assertEquals(x-1, historyManager.getHistory().size());
    }

    @Test
    void historyShouldHaveSameOrder() {
        List<Task> checkList = new ArrayList<>();
        int x = 5;
        for (int i = 1; i <= x; i++) {
            Task task = new Task("task name " + i, "task description " + i, TaskStatus.NEW);
            int id = taskManager.addTask(task).getId();
            taskManager.getTask(id);
            checkList.add(task);
        }

        List<Task> history = historyManager.getHistory();
        for (int i = 0; i < x; i++) {
            assertEquals(checkList.get(i), history.get(i));
        }
    }

    @Test
    void firstElementOfHistoryShouldChangeAfterRemovingThisTask() {
        Task check = null;
        int x = 5;
        for (int i = 1; i <= x; i++) {
            Task task = new Task("task name " + i, "task description " + i, TaskStatus.NEW);
            int id = taskManager.addTask(task).getId();
            taskManager.getTask(id);
            if (i == 2) {
                check = task;
            }
        }
        taskManager.removeTask(1);
        assertEquals(check, historyManager.getHistory().get(0));
    }

    @Test
    void lastElementOfHistoryShouldChangeAfterRemovingThisTask() {
        Task check = null;
        int x = 5;
        for (int i = 1; i <= x; i++) {
            Task task = new Task("task name " + i, "task description " + i, TaskStatus.NEW);
            int id = taskManager.addTask(task).getId();
            taskManager.getTask(id);
            if (i == x-1) {
                check = task;
            }
        }
        taskManager.removeTask(x);
        var history = historyManager.getHistory();
        assertEquals(check, history.getLast());
    }

    @Test
    void historyKeepsOnly1ReviewIfTaskWasViewedManyTimes() {
        Task check = null;
        int x = 5;
        for (int i = 1; i <= x; i++) {
            Task task = new Task("task name " + i, "task description " + i, TaskStatus.NEW);
            int id = taskManager.addTask(task).getId();
            taskManager.getTask(id);
        }

        check = taskManager.getTask(2);
        var history = historyManager.getHistory();
        for (int i = 1; i <= x; i++) {
            if (i == x) {
                assertEquals(check, history.getLast());
            } else {
                assertNotEquals(check, history.get(i-1));
            }
        }
    }
}