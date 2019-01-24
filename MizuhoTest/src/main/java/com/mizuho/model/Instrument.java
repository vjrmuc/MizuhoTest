package com.mizuho.model;

public class Instrument {

    public int Id;
    public int VendorId;
    public Vendor Vendor;
    public float Price;
    public int InstrumentTypeId;
    public long InsertTimestamp;
    public InstrumentType InstrumentType;

    public void setInstrumentType(InstrumentType instrumentType){
        this.InstrumentType = instrumentType;
        this.InstrumentTypeId = instrumentType.Id;
    }

    public void setVendor(Vendor vendor){
        this.Vendor = vendor;
        this.VendorId = vendor.Id;
    }

}
