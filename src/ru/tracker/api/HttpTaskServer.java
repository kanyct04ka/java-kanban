package ru.tracker.api;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import ru.tracker.api.handler.*;
import ru.tracker.controller.TaskManager;

public class HttpTaskServer {

    private final HttpServer httpServer;
    private TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;


        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);

        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedTaskHandler(taskManager));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }
}
