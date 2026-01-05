package group_3.model;

import group_3.model.enums.Role;

import java.time.LocalDate;

public class Admin extends Person {

    public Admin(){
        this.role = Role.EVENT_ADMIN;
    }

    public Admin(int id, String username, String passwordHash, String fullname, LocalDate dateOfBirth, String contactInformation, Role role){
        super(id, username, passwordHash, fullname, dateOfBirth, contactInformation, role);
    }
}
