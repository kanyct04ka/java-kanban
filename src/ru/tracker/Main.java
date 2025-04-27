package ru.tracker;

import ru.tracker.controller.InMemoryTaskManager;
import ru.tracker.model.TaskStatus;
import ru.tracker.model.Task;
import ru.tracker.model.Epic;
import ru.tracker.model.Subtask;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        // Тестирование ЗАДАЧ
        inMemoryTaskManager.addTask(new Task("Task1", "description1", TaskStatus.NEW));
        inMemoryTaskManager.addTask(new Task("Task2", "description2", TaskStatus.NEW));
        inMemoryTaskManager.addTask(new Task("Task3", "description3", TaskStatus.NEW));
        System.out.println(inMemoryTaskManager.getTaskList());
        System.out.println("----");

        inMemoryTaskManager.getTask(1).setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateTask(inMemoryTaskManager.getTask(1));
        System.out.println(inMemoryTaskManager.getTask(1));
        System.out.println("----");

        inMemoryTaskManager.removeTask(1);
        System.out.println(inMemoryTaskManager.getTaskList());
        System.out.println("----");

        inMemoryTaskManager.removeAllTasks();
        System.out.println(inMemoryTaskManager.getTaskList());
        System.out.println("----");


        // Тестирование ЭПИКОВ и ПОДЗАДАЧ
        inMemoryTaskManager.addEpic(new Epic("Task4", "description4", new ArrayList<Subtask>()));
        System.out.println(inMemoryTaskManager.getEpicList());
        System.out.println(inMemoryTaskManager.getSubtaskList());
        System.out.println("----");

        inMemoryTaskManager.addSubtask(new Subtask("Task5", "description5", TaskStatus.NEW), inMemoryTaskManager.getEpic(4));
        inMemoryTaskManager.addSubtask(new Subtask("Task6", "description6", TaskStatus.NEW), inMemoryTaskManager.getEpic(4));
        System.out.println(inMemoryTaskManager.getEpicList());
        System.out.println(inMemoryTaskManager.getSubtaskList());
        System.out.println("----");

        inMemoryTaskManager.getSubtask(5).setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateSubtask(inMemoryTaskManager.getSubtask(5));
        System.out.println(inMemoryTaskManager.getEpicList());
        System.out.println(inMemoryTaskManager.getSubtaskList());
        System.out.println("----");


        inMemoryTaskManager.addSubtask(new Subtask("Task7", "description7", TaskStatus.NEW), inMemoryTaskManager.getEpic(4));
        inMemoryTaskManager.removeSubtask(5);
        System.out.println(inMemoryTaskManager.getEpicList());
        System.out.println(inMemoryTaskManager.getSubtaskList());
        System.out.println("----");

        inMemoryTaskManager.removeAllEpics();
        System.out.println(inMemoryTaskManager.getEpicList());
        System.out.println(inMemoryTaskManager.getSubtaskList());
        System.out.println("----");

        inMemoryTaskManager.addEpic(new Epic("Task8", "description8", new ArrayList<Subtask>()));
        inMemoryTaskManager.addSubtask(new Subtask("Task9", "description9", TaskStatus.NEW), inMemoryTaskManager.getEpic(8));
        inMemoryTaskManager.addEpic(new Epic("Task10", "description10", new ArrayList<Subtask>()));
        inMemoryTaskManager.addSubtask(new Subtask("Task11", "description11", TaskStatus.NEW), inMemoryTaskManager.getEpic(10));
        System.out.println(inMemoryTaskManager.getEpicList());
        System.out.println(inMemoryTaskManager.getSubtaskList());
        inMemoryTaskManager.removeAllSubtasks();
        System.out.println(inMemoryTaskManager.getEpicList());
        System.out.println(inMemoryTaskManager.getSubtaskList());
    }
}
