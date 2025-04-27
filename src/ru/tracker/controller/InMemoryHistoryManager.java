package ru.tracker.controller;

import ru.tracker.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager<T extends Task> implements HistoryManager<T> {

    private ArrayList<Task> last10ViewedTask;

    public InMemoryHistoryManager() {
        this.last10ViewedTask = new ArrayList<>();
    }

    @Override
    public void add(T task) {
        if (last10ViewedTask.size() == 10) {
            last10ViewedTask.removeFirst();
        }
        last10ViewedTask.add((Task) task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return  last10ViewedTask;
    }

}
