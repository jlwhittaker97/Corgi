package com.topbloc.codechallenge.db;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DatabaseManager {
    private static final String jdbcPrefix = "jdbc:sqlite:";
    private static final String dbName = "challenge.db";
    private static String connectionString;
    private static Connection conn;

    static {
        File dbFile = new File(dbName);
        connectionString = jdbcPrefix + dbFile.getAbsolutePath();
    }

    public static void connect() {
        try {
            Connection connection = DriverManager.getConnection(connectionString);
            System.out.println("Connection to SQLite has been established.");
            conn = connection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // Schema function to reset the database if needed - do not change
    public static void resetDatabase() {
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        File dbFile = new File(dbName);
        if (dbFile.exists()) {
            dbFile.delete();
        }
        connectionString = jdbcPrefix + dbFile.getAbsolutePath();
        connect();
        applySchema();
        seedDatabase();
    }

    // Schema function to reset the database if needed - do not change
    private static void applySchema() {
        String itemsSql = "CREATE TABLE IF NOT EXISTS items (\n"
                + "id integer PRIMARY KEY,\n"
                + "name text NOT NULL UNIQUE\n"
                + ");";
        String inventorySql = "CREATE TABLE IF NOT EXISTS inventory (\n"
                + "id integer PRIMARY KEY,\n"
                + "item integer NOT NULL UNIQUE references items(id) ON DELETE CASCADE,\n"
                + "stock integer NOT NULL,\n"
                + "capacity integer NOT NULL\n"
                + ");";
        String distributorSql = "CREATE TABLE IF NOT EXISTS distributors (\n"
                + "id integer PRIMARY KEY,\n"
                + "name text NOT NULL UNIQUE\n"
                + ");";
        String distributorPricesSql = "CREATE TABLE IF NOT EXISTS distributor_prices (\n"
                + "id integer PRIMARY KEY,\n"
                + "distributor integer NOT NULL references distributors(id) ON DELETE CASCADE,\n"
                + "item integer NOT NULL references items(id) ON DELETE CASCADE,\n"
                + "cost float NOT NULL\n" +
                ");";

        try {
            System.out.println("Applying schema");
            conn.createStatement().execute(itemsSql);
            conn.createStatement().execute(inventorySql);
            conn.createStatement().execute(distributorSql);
            conn.createStatement().execute(distributorPricesSql);
            System.out.println("Schema applied");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Schema function to reset the database if needed - do not change
    private static void seedDatabase() {
        String itemsSql = "INSERT INTO items (id, name) VALUES (1, 'Licorice'), (2, 'Good & Plenty'),\n"
            + "(3, 'Smarties'), (4, 'Tootsie Rolls'), (5, 'Necco Wafers'), (6, 'Wax Cola Bottles'), (7, 'Circus Peanuts'), (8, 'Candy Corn'),\n"
            + "(9, 'Twix'), (10, 'Snickers'), (11, 'M&Ms'), (12, 'Skittles'), (13, 'Starburst'), (14, 'Butterfinger'), (15, 'Peach Rings'), (16, 'Gummy Bears'), (17, 'Sour Patch Kids')";
        String inventorySql = "INSERT INTO inventory (item, stock, capacity) VALUES\n"
                + "(1, 22, 25), (2, 4, 20), (3, 15, 25), (4, 30, 50), (5, 14, 15), (6, 8, 10), (7, 10, 10), (8, 30, 40), (9, 17, 70), (10, 43, 65),\n" +
                "(11, 32, 55), (12, 25, 45), (13, 8, 45), (14, 10, 60), (15, 20, 30), (16, 15, 35), (17, 14, 60)";
        String distributorSql = "INSERT INTO distributors (id, name) VALUES (1, 'Candy Corp'), (2, 'The Sweet Suite'), (3, 'Dentists Hate Us')";
        String distributorPricesSql = "INSERT INTO distributor_prices (distributor, item, cost) VALUES \n" +
                "(1, 1, 0.81), (1, 2, 0.46), (1, 3, 0.89), (1, 4, 0.45), (2, 2, 0.18), (2, 3, 0.54), (2, 4, 0.67), (2, 5, 0.25), (2, 6, 0.35), (2, 7, 0.23), (2, 8, 0.41), (2, 9, 0.54),\n" +
                "(2, 10, 0.25), (2, 11, 0.52), (2, 12, 0.07), (2, 13, 0.77), (2, 14, 0.93), (2, 15, 0.11), (2, 16, 0.42), (3, 10, 0.47), (3, 11, 0.84), (3, 12, 0.15), (3, 13, 0.07), (3, 14, 0.97),\n" +
                "(3, 15, 0.39), (3, 16, 0.91), (3, 17, 0.85)";

        try {
            System.out.println("Seeding database");
            conn.createStatement().execute(itemsSql);
            conn.createStatement().execute(inventorySql);
            conn.createStatement().execute(distributorSql);
            conn.createStatement().execute(distributorPricesSql);
            System.out.println("Database seeded");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Helper methods to convert ResultSet to JSON - change if desired, but should not be required
    private static JSONArray convertResultSetToJson(ResultSet rs) throws SQLException{
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        List<String> colNames = IntStream.range(0, columns)
                .mapToObj(i -> {
                    try {
                        return md.getColumnName(i + 1);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());

        JSONArray jsonArray = new JSONArray();
        while (rs.next()) {
            jsonArray.add(convertRowToJson(rs, colNames));
        }
        return jsonArray;
    }

    private static JSONObject convertRowToJson(ResultSet rs, List<String> colNames) throws SQLException {
        JSONObject obj = new JSONObject();
        for (String colName : colNames) {
            obj.put(colName, rs.getObject(colName));
        }

        return obj;
    }

    // Controller functions - add your routes here. getItems is provided as an example
    public static JSONArray getItems() {
        String sql = "SELECT * FROM items";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    // Below are my GET sql logic functions for inventory
    // Get all items in your inventory, including the item name, ID, amount in stock, and total capacity
    public static JSONArray getInventory() {
        String sql = "SELECT items.id, items.name, inventory.stock, inventory.capacity FROM items JOIN inventory ON items.id = inventory.item";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    // Get all items in your inventory that are currently out of stock, including the item name, ID, amount in stock, and total capacity
    public static JSONArray getOutOfStockItems() {
        String sql = "SELECT items.id, items.name, inventory.stock, inventory.capacity FROM items JOIN inventory ON items.id = inventory.item WHERE inventory.stock == 0";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    // Get all items in your inventory that are currently overstocked, including the item name, ID, amount in stock, and total capacity
    public static JSONArray getOverStockedItems() {
        String sql = "SELECT items.id, items.name, inventory.stock, inventory.capacity FROM items JOIN inventory ON items.id = inventory.item WHERE inventory.stock > inventory.capacity";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    // Get all items in your inventory that are currently low on stock (<35%), including the item name, ID, amount in stock, and total capacity
    public static JSONArray getLowOnStockItems() {
        String sql = "SELECT items.id, items.name, inventory.stock, inventory.capacity FROM items JOIN inventory ON items.id = inventory.item WHERE inventory.stock < inventory.capacity * .35";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    // Get, when given an ID, the item name, ID, amount in stock, and total capacity of that item
    public static JSONArray getItemById(String itemID) {
        String sql = "SELECT items.id, items.name, inventory.stock, inventory.capacity FROM items JOIN inventory ON items.id = inventory.item WHERE items.id =" + itemID;
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Below are my GET sql logic functions for distributors
    // Get all distributors, including the id and name
    public static JSONArray getAllDistributors() {
        String sql = "SELECT * FROM distributors";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    // Get, given a distributors ID, the items distributed by a given distributor, including the item name, ID, and cost
    public static JSONArray getDistributorsItems(String distID) {
        String sql = "SELECT items.id AS item_id, items.name AS item_name, distributor_prices.cost FROM distributor_prices JOIN items ON distributor_prices.item = items.id WHERE distributor_prices.distributor =" + distID + ")";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    // Get, given an item ID, all offerings from all distributors for that item, including the distributor name, ID, and cost
    public static JSONArray getOfferingsForItem(String itemID) {
        String sql = "SELECT distributors.id AS dist_id, distributors.name AS dist_name, distributor_prices.cost FROM distributor_prices JOIN distributors ON distributor_prices.distributor = distributors.id WHERE distributor_prices.item = (SELECT id FROM items WHERE id ="+ itemID + ")";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    // Get the cheapest price for restocking an item at a given quantity from all distributors
    public static JSONArray getCheapestRestockPrice(String itemID, String quantity) {
        if(itemID != null && quantity != null) {
            String sql = "SELECT distributor, item, cost*" + quantity + " AS restockPrice FROM distributor_prices WHERE item =" + itemID + " ORDER BY restockPrice limit 1";
            try {
                ResultSet set = conn.createStatement().executeQuery(sql);
                return convertResultSetToJson(set);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return null;
            }
        }
        else {
            System.out.println("ERROR: Please provide itemID and quantity");
            return null;
        }
    }

    // Below are my put/post/delete sql functions for inventory
    // Add a new item to the database
    public static String addItem(String newID, String newName) {
        if (newID != null && newName != null) {
            String sql = "INSERT INTO items (id, name) VALUES (\"" + newID + "\",\"" + newName + "\")";
            try {
                conn.createStatement().execute(sql);
                return "Added the following (ID, NAME) pair to items: (" + newID + ", " + newName + ")!";
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return null;
            }
        } else {
            return "ERROR: Missing (ID, NAME) Pair!";
        }
    }
    // Add a new item to your inventory
    public static String addToInventory(String newItem, String newStock, String newCapacity) {
        if (newItem != null && newStock != null && newCapacity != null) {
            String sql = "INSERT INTO inventory (item, stock, capacity) VALUES (\"" + newItem + "\",\"" + newStock + "\",\"" + newCapacity + "\")";
            try {
                conn.createStatement().execute(sql);
                return "Added the following (ITEM, STOCK, CAPACITY) to inventory: (" + newItem + ", " + newStock + ", " + newCapacity + ")!";
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return null;
            }
        } else {
            return "ERROR: Missing (ITEM, STOCK, CAPACITY)!";
        }
    }
    // Modify an existing item in your inventory
    public static String modifyInventory(String modItem, String modStock, String modCapacity) {
        if (modItem != null) {
            String sql = "UPDATE inventory SET";
            if (modStock != null && modCapacity != null) {
                sql += " stock =" + modStock + ", capacity =" + modCapacity;
            } else if (modStock != null) {
                sql += " stock = " + modStock;
            } else if (modCapacity != null) {
                sql += " capacity =" + modCapacity;
            } else {
                return "ERROR: Please provide a stock or capacity to modify!";
            }
            sql += " WHERE item = " + modItem;
            try {
                conn.createStatement().execute(sql);
                return "Modified the item " + modItem + "'s stock: " + modStock + " and/or capacity: " + modCapacity;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return null;
            }
        } else {
            return "ERROR: Missing an item to modify!";
        }
    }
    // Delete an existing item from your inventory
    public static String deleteItem(String itemID) {
        if (itemID != null) {
            String sql = "DELETE FROM inventory WHERE item =" + itemID;
            try {
                conn.createStatement().execute(sql);
                return "Deleted item " + itemID + " from inventory.";
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return null;
            }
        } else {
            return "ERROR: Please provide an item ID!";
        }
    }

    // Below are my put/post/delete sql functions for distributors
    // Add a distributor
    public static String addDistributor(String newID, String newName) {
        if (newID != null && newName != null) {
            String sql = "INSERT INTO distributors (id, name) VALUES (\"" + newID + "\",\"" + newName + "\")";
            try {
                conn.createStatement().execute(sql);
                return "Added the following (ID, NAME) pair to distributors: (" + newID + ", " + newName + ")!";
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return null;
            }
        } else {
            return "ERROR: Missing (ID, NAME) Pair!";
        }
    }
    // Add items to a distributor's catalog (including the cost)
    public static String addToCatalog(String distID, String itemID, String price) {
        if (distID != null && itemID != null && price != null) {
            String sql = "INSERT INTO distributor_prices (distributor, item, cost) VALUES (\"" + distID + "\",\"" + itemID + "\",\"" + price + "\")";
            try {
                conn.createStatement().execute(sql);
                return "Added the following (distributor, item, cost) to distributor prices!: (" + distID + ", " + itemID + ", " + price + ")!";
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return null;
            }
        } else {
            return "ERROR: Missing (distID, itemID, price)!";
        }
    }
    // Modify the price of an item in a distributor's catalog
    public static String modifyPrice(String distID, String itemID, String modPrice) {
        if (distID != null && itemID != null && modPrice != null) {
            String sql = "UPDATE distributor_prices SET cost = " + modPrice + " WHERE distributor =" + distID + " AND item =" + itemID;
            try {
                conn.createStatement().execute(sql);
                return "Modified the following (distributor, item): (" + distID + ", " + itemID + ") to the following price: " + modPrice;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return null;
            }
        } else {
            return "ERROR: Please provide the following to modify price (DIST_ID, ITEM_ID, PRICE)!";
        }
    }
    // Delete an existing item from your inventory
    public static String deleteDistributor(String distID) {
        if (distID != null) {
            String sql = "DELETE FROM distributors WHERE id =" + distID;
            try {
                conn.createStatement().execute(sql);
                return "Deleted distrbutor " + distID + " from distributor database.";
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return null;
            }
        } else {
            return "ERROR: Please provide a distributor ID!";
        }
    }
}
