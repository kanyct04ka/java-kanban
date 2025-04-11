import java.util.ArrayList;

public class Epic extends Task{

    ArrayList<Subtask> subtasks;

    public Epic(int id, String name, String description, TaskStatus status, ArrayList<Subtask> subtasks) {
        super(id, name, description, status);
        this.subtasks = subtasks;
    }

    ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    void addLinkedSubtasks(Subtask subtask) {
        // перед добавлением в списке связанных подзадач
        // удаляем задачу с таким же ИД
        subtasks.removeIf(sb -> sb.getId() == subtask.getId());
        subtasks.add(subtask);
    }

    void removeLinkedSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    void removeAllLinkedSubtask() {
        subtasks.clear();
    }
}
