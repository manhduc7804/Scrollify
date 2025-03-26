package org.scrollify.backend;

import org.scrollify.enums.UserEnum;

public class User {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String customisableId;
    private UserEnum userType; // Change this to UserEnum

    public User(String username, String firstName, String lastName, String email, String phone, String customisableId, boolean isAdmin) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.customisableId = customisableId;
        this.userType = isAdmin ? UserEnum.ADMIN : UserEnum.USER; // Set UserEnum based on isAdmin
    }

    // Getters for user details
    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public UserEnum getUserType() {
        return this.userType;
    }

    public String getCustomisableID() {return this.customisableId;}

    public boolean isAdmin() { return this.userType == UserEnum.ADMIN; }

}
