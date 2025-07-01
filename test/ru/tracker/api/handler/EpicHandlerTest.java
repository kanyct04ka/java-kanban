package ru.tracker.api.handler;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.tracker.api.HttpTaskServerTest;
import ru.tracker.model.Epic;
import ru.tracker.model.Subtask;
import ru.tracker.model.TaskStatus;

import java.io.IOException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.ArrayList;


public class EpicHandlerTest extends HttpTaskServerTest {
    public EpicHandlerTest() throws IOException, InterruptedException {
    }

    @Test
    public void deleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("task name", "task description", new ArrayList<>());
        taskManager.addEpic(epic);
        taskManager.getEpic(epic.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("", response.body());
        assertEquals(0, taskManager.getEpicList().size());
        assertEquals(0, taskManager.getHistory().size());

        client.close();
    }

    @Test
    public void postEpic()  throws IOException, InterruptedException {
        String requestBody = "{"
                + "\"name\":\"epic name\","
                + "\"description\":\"epic desc\""
                + "}";

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getEpicList().size());

        client.close();
    }

    @Test
    public void getEpicSubtasks_Empty() throws IOException, InterruptedException {
        Epic epic = new Epic("task name", "task description", new ArrayList<>());
        taskManager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]" ,response.body());

        client.close();
    }

    @Test
    public void getEpicSubtasks() throws IOException, InterruptedException {
        var epic = taskManager.addEpic(new Epic("epic name", "epic description", new ArrayList<>()));
        taskManager.addSubtask(new Subtask("sub name", "sub desc", TaskStatus.NEW), epic);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("sub name"));

        client.close();
    }

}
