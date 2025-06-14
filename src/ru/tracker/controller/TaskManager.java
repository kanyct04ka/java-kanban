package ru.tracker.controller;

import ru.tracker.model.Epic;
import ru.tracker.model.Subtask;
import ru.tracker.model.Task;

import java.util.Collection;
import java.util.List;
import java.util.Set;


public interface TaskManager {

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();

    // МЕТОДЫ ДЛЯ РАБОТЫ С ЗАДАЧАМИ
    Task addTask(Task task);

    Task getTask(int id);

    void updateTask(Task task);

    Collection<Task> getTaskList();

    void removeTask(int id);

    void removeAllTasks();


    // МЕТОДЫ ДЛЯ РАБОТЫ С ЭПИКАМИ
    Epic addEpic(Epic epic);

    Epic getEpic(int id);

    List<Subtask> getEpicSubtasks(Epic epic);

    void updateEpic(Epic epic);

    Collection<Epic> getEpicList();

    void removeEpic(int id);

    void removeAllEpics();


    // МЕТОДЫ ДЛЯ РАБОТЫ С ПОДЗАДАЧАМИ
    Subtask addSubtask(Subtask subtask, Epic epic);

    Subtask getSubtask(int id);

    void updateSubtask(Subtask subtask);

    Collection<Subtask> getSubtaskList();

    void removeSubtask(int id);

    void removeAllSubtasks();
}
