package Client.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public abstract class Node implements Serializable {
    protected String name;
    protected String description;
    protected LocalDate deadline;
    protected LocalDate startDate;
    protected Node parent;


    public Node(Node parent, String name, String description, LocalDate startDate, LocalDate deadline) {
        this.description = description;
        this.name = name;
        this.parent = parent;
        this.startDate = startDate;
        this.deadline = deadline;
    }

    /**
     * Vérifie que la date de début soit avant la date de fin et inversément.
     * @param startDate date de début de projet
     * @param deadline date de fin de projet
     * @return boolean résultat de la vérification
     */
    public static boolean checkLimitDates(LocalDate startDate, LocalDate deadline){
        LocalDate now = LocalDate.now();
        return startDate.isBefore(deadline) && (startDate.isAfter(now) || startDate.isEqual(now));
    }

    /**
     * Vérifie que les dates de début/deadlines des sous-projets ne soient pas antérieures/postérieures
     * aux dates de début/deadlines du projet parent
     * @param parent projet parent
     * @param startDate date de début d'un sous-projet ou d'une tâche
     * @param deadline date de fin d'un sous-projet ou d'une tâche
     * @return boolean résultat de la vérification
     */
    public static boolean checkDatesCoherence(Node parent, LocalDate startDate, LocalDate deadline){
        if (parent == null) {
            return true;
        } else {
            return !parent.getStartDate().isAfter(startDate) && !deadline.isAfter(parent.getDeadline());
        }
    }

    public boolean isProject() {
        return false;
    }

    public boolean isTask(){
        return false;
    }

    public Node getParent(){
        return this.parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername(){
        return null;
    }

    public ArrayList<Node> getNodes() {
        return null;
    }

    public String toString(){
        return name;
    }

    public void setDeadline(LocalDate newDeadline) { this.deadline = newDeadline; }

    public void setStarDate(LocalDate newStartDate) { this.startDate = newStartDate; }

    public LocalDate getDeadline() { return deadline; }

    public LocalDate getStartDate() { return startDate; }
}
