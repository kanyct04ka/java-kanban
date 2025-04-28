package ru.tracker.controller;

import ru.tracker.model.Task;

public abstract class Managers {
    public static TaskManager getDefault() {
//        в ТЗ данного спринта не описана логика, предъявляемая к данному методу
//        поэтому добавлено пока так
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
