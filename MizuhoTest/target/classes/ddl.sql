create table if not exists InstrumentType
(
  Id int auto_increment
    primary key,
  Name varchar(255) charset utf8 null,
  constraint InstrumentType_Name_uindex
    unique (Name)
);

create table if not exists sql2275037.Vendor
(
  Id int auto_increment
    primary key,
  Name varchar(255) charset utf8 null,
  constraint Vendor_Name_uindex
    unique (Name)
);

create table if not exists Instrument
(
  Id int auto_increment
    primary key,
  VendorId int null,
  Price float null,
  InstrumentTypeId int null,
  InsertTimestamp timestamp default CURRENT_TIMESTAMP not null
);