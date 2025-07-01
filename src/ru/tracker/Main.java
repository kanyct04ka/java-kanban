package ru.tracker;

import ru.tracker.api.HttpTaskServer;
import ru.tracker.controller.Managers;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            HttpTaskServer taskServer = new HttpTaskServer(Managers.getDefault());
            taskServer.start();
        } catch (IOException e) {
            System.out.println("Ошибка старта приложения: \n");
            e.printStackTrace();
        }
    }
}