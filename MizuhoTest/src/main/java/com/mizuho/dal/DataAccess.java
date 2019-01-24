package com.mizuho.dal;

import com.mizuho.model.Instrument;
import com.mizuho.model.InstrumentType;
import com.mizuho.model.Vendor;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DataAccess {

    protected Connection connection;

    public DataAccess() throws SQLException {
        this.connection = this.getConnection();
    }

    /**
     * @param evictionDays Executes eviction query
     * @throws SQLException
     */
    public void evictDataAfterPeriod(int evictionDays) throws SQLException {
        Connection connection = this.connection;
        PreparedStatement statement =  connection.prepareStatement("DELETE FROM Instrument WHERE InsertTimestamp >= DATE(NOW()) - INTERVAL ? DAY ");
        statement.setInt(1, evictionDays);
        statement.executeUpdate();
    }


    /**
     * Builds instrument types query
     * @param instrumentTypes
     */
    protected void createInstrumentTypes(HashSet<String> instrumentTypes) {
        final String sql = "INSERT INTO InstrumentType(Name) Values(?)";
        createParentTypes(instrumentTypes, sql);
    }

    /**
     * Builds vendors query
     * @param instrumentTypes
     */
    protected void createVendors(HashSet<String> instrumentTypes) {
        final String sql = "INSERT INTO Vendor(Name) Values(?)";
        createParentTypes(instrumentTypes, sql);
    }

    /**
     * Creates a batch of insert queries inserting data for parent types, the Names have a unique constraint and
     * if the Name is already present it will trigger SqlException
     * @param parentNameTypes
     * @param sql
     */
    protected void createParentTypes(HashSet<String> parentNameTypes, String sql) {

        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            for (String parentName: parentNameTypes) {
                statement.setString(1, parentName);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    protected PreparedStatement prepareStatement(Set<String> instrumentTypeNames) throws SQLException {

        String values = StringUtils.repeat(",?", instrumentTypeNames.size()).substring(1);
        String query = String.format("SELECT Id, Name FROM InstrumentType WHERE Name In (%s)", values);

        PreparedStatement statement = connection.prepareStatement(query);
        int index = 0;
        for(String name : instrumentTypeNames){
            index++;
            statement.setString(index, name);
        }
        return statement;
    }

    /**
     * Retrieves all vendors
     * @param vendorNames
     * @return List<Vendor>, if a specific vendor is not present in the db it will be created
     * @throws SQLException
     */
    public List<Vendor> getVendors(Set<String> vendorNames) throws SQLException {
        PreparedStatement statement = prepareStatement(vendorNames);
        ResultSet results = statement.executeQuery();
        List<Vendor> vendorList = new ArrayList<>();
        while (results.next()){
            Vendor vendor = new Vendor();
            vendor.Id = results.getInt(1);
            vendor.Name = results.getString(2);
            vendorList.add(vendor);
        }
        return vendorList;
    }

    /**
     * Retrieves all vendors
     * @param instrumentTypes HashSet of instrument type names
     * @return List<InstrumentType>, if a specific instrument type is not present in the db it will be created
     * @throws SQLException
     */
    public List<InstrumentType> getInstrumentTypes(Set<String> instrumentTypes) throws SQLException {
        PreparedStatement statement = prepareStatement(instrumentTypes);
        ResultSet results = statement.executeQuery();
        List<InstrumentType> updatedInstrumentTypes = new ArrayList<>();
        while (results.next()){
            InstrumentType instrumentType = new InstrumentType();
            instrumentType.Id = results.getInt(1);
            instrumentType.Name = results.getString(2);
            updatedInstrumentTypes.add(instrumentType);
        }
        return updatedInstrumentTypes;
    }


    /**
     * Ingest parent types of all intstruments
     * @param instrumentList
     * @return
     * @throws SQLException
     */
    public List<Instrument> getOrCreateInstrumentTypes(List<Instrument> instrumentList) throws SQLException {
        HashSet<String> instrumentTypeNames = new HashSet<>();
        instrumentList.forEach(instrument -> instrumentTypeNames.add(instrument.InstrumentType.Name));
        this.createInstrumentTypes(instrumentTypeNames);
        List<InstrumentType> instrumentTypeList = getInstrumentTypes(instrumentTypeNames);
        List<Instrument> updatedInstrumentList = new ArrayList<>();
        instrumentList.forEach(instrument -> {
            InstrumentType retrievedInstrumentType = instrumentTypeList.stream()
                    .filter(i -> i.Name.equals(instrument.InstrumentType.Name)).collect(Collectors.toList()).get(0);
            updatedInstrumentList.add(instrument);
            instrument.setInstrumentType(retrievedInstrumentType);
            updatedInstrumentList.add(instrument);
        });
        return updatedInstrumentList;
    }


    /**
     * Gets all vendors of a specific type and updates the instrument list
     * @param instrumentList
     * @return List of updated instruments
     * @throws SQLException
     */
    public List<Instrument> getOrCreateVendors(List<Instrument> instrumentList) throws SQLException {
        HashSet<String> instrumentVendorNames = new HashSet<>();
        instrumentList.forEach(instrument -> instrumentVendorNames.add(instrument.Vendor.Name));
        this.createVendors(instrumentVendorNames);
        List<Vendor> vendorList = getVendors(instrumentVendorNames);
        List<Instrument> updatedInstrumentList = new ArrayList<>();
        instrumentList.forEach(instrument -> {
            Vendor retrievedVendor = vendorList.stream()
                    .filter(i -> i.Name.equals(instrument.Vendor.Name)).collect(Collectors.toList()).get(0);
            instrument.setVendor(retrievedVendor);
            updatedInstrumentList.add(instrument);
        });
        return updatedInstrumentList;
    }

    public void persistInstruments(List<Instrument> instrumentList) throws SQLException {

        instrumentList = getOrCreateInstrumentTypes(instrumentList);
        instrumentList = getOrCreateVendors(instrumentList);

        final String sql = "INSERT INTO Instrument(Price, VendorId, InstrumentTypeId) Values(?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(sql);

        instrumentList.forEach((instrument -> {
            try {
                statement.setFloat(1, instrument.Price);
                statement.setInt(2, instrument.VendorId);
                statement.setInt(3, instrument.InstrumentTypeId);
                statement.addBatch();
            } catch (SQLException e) {
//              TO-DO: Move that to logger
                System.out.println("Insert prepared statement could not be created");
            }
        }));
        statement.executeBatch();
    }




    /** The method builds a query to retrieve instruments of a given vendor from the db
     * @param vendorId
     * @return
     * @throws SQLException
     */
    public List<Instrument> getAllInstrumentsForVendor(int vendorId) throws SQLException {
        String query = "Select Id, VendorId, Price, InstrumentTypeId, InsertTimestamp FROM Instrument WHERE VendorId = ?";
        return this.getAllInstruments(vendorId, query);
    }


    /** The method builds a query to retrieve instruments of a given type from the db
     * @param vendorId
     * @return
     * @throws SQLException
     */
    public List<Instrument> getAllInstrumentsForInstrumentType(int vendorId) throws SQLException {
        String query = "Select Id, VendorId, Price, InstrumentTypeId, InsertTimestamp FROM Instrument WHERE InstrumentTypeId = ?";
        return this.getAllInstruments(vendorId, query);
    }


    /**
     * The method builds and executes a prepared statement to retrieve instruments from the db
     * @param id to be substituted in the prepared statement
     * @param query to retrieve the records
     * @return List<Instrument> for the requested id
     * @throws SQLException
     */
    protected List<Instrument> getAllInstruments(int id, String query) throws SQLException {
        PreparedStatement getStatement = this.connection.prepareStatement(query);
        getStatement.setInt(1, id);
        ResultSet results =  getStatement.executeQuery();
        List<Instrument> instrumentList = new ArrayList<>();
        while(results.next()){
            Instrument instrument = new Instrument();
            instrument.Id = results.getInt("Id");
            instrument.VendorId = results.getInt("VendorId");
            instrument.Price = results.getFloat("Price");
            instrument.InstrumentTypeId = results.getInt("InstrumentTypeId");
            instrument.InsertTimestamp = results.getInt("InsertTimestamp");
            instrumentList.add(instrument);
        }
        return instrumentList;
    }


    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://sql2.freemysqlhosting.net/sql2275037", "sql2275037", "tZ1*jJ3%");
            return con;

        } catch (SQLException | ClassNotFoundException e) {
            throw new SQLException("Connection to the database could not be established!", e);
        }
    }

}
