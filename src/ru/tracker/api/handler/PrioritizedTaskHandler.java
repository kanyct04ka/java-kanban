package ru.tracker.api.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import ru.tracker.controller.TaskManager;

import java.io.IOException;


public class PrioritizedTaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public PrioritizedTaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()));
    }
}