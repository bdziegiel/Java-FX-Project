package kanban;

import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Serialization {
    public void saveAction(ListView<Task> toDoList, ListView<Task> inProgressList, ListView<Task> doneList){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save lists");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"),
                new FileChooser.ExtensionFilter("Bin files (*.bin)", "*.bin")
        );

            File file = fileChooser.showSaveDialog(null);

            if (file.getName().contains("bin")) saveToBin(file, toDoList, inProgressList, doneList);
            else if (file.getName().contains("csv")) saveToCSV(file, toDoList, inProgressList, doneList);

    }

    private void saveToBin(File file, ListView<Task> toDoList, ListView<Task> inProgressList, ListView<Task> doneList){
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            ArrayList<Task> td=new ArrayList<>(toDoList.getItems());
            outputStream.writeObject(td);
            ArrayList<Task>pr= new ArrayList<>(inProgressList.getItems());
            outputStream.writeObject(new ArrayList<>(pr));
            ArrayList<Task> dl=new ArrayList<>(doneList.getItems());
            outputStream.writeObject(new ArrayList<>(dl));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static final String CSV_SEPARATOR = ",";
    private void saveToCSV(File file, ListView<Task> toDoList, ListView<Task> inProgressList, ListView<Task> doneList){
        try
        {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));

            for (Task task : toDoList.getItems()) {
                bufferedWriter.append(csvWriter(task,0));
                bufferedWriter.newLine();
            }

            for (Task task : inProgressList.getItems()) {
                bufferedWriter.append(csvWriter(task,1));
                bufferedWriter.newLine();
            }

            for (Task task : doneList.getItems()) {
                bufferedWriter.append(csvWriter(task,2));
                bufferedWriter.newLine();
            }

            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private String csvWriter(Task task, int nr){
        String oneLine = (task.getTitle().trim().length() == 0 ? "" : task.getTitle()) +
                CSV_SEPARATOR +
                (task.getPriority() == null ? "" : task.getPriority()) +
                CSV_SEPARATOR +
                (task.getLocalDate() == null ? "" : task.getLocalDate()) +
                CSV_SEPARATOR +
                (task.getDescription().trim().length() == 0 ? "" : task.getDescription())+
                CSV_SEPARATOR + nr;
        return oneLine;
    }

    public void openAction(ListView<Task> toDoList, ListView<Task> inProgressList, ListView<Task> doneList){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open lists");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"),
                new FileChooser.ExtensionFilter("Bin files (*.bin)", "*.bin")
        );

        File file = fileChooser.showOpenDialog(null);

        if(file.getName().contains("bin")) openFromBin(file,toDoList,inProgressList,doneList);
        else if (file.getName().contains("csv")) openFromCSV(file,toDoList,inProgressList,doneList);
    }

    private void openFromBin(File file, ListView<Task> toDoList, ListView<Task> inProgressList, ListView<Task> doneList){

        FileInputStream fileInputStream;
        ObjectInputStream objectInputStream;
        ArrayList<Task> td;
        ArrayList<Task>pr;
        ArrayList<Task> dl;

        try {
            fileInputStream = new FileInputStream(file);
            objectInputStream = new ObjectInputStream(fileInputStream);
            td= (ArrayList<Task>) objectInputStream.readObject();
            pr= (ArrayList<Task>)objectInputStream.readObject();
            dl= (ArrayList<Task>)objectInputStream.readObject();

            toDoList.getItems().addAll(td);
            inProgressList.getItems().addAll(pr);
            doneList.getItems().addAll(dl);

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

    }

    private void openFromCSV(File file, ListView<Task> toDoList, ListView<Task> inProgressList, ListView<Task> doneList){

        Path pathToFile = Paths.get(file.getPath());
        try(BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.UTF_8))
        {
            String line = br.readLine();
            while (line != null)
            {
                String[] items = line.split(",");
                if(items.length<5){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setContentText("Niekompletny plik!");
                    alert.showAndWait();
                    break;
                }
                Task task = new Task(items);
                if(items[4].equals("0")) toDoList.getItems().add(task);
                else if(items[4].equals("1")) inProgressList.getItems().add(task);
                else if(items[4].equals("2")) doneList.getItems().add(task);
                line = br.readLine();
            }
        } catch (IOException e) {e.printStackTrace();}
    }
}
