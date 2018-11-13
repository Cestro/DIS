package controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import cache.UserCache;
import model.User;
import org.apache.solr.common.util.Hash;
import utils.Hashing;
import utils.Log;

public class UserController {

  public static Hashing hashing = new Hashing(); //---tilføjer nyt objekt som giver adgang til hashing klassen

  private static DatabaseController dbCon;

  public UserController() {
    dbCon = new DatabaseController();
  }

  public static User getUser(int id) {

    // Check for connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Build the query for DB
    String sql = "SELECT * FROM user where id=" + id;

    // Actually do the query
    ResultSet rs = dbCon.query(sql);
    User user = null;

    try {
      // Get first object, since we only have one
      if (rs.next()) {
        user =
                new User(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getLong("created_at"),
                        rs.getString("token"));

        // return the create object
        return user;
      } else {
        System.out.println("No user found");
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Return null
    return user;
  }

  /**
   * Get all users in database
   *
   * @return
   */
  public static ArrayList<User> getUsers() {

    // Check for DB connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Build SQL
    String sql = "SELECT * FROM user";

    // Do the query and initialyze an empty list for use if we don't get results
    ResultSet rs = dbCon.query(sql);
    ArrayList<User> users = new ArrayList<User>();

    try {
      // Loop through DB Data
      while (rs.next()) {
        User user =
                new User(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getLong("created_at"),
                        rs.getString("token"));

        // Add element to list
        users.add(user);
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Return the list of users
    return users;
  }

  public static User createUser(User user) {



    // Write in log that we've reach this step
    Log.writeLog(UserController.class.getName(), user, "Actually creating a user in DB", 0);

    // Set creation time for user.
    user.setCreatedTime(System.currentTimeMillis() / 1000L);

    // Check for DB Connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Insert the user in the DB
    // TODO: Hash the user password before saving it.: Fixed tjek efter
    int userID = dbCon.insert(
            "INSERT INTO user(first_name, last_name, password, email, created_at) VALUES('"
                    + user.getFirstname()
                    + "', '"
                    + user.getLastname()
                    + "', '"
                    + hashing.LoginHashWithSalt(user.getPassword()) //---benytter hashing metoden inden password hentes.
                    + "', '"
                    + user.getEmail()
                    + "', "
                    + user.getCreatedTime()
                    + ")");

    if (userID != 0) {
      //Update the userid of the user before returning
      user.setId(userID);
    } else {
      // Return null if user has not been inserted into database
      return null;
    }

    // Return user
    return user;
  }

  //hvorfor void?
  public static void deleteUser(int id) {
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    String sql = "DELETE FROM user WHERE id=" + id;
    dbCon.deleteUser(sql);
  }

  public static void updateUSer(int id, User updates) {
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    String sql = "Update user set first_name = ' " + updates.getFirstname() + "', last_name ='" + updates.getLastname() + "', Email =' " + updates.getEmail() + "' Where id = " + id;
    dbCon.updateUser(sql);
  }

  public static String AuthUser(User userlogin) {

    ArrayList<User> allTheUsers = UserController.getUsers();//cache istedet

    for (User user : allTheUsers) {
      if (user.getEmail().equals(userlogin.getEmail())){

        hashing.LoginHashWithSalt(String.valueOf(user.getCreatedTime()));

        String password = hashing.LoginHashWithSalt(userlogin.getPassword());

        if (password.equals(user.getPassword())){

          hashing.setLoginSalt(String.valueOf(System.currentTimeMillis() / 100L));

          String token = user.getFirstname()+user.getLastname()+user.getEmail();

          token = hashing.LoginHashWithSalt(token);

          updateToken(user.id,token);

          return token;

        }
      }
    }
    return null;

/*    if (dbCon == null) {
      dbCon = new DatabaseController();
    }
    // Build SQL
    String sql = "SELECT * FROM user where='" + email + "' AND password'" + password + "'";

    // Do the query and initialyze an empty list for use if we don't get results
    ResultSet rs = dbCon.query(sql);
    //ArrayList<User> users = new ArrayList<User>();
    User user = null;

    try {
      if (rs.next()) {
        user = new User(
                rs.getInt("id"),
                rs.getString("First_name"),
                rs.getString("Last_name"),
                rs.getString("password"),
                rs.getString("email"));
        return user;
      } else {
        System.out.println("No user found");
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
    */
  }

  /*public static String login(User userLogin) {

    ArrayList<User> alltheusers = UserController.getUsers(); //brug cache i stedet
    for (User user : alltheusers) {
      if (user.getEmail().equals(userLogin.getEmail()))

        hashing.setLoginSalt(String.valueOf(user.getCreatedTime()));

        String password = hashing.UserHashWithSalt(userLogin.getPassword());

      if (password.equals(user.getPassword())) {

        //String token = ;
        hashing.setLoginSalt(String.valueOf(System.currentTimeMillis() / 100L));

         = hashing.UserHashWithSalt(token);

        //updateToken(user.id,token);

        return token;
      }
    }
    return null;
  }*/

  public static void updateToken (int id, String token){

    Log.writeLog(UserController.class.getName(), token, "updating token in database", 0);

    if (dbCon == null){
      dbCon = new DatabaseController();
    }

    String sql = "UPDATE dis.user SET token = '" + token + "' where id= " + id;

    dbCon.voidToDB(sql);

  }
}
