package ru.redandspring.model;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

public class Storage implements Serializable {

    public static final Storage BLANK_STORAGE = new Storage(Config.START_ID);

    private final Deque<Adv> posts = new ArrayDeque<>();
    private volatile long currentId;
    private volatile long lastSuccessId;

    private Storage(long currentId) {
        this.currentId = currentId;
        this.lastSuccessId = currentId;
    }

    public Deque<Adv> getPosts() {
        return posts;
    }

    public long getCurrentId() {
        return currentId;
    }

    public long getLastSuccessId() {
        return lastSuccessId;
    }

    public void setCurrentId(long currentId) {
        this.currentId = currentId;
    }

    public void setLastSuccessId(long lastSuccessId) {
        this.lastSuccessId = lastSuccessId;
    }

    @Override
    public String toString() {
        return "Storage{" +
                "posts=" + posts +
                ", currentId=" + currentId +
                ", lastSuccessId=" + lastSuccessId +
                '}';
    }
}
