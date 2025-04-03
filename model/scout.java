package model;


import java.util.Properties;
import java.util.Vector;
import java.util.Enumeration;
import java.sql.*;

import database.*;
import exception.InvalidPrimaryKeyException;
import exception.PasswordMismatchException;
import javafx.scene.Scene;
import javafx.stage.Stage;
import userinterface.PatronView;
import userinterface.View;

public class Scout extends EntityBase{
    private static String table_name = "Scout";

    protected Properties persistentState;
    protected Properties dependencies;
    private String updateStatusMessage = "";

    //protected Librarian myLibrarian;
    protected Stage myStage;


    //----------------------------------------------------------
    //Constructor used by librarian
    //----------------------------------------------------------
    //public Patron(Librarian lib)
    //{
    //    super(table_name);
    //    myLibrarian = lib;
    //    persistentState = new Properties();

    //}

    public Scout (String query_id) throws InvalidPrimaryKeyException, PasswordMismatchException {
        super(table_name);

        String query = "SELECT * FROM" + table_name + " WHERE (ID= " + query_id + ")";
        Vector<Properties> dataRetrieved = getSelectQueryResult(query);

        if (dataRetrieved != null){
            int size = dataRetrieved.size();

            if (size !=1) throw new InvalidPrimaryKeyException("Wrong number of primary keys");
            else{
                Properties retrievedScoutData = (Properties)dataRetrieved.elementAt(0);
                //persistentState = new Properties();

                Enumeration allKeys = retrievedScoutData.propertyNames();
                while(allKeys.hasMoreElements() == true){
                    String nextKey = (String)allKeys.nextElement();
                    String nextValue = retrievedScoutData.getProperty(nextKey);

                    if(nextValue != null){
                        //persistentState.setProperty(nextKey, NextValue);

                    }
                }
            }
        }else{
            throw new InvalidPrimaryKeyException("More than one value associated with that key");
        }

    }

    //public void createAndShowPatronView() {

      //  Scene currentScene = (Scene)myLibrarian.myViews.get("PatronView");

        //if (currentScene == null) {

          //  View newView = new PatronView(this);
            //currentScene = new Scene(newView);
            //myLibrarian.myViews.put("PatronView", currentScene);
        //}
        //myLibrarian.swapToView(currentScene);
    //}

    public Scout(Properties props){
        super(table_name);

        setDependencies();

        persistentState = new Properties();
        Enumeration allKeys = props.propertyNames();
        while (allKeys.hasMoreElements() == true){
            String nextKey = (String)allKeys.nextElement();
            String nextValue = props.getProperty(nextKey);
            if(nextValue != null){
                persistentState.setProperty(nextKey, nextValue);
            }
        }
    }

    private void setDependencies(){
        dependencies = new Properties();
        myRegistry.setDependencies(dependencies);
    }

    public Object getState(String key){
        if (key.equals("UpdateStatusMessage") == true)
            return updateStatusMessage;
        return persistentState.getProperty(key);
    }

    public void stateChangeRequest(String key, Object value)
    {

        myRegistry.updateSubscribers(key, this);
    }

    public static int compare(Book a, Book b)
    {
        String aNum = (String)a.getState("ScoutId");
        String bNum = (String)b.getState("ScoutId");

        return aNum.compareTo(bNum);
    }

    public Vector<String> getEntryListView()
    {
        Vector<String> v = new Vector<String>();

        v.addElement(persistentState.getProperty("scoutId"));
        v.addElement(persistentState.getProperty("LastName"));
        v.addElement(persistentState.getProperty("FirstName"));
        v.addElement(persistentState.getProperty("MiddleName"));
        v.addElement(persistentState.getProperty("DateOfBirth"));
        v.addElement(persistentState.getProperty("PhoneNumber"));
        v.addElement(persistentState.getProperty("Email"));
        v.addElement(persistentState.getProperty("TroopID"));
        v.addElement(persistentState.getProperty("Status"));
        v.addElement(persistentState.getProperty("DateStatusUpdated"));

        return v;
    }


    public void updateStateInDatabase()
    {
        try
        {
            if (persistentState.getProperty("ScoutId") != null)
            {
                // update
                Properties whereClause = new Properties();
                whereClause.setProperty("ScoutId",
                        persistentState.getProperty("ScoutId"));
                updatePersistentState(mySchema, persistentState, whereClause);
                updateStatusMessage = "Scout data for Scout id : " + persistentState.getProperty("ScoutId") + " updated successfully in database!";
            }
            else
            {
                // insert
                Integer PatronId = //wrong variable?
                        insertAutoIncrementalPersistentState(mySchema, persistentState);
                persistentState.setProperty("ScoutId", "" + PatronId.intValue());
                updateStatusMessage = "Scout data for new Scout : " +  persistentState.getProperty("ScoutId")
                        + "installed successfully in database!";
            }
        }
        catch (SQLException ex)
        {
            updateStatusMessage = "Error in installing Scout data in database!";
        }
        //DEBUG System.out.println("updateStateInDatabase " + updateStatusMessage);
    }
    public void processNewScout(Properties p) {
        // Set the patron data from the Properties object into the persistentState
        persistentState = new Properties();
        persistentState.setProperty("LastName", p.getProperty("LastName"));
        persistentState.setProperty("FirstName", p.getProperty("FirstName"));
        persistentState.setProperty("MiddleName", p.getProperty("MiddleName"));
        persistentState.setProperty("DateOfBirth", p.getProperty("DateOfBirth"));
        persistentState.setProperty("PhoneNumber", p.getProperty("PhoneNumber"));
        persistentState.setProperty("Email", p.getProperty("Email"));
        persistentState.setProperty("TroopID", p.getProperty("TroopId"));
        persistentState.setProperty("Status", p.getProperty("Status"));
        persistentState.setProperty("DateStatusUpdated"), p.getProperty("DateStatusUpdated");


        // Now that the patron data is set, insert the patron into the database
        try {
            // Call the method to update the database (could be insert or update depending on your logic)
            updateStateInDatabase(); // Assuming updateStateInDatabase() is the method to handle the DB insertion/updating

            // If successful, display a success message
            System.out.println("Scout successfully added to the database!");
        } catch (Exception ex) {
            // If an error occurs during database insertion, display an error message
            System.out.println("Failed to add scout to the database.");
            ex.printStackTrace();
        }
    }
    protected void initializeSchema(String table_name){
        if(mySchema == null){
            mySchema = getSchemaInfo(table_name);
        }
    }
}