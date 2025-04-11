public class Subtask extends Task {

    Epic epicLink;

    public Subtask(int id, String name, String description, TaskStatus status, Epic epic) {
        super(id, name, description, status);
        epicLink = epic;
        epic.addLinkedSubtasks(this);
    }

    public Epic getEpicLink() {
        return epicLink;
    }
}
