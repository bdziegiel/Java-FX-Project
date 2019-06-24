package kanban.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import kanban.Task;
import javafx.stage.Stage;
import kanban.TaskPriority;

public class TaskController {

    @FXML private Button taskButton;
    @FXML public TextField titleField;
    @FXML public DatePicker expDate;
    @FXML public TextArea descField;
    @FXML public ComboBox<TaskPriority> choiceBox;
    private ListView<Task> toDoList;
    private Task item;

    @FXML public void initialize() {
        choiceBox.getItems().addAll(TaskPriority.LOW, TaskPriority.MEDIUM, TaskPriority.HIGH);
    }

    @FXML void addButton(){
        Task task = new Task();
        task.title = titleField.getText();
        task.priority = choiceBox.getValue();
        task.localDate = expDate.getValue();
        task.description = descField.getText();
        toDoList.getItems().add(task);
        Stage stage = (Stage)taskButton.getScene().getWindow();
        stage.close();
    }

    void setToDoList(ListView<Task> toDoList) {
        this.toDoList = toDoList;
    }

    void setToDoList(ListView<Task> toDoList, Task item) {
        this.toDoList = toDoList;
        this.item = item;
        taskButton.setText("Edit");
        taskButton.setOnAction(actionEvent -> editTask());
    }

    private void editTask(){
        item.title = titleField.getText();
        item.localDate = expDate.getValue();
        item.priority = choiceBox.getValue();
        item.description = descField.getText();
        toDoList.refresh();
        toDoList.getItems().add(item);
        Stage stage = (Stage)taskButton.getScene().getWindow();
        stage.close();
    }
}
