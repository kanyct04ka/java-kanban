package ru.tracker;
import ru.tracker.controller.TaskManager;
import ru.tracker.model.TaskStatus;
import ru.tracker.model.Task;
import ru.tracker.model.Epic;
import ru.tracker.model.Subtask;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // Тестирование ЗАДАЧ
        taskManager.addTask(new Task("Task1", "description1", TaskStatus.NEW));
        taskManager.addTask(new Task("Task2", "description2", TaskStatus.NEW));
        taskManager.addTask(new Task("Task3", "description3", TaskStatus.NEW));
        System.out.println(taskManager.getTaskList());
        System.out.println("----");

        taskManager.getTask(1).setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(taskManager.getTask(1));
        System.out.println(taskManager.getTask(1));
        System.out.println("----");

        taskManager.removeTask(1);
        System.out.println(taskManager.getTaskList());
        System.out.println("----");

        taskManager.removeAllTasks();
        System.out.println(taskManager.getTaskList());
        System.out.println("----");


        // Тестирование ЭПИКОВ и ПОДЗАДАЧ
        taskManager.addEpic(new Epic("Task4", "description4", new ArrayList<Subtask>()));
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubtaskList());
        System.out.println("----");

        taskManager.addSubtask(new Subtask("Task5", "description5", TaskStatus.NEW), taskManager.getEpic(4));
        taskManager.addSubtask(new Subtask("Task6", "description6", TaskStatus.NEW), taskManager.getEpic(4));
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubtaskList());
        System.out.println("----");

        taskManager.getSubtask(5).setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(taskManager.getSubtask(5));
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubtaskList());
        System.out.println("----");


        taskManager.addSubtask(new Subtask("Task7", "description7", TaskStatus.NEW), taskManager.getEpic(4));
        taskManager.removeSubtask(5);
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubtaskList());
        System.out.println("----");

        taskManager.removeAllEpics();
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubtaskList());
        System.out.println("----");

        taskManager.addEpic(new Epic("Task8", "description8", new ArrayList<Subtask>()));
        taskManager.addSubtask(new Subtask("Task9", "description9", TaskStatus.NEW), taskManager.getEpic(8));
        taskManager.addEpic(new Epic("Task10", "description10", new ArrayList<Subtask>()));
        taskManager.addSubtask(new Subtask("Task11", "description11", TaskStatus.NEW), taskManager.getEpic(10));
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubtaskList());
        taskManager.removeAllSubtasks();
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubtaskList());
    }
}
