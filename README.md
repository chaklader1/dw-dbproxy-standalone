# DBPRoxy Standalone


<br>
<br>

## Instructions

<br>

1. Create the JAR using the command  &nbsp;&nbsp;&nbsp; ```mvn package```  &nbsp;&nbsp;&nbsp; and import to the project 
2. Import the class &nbsp;&nbsp;&nbsp; ```com.drivewealth.dbproxy.service.DBProxyService``` &nbsp;&nbsp;&nbsp; into the source code - preferably in the service layer
3. You will need to use the method provided below -
<br>
<br>
&nbsp; ```Map<String, String> processRequest(String requestBody, Map<String, String> headers, Map<String, String>  dbProxyConfigs) ``` &nbsp;&nbsp;&nbsp; 
<br>
<br>
This method needs to replace the DynamoDB request calling code with the follwoing info - 

   - ```requestBody``` &nbsp;&nbsp;&nbsp; needs to be the same JSON String that is beining provided to the Dynamo client 
   - ```headers``` &nbsp;&nbsp;&nbsp; the map contains 2 Strings:
        - Dynamo action name 
        - table name 
   - ```dbProxyConfigs``` &nbsp;&nbsp;&nbsp; the map contains 2 Strings:  
        - Mode type e.g "ACTIVE_PASSIVE", "PASSIVE_ACTIVE" etc 
        - Synchronosity type  e.g "Synchronous", "Asynchronous" etc 


## Sample code 

<br>

To provide the data for the ```headers```, we can write code as below:

<br>

```
Map<String, String> headers = new HashMap<>();
// "GetItem", 
headers.put("ACTION_NAME", actionName);
// "User"
headers.put("TABLE_NAME", tableName);
```

<br>

To provide the data for the ```configs```, we can write code as below:

<br>

```
Map<String, String> configs = new HashMap<>();
// ACTIVE_PASSIVE
configs.put("MODE_TYPE", modeType);
// SYNCHRONOUS
configs.put("SYNCHRONICITY_TYPE", synchronosityType);
```