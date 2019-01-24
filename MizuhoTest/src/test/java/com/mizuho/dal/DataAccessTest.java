package com.mizuho.dal;

import com.mizuho.model.Instrument;
import com.mizuho.model.InstrumentType;
import com.mizuho.model.Vendor;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {


    @Test
    void getVendors() {
        Set<String> vendorNames = new HashSet<>();
        vendorNames.add("Bloomberg");
        vendorNames.add("TR");

        try {
            DataAccess dao = new DataAccess();
            List<Vendor> vendorList = dao.getVendors(vendorNames);
            assert  vendorList.get(0).Name == "Bloomberg";
            assert  vendorList.get(1).Name == "TR";

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getInstrumentTypes() {
        Set<String> instrumentTypes = new HashSet<>();
        instrumentTypes.add("Contract");
        instrumentTypes.add("Bond");

        try {
            DataAccess dao = new DataAccess();
            List<InstrumentType> instrumentTypeList = dao.getInstrumentTypes(instrumentTypes);
            assert  instrumentTypeList.get(0).Name == "Contract";
            assert  instrumentTypeList.get(1).Name == "Bond";

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getAllInstrumentsForVendor() {
        try {
//            Insert scripts need to be run first or replace the db with a mockup
            DataAccess dao = new DataAccess();
            assert dao.getAllInstrumentsForVendor(1).size() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Test
    void getAllInstrumentsForInstrumentType() {
        try {
//            Insert scripts need to be run first or replace the db with a mockup
            DataAccess dao = new DataAccess();
            assert dao.getAllInstrumentsForInstrumentType(1).size() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}