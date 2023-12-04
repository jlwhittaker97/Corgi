package com.topbloc.codechallenge;

import com.topbloc.codechallenge.db.DatabaseManager;
import org.json.simple.JSONArray;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        DatabaseManager.connect();
        // Don't change this - required for GET and POST requests with the header 'content-type'
        options("/*",
                (req, res) -> {
                    res.header("Access-Control-Allow-Headers", "content-type");
                    res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
                    return "OK";
                });

        // Don't change - if required you can reset your database by hitting this endpoint at localhost:4567/reset
        get("/reset", (req, res) -> {
            DatabaseManager.resetDatabase();
            return "OK";
        });

        //TODO: Add your routes here. a couple of examples are below
        get("/items", (req, res) -> DatabaseManager.getItems());
        get("/version", (req, res) -> "TopBloc Code Challenge v1.0");

        // Below are my inventory get routes with message erroring!
        // Get all items in your inventory, including the item name, ID, amount in stock, and total capacity
        get("/inventory/allInventory", (req, res) -> {
            String resultPrint = null;
            try {
                JSONArray itemsInventory = DatabaseManager.getInventory();
                resultPrint = itemsInventory.toString();
                res.status(200);
            } catch (Exception e) {
                resultPrint = e.getMessage();
                res.status(400);
            }
            res.body(resultPrint);
            return res.body();
        });
        // Get all items in your inventory that are currently out of stock, including the item name, ID, amount in stock, and total capacity
        get("/inventory/outOfStockItems", (req, res) -> {
            String resultPrint = null;
            try {
                JSONArray outOfStock = DatabaseManager.getOutOfStockItems();
                resultPrint = outOfStock.toString();
                res.status(200);
            } catch (Exception e) {
                resultPrint = e.getMessage();
                res.status(400);
            }
            res.body(resultPrint);
            return res.body();
        });
        // Get all items in your inventory that are currently overstocked, including the item name, ID, amount in stock, and total capacity
        get("/inventory/overStockedItems", (req, res) -> {
            String resultPrint = null;
            try {
                JSONArray overStocked = DatabaseManager.getOverStockedItems();
                resultPrint = overStocked.toString();
                res.status(200);
            } catch (Exception e) {
                resultPrint = e.getMessage();
                res.status(400);
            }
            res.body(resultPrint);
            return res.body();
        });
        // Get all items in your inventory that are currently low on stock (<35%), including the item name, ID, amount in stock, and total capacity
        get("/inventory/lowStockedItems", (req, res) -> {
            String resultPrint = null;
            try {
                JSONArray lowStocked = DatabaseManager.getLowOnStockItems();
                resultPrint = lowStocked.toString();
                res.status(200);
            } catch (Exception e) {
                resultPrint = e.getMessage();
                res.status(400);
            }
            res.body(resultPrint);
            return res.body();
        });
        // Get, when given an ID, the item name, ID, amount in stock, and total capacity of that item
        get("/inventory/itemByID", (req, res) -> {
            String resultPrint = null;
            try {
                String itemID = req.queryParams("ITEM_ID");
                JSONArray item = DatabaseManager.getItemById(itemID);
                resultPrint = item.toString();
                res.status(200);
            } catch (Exception e) {
                resultPrint = e.getMessage();
                res.status(400);
            }
            res.body(resultPrint);
            return res.body();
        });

        // Below are my distributor get routes with message erroring!
        // Get all distributors, including the id and name
        get("/distributors/allDistributors", (req, res) -> {
            String resultPrint = null;
            try {
                JSONArray distributors = DatabaseManager.getAllDistributors();
                resultPrint = distributors.toString();
                res.status(200);
            } catch (Exception e) {
                resultPrint = e.getMessage();
                res.status(400);
            }
            res.body(resultPrint);
            return res.body();
        });
        // Get, given a distributors ID, the items distributed by a given distributor, including the item name, ID, and cost
        get("/distributors/distributorsItems", (req, res) -> {
            String resultPrint = null;
            try {
                String distID = req.queryParams("DIST_ID");
                JSONArray distributorItems = DatabaseManager.getDistributorsItems(distID);
                resultPrint = distributorItems.toString();
                res.status(200);
            } catch (Exception e) {
                resultPrint = e.getMessage();
                res.status(400);
            }
            res.body(resultPrint);
            return res.body();
        });
        // Get, given an item ID, all offerings from all distributors for that item, including the distributor name, ID, and cost
