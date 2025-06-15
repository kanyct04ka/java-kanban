package ru.tracker.controller;

import ru.tracker.exceptions.ManagerAddTaskException;

import ru.tracker.model.Epic;
import ru.tracker.model.Subtask;
import ru.tracker.model.Task;

import java.util.*;


public class InMemoryTaskManager implements TaskManager {

    private int taskCount;

    private final HashMap<Integer, Task> taskList;
    private final HashMap<Integer, Epic> epicList;
    private final HashMap<Integer, Subtask> subtaskList;
    private final Set<Task> prioritizedTasks;

    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.taskCount = 0;
        this.taskList = new HashMap<>();
        this.epicList = new HashMap<>();
        this.subtaskList = new HashMap<>();

        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(task -> task.getStartTime().get()));

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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean hasConflictWithPrioritizedTasks(Task task) {
        // optional-ы по сути проверены, т.к. в метод будет подаваться задача только с датами
        return prioritizedTasks.stream()
                .anyMatch(priorTask -> priorTask.getEndTime().get().isAfter(task.getStartTime().get())
                        && priorTask.getStartTime().get().isBefore(task.getEndTime().get()));
    }

    // МЕТОДЫ ДЛЯ РАБОТЫ С ЗАДАЧАМИ
    @Override
    public Task addTask(Task task) {
        if (task.getStartTime().isPresent()
                && task.getDuration().isPresent()
                && hasConflictWithPrioritizedTasks(task)) {
            throw new ManagerAddTaskException("Задача пересекается во времени с запланированными ранее задачами.");
        }

        var id = generateTaskId();
        task.setId(id);
        taskList.put(id, task);

        if (task.getStartTime().isPresent() && task.getDuration().isPresent()) {
            prioritizedTasks.add(task);
        }

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
        if (task.getStartTime().isPresent()
                && task.getDuration().isPresent()
                && hasConflictWithPrioritizedTasks(task)) {
            throw new ManagerAddTaskException("Задача пересекается во времени с запланированными ранее задачами.");
        }

        // из списка приоритизированных удаляем сначала предыдущую версию задачи
        var previousVersion = taskList.get(task.getId());
        if (previousVersion != null) {
            prioritizedTasks.remove(previousVersion);
        }

        if (task.getStartTime().isPresent() && task.getDuration().isPresent()) {
            prioritizedTasks.add(task);
        }

        taskList.put(task.getId(), task);
    }

    @Override
    public Collection<Task> getTaskList() {
        return taskList.values();
    }

    @Override
    public void removeTask(int id) {
        prioritizedTasks.remove(taskList.get(id));
        historyManager.remove(id);
        taskList.remove(id);
    }

    @Override
    public void removeAllTasks() {
        for (Integer id : taskList.keySet()) {
            prioritizedTasks.remove(taskList.get(id));
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
        if (subtask.getStartTime().isPresent()
                && subtask.getDuration().isPresent()
                && hasConflictWithPrioritizedTasks(subtask)) {
            throw new ManagerAddTaskException("Задача пересекается во времени с запланированными ранее задачами.");
        }

        var id = generateTaskId();
        subtask.setId(id);
        // исходим из того, что сама подзадача как простая задача создается где-то во вне,
        // а управление и связка с эпиком обеспечивается ТаскМенеджером
        subtask.setEpicLink(epic);
        subtaskList.put(id, subtask);

        if (subtask.getStartTime().isPresent() && subtask.getDuration().isPresent()) {
            prioritizedTasks.add(subtask);
        }

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
        if (subtask.getStartTime().isPresent()
                && subtask.getDuration().isPresent()
                && hasConflictWithPrioritizedTasks(subtask)) {
            throw new ManagerAddTaskException("Задача пересекается во времени с запланированными ранее задачами.");
        }

        var previousVersion = subtaskList.get(subtask.getId());
        if (previousVersion != null) {
            prioritizedTasks.remove(previousVersion);
        }

        if (subtask.getStartTime().isPresent() && subtask.getDuration().isPresent()) {
            prioritizedTasks.add(subtask);
        }

        subtaskList.put(subtask.getId(), subtask);
    }

    @Override
    public Collection<Subtask> getSubtaskList() {
        return subtaskList.values();
    }

    @Override
    public void removeSubtask(int id) {
        var subtask = subtaskList.get(id);
        prioritizedTasks.remove(subtask);
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
            prioritizedTasks.remove(subtaskList.get(id));
            historyManager.remove(id);
        }
        subtaskList.clear();
        for (Epic epic : epicList.values()) {
            epic.removeAllLinkedSubtask();
        }
    }
}
