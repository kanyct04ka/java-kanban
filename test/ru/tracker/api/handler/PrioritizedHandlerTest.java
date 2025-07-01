package ru.tracker.api.handler;

import org.junit.jupiter.api.Test;
import ru.tracker.api.HttpTaskServerTest;
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

public class PrioritizedHandlerTest extends HttpTaskServerTest {

    public PrioritizedHandlerTest() throws IOException, InterruptedException {
    }

    @Test
    public void getPrioritized() throws IOException, InterruptedException {
        Task task = new Task("task name", "task description", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.of(2025, 07, 01, 12, 30));
        task.setDuration(Duration.ofMinutes(38));
        taskManager.addTask(task);
        taskManager.getTask(1);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"startTime\":\"2025-07-01 12:30\""));
        assertTrue(response.body().contains("\"duration\":38"));

        client.close();
    }
}
