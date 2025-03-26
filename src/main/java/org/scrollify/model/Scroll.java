package org.scrollify.model;

import java.time.LocalDateTime;

public class Scroll {
    private String scrollId;
    private String scrollName;
    private LocalDateTime dateAdded;
    private String owner;
    private String fileFormat;
    private int version;
    // private String filePath;

    // Constructor
    public Scroll(String scrollId, String scrollName, LocalDateTime dateAdded, String owner, int version) {
        this.scrollId = scrollId;
        this.scrollName = scrollName;
        this.dateAdded = dateAdded;
        this.owner = owner;
        this.version = version;
    }

    public int getVersion() {
        return this.version;
    }

    // Getters and Setters
    public String getScrollId() {
        return scrollId;
    }

    public void setScrollId(String scrollId) {
        this.scrollId = scrollId;
    }

    public String getScrollName() {
        return scrollName;
    }

    public void setScrollName(String scrollName) {
        this.scrollName = scrollName;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }


    @Override
    public String toString() {
        return scrollName; // Display the scroll's name in the list
    }
}
