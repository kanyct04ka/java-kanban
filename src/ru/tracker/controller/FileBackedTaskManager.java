package ru.tracker.controller;

import ru.tracker.exceptions.ManagerLoadException;
import ru.tracker.exceptions.ManagerSaveException;
import ru.tracker.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File backupFile;

    public FileBackedTaskManager() {
        super();
        this.backupFile = FileBackedTaskManager.getBackupFile();
    }

    // конструктор для тестов с временным файлом
    public FileBackedTaskManager(File backupFile) {
        super();
        this.backupFile = backupFile;
    }

    private static File getBackupFile() {
        // пусть у нас будет какая-то логика формирования файла для хранения
        // возможно на основе даты или чего-либо еще
        // пока что просто заглушка
        return new File("resources/backup.csv");
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        var fileBackedTaskManager = new FileBackedTaskManager();

        // используется LinkedList чтобы сохранить порядок чтения строк из файла
        // чтобы при наполнении задач сабтаски шли после эпиков и нормально к ним привязывались
        // исходим из того что и записываться в файл будет тоже с соблюдением последовательности
        List<String[]> tasksForUpload = new LinkedList<>();

        try (var fileReader = new FileReader(file)) {
            var bufferReader = new BufferedReader(fileReader);

            while (bufferReader.ready()) {
                String line = bufferReader.readLine();
                tasksForUpload.add(line.split(";"));
            }
        } catch (IOException exception) {
            throw new ManagerLoadException("Ошибка при чтении из файла: " + exception.getMessage());
        }

        if (!tasksForUpload.isEmpty()) {
            var maxTaskId = 0;
            for (String[] task : tasksForUpload) {
                switch (TaskType.valueOf(task[1])) {
                    case TaskType.TASK -> {
                        var t = new Task(task[2], task[4], TaskStatus.valueOf(task[3]));
                        t.setId(Integer.parseInt(task[0]));
                        fileBackedTaskManager.updateTask(t);
                    }
                    case TaskType.EPIC -> {
                        var e = new Epic(task[2], task[4], new ArrayList<Subtask>());
                        e.setId(Integer.parseInt(task[0]));
                        fileBackedTaskManager.updateEpic(e);
                    }
                    case TaskType.SUBTASK -> {
                        var e = fileBackedTaskManager.getEpic(Integer.parseInt(task[5]));
                        // если эпик не нашли, сабтаска не загружается
                        if (e == null) {
                            break;
                        }

                        var s = new Subtask(task[2], task[4], TaskStatus.valueOf(task[3]));
                        s.setId(Integer.parseInt(task[0]));
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
        // при каждом изменении состояния снепшота файл полностью перезаписывается
        try (FileWriter fileWriter = new FileWriter(backupFile)) {
            if (!getTaskList().isEmpty()) {
                for (Task task : getTaskList()) {
                    fileWriter.write(task.toStringForSaving() + "\n");
                }
            }

            if (!getEpicList().isEmpty()) {
                for (Epic task : getEpicList()) {
                    fileWriter.write(task.toStringForSaving() + "\n");
                }
                // сабтаски проверяем только если в принципе есть эпики
                // это снимет ошибку если вдруг при отсутствии эпиков остались какие-то сабтаски
                // и пишем их в последнюю очередь
                if (!getSubtaskList().isEmpty()) {
                    for (Subtask task : getSubtaskList()) {
                        fileWriter.write(task.toStringForSaving() + "\n");
                    }
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка сохранения снепшота задач в файл: " +
                    exception.getMessage());
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
