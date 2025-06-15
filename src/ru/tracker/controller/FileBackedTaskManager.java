package ru.tracker.controller;

import ru.tracker.exceptions.ManagerLoadException;
import ru.tracker.exceptions.ManagerSaveException;
import java.io.IOException;

import ru.tracker.model.*;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path backupFile;

    public FileBackedTaskManager() {
        super();
        this.backupFile = FileBackedTaskManager.getBackupFile();
    }

    private static Path getBackupFile() {
        // пусть у нас будет какая-то логика формирования файла для хранения
        // возможно на основе даты или чего-либо еще
        // пока что просто заглушка
        return Path.of("resources/backup.csv");
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        if (Files.isDirectory(path)) {
            throw new ManagerLoadException("Указана директория, требуется указать путь к файлу.");
        }

        var fileBackedTaskManager = new FileBackedTaskManager();

        List<String[]> tasksForUpload;
        try {
            List<String> lines = Files.readAllLines(path);

            // отбираем из файла только строки с явным указанием типа задач
            // остальные считаем ошибочными/битыми
            tasksForUpload = lines.stream()
                    .filter(line -> line.contains("TASK")
                            || line.contains("EPIC")
                            || line.contains("SUBTASK"))
                    .map(line -> line.split(";", -1))
                    .toList();
        } catch (IOException exception) {
            throw new ManagerLoadException("Ошибка при чтении из файла: " + exception.getMessage());
        }

        if (!tasksForUpload.isEmpty()) {
            var maxTaskId = 0;
            for (String[] task : tasksForUpload) {
                switch (TaskType.valueOf(task[1])) {
                    case TaskType.TASK -> {
                        var t = new Task(task[3], task[4], TaskStatus.valueOf(task[2]));
                        t.setId(Integer.parseInt(task[0]));
                        if (!task[6].isBlank()) {
                            t.setStartTime(LocalDateTime.parse(task[6], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                        }
                        if (!task[7].isBlank()) {
                            t.setDuration(Duration.ofMinutes(Long.parseLong(task[7])));
                        }
                        fileBackedTaskManager.updateTask(t);
                    }
                    case TaskType.EPIC -> {
                        var e = new Epic(task[3], task[4], new ArrayList<Subtask>());
                        e.setId(Integer.parseInt(task[0]));
                        fileBackedTaskManager.updateEpic(e);
                    }
                    case TaskType.SUBTASK -> {
                        var e = fileBackedTaskManager.getEpic(Integer.parseInt(task[5]));
                        // если эпик не нашли, сабтаска не загружается
                        if (e == null) {
                            break;
                        }

                        var s = new Subtask(task[3], task[4], TaskStatus.valueOf(task[2]));
                        s.setId(Integer.parseInt(task[0]));
                        if (!task[6].isBlank()) {
                            s.setStartTime(LocalDateTime.parse(task[6], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                        }
                        if (!task[7].isBlank()) {
                            s.setDuration(Duration.ofMinutes(Long.parseLong(task[7])));
                        }
                        s.setEpicLink(e);
                        fileBackedTaskManager.updateSubtask(s);
                    }
                }
                maxTaskId = Math.max(maxTaskId, Integer.parseInt(task[0]));
            }
            // двигаем счетчик номеров задач на значение максимального id загруженных задач из файла
            fileBackedTaskManager.setTaskCount(maxTaskId);
        }

        return fileBackedTaskManager;
    }

    private void save() {
        if (!Files.exists(backupFile)) {
            try {
                Files.createFile(backupFile);
            } catch (IOException exception) {
                throw new ManagerSaveException("Ошибка сохранения снепшота задач в файл: "
                        + exception.getMessage());
            }
        }

        List<String> dataForSaving = new ArrayList<>();

        getTaskList().forEach(task -> dataForSaving.add(task.toStringForSaving()));

        getEpicList().forEach(epic -> {
            dataForSaving.add(epic.toStringForSaving());
            // сабтаски пишем только те, что связаны с эпиками
            // так избежим ошибок, если остались непривязанные сабтаски
            epic.getSubtasks().forEach(subtask ->
                dataForSaving.add(subtask.toStringForSaving()));
        });

        try {
            // при каждом изменении состояния снепшота файл полностью перезаписывается
            Files.write(backupFile, dataForSaving, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка сохранения снепшота задач в файл: "
                    + exception.getMessage());
        }
    }

    // МЕТОДЫ ДЛЯ РАБОТЫ С ЗАДАЧАМИ
    @Override
    public Task addTask(Task task) {
        var fTask = super.addTask(task);
        save();
        return fTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    // МЕТОДЫ ДЛЯ РАБОТЫ С ЭПИКАМИ
    @Override
    public Epic addEpic(Epic epic) {
        var fEpic = super.addEpic(epic);
        save();
        return fEpic;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    // МЕТОДЫ ДЛЯ РАБОТЫ С ПОДЗАДАЧАМИ
    @Override
    public Subtask addSubtask(Subtask subtask, Epic epic) {
        var fSubtask = super.addSubtask(subtask, epic);
        save();
        return fSubtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }
}
