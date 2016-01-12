package org.influxdb.inflow.examples;

import java.util.concurrent.TimeUnit;
import org.influxdb.dto.Point;
import org.influxdb.inflow.Client;
import org.influxdb.inflow.Database;
import org.influxdb.inflow.InflowException;

public class ExamplesApp {

  public static void main(String[] args) throws InflowException {
    
    // Initializeation examples
    exampleInitializeClient();
   
    // Reading Data Examples
    exampleReadData();
    
    // Writing Data Examples
    exampleWriteData();
  }
  
  public static void exampleInitializeClient() throws InflowException {
    // Initialize a new client with hostname and port
    Client client = new Client("influxdb.local", 8086, "inflowexample", "inflow011");
    
    // You can also create a client from a URI describing the server
    Client clientFromURI = Client.fromURI("http://inflowexample:inflow011@bludgeon:8086");
    
    // if you use Database.fromURI(), it will be setup with the database name in the URI
    Database databaseFromURI = Database.fromURI("http://inflowexample:inflow011@bludgeon:8086/inflow1");
  }
  
  public static void exampleWriteData() throws InflowException {
    Client client = Client.fromURI("http://inflowexample:inflow011@bludgeon:8086");

    // specify to use inflow_test database
    Database database = client.selectDB("inflow_test");

    // create some points to write.
    // The name of a measurement and the value are mandatory. Additional fields, tags and a timestamp are optional.
    Point point1 = Point
            .measurement("cpu_load_short")
            .field("value", 0.64)
            .tag("host", "server01")
            .tag("region", "us-west")
            .field("cpucount", 10)
            .time(1435222310, TimeUnit.SECONDS) // 2015-06-25 08:51:50 GMT
            .build();
    // Note: It's possible to add multiple fields (see https://influxdb.com/docs/v0.9/concepts/key_concepts.html) when writing measurements to InfluxDB.

    Point point2 = Point
            .measurement("cpu_load_short")
            .field("value", 0.84)
            .build();
    // Note: No .time() - InfluxDB uses the current time as the default timestamp.

    Point[] points = new Point[]{point1, point2};

    database.writePoints(points);
  }

}
