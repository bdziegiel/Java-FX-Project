package kanban;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Task implements Serializable {
    public String title;
    public TaskPriority priority;
    public LocalDate localDate;
    public String description;

    public Task(){
        this.title="";
        this.description="";
        priority = TaskPriority.LOW;
        localDate = (new Date()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public Task(String[]items) {
        this.title = items[0];
        if(items[1].equals("HIGH")) this.priority = TaskPriority.HIGH;
        if(items[1].equals("MEDIUM")) this.priority = TaskPriority.MEDIUM;
        if(items[1].equals("LOW")) this.priority = TaskPriority.LOW;
        if(!items[2].equals("")) this.localDate= localDate.parse(items[2]);
        this.description=items[3];
    }

    public String getTitle(){
        return this.title;
    }
    public TaskPriority getPriority(){return this.priority;}
    public LocalDate getLocalDate(){return this.localDate;}
    public String getDescription(){
        return this.description;
    }
}
