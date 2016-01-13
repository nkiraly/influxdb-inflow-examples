package org.influxdb.inflow.examples;

import java.util.concurrent.TimeUnit;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.influxdb.inflow.Client;
import org.influxdb.inflow.Database;
import org.influxdb.inflow.DriverInterface;
import org.influxdb.inflow.DriverUDP;
import org.influxdb.inflow.InflowDatabaseException;
import org.influxdb.inflow.InflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExamplesApp {

  private final static Logger logger = LoggerFactory.getLogger(ExamplesApp.class);

  public static void main(String[] args) throws InflowException, InflowDatabaseException {

    // Initializeation examples
    exampleInitializeClient();

    // Reading Data Examples
    exampleReadData();

    // Writing Data Examples
    exampleWriteData();

    // Writing Data over UDP Examples
    exampleWriteDataOverUDP();
  }
  
  public static void exampleInitializeClient() throws InflowException {
    // Initialize a new client with hostname and port
    Client client = new Client("influxdb.local", 8086, "inflowexample", "inflow011");
    
    // You can also create a client from a URI describing the server
    Client clientFromURI = Client.fromURI("http://inflowexample:inflow011@influxdb.local:8086");
    
    // if you use Database.fromURI(), it will be setup with the database name in the URI
    Database databaseFromURI = Database.fromURI("http://inflowexample:inflow011@influxdb.local:8086/inflow1");
  }
  
  public static void exampleReadData() throws InflowException {
    String influxdbURI = "http://inflowexample:inflow011@influxdb.local:8086/inflow_test";
    
    // To fetch records from InfluxDB, you can:

    // 1) Do a manual query directly on a database:
    logger.info("Creating database object from URI " + influxdbURI);
    Database database = Database.fromURI(influxdbURI);

    // executing a raw query string yields a QueryResult object
    String query1 = "SELECT * FROM test_metric LIMIT 5";
    QueryResult result1 = database.query(query1);

    // get the point values from the QueryResult as an array with the collapser getValuesAsStringArray
    String[] values = result1.getValuesAsStringArray();
    logger.info("got " + values.length + " values back:");
    for( String value : values ) {
      logger.info("value = " + value);
    }

    // 2) Query for data using the QueryBuilder object

    // retrieve points value array with the query builder
    String[] values2 = database.getQueryBuilder()
            .select("cpucount")
            .from("test_metric")
            .limit(2)
            .getQueryResult()
            .getValuesAsStringArray();

    // get the query command from the QueryBuilder
    String query3 = database.getQueryBuilder()
            .select("cpucount")
            .from("test_metric")
            .where("region = 'us-west'")
            .getQueryCommand();
    // Make sure that you enter single quotes when doing a where query on strings.
    // Otherwise InfluxDB will return an empty result.

    // You can get the last executed query from the client static method getLastQuery()
    String lastQuery1 = Client.getLastQuery();
  }
  
  public static void exampleWriteData() throws InflowException, InflowDatabaseException {
    String influxdbURI = "http://inflowexample:inflow011@influxdb.local:8086";
    
    logger.info("Creating client from URI " + influxdbURI);
    Client client = Client.fromURI(influxdbURI);

    // specify to use inflow_test database
    Database database = client.selectDB("inflow_test");
    
    // make sure database exists before trying to write to it
    logger.info("Creating database " + database.getName() + " before writing to it");
    database.create();

    // create some points to write.
    // The name of a measurement and the value are mandatory. Additional fields, tags and a timestamp are optional.
    Point point1 = Point
            .measurement("cpu_load_short")
            .field("value", 0.64)
            .tag("host", "server01")
            .tag("region", "us-west")
            .field("cpucount", 10)
            .time(1452129125, TimeUnit.SECONDS) // 2016-01-07 01:12:05 GMT
            .build();
    // Note: It's possible to add multiple fields (see https://influxdb.com/docs/v0.9/concepts/key_concepts.html) when writing measurements to InfluxDB.

    Point point2 = Point
            .measurement("cpu_load_short")
            .field("value", 0.84)
            .build();
    // Note: No .time() - InfluxDB uses the current time as the default timestamp.
    
    // data points of test_metric measurement, which include optional tags and fields
    Point point3 = Point
            .measurement("test_metric")         // name of the measurement
            .field("value", 0.64)               // the measurement value
            .tag("host", "server01")            // optional tags
            .tag("region", "us-west")
            .field("cpucount", 10)              // optional additional fields
            .time(1452659705, TimeUnit.SECONDS) // 2016-01-13 04:34:05 GMT
            .build();
    
    Point point4 = Point
            .measurement("test_metric")         // name of the measurement
            .field("value", 0.84)               // the measurement value
            .tag("host", "server01")            // optional tags
            .tag("region", "us-west")
            .field("cpucount", 10)              // optional additional fields
            .time(1452659709, TimeUnit.SECONDS) // 2016-01-13 04:34:09 GMT
            .build();

    Point[] points = new Point[]{point1, point2, point3, point4};

    logger.info("Writing " + points.length + " points to database " + database.getName());
    database.writePoints(points);
  }

  public static void exampleWriteDataOverUDP() throws InflowException, InflowDatabaseException {
    String influxdbURI = "http://inflowexample:inflow011@influxdb.local:8086";

    logger.info("Creating client from URI " + influxdbURI);
    Client client = Client.fromURI(influxdbURI);

    logger.info("Specifying UDP driver for client to host " + client.getHost() + " port " + "4444");
    DriverInterface driver = new DriverUDP(client.getHost(), 4444);
    client.setDriver(driver);

    // specify to use inflow_test database
    Database database = client.selectDB("inflow_test");

    // create a point to be recorded as "now"
    Point point5 = Point
            .measurement("test_metric")
            .field("value", 0.85)
            .tag("host", "server01")
            .tag("region", "us-west")
            .tag("proto", "udp")
            .field("cpucount", 10)
            .build();

    // write the point using UDP
    logger.info("Writing point5 to database " + database.getName() + " over UDP");
    database.writePoint(point5);
  }

}
