package ru.tracker.controller;

import ru.tracker.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<Task> last10ViewedTask;

    public InMemoryHistoryManager() {
        this.last10ViewedTask = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (last10ViewedTask.size() == 10) {
            last10ViewedTask.removeFirst();
        }
        last10ViewedTask.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(last10ViewedTask);
    }

}
