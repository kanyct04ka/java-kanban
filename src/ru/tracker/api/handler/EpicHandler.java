package ru.tracker.api.handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import ru.tracker.controller.TaskManager;
import ru.tracker.exceptions.ManagerAddTaskException;
import ru.tracker.model.Epic;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPIC -> handleGetEpic(exchange);
            case GET_EPICS -> handleGetEpics(exchange);
            case GET_EPIC_SUBTASKS -> handleGetEpicSubtasks(exchange);
            case POST_EPIC -> handlePostEpic(exchange);
            case DELETE_EPIC -> handleDeleteEpic(exchange);
            case UNKNOWN -> sendMethodNotAllowed(exchange);
        }
    }

    private Endpoint getEndpoint(URI uri, String method) {
        String[] path = uri.getPath().split("/");

        switch (method) {
            case "GET" -> {
                if (path.length == 2) {
                    return Endpoint.GET_EPICS;
                } else if (path.length == 3) {
                    return Endpoint.GET_EPIC;
                } else if (path.length == 4 && path[3].equals("subtasks")) {
                    return Endpoint.GET_EPIC_SUBTASKS;
                } else {
                    return Endpoint.UNKNOWN;
                }
            }
            case "POST" -> {
                return Endpoint.POST_EPIC;
            }
            case "DELETE" -> {
                return (path.length > 2 ?  Endpoint.DELETE_EPIC : Endpoint.UNKNOWN);
            }
            default -> {
                return Endpoint.UNKNOWN;
            }
        }
    }

    enum Endpoint {
        GET_EPIC,
        GET_EPICS,
        GET_EPIC_SUBTASKS,
        POST_EPIC,
        DELETE_EPIC,
        UNKNOWN
    }

    private void handleGetEpic(HttpExchange exchange) throws IOException {
        String stringId = exchange.getRequestURI().getPath().split("/")[2];

        try {
            int id = Integer.parseInt(stringId);
            var task = taskManager.getEpic(id);
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

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        String stringId = exchange.getRequestURI().getPath().split("/")[2];

        try {
            int id = Integer.parseInt(stringId);
            var task = taskManager.getEpic(id);
            if (task != null) {
                sendText(exchange, gson.toJson(task.getSubtasks()));
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            var message = String.format("Невозможно привести идентификатор %s к числу", stringId);
            sendBadRequest(exchange, message);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getEpicList()));
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        JsonObject object = JsonParser.parseString(requestBody).getAsJsonObject();

        if (object.get("name").isJsonNull()
                && object.get("description").isJsonNull()) {
            sendBadRequest(exchange, "Недостаточно минимального набора параметров для создания задачи");
        }

        Epic incomeEpic = new Epic(object.get("name").getAsString(),
                object.get("name").getAsString(),
                new ArrayList<>());

        if (requestBody.contains("\"id\"")
                && !object.get("id").isJsonNull()) {
            incomeEpic.setId(object.get("id").getAsInt());
        }

        if (incomeEpic.getId() == 0) {
            try {
                var task = taskManager.addEpic(incomeEpic);
                sendCreated(exchange, gson.toJson(task));
            } catch (ManagerAddTaskException e) {
                sendHasInteractions(exchange);
            }
        } else {
            try {
                taskManager.updateTask(incomeEpic);
                sendCreated(exchange, gson.toJson(incomeEpic));
            } catch (ManagerAddTaskException e) {
                sendHasInteractions(exchange);
            }
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        String stringId = exchange.getRequestURI().getPath().split("/")[2];

        try {
            int id = Integer.parseInt(stringId);
            taskManager.removeEpic(id);
            sendOk(exchange);
        } catch (NumberFormatException e) {
            var message = String.format("Невозможно привести идентификатор %s к числу", stringId);
            sendBadRequest(exchange, message);
        }
    }
}