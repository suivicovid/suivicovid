package org.suivicovid.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class Deleted implements Serializable {
    private static final long serialVersionUID = 5559188075282050726L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String uuid;
    
    @Transient
    boolean fromSync;

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    public boolean isFromSync() {
        return this.fromSync;
    }

    public void setFromSync() {
        this.fromSync = true;
    }

    public void clearId() {
        id = 0;
    }
}