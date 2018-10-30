package utils;

public final class Encryption {
//-----------Tiføj evt. forskellige ecryptions, så man spreder risikoen.
  public static String encryptDecryptXOR(String rawString) {

    // If encryption is enabled in Config.
    if (Config.getEncryption()) {

      // The key is predefined and hidden in code
      // TODO: Create a more complex code and store it somewhere better
      //-----Vi vil gerne have flyttet vores key ned i config.json så den ikke står direkte i koden.
      //-----Ressources bliver ikke committed til git. Der skal derfor også laves referencer i Config klassen.
      //-----For at kunne tilgå "key" i config.json.
      char[] key =  {'C', 'B', 'S'};

      // Stringbuilder enables you to play around with strings and make useful stuff
      // Stringbuilder bygger en string, som er optimeret til at bygge den i chunks. Append = sætte på bagerst.
      StringBuilder thisIsEncrypted = new StringBuilder();

      // TODO: This is where the magic of XOR is happening. Are you able to explain what is going on?
      //------- i = 0 tæller ned for hvert bogstav, dvs. hvert bogstav bliver krypteret.
      // ^ = binær operation. Tager karakterene og laver dem om til deres binær værdier.
      for (int i = 0; i < rawString.length(); i++) {
        thisIsEncrypted.append((char) (rawString.charAt(i) ^ key[i % key.length]));
      }

      // We return the encrypted string
      return thisIsEncrypted.toString();

    } else {
      // We return without having done anything
      return rawString;
    }
  }
}
