package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.util.encoders.Hex;
import model.User;
import utils.Config;

public final class Hashing {

  //Opretter salt
  private String salt = Config.getSaltKey();

  // TODO: You should add a salt and make this secure: Fixed tjek efter
  public static String md5(String rawString) {
    try {

      // We load the hashing algoritm we wish to use.
      MessageDigest md = MessageDigest.getInstance("MD5");

      // We convert to byte array
      byte[] byteArray = md.digest(rawString.getBytes());

      // Initialize a string buffer
      StringBuffer sb = new StringBuffer();

      // Run through byteArray one element at a time and append the value to our stringBuffer
      for (int i = 0; i < byteArray.length; ++i) {
        sb.append(Integer.toHexString((byteArray[i] & 0xFF) | 0x100).substring(1, 3));
      }

      //Convert back to a single string and return
      return sb.toString();

    } catch (java.security.NoSuchAlgorithmException e) {

      //If somethings breaks
      System.out.println("Could not hash string");
    }

    return null;
  }

  // TODO: You should add a salt and make this secure: Fixed tjek efter
  public static String sha(String rawString) {
    try {
      // We load the hashing algoritm we wish to use.
      MessageDigest digest = MessageDigest.getInstance("SHA-256");

      // We convert to byte array
      byte[] hash = digest.digest(rawString.getBytes(StandardCharsets.UTF_8));

      // We create the hashed string
      String sha256hex = new String(Hex.encode(hash));

      // And return the string
      return sha256hex;

    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    return rawString;
  }
  //--- Der oprettes en metode for hvert objekt i databasen, bortset fra address og line_item
  //--- Opretter metode til at kombinere hashing og salt, den returnere så md5(salt), dvs. hasher salt vores nye varible
  public String OrderHashWithSalt(String str) {
    String salt = str + this.salt;
    return md5(salt);
  }
  //--- Opretter metode til at kombinere hashing og salt, den returnere så md5(salt), dvs. hasher salt vores nye varible
  public String ProductHashWithSalt(String str){
    String salt = str+this.salt;
    return md5(salt);
  }
  //--- Opretter metode til at kombinere hashing og salt, den returnere så md5(salt), dvs. hasher salt vores nye varible
  public String UserHashWithSalt(String str){
    String salt = str+this.salt;
    return md5(salt);
  }

}