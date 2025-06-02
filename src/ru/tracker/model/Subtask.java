package ru.tracker.model;

public class Subtask extends Task {

    Epic epicLink;

    public Subtask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public void setEpicLink(Epic epic) {
        epicLink = epic;
        epic.addLinkedSubtasks(this);
    }

    public Epic getEpicLink() {
        return epicLink;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", epicLink=" + epicLink.getId() +
                '}';
    }

    @Override
    public String toStringForSaving() {
        return getId() + ";" + "SUBTASK" + ";" + getName()  +
                ";" + getStatus().toString() + ";" + getDescription() + ";" + epicLink.getId();
    }
}