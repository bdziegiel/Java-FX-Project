package kanban.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import kanban.Main;
import kanban.Serialization;
import kanban.Task;
import kanban.TaskPriority;

import java.io.*;

public class MainController {

    @FXML private ListView<Task> toDoList;
    @FXML private ListView<Task> inProgressList;
    @FXML private ListView<Task> doneList;
    private Task dragTask;
    private int dragItemIndex;
    private ContextMenu contextMenu = new ContextMenu();

    private Serialization ser = new Serialization();
    @FXML public void saveAction(){
        ser.saveAction(toDoList,inProgressList,doneList);
    }
    @FXML public void openAction(){
        ser.openAction(toDoList,inProgressList,doneList);
    }

    @FXML void closeWindow(){ Platform.exit();}

    @FXML void aboutAuthor(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Author");
        alert.setHeaderText(null);
        alert.setContentText("Barbara Dzięgiel");
        alert.showAndWait();
    }

    @FXML public void newTask() throws Exception {
        Stage taskStage = new Stage();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/task_view.fxml"));
        AnchorPane layout = loader.load();
        taskStage.setTitle("New task");
        Scene scene = new Scene(layout,400,400);
        taskStage.setScene(scene);
        taskStage.setResizable(false);

        TaskController taskController = loader.getController();
        taskController.setToDoList(this.toDoList);
        taskStage.showAndWait();
    }

    @FXML public void initialize(){
        MenuItem item = new MenuItem("Edit");
        MenuItem item2 = new MenuItem("Delete");
        contextMenu.getItems().addAll(item,item2);
        toDoList.setContextMenu(contextMenu);
        inProgressList.setContextMenu(contextMenu);
        doneList.setContextMenu(contextMenu);

        setMyTooltip(toDoList);
        setMyTooltip(inProgressList);
        setMyTooltip(doneList);

        setCellFactory(toDoList);
        setCellFactory(inProgressList);
        setCellFactory(doneList);
    }

    private void setMyTooltip(ListView<Task> list){
        list.setCellFactory(new Callback<ListView<Task>, ListCell<Task>>() {
            @Override
            public ListCell<Task> call(ListView<Task> list) {
                return new MyTooltip();
            }
        });
    }
    private void setCellFactory(ListView<Task> list){

        list.setCellFactory(param -> new ListCell<Task>() {
        @Override protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);

                if(item!=null) {
                    if (empty || item.getTitle() == null) {
                        setText(null);
                    } else {
                        setText(item.getTitle());
                    }

                    if(item.getPriority() == TaskPriority.LOW){
                        String style = "-fx-background-color: #c1ffbb;";
                        setStyle(style);
                    }
                    else if(item.getPriority() == TaskPriority.MEDIUM){
                        String style = "-fx-background-color: rgb(255,251,185);";
                        setStyle(style);
                    }
                    else if(item.getPriority()==TaskPriority.HIGH){
                        String style = "-fx-background-color: rgb(255,211,207);";
                        setStyle(style);
                    }
                }
                else {
                    setText(null);
                    setStyle(null);
                }
            }
        });
    }

    @FXML public void handleDragOver(DragEvent dragEvent) {
        dragEvent.acceptTransferModes(TransferMode.ANY);
        dragEvent.consume();
    }

    @FXML public void handleDragDropped(DragEvent dragEvent) {
        if (dragTask == null)
            return;
        ListView<Task> listToDrop = (ListView<Task>) dragEvent.getSource();
        ListView<Task> listToRemoveFrom = (ListView<Task>) dragEvent.getGestureSource();
        if (listToDrop == listToRemoveFrom)
            return;
        listToDrop.getItems().add(dragTask);
        listToRemoveFrom.getItems().remove(dragItemIndex);
        dragTask = null;
        dragEvent.consume();
    }

    @FXML public void handleDragDetected(MouseEvent mouseEvent) {

        ListView<Task> listToTakeFrom = (ListView<Task>) mouseEvent.getSource();
        dragTask = listToTakeFrom.getSelectionModel().getSelectedItem();
        if (dragTask == null) {
            mouseEvent.consume();
            return;
        }

        dragItemIndex = listToTakeFrom.getSelectionModel().getSelectedIndex();
        Dragboard dragboard = listToTakeFrom.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putString("Task");
        dragboard.setContent(content);
        mouseEvent.consume();
    }

    @FXML public void contextMenuRequested (ContextMenuEvent contextMenuEvent) {
        ListView<Task> list = (ListView<Task>) contextMenuEvent.getSource();

        contextMenu.getItems().get(1).setOnAction((event -> {
            list.getItems().remove(list.getSelectionModel().getSelectedIndex());
        }));

        contextMenu.getItems().get(0).setOnAction((event -> {
            Task selectedItem = list.getSelectionModel().getSelectedItem();

            Stage editStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/task_view.fxml"));

            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Parent parent = loader.getRoot();
            editStage.setTitle("Edit task");
            editStage.setScene(new Scene(parent, 400, 400));

            TaskController taskController = loader.getController();
            if(taskController!=null) {
                taskController.titleField.setText(selectedItem.title);
                taskController.choiceBox.getSelectionModel().select(selectedItem.priority);
                taskController.expDate.setValue(selectedItem.localDate);
                taskController.descField.setText(selectedItem.description);
                taskController.setToDoList(list, selectedItem);

                list.getItems().remove(list.getSelectionModel().getSelectedIndex());
                editStage.show();
            }
        }));
    }

    static class MyTooltip extends ListCell<Task> {
        final Tooltip tip = new Tooltip();
        @Override
        public void updateItem(Task item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
            } else {
                    setText(item.title);
                }
                tip.setText(item.description);
                tip.setWrapText(true);
                setTooltip(tip);
            }
        }
    }


