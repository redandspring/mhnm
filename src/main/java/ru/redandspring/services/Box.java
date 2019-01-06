package ru.redandspring.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.redandspring.model.Adv;
import ru.redandspring.model.Config;
import ru.redandspring.model.Storage;

import java.io.*;
import java.util.function.Consumer;

public class Box {

    private static final Logger log = LoggerFactory.getLogger(Box.class);

    private Storage storage;

    private static volatile Box instance;
    private static final String FILE_NAME = Config.HOME_TEMP + "storage.out";

    void saveStorage(){
        try(final OutputStream fos = new FileOutputStream(FILE_NAME)) {
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(storage);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            log.error("saveStorage(): dont save storage ", e);
        }
    }

    void add(final Adv adv){
        storage.getPosts().push(adv);
    }

    void forEach(final Consumer<? super Adv> consumer) {
        storage.getPosts().forEach(consumer);
    }

    public boolean remove(final long id) {
        return storage.getPosts().remove(new Adv(id));
    }

    int size() {
        return storage.getPosts().size();
    }

    long getStorageCurrentId(){
        return storage.getCurrentId();
    }

    long getStorageLastSuccessId(){
        return storage.getLastSuccessId();
    }

    void setStorageCurrentId(final long id){
        storage.setCurrentId(id);
    }

    void setStorageLastSuccessId(final long id){
        storage.setLastSuccessId(id);
    }

    private Box() {
        initStorage();
    }

    private void initStorage(){
        try(final InputStream fis = new FileInputStream(FILE_NAME)) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            storage = (Storage) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            storage = Storage.BLANK_STORAGE;
        }
    }

    public static Box getInstance() {
        if (instance == null) {
            synchronized (Box.class) {
                if (instance == null) {
                    instance = new Box();
                }
            }
        }
        return instance;
    }
}
