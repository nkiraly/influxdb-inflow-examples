package org.influxdb.inflow.examples;

import org.influxdb.inflow.Client;
import org.influxdb.inflow.Database;
import org.influxdb.inflow.InflowException;

public class ExamplesApp {
  
  public static String SERVER_HOSTNAME = "bludgeon";
  public static int SERVER_PORT = 8086;
  public static int SERVER_UDP_PORT = 4444;
  public static String DATABASE_NAME = "inflow1";
  public static String DATABASE_URI = "http://bludgeon:8086/inflow1";

  public static void main(String[] args) throws InflowException {
    
    // Initializeation examples
    exampleInitializeClient();
    
  }
  
  public static void exampleInitializeClient() throws InflowException {
    // Initialize a new client with hostname and port
    Client client = new Client(SERVER_HOSTNAME, SERVER_PORT);
    
    // You can also create a client from a URI describing the server or database
    Client clientFromURI = Client.fromURI(DATABASE_URI);
    
    // if you use Database.fromURI(), it will be setup with the database name in the URI
    Database databaseFromURI = Database.fromURI(DATABASE_URI);
  }

}
