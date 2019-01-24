# Mizuho Vendor Instruments


## 1.Project Dependencies:
### 1.1.Maven dependencies

The project dependencies are listed in the pom.xml file.
The maven dependencies are:

    -org.springframework.boot
    -org.junit.jupiter
    -junit
    -mysql
    -org.apache.commons


### 1.2.Technologies used:
* Spring Boot
* Junit
* MySql 
* Java 8

The project is exposing a REST API with Spring Boot framework.
The ingested data is submitted via Post request to the PriceController and mapped to a POJO.
After the data validation the objects are ingested in the database, in our case MySql.

## 2.Running tests:
The Unit tests for the project are in the test/java/com.mizuho directory. The technology used is Junit.
The Tests can be run manually(with all dependencies installed) or on artifact build event.

**Unit tests improvements**:

The DataAccess layer needs to be improved by providing a mockup for the database connection object.



##3.Moving to Production:

### 3.1. Setting up the environment dependencies to move to production:

* A Tomcat server needs to be deployed and confured before the application can be moved to production.
* MySql database needs to be setup
* Java 8 installation
* Maven installation
* Github command line tools
 
### 3.2 Deploying the application to production:

* Checkout the application from Github
* Once the requirements above are met and an artifact needs to be built from source.
* Deploy the artifact to the web server
* Setup a Cron job for the data eviction after 30 days
 

### 4.Data flow sequence

#### 4.1. Ingestion Data Flow

The ingestion data flow has one main entry point - the /api/insertInstrument url.
It accepts a POST request with request body json data, which will be serialized via the system to POJOs

![Alt text](images/Mizuho ingestion data flow.png?raw=true "Mizuho Ingestion data flow")


#### 4.1. Read Data Flow


The read data flow has 2 entry points:

/api/getInstrumentsForInstrumentType to get all instruments for all vendors for given type

/api/getInstrumentsForVendor to get all instruments for a given vendor

Currently out of the scope, but in the future authentication mechanism would be needed

![Alt text](images/Mizuho read data flow.png?raw=true "Mizuho Read data flow")


### 5. Object flow 

#### 5.1 Ingestion Object Flow

For ingestion the request is originated by a vendor and handled by the InstrumentController.
The business logic is held in InstrumentService, which handles invoking the validator and the Database to handle the ingestion.
The DataAccess class is part of the data access layer and is responsible for building the actual sql query
upon ingestion completion the method returns status message 200, Created.

##### **Improvements for ingestion:**
The working assumption is that the data coming from different vendors will be in the same format.
In reality that is rarely the case. Transformation logic will be needed to set the values to a common format.
The task is complicated further by the fact that when involving new vendors usually the data transfer medium may be different
 and the logic to ingest data from other data sources will be needed.

![Alt text](images/Mizuho ingestion object flow.png?raw=true "Mizuho Read data flow")



#### 5.2 Read object Flow

For the reading logic the object flow is similar, with the request requesting the open urls - for getting all instruments 
for a specific vendor or all instruments for all instruments.

##### **Improvements for reading data:**
A few improvements can be done to scale the system.
First and most important is to consider the volume of data requested.
Another important consideration is to provide additional api request points to provide different grains of data(Hourly data, monthly aggregation, etc)
 


![Alt text](images/Mizuho read object flow.png?raw=true "Mizuho Read data flow")


### 6. Data Eviction
Data is evicted after preconfigured number of days(currently 30).
The entry point for it is in DataEvictor.
The data eviction should be build as a standalone artifact with Starting class DataEvictor and set to run on preconfigured 
intervals in Cron or a scheduled job in Jenkins.



### 7. Future Considerations and improvements

#### 7.1. Improvements
1.Logging system
2.Better error handling
3.Authentication system
4.Improving the data validation system - more rules, more precise boundries for valid data
5. Implementation of a real time ingestion system with Kafka or another message broker service
6. Better unit test code coverage and a mockup system to unit test the logic dependent on the database
7.Connections details and other configurations need to be moved to configuration file

#### 7.2 Future Considerations
As a main consideration we need to take in account adding new data sources, branching the data and vendor specific schemas
I would suggest to create a mapping service for the data and ensure the uniqueness of the data(if the same data is expected from different vendors)
The overall architecture needs to be modularized to insulate different ingestion mediums from the transformation logic
