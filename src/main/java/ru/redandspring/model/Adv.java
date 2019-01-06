package ru.redandspring.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class Adv implements Serializable {

    public long id;
    public String text;
    public String link;

    public Adv(long id, String text, String link) {
        this.id = id;
        this.text = text;
        this.link = link;
    }

    public Adv(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Adv adv = (Adv) o;

        return new EqualsBuilder()
                .append(id, adv.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Adv{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
