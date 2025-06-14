package ru.tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public class Task {

    private int id;
    private String name;
    private String description;
    private TaskStatus status;
    private LocalDateTime startTime;
    private Duration duration;

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    /* пока не понятно нужен ли отдельно такой конструктор ?
    public Task(
            String name,
            String description,
            TaskStatus status,
            LocalDateTime startTime,
            Duration duration
    ) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }
     */

    // СЕТТЕРЫ
    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(TaskStatus newStatus) {
        this.status = newStatus;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    // ГЕТТЕРЫ
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Optional<LocalDateTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public Optional<Duration> getDuration() {
        return Optional.ofNullable(duration);
    }

    public Optional<LocalDateTime> getEndTime() {
        if (getStartTime().isEmpty() || getDuration().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(startTime.plus(duration));
    }

    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(description);
        result = 31 * result + Objects.hashCode(status);
        return result;
    }

    @Override
    public String toString() {
        String strStart = getStartTime().isEmpty() ? "not filled"
                : getStartTime().get().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String strDuration = getDuration().isEmpty() ? "not filled"
                : getDuration().get().toHoursPart() + ":" + getDuration().get().toMinutesPart();
        return "Task{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", description='" + description + '\''
                + ", status=" + status
                + ", startTime=" + strStart
                + ", duration=" + strDuration
                + '}';
    }

    // формат для сохранения в файл CSV с разделителем ;
    // id;type;status;name;description;epic;start;duration
    // вынесен в отдельный метод т.к. это отдельная задача
    public String toStringForSaving() {
        String strStart = getStartTime().isEmpty() ? ""
                : getStartTime().get().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String strDuration = getDuration().isEmpty() ? ""
                : String.valueOf(getDuration().get().toMinutes());
        return id + ";"
                + "TASK" + ";"
                + status.toString() + ";"
                + name  + ";"
                + description + ";"
                + ";"   // epic заполняется только для subtask
                + strStart + ";"
                + strDuration;
    }

}
