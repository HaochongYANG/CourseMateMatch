package data_access;

import entity.User;
import entity.UserFactory;
import use_case.profile.ProfileDataAccessInterface;
import use_case.signup.SignupUserAccessInterface;
import use_case.user_list.UserListDataAccessInterface;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class UserDataAccessObject implements SignupUserAccessInterface, UserListDataAccessInterface, ProfileDataAccessInterface {

    private final String filePath;
    private final Map<String, String> usersDataMap;//should we refactor the name to "authentication"? since this is username+password

    //TODO:(Kelly)This needs to have String:User map.( For latter extensions and for Usecase Data Access).I would need to
    // Add one for now. Please see if changes needed for this added accounts attribute.

    private final ArrayList<User> allUsers = new ArrayList<>();

    public UserDataAccessObject() throws IOException {
        this.filePath = "./src/main/data_access/users.csv";
        this.usersDataMap = new HashMap<>();

        loadUsersFromFile();
    }

    private void loadUsersFromFile() throws IOException {
        List<String> lines;

        try {
            lines = Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Error reading users file", e);
        }

        for (String line: lines) {
            String[] p = line.split(",");
            if (p.length >= 2) {
                usersDataMap.put(p[0], p[1]);
                //(Kelly): populating also the Account attribute here
                ArrayList<String> courses = turnCoursesIntoList(p[4]);
                //TODO: Please edit the creation of this courses Arraylist after changing the File format, including
                // email, courses info in the file.
                //create user object from the information stored in the file, put them in the allUsers list.
                User user = UserFactory.creatUser(p[0],p[1],p[2],p[3],courses);
                allUsers.add(user);
            }

        }

    }

    private ArrayList<String> turnCoursesIntoList(String s) {
        //TODO:(ye) please implement this. This should be able to turn the String we get from the file into an ArrayList
        // of Strings. each String is a course name.
        return null;
    }

    @Override
    // TODO: 11/8/2023 use api?
    public boolean checkValidEmail(String username) {
        return false;
    }

    @Override
    public boolean checkValidUsername(String username) {
        return !usersDataMap.containsKey(username);
    }

    @Override
    public void save(User user) {
        usersDataMap.put(user.getName(), user.getPassword());
        // Step 2: Append the new user's data to the CSV file
        String userData = user.getName() + "," + user.getPassword()+ "," + user.getId()+ "," + user.storeCourses()+
                "," + user.getEmail()+ "\n"; // Format the user data for CSV

        try {
            Files.write(Paths.get(filePath), userData.getBytes(), StandardOpenOption.APPEND); // Append to the CSV file
        } catch (IOException e) {
            throw new RuntimeException("Error writing to users file", e);
        }
    }

    // by Kelly: for UserList Interactor.
    @Override
    public ArrayList<User> getAllUsers() {// by Kelly: for UserList Interactor.

        return allUsers;
    }

    // Get the current user object.
    @Override
    public User getUser(String username) {
        User user = null;
        if (checkValidUsername(username)) {
            for (User i : allUsers) {
                if (Objects.equals(i.getName(), username)) {
                    user = i;
                }
            }
        }
        return user;
    }
}

