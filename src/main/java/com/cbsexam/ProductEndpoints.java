package com.cbsexam;

import cache.ProductCache;
import com.google.gson.Gson;
import controllers.ProductController;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.Product;
import utils.Encryption;

@Path("product")
public class ProductEndpoints {

  /**
   * @param idProduct
   * @return Responses
   */
  @GET
  @Path("/{idProduct}")
  public Response getProduct(@PathParam("idProduct") int idProduct) {

    // Call our controller-layer in order to get the order from the DB
    Product product = ProductController.getProduct(idProduct);

    // TODO: Add Encryption to JSON: Fixed tjek efter
    // We convert the java object to json with GSON library imported in Maven
    String json = new Gson().toJson(product);

    //--------encryption er klassens navn og encryptDecrypt... er metoden er benyttes.
    json = Encryption.encryptDecryptXOR(json);

    // Return a response with status 200 and JSON as type
    return Response.status(200).type(MediaType.TEXT_PLAIN_TYPE).entity(json).build();
  }

  //---Tilføjer productchache som objekt så den kan tilgåes, den sættes uden for get-metoden så der ikke oprettes en ny chache hver eneste gang.
  private static ProductCache productCache = new ProductCache();

  /** @return Responses */
  @GET
  @Path("/")
  public Response getProducts() {

    // Call our controller-layer in order to get the order from the DB
    //--- ændret til productcache istedet for productcontroller så arraylisten hentes fra chachen
    ArrayList<Product> products = productCache.getProducts(false);

    // TODO: Add Encryption to JSON: Fixed tjek efter
    // We convert the java object to json with GSON library imported in Maven
    String json = new Gson().toJson(products);

    //--------encryption er klassens navn og encryptDecrypt... er metoden er benyttes.
    json = Encryption.encryptDecryptXOR(json);

    // Return a response with status 200 and JSON as type
    return Response.status(200).type(MediaType.TEXT_PLAIN_TYPE).entity(json).build();
  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createProduct(String body) {

    productCache.getProducts(true);

    // Read the json from body and transfer it to a product class
    Product newProduct = new Gson().fromJson(body, Product.class);

    // Use the controller to add the user
    Product createdProduct = ProductController.createProduct(newProduct);

    // Get the user back with the added ID and return it to the user
    String json = new Gson().toJson(createdProduct);

    // Return the data to the user
    if (createdProduct != null) {
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Could not create user").build();
    }
  }
}
