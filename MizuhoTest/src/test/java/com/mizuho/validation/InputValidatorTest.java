package com.mizuho.validation;

import com.mizuho.model.Instrument;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputValidatorTest {

    @Test
    void isInstrumentValid() {
        Instrument instrument = new Instrument();
        InputValidator validator = new InputValidator();
        assertFalse(validator.isInstrumentValid(instrument));

        instrument.Price = 10;
        assert validator.isInstrumentValid(instrument);
    }
}