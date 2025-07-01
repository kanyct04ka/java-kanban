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

public class TaskHandlerTest extends HttpTaskServerTest {
    public TaskHandlerTest() throws IOException, InterruptedException {
    }

    @Test
    public void wrongHttpMethod() throws IOException, InterruptedException {
        Task task = new Task("task name", "task description", TaskStatus.NEW);
        String requestBody = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals(0, taskManager.getTaskList().size());
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());

        client.close();
    }

    @Test
    public void getTask_NotFound() throws IOException, InterruptedException {
        int id = 8;

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/" + id);
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
    public void getTask_IncorrectIdFormat() throws IOException, InterruptedException {
        String id = "abc";

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/" + id);
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
    public void getTasks_EmptyList() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]" ,response.body());

        client.close();
    }

    @Test
    public void addTask_WithoutStartAndDuration() throws IOException, InterruptedException {
        Task task = new Task("task name", "task description", TaskStatus.NEW);
        String requestBody = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getTaskList().size());
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());
        assertEquals("task name", taskManager.getTask(1).getName());
        assertTrue(taskManager.getTask(1).getStartTime().isEmpty());

        client.close();
    }

    @Test
    public void addTask_WithStartAndDuration() throws IOException, InterruptedException {
        Task task = new Task("task name", "task description", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.of(2025, 07, 01, 12, 30));
        task.setDuration(Duration.ofMinutes(38));
        String requestBody = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getTaskList().size());
        assertEquals(1, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());
        assertEquals("task name", taskManager.getTask(1).getName());
        assertEquals(38L, taskManager.getTask(1).getDuration().get().toMinutes());
        assertTrue(taskManager.getTask(1).getStartTime().isPresent());

        client.close();
    }

    @Test
    public void addTask_Update() throws IOException, InterruptedException {
        Task task = new Task("task name", "task description", TaskStatus.NEW);
        taskManager.addTask(task);
        Task updatedTask = new Task("updated name", "updated description", TaskStatus.DONE);
        updatedTask.setId(task.getId());
        String requestBody = gson.toJson(updatedTask);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getTaskList().size());
        assertEquals("updated name", taskManager.getTask(1).getName());
        assertEquals(TaskStatus.DONE, taskManager.getTask(1).getStatus());

        client.close();
    }

    @Test
    public void deleteTask() throws IOException, InterruptedException {
        Task task = new Task("task name", "task description", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.of(2025, 07, 01, 12, 30));
        task.setDuration(Duration.ofMinutes(38));
        taskManager.addTask(task);
        taskManager.getTask(task.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("", response.body());
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());

        client.close();
    }
}
