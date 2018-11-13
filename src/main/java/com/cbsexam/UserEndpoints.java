package com.cbsexam;

import cache.UserCache;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.google.gson.Gson;
import controllers.DatabaseController;
import controllers.UserController;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.User;
import utils.Encryption;
import utils.Log;
import utils.Hashing;



@Path("user")
public class UserEndpoints {

  //---Tilføjer productchache som objekt så den kan tilgåes, den sættes uden for get-metoden så der ikke oprettes en ny chache hver eneste gang.
  public static UserCache userCache = new UserCache();

  /**
   * @param idUser
   * @return Responses
   */
  @GET
  @Path("/{idUser}")
  public Response getUser(@PathParam("idUser") int idUser) {

    // Use the ID to get the user from the controller.
    User user = UserController.getUser(idUser);

    // TODO: Add Encryption to JSON: Fixed tjek efter
    // Convert the user object to json in order to return the object
    String json = new Gson().toJson(user);

    //---encryption er klassens navn og encryptDecrypt... er metoden er benyttes.
    json = Encryption.encryptDecryptXOR(json);

    // Return the user with the status code 200
    // TODO: What should happen if something breaks down?
    if (user != null) {
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("The user id: " + idUser + " have been found").build();
    }
    else {
      return Response.status(400).type(MediaType.APPLICATION_JSON_TYPE).entity("The user id: " + idUser + " does not exist").build();
    }

  }

  /** @return Responses */
  @GET
  @Path("/")
  public Response getUsers() {

    // Write to log that we are here
    Log.writeLog(this.getClass().getName(), this, "Get all users", 0);

    // Get a list of users
    //---Calling from the chache in order to minimize the times needed to ping the DB
    ArrayList<User> users = userCache.getUsers(false);

    // TODO: Add Encryption to JSON: Fixed tjek efter

    boolean check = true;

    for (User user : users){
      String token = null;
      if (user.getToken() != null && user.getToken().equals(token)){

        check = false;
      }
      //sætter token til null så de ikke bliver udskrevet.
      user.setToken(null);
    }

    // Transfer users to json in order to return it to the user
    String json = new Gson().toJson(users);
    if(check){
      //---encryption er klassens navn og encryptDecrypt... er metoden er benyttes.
      json = Encryption.encryptDecryptXOR(json);
    }

    // Return the users with the status code 200
    return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createUser(String body) {

    // Read the json from body and transfer it to a user class
    User newUser = new Gson().fromJson(body, User.class);

    // Use the controller to add the user
    User createUser = UserController.createUser(newUser);

    // Get the user back with the added ID and return it to the user
    String json = new Gson().toJson(createUser);

    // Return the data to the user
    if (createUser != null) {
      // Sørger for at serveren opdatere efter der laves en ændring.
      userCache.getUsers(true);
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Could not create user").build();
    }
  }

  // TODO: Make the system able to login users and assign them a token to use throughout the system.
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response loginUser(String UserBody) {

    User userlogin = new Gson().fromJson(UserBody, User.class);

    String token = UserController.AuthUser(userlogin);

    if (token != null) {
      return Response.status(200).entity(token).build();
    }
    return Response.status(400).entity("Password or email didn't match").build();
  }

    /*Hashing hashing = new Hashing();
    User userData = new Gson().fromJson(UserBody, User.class);
    User userLogin = UserController.AuthUser(userData.getEmail(), userData.getPassword());
    String json = new Gson().toJson(userLogin);

    if (userData.getEmail().equals(equals(userLogin.getEmail()) && hashing.UserHashWithSalt(userData.getPassword()).equals(userLogin.getPassword()))){
//lav egen algoritme, alle user data plus currenttimemillis ved login
      try {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token = JWT.create()
                .withIssuer("auth0")
                .sign(algorithm);
      } catch (JWTCreationException exception){
        //Invalid Signing configuration / Couldn't convert Claims.
      }

      //return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(token).build();
    }
    // Return a response with status 400 and JSON as type
    return Response.status(400).entity("Email or password was wrong or didn't match").build();
  }*/

  @POST
  @Path("/delete/{delete}/{token}")
  @Consumes(MediaType.APPLICATION_JSON)
  // TODO: Make the system able to delete users: Fixed, tjek efter.
  public Response deleteUser(@PathParam("delete") int idToDelete, @PathParam("token") String token) {

    if (UserController.getUser(idToDelete).getToken().equals(token)){
      UserController.deleteUser(idToDelete);

      //!=0 sikre det altid er et positivt tal.
      if(idToDelete!=0){
        // Sørger for at serveren opdatere efter der laves en ændring.
        userCache.getUsers(true);
        return Response.status(200).entity("The chosen user " + idToDelete + " has now been deleted").build();
      }
    }

    else {
      // Return a response with status 400 and JSON as type
      //---Status 400 betyder at dataen fra klienten til serveren ikke overholdte reglerne og en fejlmeddelse vises.
      return Response.status(400).entity("An error occured in connection to the deletion of a user").build();
    }
    return Response.status(200).entity(" ").build();
  }

  // TODO: Make the system able to update users: Fixed, tjek efter
  @POST
  @Path("update/{update}")
  public Response updateUser(@PathParam("update") int idToUpdate, String body) {

    User updates = new Gson().fromJson(body, User.class);

    User currentuser = UserController.getUser(idToUpdate);

    if (updates.getFirstname() == null){
      updates.setFirstname(currentuser.getFirstname());
    }

    if (updates.getLastname() == null){
      updates.setLastname(currentuser.getLastname());
    }

    if (updates.getEmail() == null){
      updates.setEmail(currentuser.getEmail());
    }

    UserController.updateUSer(idToUpdate, updates);

    if (idToUpdate !=0){
      userCache.getUsers(true);
      return Response.status(200).entity("User with id: " + idToUpdate + " is now updated").build();
    }
    else {
      //400 = fejl, 200 = godt
      // API udsteder endpoints
      //
      Response.status(400).entity("Something went wrong, not able to update user");
    }
    // Return a response with status 200 and JSON as type
    return Response.status(400).entity("Endpoint not implemented yet").build();
  }
}


