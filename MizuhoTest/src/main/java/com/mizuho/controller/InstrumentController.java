package com.mizuho.controller;

import com.mizuho.model.Instrument;
import com.mizuho.service.InstrumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.*;
import java.util.List;


@RestController
@RequestMapping("/api")
public class InstrumentController {

    public static final Logger logger = LoggerFactory.getLogger(InstrumentController.class);


    @RequestMapping(value="/getInstrumentsForVendor", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<List<Instrument>> getInstrumentsForVendor(@RequestParam("vendorId") Integer vendorId) {
        InstrumentService instrumentService = new InstrumentService();
        try {
            return new ResponseEntity<List<Instrument>>(instrumentService.getAllInstrumentsForVendor(vendorId), HttpStatus.OK);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Could not retrieve instruments, problem with the database", e);
        }
    }


    @RequestMapping(value="/getInstrumentsForInstrumentType", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<List<Instrument>> getInstrumentsForInstrumentType(@RequestParam("instrumentTypeId") Integer instrumentTypeId) {
        InstrumentService instrumentService = new InstrumentService();
        try {
            return new ResponseEntity<List<Instrument>>(instrumentService.getAllInstrumentsForInstrumentType(instrumentTypeId), HttpStatus.OK);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Could not retrieve instruments, problem with the database", e);
        }
    }


    @RequestMapping(value = "/insertInstrument", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody List<Instrument> instrumentList) {
        InstrumentService instrumentService = new InstrumentService();
        try {
             instrumentService.persistInstruments(instrumentList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
}