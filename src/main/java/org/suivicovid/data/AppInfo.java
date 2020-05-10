package org.suivicovid.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AppInfo {
    @Id
    private long id;

    private int appVersion;


    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean networked = false;

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @return the appVersion
     */
    public int getAppVersion() {
        return appVersion;
    }

    /**
     * @param appVersion the appVersion to set
     */
    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

    /**
     * @return the networked
     */
    public boolean isNetworked() {
        return networked;
    }

    /**
     * @param networked the networked to set
     */
    public void setNetworked(boolean networked) {
        this.networked = networked;
    }
}