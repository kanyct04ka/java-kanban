package ru.tracker.api.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import ru.tracker.api.adapters.DurationAdapter;
import ru.tracker.api.adapters.EpicAdapter;
import ru.tracker.api.adapters.LocalDateTimeAdapter;
import ru.tracker.api.adapters.SubtaskAdapter;
import ru.tracker.model.Epic;
import ru.tracker.model.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;


public class BaseHttpHandler {
    protected final Gson gson;

    public BaseHttpHandler() {
        gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Epic.class, new EpicAdapter())
                .registerTypeAdapter(Subtask.class, new SubtaskAdapter())
                .create();
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");

        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        exchange.close();
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        byte[] response = "Задача пересекается с запланированными ранее!".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");

        exchange.sendResponseHeaders(406, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void sendOk(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, 0);
        exchange.close();
    }

    protected void sendCreated(HttpExchange exchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");

        exchange.sendResponseHeaders(201, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void sendBadRequest(HttpExchange exchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");

        exchange.sendResponseHeaders(400, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }
}
