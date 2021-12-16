package servergroupe1.models.databases;

import clientgroupe1.models.databases.exceptions.DatabaseLoadException;
import clientgroupe1.models.databases.exceptions.DatabaseSaveException;

import java.io.*;
import java.util.ArrayList;

public abstract class Database<T> implements Serializable {
    protected ArrayList<T> data;
    protected String path;
    public static final String SAVE_ERROR = "The database could not be saved";
    public static final String LOAD_ERROR = "The database could not be loaded";

    /**
     * Vérifie si le fichier '.serial' existe déjà. La méthode renverra true si il existe, et false sinon.
     * @return boolean
     */
    public boolean fileExists() {
        File file = new File(path);
        return !(file.isDirectory()) && file.exists();
    }

    /**
     * Sauvegarde tous les objects de la liste 'data' dans le fichier '.serial' correspondant.
     */
    public void save() throws DatabaseSaveException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new DatabaseSaveException(e);
        }
    }

    /**
     * Charge les objets du fichier '.serial' dans une liste d'objets 'data'.
     * @throws DatabaseLoadException si il y a une exception
     */
    public void load() throws DatabaseLoadException {
        try {
            File folder = new File("database");
            File file = new File(path);
            if (!folder.mkdirs() && !file.createNewFile()){
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
                    data = (ArrayList<T>) ois.readObject();
                }
            }
        } catch (EOFException ignored) {
        } catch (ClassNotFoundException | IOException e) {
            throw new DatabaseLoadException(e);
        }
    }

    /**
     * Ajoute un objet dans la liste 'data'.
     * @param object objet à ajouter à la liste 'data'
     */
    public void add(T object){
        data.add(object);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setData(ArrayList<T> data) {
        this.data = data;
    }

    public ArrayList<T> getData(){
        return data;
    }
}
