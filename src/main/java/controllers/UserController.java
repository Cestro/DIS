package controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import cache.UserCache;
import com.cbsexam.UserEndpoints;
import model.User;
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
    // TODO: Hash the user password before saving it.: Fixed
    int userID = dbCon.insert(
            "INSERT INTO user(first_name, last_name, password, email, created_at) VALUES('"
                    + user.getFirstname()
                    + "', '"
                    + user.getLastname()
                    + "', '"
                    + hashing.UserHashWithSalt(user.getPassword()) //---benytter hashing metoden inden password hentes.
                    + "', '"
                    + user.getEmail()
                    + "', "
                    + user.getCreatedTime()
                    + ")");

    if (userID != 0) {
      //Update the userID of the user before returning
      user.setId(userID);
    } else {
      // Return null if user has not been inserted into database
      return null;
    }

    // Return user
    return user;
  }

  public static void deleteUser(int id) {
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    String sql = "DELETE FROM user WHERE id=" + id;
    dbCon.deleteUser(sql);
  }

  public static void updateUser(int id, User updates) {
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    User currentuser = getUser(id);

      if (updates.getFirstname() == null) {
        updates.setFirstname(currentuser.getFirstname());
      }

      if (updates.getLastname() == null) {
        updates.setLastname(currentuser.getLastname());
      }

      if (updates.getEmail() == null) {
        updates.setEmail(currentuser.getEmail());
      }


    String sql = "Update user set first_name = ' " + updates.getFirstname() + "', last_name ='" + updates.getLastname() + "', Email =' " + updates.getEmail() + "' Where id = " + id;
    dbCon.updateUser(sql);
  }

  public static String AuthUser(User userlogin) {

    ArrayList<User> allTheUsers = UserEndpoints.userCache.getUsers(false);//cache istedet

    for (User user : allTheUsers) {
      if (user.getEmail().equals(userlogin.getEmail())){

        //sætter password med det nye hash
        String password = hashing.UserHashWithSalt(userlogin.getPassword());

        if (password.equals(user.getPassword())){

          //sætter salt til at være den specifikke brugers created_at
          hashing.setLoginSalt(String.valueOf(user.getCreatedTime()));

          //hasher
          hashing.LoginHashWithSalt(String.valueOf(user.getCreatedTime()));

          //Sætter token til at være lig de viste værdier sat sammen.
          String token = user.getFirstname()+user.getLastname()+user.getEmail();

          //kryptere den ovenstående streng
          token = hashing.LoginHashWithSalt(token);

          updateToken(user.id,token);

          UserEndpoints.userCache.getUsers(true);

          return token;

        }
      }
    }
    return null;
  }

  public static void updateToken (int id, String token){

    Log.writeLog(UserController.class.getName(), token, "updating token in database", 0);

    if (dbCon == null){
      dbCon = new DatabaseController();
    }

    String sql = "UPDATE dis.user SET token = '" + token + "' where id= " + id;

    dbCon.voidToDB(sql);
  }
}
