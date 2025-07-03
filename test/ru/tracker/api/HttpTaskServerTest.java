package ru.tracker.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.jupiter.api.*;

import ru.tracker.api.adapters.DurationAdapter;
import ru.tracker.api.adapters.LocalDateTimeAdapter;
import ru.tracker.controller.Managers;
import ru.tracker.controller.TaskManager;
import ru.tracker.model.Task;
import ru.tracker.model.TaskStatus;

import java.io.IOException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskServerTest {
    protected TaskManager taskManager = Managers.getDefault();
    protected HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    protected Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HttpTaskServerTest() throws IOException, InterruptedException {
    }

    @BeforeEach
    public void prepareToTest() {
        taskManager.removeAllTasks();
        taskManager.removeAllEpics();
        taskManager.removeAllSubtasks();
        taskServer.start();
    }

    @AfterEach
    public void finishTest() {
        taskServer.stop();
    }

    // проверка, что сервер в принципе запущен и работает
    @Test
    public void checkRunningServer() throws IOException, InterruptedException {

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/");
            HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(404, response.statusCode());
        }
    }

    // далее проверки, что основные обработчики работают
    @Test
    public void checkTaskHandler() throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());
        }
    }

    @Test
    public void checkEpicHandler() throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());
        }
    }

    @Test
    public void checkSubtaskHandler() throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());
        }
    }

    @Test
    public void checkHistoryHandler() throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/history");
            HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());
        }
    }

    @Test
    public void checkPrioritizedHandler() throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/prioritized");
            HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());
        }
    }
}
