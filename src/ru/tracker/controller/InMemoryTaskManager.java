package ru.tracker.controller;

import ru.tracker.model.Epic;
import ru.tracker.model.Subtask;
import ru.tracker.model.Task;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {

    private int taskCount;

    private final HashMap<Integer, Task> taskList;
    private final HashMap<Integer, Epic> epicList;
    private final HashMap<Integer, Subtask> subtaskList;

    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.taskCount = 0;
        this.taskList = new HashMap<>();
        this.epicList = new HashMap<>();
        this.subtaskList = new HashMap<>();

        this.historyManager = Managers.getDefaultHistory();
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int generateTaskId() {
        this.taskCount++;
        return this.taskCount;
    }

    protected int getTaskCount() {
        return taskCount;
    }

    protected void setTaskCount(int count) {
        taskCount = count;
    }


    // МЕТОДЫ ДЛЯ РАБОТЫ С ЗАДАЧАМИ
    @Override
    public Task addTask(Task task) {
        var id = generateTaskId();
        task.setId(id);
        taskList.put(id, task);
        return task;
    }

    @Override
    public Task getTask(int id) {
        Task task = taskList.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public void updateTask(Task task) {
        taskList.put(task.getId(), task);
    }

    @Override
    public Collection<Task> getTaskList() {
        return taskList.values();
    }

    @Override
    public void removeTask(int id) {
        historyManager.remove(id);
        taskList.remove(id);
    }

    @Override
    public void removeAllTasks() {
        for (Integer id : taskList.keySet()) {
            historyManager.remove(id);
        }
        taskList.clear();
    }

    // МЕТОДЫ ДЛЯ РАБОТЫ С ЭПИКАМИ
    @Override
    public Epic addEpic(Epic epic) {
        var id = generateTaskId();
        epic.setId(id);
        epicList.put(id, epic);
        return epic;
    }

    @Override
    public Epic getEpic(int id) {
        var task = epicList.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        return epic.getSubtasks();
    }

    @Override
    public void updateEpic(Epic epic) {
        epicList.put(epic.getId(), epic);
    }

    @Override
    public Collection<Epic> getEpicList() {
        return epicList.values();
    }

    @Override
    public void removeEpic(int id) {
        for (Subtask subtask : epicList.get(id).getSubtasks()) {
            historyManager.remove(subtask.getId());
            subtaskList.remove(subtask.getId());
        }
        epicList.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeAllEpics() {
        removeAllSubtasks();
        for (Integer id : epicList.keySet()) {
            historyManager.remove(id);
        }
        epicList.clear();
    }

    // МЕТОДЫ ДЛЯ РАБОТЫ С ПОДЗАДАЧАМИ
    @Override
    public Subtask addSubtask(Subtask subtask, Epic epic) {
        var id = generateTaskId();
        subtask.setId(id);
        // исходим из того, что сама сабтаска как простая задача создается где-то во вне,
        // а управление и связка с эпиком обеспечивается ТаскМенеджером
        subtask.setEpicLink(epic);
        subtaskList.put(id, subtask);
        return subtask;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask task = subtaskList.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtaskList.put(subtask.getId(), subtask);
        subtask.getEpicLink().defineStatus();
    }

    @Override
    public Collection<Subtask> getSubtaskList() {
        return subtaskList.values();
    }

    @Override
    public void removeSubtask(int id) {
        var subtask = subtaskList.get(id);
        historyManager.remove(id);
        subtaskList.remove(id);
        if (subtask != null) {
            var epic = subtask.getEpicLink();
            epic.removeLinkedSubtask(subtask);
        }
    }

    @Override
    public void removeAllSubtasks() {
        for (Integer id : subtaskList.keySet()) {
            historyManager.remove(id);
        }
        subtaskList.clear();
        for (Epic epic : epicList.values()) {
            epic.removeAllLinkedSubtask();
        }
    }
}
