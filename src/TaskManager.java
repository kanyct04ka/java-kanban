import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TaskManager {
    private static int taskCount = 0;

    private HashMap<Integer, Task> taskList;
    private HashMap<Integer, Epic> epicList;
    private HashMap<Integer, Subtask> subtaskList;

    public TaskManager() {
        taskList = new HashMap<>();
        epicList = new HashMap<>();
        subtaskList = new HashMap<>();
    }

    private static int generateTaskId() {
        taskCount++;
        return taskCount;
    }

    // МЕТОДЫ ДЛЯ РАБОТЫ С ЗАДАЧАМИ
    public Task createTask(String name, String description) {
        var id = generateTaskId();
        Task newTask = new Task(id, name, description, TaskStatus.NEW);
        taskList.put(id, newTask);
        return newTask;
    }

    public Task getTask(int id) {
        return taskList.get(id);
    }

    public void updateTask(Task task) {
        taskList.put(task.getId(), task);
    }

    public Collection<Task> getTaskList() {
        return taskList.values();
    }

    public void removeTask(int id) {
        taskList.remove(id);
    }

    public void removeAllTasks() {
        taskList.clear();
    }

    // МЕТОДЫ ДЛЯ РАБОТЫ С ЭПИКАМИ
    public Epic createEpic(String name, String description) {
        var id = generateTaskId();
        Epic newEpic = new Epic(id, name, description, TaskStatus.NEW, new ArrayList<Subtask>());
        epicList.put(id, newEpic);
        return newEpic;
    }

    public Task getEpic(int id) {
        return epicList.get(id);
    }

    public void updateEpic(Epic epic) {
        epicList.put(epic.getId(), epic);
    }

    public Collection<Epic> getEpicList() {
        return epicList.values();
    }

    public void removeEpic(int id) {
        epicList.remove(id);
    }

    public void removeAllEpics() {
        epicList.clear();
    }

    private void defineEpicStatus(Epic epic) {
        TaskStatus newEpicStatus;
        ArrayList<Subtask> epicSubtasks = epic.getSubtasks();

        if (epicSubtasks.isEmpty()) {
            newEpicStatus = TaskStatus.NEW;
        } else {
            boolean isAllSubtasksNew = true;
            boolean isAllSubtasksDone = true;
            for (Subtask subtask : epicSubtasks) {
                if (!(subtask.getStatus() == TaskStatus.NEW)) {
                    isAllSubtasksNew = false;
                }
                if (!(subtask.getStatus() == TaskStatus.DONE)) {
                    isAllSubtasksDone = false;
                }
                if (!isAllSubtasksNew || !isAllSubtasksDone) {
                    break;
                }
            }

            if (isAllSubtasksNew) {
                newEpicStatus = TaskStatus.NEW;
            } else if (isAllSubtasksDone) {
                newEpicStatus = TaskStatus.DONE;
            } else {
                newEpicStatus = TaskStatus.IN_PROGRESS;
            }
        }

        Epic updatedEpic = new Epic(epic.getId(), epic.getName(), epic.getDescription(), newEpicStatus, epicSubtasks);
        epicList.put(epic.getId(), updatedEpic);
    }

    // МЕТОДЫ ДЛЯ РАБОТЫ С ПОДЗАДАЧАМИ
    public Subtask createSubtask(String name, String description, Epic epicLink) {
        var id = generateTaskId();
        Subtask newSubtask = new Subtask(id, name, description, TaskStatus.NEW, epicLink);
        subtaskList.put(id, newSubtask);
        defineEpicStatus(epicLink);  // имеет смысл только если эпик закрыт, но вопрос можно ли добавлять подзадачи в закрытый эпик?
        return newSubtask;
    }

    public Subtask getSubtask(int id) {
        return subtaskList.get(id);
    }

    public void updateSubtask(Subtask subtask) {
        subtaskList.put(subtask.getId(), subtask);
        defineEpicStatus(subtask.getEpicLink());
    }

    public Collection<Subtask> getSubtaskList() {
        return subtaskList.values();
    }

    public void removeSubtask(int id) {
        var subtask = subtaskList.get(id);
        subtaskList.remove(id);
        if (subtask != null) {
            var epic = subtask.getEpicLink();
            epic.removeLinkedSubtask(subtask);
            defineEpicStatus(epic);
        }
    }

    public void removeAllSubtasks() {
        subtaskList.clear();
        for (Epic epic : epicList.values()) {
            epic.removeAllLinkedSubtask();
            defineEpicStatus(epic);
        }
    }
}
