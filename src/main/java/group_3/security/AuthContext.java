package group_3.security;

import group_3.model.Person;

public final class AuthContext {
    private static Person currentUser;

    private AuthContext(){};

    public static void setCurrentUser(Person user){
        currentUser = user;
    }

    public static Person getCurrentUser(){
        return currentUser;
    }

    public static Integer getCurrentUserId(){
        return currentUser != null ? currentUser.getId() : null;
    }

    public static void clear(){
        currentUser = null;
    }
}
