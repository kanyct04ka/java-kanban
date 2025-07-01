package ru.tracker.api.handler;

import org.junit.jupiter.api.Test;
import ru.tracker.api.HttpTaskServerTest;
import ru.tracker.model.Epic;
import ru.tracker.model.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskHandlerTest extends HttpTaskServerTest {
    public SubtaskHandlerTest() throws IOException, InterruptedException {
    }

    @Test
    public void getSubtask_NotFound() throws IOException, InterruptedException {
        int id = 8;

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("" ,response.body());

        client.close();
    }

    @Test
    public void getSubtask_IncorrectIdFormat() throws IOException, InterruptedException {
        String id = "abc";

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Невозможно привести идентификатор abc к числу", response.body());

        client.close();
    }

    @Test
    public void getSubtasks_EmptyList() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]" ,response.body());

        client.close();
    }

    @Test
    public void postSubtask_EpicNotFound()  throws IOException, InterruptedException {
        String requestBody = "{"
                + "\"name\":\"subtask name\","
                + "\"description\":\"subtask desc\","
                + "\"status\":\"NEW\","
                + "\"epicId\":19"
                + "}";

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Эпик с указанным идентификатором не существует" ,response.body());

        client.close();
    }

    @Test
    public void postSubtask_EpicExist()  throws IOException, InterruptedException {
        Epic epic = new Epic("epic name", "epic desc", new ArrayList<Subtask>());
        taskManager.addEpic(epic);
        String requestBody = "{"
                + "\"name\":\"subtask name\","
                + "\"description\":\"subtask desc\","
                + "\"status\":\"NEW\","
                + "\"epicId\":" + epic.getId() + ","
                + "\"startTime\":\"2025-07-01 12:30\","
                + "\"duration\":38"
                + "}";

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getSubtaskList().size());
        assertEquals(1, taskManager.getPrioritizedTasks().size());

        client.close();
    }
}
