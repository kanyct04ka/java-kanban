package ru.tracker.controller;

import ru.tracker.model.Task;

import java.util.ArrayList;

public interface HistoryManager<T extends Task> {

    void add(T task);
    ArrayList<Task> getHistory();
}
