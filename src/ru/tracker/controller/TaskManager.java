package ru.tracker.controller;

import ru.tracker.model.Epic;
import ru.tracker.model.Subtask;
import ru.tracker.model.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TaskManager {
    private static int taskCount = 0;

    private HashMap<Integer, Task> taskList;
    private HashMap<Integer, Epic> epicList;
    private HashMap<Integer, Subtask> subtaskList;

    public TaskManager() {
        taskList = new HashMap<>();
        epicList = new HashMap<>();
        subtaskList = new HashMap<>();
    }

    private static int generateTaskId() {
        taskCount++;
        return taskCount;
    }

    // МЕТОДЫ ДЛЯ РАБОТЫ С ЗАДАЧАМИ
    public Task addTask(Task task) {
        var id = generateTaskId();
        task.setId(id);
        taskList.put(id, task);
        return task;
    }

    public Task getTask(int id) {
        return taskList.get(id);
    }

    public void updateTask(Task task) {
        taskList.put(task.getId(), task);
    }

    public Collection<Task> getTaskList() {
        return taskList.values();
    }

    public void removeTask(int id) {
        taskList.remove(id);
    }

    public void removeAllTasks() {
        taskList.clear();
    }

    // МЕТОДЫ ДЛЯ РАБОТЫ С ЭПИКАМИ
    public Epic addEpic(Epic epic) {
        var id = generateTaskId();
        epic.setId(id);
        epicList.put(id, epic);
        return epic;
    }

    public Epic getEpic(int id) {
        return epicList.get(id);
    }

    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        return epic.getSubtasks();
    }

    public void updateEpic(Epic epic) {
        epicList.put(epic.getId(), epic);
    }

    public Collection<Epic> getEpicList() {
        return epicList.values();
    }

    public void removeEpic(int id) {
        for (Subtask subtask : epicList.get(id).getSubtasks()) {
            subtaskList.remove(subtask.getId());
        }
        epicList.remove(id);
    }

    public void removeAllEpics() {
        epicList.clear();
        subtaskList.clear();
    }

    // МЕТОДЫ ДЛЯ РАБОТЫ С ПОДЗАДАЧАМИ
    public Subtask addSubtask(Subtask subtask, Epic epic) {
        var id = generateTaskId();
        subtask.setId(id);
        // исходим из того, что сама сабтаска как простая задача создается где-то во вне,
        // а управление и связка с эпиком обеспечивается ТаскМенеджером
        subtask.setEpicLink(epic);
        subtaskList.put(id, subtask);
        return subtask;
    }

    public Subtask getSubtask(int id) {
        return subtaskList.get(id);
    }

    public void updateSubtask(Subtask subtask) {
        subtaskList.put(subtask.getId(), subtask);
        subtask.getEpicLink().defineStatus();
    }

    public Collection<Subtask> getSubtaskList() {
        return subtaskList.values();
    }

    public void removeSubtask(int id) {
        var subtask = subtaskList.get(id);
        subtaskList.remove(id);
        if (subtask != null) {
            var epic = subtask.getEpicLink();
            epic.removeLinkedSubtask(subtask);
        }
    }

    public void removeAllSubtasks() {
        subtaskList.clear();
        for (Epic epic : epicList.values()) {
            epic.removeAllLinkedSubtask();
        }
    }
}
