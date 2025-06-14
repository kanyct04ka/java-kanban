package ru.tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {

    Epic epicLink;

    public Subtask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    /* пока не понятно нужен ли отдельно такой конструктор ?
    public Subtask(
            String name,
            String description,
            TaskStatus status,
            LocalDateTime startTime,
            Duration duration;
    ) {
        super(name, description, status, startTime, duration);
    }
     */

    // СЕТТЕРЫ
    public void setEpicLink(Epic epic) {
        epicLink = epic;
        epic.addLinkedSubtasks(this);
    }

    // ГЕТТЕРЫ
    public Epic getEpicLink() {
        return epicLink;
    }

    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    @Override
    public String toString() {
        String strStart = getStartTime().isEmpty() ? "not filled"
                : getStartTime().get().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String strDuration = getDuration().isEmpty() ? "not filled"
                : getDuration().get().toHoursPart() + ":" + getDuration().get().toMinutesPart();
        return "Subtask{"
                + "id=" + getId()
                + ", name='" + getName() + '\''
                + ", description='" + getDescription() + '\''
                + ", status=" + getStatus()
                + ", epicLink=" + epicLink.getId()
                + ", startTime=" + strStart
                + ", duration=" + strDuration
                + '}';
    }

    @Override
    public String toStringForSaving() {
        String strStart = getStartTime().isEmpty() ? ""
                : getStartTime().get().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String strDuration = getDuration().isEmpty() ? ""
                : String.valueOf(getDuration().get().toMinutes());
        return getId() + ";"
                + "SUBTASK" + ";"
                + getStatus().toString() + ";"
                + getName() + ";"
                + getDescription() + ";"
                + epicLink.getId() + ";"
                + strStart + ";"
                + strDuration;
    }

}