//        get("/distributors/offeringsForItem", (req, res) -> {
//            String resultPrint = null;
//            try {
//                String itemID = req.queryParams("ITEM_ID");
//                JSONArray distributors = DatabaseManager.getOfferingsForItem(itemID);
//                resultPrint = distributors.toString();
//                res.status(200);
//            } catch (Exception e) {
//                resultPrint = e.getMessage();
//                res.status(400);
//            }
//            res.body(resultPrint);
//            return res.body();
//        });
        // Get the cheapest price for restocking an item at a given quantity from all distributors
        get("/distributors/cheapestRestockPrice", (req, res) -> {
            String resultPrint = null;
            try {
                String itemID = req.queryParams("ITEM_ID");
                String quantity = req.queryParams("QUANTITY");
                JSONArray restockPrice = DatabaseManager.getCheapestRestockPrice(itemID, quantity);
                resultPrint = restockPrice.toString();
                res.status(200);
            } catch (Exception e) {
                resultPrint = e.getMessage();
                res.status(400);
            }
            res.body(resultPrint);
            return res.body();
        });

        // Below are my inventory put/post/delete routes with message erroring!
        // Add a new item to the database
        post("/inventory/addItem", (req, res) -> {
            String resultPrint = null;
            try {
                String newID = req.queryParams("ITEM_ID");
                String newName = req.queryParams("ITEM_NAME");
                resultPrint = DatabaseManager.addItem(newID, newName);
                res.status(200);
            } catch (Exception e) {
               resultPrint = e.getMessage();
               res.status(400);
            }
            res.body(resultPrint);
            return res.body();
        });
        // Add a new item to your inventory
        post("/inventory/addToInventory", (req, res) -> {
            String resultPrint = null;
            //item, stock, capacity
            try {
                String newItem = req.queryParams("ITEM_ID");
                String newStock = req.queryParams("STOCK");
                String newCapacity = req.queryParams("CAPACITY");
                resultPrint = DatabaseManager.addToInventory(newItem, newStock, newCapacity);
                res.status(200);
            } catch (Exception e) {
                resultPrint = e.getMessage();
                res.status(400);
            }
            res.body(resultPrint);
            return res.body();
        });
        // Modify an existing item in your inventory
        put("/inventory/modifyInventory", (req, res) -> {
            String resultPrint = null;
            //item, stock, capacity
            try {
                String modItem = req.queryParams("ITEM_ID");
                String modStock = req.queryParams("STOCK");
                String modCapacity = req.queryParams("CAPACITY");
                resultPrint = DatabaseManager.modifyInventory(modItem, modStock, modCapacity);
                res.status(200);
            } catch (Exception e) {
                resultPrint = e.getMessage();
                res.status(400);
            }
            res.body(resultPrint);
            return res.body();
        });
        // Delete an existing item from your inventory
        delete("/inventory/deleteItem", (req, res) -> {
            String resultPrint = null;
            try {
                String itemID = req.queryParams("ITEM_ID");
                resultPrint = DatabaseManager.deleteItem(itemID);
                res.status(200);
            } catch (Exception e) {
                resultPrint = e.getMessage();
                res.status(400);
            }
            res.body(resultPrint);
            return res.body();
        });

        // Below are my distributor put/post/delete routes with message erroring!
        // Add a distributor
        post("/distributors/addDistributor", (req, res) -> {
            String resultPrint = null;
            try {
                String newID = req.queryParams("DIST_ID");
                String newName = req.queryParams("NAME");
                resultPrint = DatabaseManager.addDistributor(newID, newName);
                res.status(200);
            } catch (Exception e) {
                resultPrint = e.getMessage();
                res.status(400);
            }
            res.body(resultPrint);
            return res.body();
        });
        // Add items to a distributor's catalog (including the cost)
        post("/distributors/addToCatalog", (req, res) -> {
            String resultPrint = null;
            try {
                String distID = req.queryParams("DIST_ID");
                String itemID = req.queryParams("ITEM_ID");
                String newPrice = req.queryParams("PRICE");
                resultPrint = DatabaseManager.addToCatalog(distID, itemID, newPrice);
                res.status(200);
            } catch (Exception e) {
                resultPrint = e.getMessage();
                res.status(400);
            }
            res.body(resultPrint);
            return res.body();
        });
        // Modify the price of an item in a distributor's catalog
        put("/distributors/modifyPrice", (req, res) -> {
            String resultPrint = null;
            //item, stock, capacity
            try {
                String distID = req.queryParams("DIST_ID");
                String itemID = req.queryParams("ITEM_ID");
                String modPrice = req.queryParams("PRICE");
                resultPrint = DatabaseManager.modifyPrice(distID, itemID, modPrice);
                res.status(200);
            } catch (Exception e) {
                resultPrint = e.getMessage();
                res.status(400);
            }
            res.body(resultPrint);
            return res.body();
        });
        // Delete an existing item from your inventory
        delete("/distributors/deleteDistributor", (req, res) -> {
            String resultPrint = null;
            try {
                String distID = req.queryParams("DIST_ID");
                resultPrint = DatabaseManager.deleteDistributor(distID);
                res.status(200);
            } catch (Exception e) {
                resultPrint = e.getMessage();
                res.status(400);
            }
            res.body(resultPrint);
            return res.body();
        });
    }
}