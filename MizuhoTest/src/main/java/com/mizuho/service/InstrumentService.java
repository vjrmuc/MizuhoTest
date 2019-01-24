package com.mizuho.service;

import com.mizuho.dal.DataAccess;
import com.mizuho.model.Instrument;
import com.mizuho.model.InstrumentType;
import com.mizuho.validation.InputValidator;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class InstrumentService {


    /**
     * Persisting instruments to the database
     * @param instrumentList the list will be persited to the database
     * @throws SQLException produced by database
     */
    public void persistInstruments(List<Instrument> instrumentList) throws SQLException {
        DataAccess dao = new DataAccess();
        InputValidator validator = new InputValidator();
        List<Instrument> filteredInstrumentList = instrumentList.stream().filter(validator::isInstrumentValid).collect(Collectors.toList());
        dao.persistInstruments(filteredInstrumentList);
    }


    /**
     * @param vendorId to retrieve all instruments for
     * @return List of Instruments for the requested vendor
     * @throws SQLException If the DataAccess produces an error it will be propagated to the InstrumentController
     */
    public List<Instrument> getAllInstrumentsForVendor(int vendorId) throws SQLException {
        DataAccess dao = new DataAccess();
        return dao.getAllInstrumentsForVendor(vendorId);
    }


    /**
     * @param instrumentTypeId retrieve all instruments for
     * @return List of Instruments for the requested vendor
     * @throws SQLException If the DataAccess produces an error it will be propagated to the InstrumentController
     */
    public List<Instrument> getAllInstrumentsForInstrumentType(int instrumentTypeId) throws SQLException {
        DataAccess dao = new DataAccess();
        return dao.getAllInstrumentsForInstrumentType(instrumentTypeId);
    }
}
