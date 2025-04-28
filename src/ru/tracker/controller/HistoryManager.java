package ru.tracker.controller;

import ru.tracker.model.Task;

import java.util.ArrayList;

public interface HistoryManager {

    void add(Task task);
    ArrayList<Task> getHistory();
}
