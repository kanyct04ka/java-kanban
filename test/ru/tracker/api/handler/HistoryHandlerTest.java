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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryHandlerTest extends HttpTaskServerTest {

    public HistoryHandlerTest() throws IOException, InterruptedException {
    }

    @Test
    public void getHistory() throws IOException, InterruptedException {
        Task task = new Task("task name", "task description", TaskStatus.NEW);
        taskManager.addTask(task);
        taskManager.getTask(1);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"description\":\"task description\""));

        client.close();
    }
}
