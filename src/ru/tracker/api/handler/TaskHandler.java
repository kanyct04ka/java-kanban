package ru.tracker.api.handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import ru.tracker.controller.TaskManager;
import ru.tracker.exceptions.ManagerAddTaskException;
import ru.tracker.model.Task;
import ru.tracker.model.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASK -> handleGetTask(exchange);
            case GET_TASKS -> handleGetTasks(exchange);
            case POST_TASK -> handlePostTask(exchange);
            case DELETE_TASK -> handleDeleteTask(exchange);
            case UNKNOWN -> sendMethodNotAllowed(exchange);
        }
    }

    private Endpoint getEndpoint(URI uri, String method) {
        String[] path = uri.getPath().split("/");

        switch (method) {
            case "GET" -> {
                return (path.length > 2 ?  Endpoint.GET_TASK : Endpoint.GET_TASKS);
            }
            case "POST" -> {
                return Endpoint.POST_TASK;
            }
            case "DELETE" -> {
                return (path.length > 2 ?  Endpoint.DELETE_TASK : Endpoint.UNKNOWN);
            }
            default -> {
                return Endpoint.UNKNOWN;
            }
        }
    }

    enum Endpoint {
        GET_TASK,
        GET_TASKS,
        POST_TASK,
        DELETE_TASK,
        UNKNOWN
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        String stringId = exchange.getRequestURI().getPath().split("/")[2];

        try {
            int id = Integer.parseInt(stringId);
            var task = taskManager.getTask(id);
            if (task != null) {
                sendText(exchange, gson.toJson(task));
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            var message = String.format("Невозможно привести идентификатор %s к числу", stringId);
            sendBadRequest(exchange, message);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getTaskList()));
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        JsonObject object = JsonParser.parseString(requestBody).getAsJsonObject();

        if (object.get("name").isJsonNull()
                && object.get("description").isJsonNull()
                && object.get("status").isJsonNull()) {
            sendBadRequest(exchange, "Недостаточно минимального набора параметров для создания задачи");
        }

        Task incomeTask = new Task(object.get("name").getAsString(),
                object.get("name").getAsString(),
                TaskStatus.valueOf(object.get("status").getAsString()));

        if (requestBody.contains("\"id\"")
                && !object.get("id").isJsonNull()) {
            incomeTask.setId(object.get("id").getAsInt());
        }
        if (requestBody.contains("\"startTime\"")
                && !object.get("startTime").isJsonNull()) {
            var start = LocalDateTime.parse(object.get("startTime").getAsString(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            incomeTask.setStartTime(start);
        }
        if (requestBody.contains("\"duration\"")
                && !object.get("duration").isJsonNull()) {
            incomeTask.setDuration(Duration.ofMinutes(object.get("duration").getAsLong()));
        }

        if (incomeTask.getId() == 0) {
            try {
                var task = taskManager.addTask(incomeTask);
                sendCreated(exchange, gson.toJson(task));
            } catch (ManagerAddTaskException e) {
                sendHasInteractions(exchange);
            }
        } else {
            try {
                taskManager.updateTask(incomeTask);
                sendCreated(exchange, gson.toJson(incomeTask));
            } catch (ManagerAddTaskException e) {
                sendHasInteractions(exchange);
            }
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        String stringId = exchange.getRequestURI().getPath().split("/")[2];

        try {
            int id = Integer.parseInt(stringId);
            taskManager.removeTask(id);
            sendOk(exchange);
        } catch (NumberFormatException e) {
            var message = String.format("Невозможно привести идентификатор %s к числу", stringId);
            sendBadRequest(exchange, message);
        }
    }
}