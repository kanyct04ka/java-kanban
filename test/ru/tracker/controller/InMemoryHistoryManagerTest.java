package ru.tracker.controller;

import ru.tracker.model.Epic;
import ru.tracker.model.Subtask;
import ru.tracker.model.Task;

import org.junit.jupiter.api.Test;
import ru.tracker.model.TaskStatus;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    InMemoryTaskManager taskManager = new InMemoryTaskManager();
    HistoryManager historyManager = taskManager.getHistoryManager();

    @Test
    void add() {
        for (int i = 1; i <= 12; i++) {
            int goalHistorySize = (i > 10 ? 10 : i);
            if (i%2 == 0) {
                Epic epic = new Epic("epic name " + i, "epic description " + i, new ArrayList<Subtask>());
                int id = taskManager.addEpic(epic).getId();
                taskManager.getEpic(id);
                assertEquals(goalHistorySize, historyManager.getHistory().size());
            } else {
                Task task = new Task("task name " + i, "task description " + i, TaskStatus.NEW);
                int id = taskManager.addTask(task).getId();
                taskManager.getTask(id);
                assertEquals(goalHistorySize, historyManager.getHistory().size());
            }
        }
    }
}