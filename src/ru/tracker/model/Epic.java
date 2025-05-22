package ru.tracker.model;

import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {

    private final List<Subtask> subtasks;

    public Epic(String name, String description, List<Subtask> subtasks) {
        super(name, description, TaskStatus.NEW);
        this.subtasks = subtasks;
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks);
    }

    public void defineStatus() {
        if (subtasks.isEmpty()) {
            setStatus(TaskStatus.NEW);
            return;
        }

        var isAllSubtasksNew = true;
        var isAllSubtasksDone = true;
        for (Subtask subtask : subtasks) {
            if (!(subtask.getStatus() == TaskStatus.NEW)) {
                isAllSubtasksNew = false;
            }
            if (!(subtask.getStatus() == TaskStatus.DONE)) {
                isAllSubtasksDone = false;
            }
            if (!isAllSubtasksNew && !isAllSubtasksDone) {
                break;
            }
        }

        if (isAllSubtasksNew) {
            setStatus(TaskStatus.NEW);
        } else if (isAllSubtasksDone) {
            setStatus(TaskStatus.DONE);
        } else {
            setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public void addLinkedSubtasks(Subtask subtask) {
        // перед добавлением в списке связанных подзадач
        // удаляем задачу с таким же ИД
        subtasks.removeIf(sb -> sb.getId() == subtask.getId());
        subtasks.add(subtask);
        defineStatus();
    }

    public void removeLinkedSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        defineStatus();
    }

    public void removeAllLinkedSubtask() {
        subtasks.clear();
        defineStatus();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtasks=" + subtasks.size() +
                '}';
    }
}
