package ru.tracker.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.jupiter.api.*;

import ru.tracker.api.adapters.DurationAdapter;
import ru.tracker.api.adapters.LocalDateTimeAdapter;
import ru.tracker.controller.Managers;
import ru.tracker.controller.TaskManager;

import java.io.IOException;

import java.time.Duration;
import java.time.LocalDateTime;

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

}
