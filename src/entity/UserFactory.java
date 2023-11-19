package entity;

import java.util.ArrayList;

/**
 * This entity is the implementation that create Users for the program.
 */

public class UserFactory {
    public static User createUser(String username,
                                  String password, String id,
                                  String email,
                                  ArrayList<String> courses){

        User user = new User(username, password, id, email, courses);
        System.out.println("(User factory) created: user "+ user.getName()+" with courses :"+user.getCourses());
        return user;

    }

}

