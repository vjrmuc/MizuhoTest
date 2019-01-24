package com.mizuho.validation;

import com.mizuho.dal.DataAccess;
import com.mizuho.model.Instrument;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InputValidator {


    /**
     * Sample simple validation function
     *
     * @param instrument
     * @return
     */
    public boolean isInstrumentValid(Instrument instrument) {
        return instrument.Price != 0;
    }
}
