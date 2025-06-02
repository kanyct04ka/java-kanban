package ru.tracker.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.tracker.model.*;

import java.util.List;
import java.util.ArrayList;


class InMemoryHistoryManagerTest {

    InMemoryTaskManager taskManager;

    @BeforeEach
    void createManagers() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void historyManagerKeeps100UniqueTasks() {
        for (int i = 1; i <= 100; i++) {
            Task task = new Task("task name " + i, "task description " + i, TaskStatus.NEW);
            int id = taskManager.addTask(task).getId();
            taskManager.getTask(id);
            assertEquals(i, taskManager.getHistory().size());
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
        assertEquals(x-1, taskManager.getHistory().size());
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

        List<Task> history = taskManager.getHistory();
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
        assertEquals(check, taskManager.getHistory().get(0));
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
        var history = taskManager.getHistory();
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
        var history = taskManager.getHistory();
        for (int i = 1; i <= x; i++) {
            if (i == x) {
                assertEquals(check, history.getLast());
            } else {
                assertNotEquals(check, history.get(i-1));
            }
        }
    }
}