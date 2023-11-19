package data_access;

import entity.User;
import entity.UserFactory;
import use_case.connect.ConnectDataAccessInterface;
import use_case.other_profile.OtherProfileDataAccessInterface;
import use_case.self_profile.SelfProfileDataAccessInterface;
import use_case.signup.SignupUserAccessInterface;
import use_case.login.LoginUserAccessInterface;
import use_case.user_list.UserListDataAccessInterface;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class UserDataAccessObject implements SignupUserAccessInterface, UserListDataAccessInterface,
        SelfProfileDataAccessInterface, LoginUserAccessInterface, OtherProfileDataAccessInterface, ConnectDataAccessInterface {

    private final String filePath;

    private final Map<String, String> usersDataMap;//should we refactor the name to "authentication"? since this is username+password

    //TODO:(Kelly)This needs to have String:User map.( For latter extensions and for Usecase Data Access).I would need to
    // Add one for now. Please see if changes needed for this added accounts attribute.

    private final Map<String, User> usernameUserMap;


    private final ArrayList<User> allUsers = new ArrayList<>();

    public UserDataAccessObject(UserFactory userFactory) throws IOException {

        System.out.println("UserDataAccess constructor reached");
        this.filePath = "src/data_access/users.csv";

        this.usersDataMap = new HashMap<>();
        this.usernameUserMap = new HashMap<>();

        loadUsersFromFile();
    }

    private void loadUsersFromFile() throws IOException {
        List<String> lines;
        System.out.println("UserLoadFromFile is called");

        // Clear the list before loading users from file
        allUsers.clear();

        try {
            lines = Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Error reading users file", e);
        }

        for (String line: lines) {
            String[] p = line.split(",");
            if (p.length >= 5) {
                usersDataMap.put(p[0], p[1]);
                //(Kelly): populating also the Account attribute here
                ArrayList<String> courses = turnCoursesIntoList(p[4]);
                //TODO: Please edit the creation of this courses ArrayList after changing the File format, including
                // email, courses info in the file.
                //create user object from the information stored in the file, put them in the allUsers list.
                User user = UserFactory.creatUser(p[0],p[1],p[2],p[3],courses);
                allUsers.add(user);
            }
        }
        System.out.println(allUsers.toString());
    }

    private ArrayList<String> turnCoursesIntoList(String s) {
        //TODO:(ye) please implement this. This should be able to turn the String we get from the file into an ArrayList
        // of Strings. each String is a course name.
        return new ArrayList<>(Arrays.asList(s.split("\\+")));
    }

    private User turnUserIntoUserObject(String line) {
        String[] data = line.split(",");
        return new User(data[0], data[1], data[2], data[3], turnCoursesIntoList(data[4]));
    }


    @Override
    // TODO: 11/8/2023 use api?
    public boolean checkValidEmail(String username) {
        return true;
    }

    @Override
    public boolean checkValidUsername(String username) {
        return !usersDataMap.containsKey(username);
    }

    @Override
    public boolean existsByName(String username) {
        return usersDataMap.containsKey(username);
    }

    @Override
    public User get(String username) {
        return usernameUserMap.get(username);
    }

    @Override
    public void save(User user) {
        usersDataMap.put(user.getName(), user.getPassword());

        String userData = user + "\n";
//        String userData = user.getName() + "," + user.getPassword()+ "," + user.getId()+ "," + user.getEmail()+ ","+
//                storeCourses(user)+"\n"; // Format the user data for CSV
        try {
            Files.write(Paths.get(filePath), userData.getBytes(), StandardOpenOption.APPEND); // Append to the CSV file
        } catch (IOException e) {
            throw new RuntimeException("Error writing to users file", e);
        }
    }

    public String storeCourses(User u) {
        List<String> courses = u.getCourses();
        //complete this method to save the user u's courses, which is an List of 5 Strings into Strings seperated by /
        return String.join("/", courses);


    }

    // by Kelly: for UserList Interactor.
    @Override
    public ArrayList<User> getAllUsers() {// by Kelly: for UserList Interactor.

        return allUsers;
    }

    // Get the current user object.
    @Override
    public User getUser(String username) {
        if (usersDataMap.containsKey(username)) {
            for (User i : getAllUsers()) {
                if (Objects.equals(i.getName(), username)) {
                    return i;
                }
            }
        }
        return null;
    }

    @Override
    public User getUserByUsername(String username) throws NoSuchElementException {
        ArrayList<User> all = getAllUsers();

        for(User u : all) {
            if(u.getName().equals(username)) {
                System.out.println("(DAO) get user by user name: the name is " + u.getName());
                return u;
            }
        }

        System.out.println("(DAO) found no user with this name");
        throw new NoSuchElementException("NoSuchUser");
    }



}

