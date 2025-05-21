package ru.tracker.controller;

import ru.tracker.model.Task;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> historyNodes;

    private Node historyHead;
    private Node historyTail;


    public InMemoryHistoryManager() {
        this.historyNodes = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        var taskId = task.getId();

        if (historyNodes.containsKey(taskId)) {
            removeNode(historyNodes.get(taskId));
        }

        var newNode = new Node(task);
        linkLast(newNode);
        historyNodes.put(taskId, newNode);
    }

    @Override
    public void remove(int id) {
        if (historyNodes.containsKey(id)) {
            removeNode(historyNodes.get(id));
            historyNodes.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> list = new ArrayList<>();

        if (historyHead != null) {
            list.add(historyHead.getTask());
            var next = historyHead.getNext();
            while (next != null) {
                list.add(next.getTask());
                next = next.getNext();
            }
        }
        return list;
    }

    private void linkLast(Node node) {
        if (historyHead == null) {
            historyHead = node;
        } else {
            historyTail.setNext(node);
            node.setPrev(historyTail);
        }
        historyTail = node;
    }

    private void removeNode(Node node) {
        if (node == historyHead) {
            historyHead = node.getNext();
        }

        if (node == historyTail) {
            historyTail = node.getPrev();
            if (historyTail != null) {
                historyTail.setNext(null);
            }
        }

        // условие ниже будет отрабатывать только для нод между
        var prev = node.getPrev();
        var next = node.getNext();
        if (prev != null && next != null) {
            prev.setNext(next);
            next.setPrev(prev);
        }
    }

    class Node {
        private Node prev;
        private Node next;
        private final Task task;

        Node(Task task) {
            this.task = task;
        }

        void setPrev(Node prev) {
            this.prev = prev;
        }

        void setNext(Node next) {
            this.next = next;
        }

        Node getPrev() {
            return prev;
        }

        Node getNext() {
            return next;
        }

        Task getTask() {
            return task;
        }
    }
}

