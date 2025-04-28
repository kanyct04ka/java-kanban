package ru.tracker.controller;

import ru.tracker.model.Epic;
import ru.tracker.model.Subtask;
import ru.tracker.model.Task;

import java.util.ArrayList;
import java.util.Collection;

public interface TaskManager {

    ArrayList<Task> getHistory();

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
    ArrayList<Subtask> getEpicSubtasks(Epic epic);
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
