package ru.tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class Epic extends Task {

    private final List<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(String name, String description, List<Subtask> subtasks) {
        super(name, description, TaskStatus.NEW);
        this.subtasks = subtasks;
        defineStartEndAndDuration();
    }

    // СЕТТЕРЫ
    private void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    // ГЕТТЕРЫ
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks);
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
        return Optional.ofNullable(endTime);
    }

    // МЕТОДЫ ЛОГИКИ ЭПИКА
    private void defineStatus() {
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

    private void defineStartEndAndDuration() {
    // для эпика считаю правильней определить по сабтаскам startTime и endTime
    // а duration считать как весь отрезок времени от начала до окончания эпика

    // так как по требованиям ФЗ-8 задачи не могут пересекаться
    // то достаточно отсортировать задачи с заполненными датами
        List<Subtask> subtasksWithTime = subtasks.stream()
                .filter(subtask -> subtask.getStartTime().isPresent()
                        && subtask.getDuration().isPresent())
                .sorted(Comparator.comparing(s -> s.getStartTime().get()))
                .toList();

        if (!subtasksWithTime.isEmpty()) {
            setStartTime(subtasksWithTime.getFirst().getStartTime().get());
            setEndTime(subtasksWithTime.getLast().getEndTime().get());
            setDuration(Duration.between(getStartTime().get(), endTime));
        } else {
            setStartTime(null);
            setEndTime(null);
            setDuration(null);
        }
    }

    public void addLinkedSubtasks(Subtask subtask) {
        // перед добавлением в списке связанных подзадач
        // удаляем задачу с таким же ИД
        subtasks.removeIf(sb -> sb.getId() == subtask.getId());
        subtasks.add(subtask);
        defineStatus();
        defineStartEndAndDuration();
    }

    public void removeLinkedSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        defineStatus();
        defineStartEndAndDuration();
    }

    public void removeAllLinkedSubtask() {
        subtasks.clear();
        defineStatus();
        defineStartEndAndDuration();
    }

    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    @Override
    public String toString() {
        String strStart = getStartTime().isEmpty() ? "not filled"
                : getStartTime().get().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String strDuration = getDuration().isEmpty() ? "not filled"
                : getDuration().get().toHoursPart() + ":" + getDuration().get().toMinutesPart();
        return "Epic{"
                + "id=" + getId()
                + ", name='" + getName() + '\''
                + ", description='" + getDescription() + '\''
                + ", status=" + getStatus()
                + ", subtasks=" + subtasks.size()
                + ", startTime=" + strStart
                + ", duration=" + strDuration
                + '}';
    }

    // формат для сохранения в файл CSV с разделителем ;
    // id;type;status;name;description;epic;start;duration
    @Override
    public String toStringForSaving() {
        String strStart = getStartTime().isEmpty() ? ""
                : getStartTime().get().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String strDuration = getDuration().isEmpty() ? ""
                : String.valueOf(getDuration().get().toMinutes());
        return getId() + ";"
                + "EPIC" + ";"
                + getStatus().toString() + ";"
                + getName() + ";"
                + getDescription() + ";"
                + ";"   // epic заполняется только для subtask
                + strStart + ";"
                + strDuration;
    }

}
