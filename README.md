# Generic IOT Infrastructure
## json representation
base Json for register Product
````json
{ 
    "Key" : "CommandName" ,
    "Data" : { 
                "Company_ID" : "companyID" ,
                "Product_Name" :"productName",
                "Description" :  "Description"
            }         
}
````
## Command registration 
abstract Command Implement Command interface
- ** = 0 = General Command**
- **companyID != 0 = customized Command**