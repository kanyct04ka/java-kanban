package ru.tracker.controller;

import ru.tracker.model.Task;

public abstract class Managers {
    public static TaskManager getDefault() {
//        в ТЗ данного спринта не описана логика, предъявляемая к данному методу
//        поэтому добавлено пока так
        TaskManager taskManager = new InMemoryTaskManager();
        return taskManager;
    }

    public static HistoryManager<Task> getDefaultHistory() {
        HistoryManager<Task> historyManager = new InMemoryHistoryManager();
        return historyManager;
    }
}
