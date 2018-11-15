package controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import cache.OrderCache;
import cache.OrderCache;
import com.mysql.cj.jdbc.ha.LoadBalancedAutoCommitInterceptor;
import model.Address;
import model.LineItem;
import model.Order;
import model.User;
import utils.Log;

import javax.xml.crypto.Data;
import javax.xml.ws.Response;

public class OrderController {

  private static DatabaseController dbCon;

  public OrderController() {
    dbCon = new DatabaseController();
  }

  public static Order getOrder(int id) {

    // check for connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Build SQL string to query
    String sql = "SELECT * FROM orders where id=" + id;

    // Do the query in the database and create an empty object for the results
    ResultSet rs = dbCon.query(sql);
    Order order = null;

    try {
      if (rs.next()) {

        // Perhaps we could optimize things a bit here and get rid of nested queries.
        User user = UserController.getUser(rs.getInt("user_id"));
        ArrayList<LineItem> lineItems = LineItemController.getLineItemsForOrder(rs.getInt("id"));
        Address billingAddress = AddressController.getAddress(rs.getInt("billing_address_id"));
        Address shippingAddress = AddressController.getAddress(rs.getInt("shipping_address_id"));

        // Create an object instance of order from the database dataa
        order =
            new Order(
                rs.getInt("id"),
                user,
                lineItems,
                billingAddress,
                shippingAddress,
                rs.getFloat("order_total"),
                rs.getLong("created_at"),
                rs.getLong("updated_at"));

        // Returns the build order
        return order;
      } else {
        System.out.println("No order found");
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Returns null
    return order;
  }

  /**
   * Get all orders in database
   *
   * @return
   */
  public static ArrayList<Order> getOrders() {

    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    String sql = "SELECT orders.id, orders.user_id, orders.billing_address_id, orders.shipping_address_id, \n" +
            "user.first_name, user.last_name, user.email, line_item.id, line_item.quantity, line_item.price,\n" +
            "product.id, product.product_name, product.sku, product.price, product.description, product.stock, \n" +
            "product.created_at, address.zipcode, orders.order_total, orders.created_at, orders.updated_at FROM orders \n" +
            "LEFT JOIN user ON orders.user_id=user.id \n" +
            "LEFT JOIN address AS billing ON orders.billing_address_id=billing.id \n" +
            "LEFT JOIN address AS shipping ON orders.shipping_address_id=shipping.id";

    /*SELECT
    *,
    a.city as billing_city,
    a1.city as shipping_city
    FROM orders o
    JOIN user ON orders.user_id=user.id
    JOIN address AS billing ON orders.billing_address_id=a.id
    JOIN address AS shipping ON orders.shipping_address_id=a1.id
    where orders.id = 2;
    */

    ResultSet rs = dbCon.query(sql);
    ArrayList<Order> orders = new ArrayList<Order>();

    try {
      while(rs.next()) {

        //TODO: Perhaps we could optimize things a bit here and get rid of nested queries.
        User user = new User(
                rs.getInt("user_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getLong("created_at"),
                rs.getString("token"));
        ArrayList<LineItem> lineItems = LineItemController.getLineItemsForOrder(rs.getInt("id"));
        Address billingAddress = new Address(
                rs.getInt("address_id"),
                rs.getString("name"),
                rs.getString("street_address"),
                rs.getString("city"),
                rs.getString("zipcode")
        );
        Address shippingAddress = new Address(
                rs.getInt("address_id"),
                rs.getString("name"),
                rs.getString("street_address"),
                rs.getString("city"),
                rs.getString("zipcode")
        );

        // Create an order from the database data
        Order order =
            new Order(
                rs.getInt("id"),
                user,
                lineItems,
                billingAddress,
                shippingAddress,
                rs.getFloat("order_total"),
                rs.getLong("created_at"),
                rs.getLong("updated_at"));

        // Add order to our list
        orders.add(order);

      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // return the orders
    return orders;
  }

  public static Order createOrder(Order order) {

    // Write in log that we've reach this step
    Log.writeLog(OrderController.class.getName(), order, "Actually creating a order in DB", 0);

    // Set creation and updated time for order.
    order.setCreatedAt(System.currentTimeMillis() / 1000L);
    order.setUpdatedAt(System.currentTimeMillis() / 1000L);

    // Check for DB Connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    try {
        //For at sikre der ikke tilføjes noget til databasen før vi er sikre på alt er verificeret.
      DatabaseController.getConnection().setAutoCommit(false);
      //---LoadBalancedAutoCommitInterceptor?
      // Save addresses to database and save them back to initial order instance
      order.setBillingAddress(AddressController.createAddress(order.getBillingAddress()));
      order.setShippingAddress(AddressController.createAddress(order.getShippingAddress()));

      // Save the user to the database and save them back to initial order instance
      order.setCustomer(UserController.createUser(order.getCustomer()));

      // TODO: Enable transactions in order for us to not save the order if somethings fails for some of the other inserts.: Fixed

      // Insert the product in the DB
      int orderID = dbCon.insert(
              "INSERT INTO orders(user_id, billing_address_id, shipping_address_id, order_total, created_at, updated_at) VALUES("
                      + order.getCustomer().getId()
                      + ", "
                      + order.getBillingAddress().getId()
                      + ", "
                      + order.getShippingAddress().getId()
                      + ", "
                      + order.calculateOrderTotal()
                      + ", "
                      + order.getCreatedAt()
                      + ", "
                      + order.getUpdatedAt()
                      + ")");

      if (orderID != 0) {
        //Update the productid of the product before returning
        order.setId(orderID);
      }

      //Smider en fejl for at teste om transaktions virker
      /*if (true) {

        throw new SQLException();
      }*/

      // Create an empty list in order to go trough items and then save them back with ID
      ArrayList<LineItem> items = new ArrayList<LineItem>();

      // Save line items to database
      for(LineItem item : order.getLineItems()){
        item = LineItemController.createLineItem(item, order.getId());
        items.add(item);
        //Fordi autoCommit er slået fra er det nødvendigt at tvinge et commit for at gemme det til DB.
        DatabaseController.getConnection().commit();
      }

      order.setLineItems(items);

      Log.writeLog(OrderController.class.getName(), order, "The order has been created", 0);

    } catch (SQLException e) {
      try {
        DatabaseController.getConnection().rollback();
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
      Log.writeLog(OrderController.class.getName(), order, "Something went wrong", 0);

    } finally {


      try {
        DatabaseController.getConnection().setAutoCommit(true);
      } catch (SQLException e2) {
        e2.printStackTrace();
      }

    }

    // Return order
    return order;
  }
}