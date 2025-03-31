![Voxloud Logo](https://www.voxloud.com/wp-content/uploads/2020/07/voxloud_logo_@1x.png)

# Provisioning server #

## Command to Request Provisiongin Application Server ##
![image](https://github.com/user-attachments/assets/c77c1686-0932-4ad2-b3ea-737947edaa93)

## Starting ProvisiongingApplication with port(s): 8080 (http) ##
![image](https://github.com/user-attachments/assets/27942642-df13-4709-9878-3d8c613a041c)


- Desk: used on office desks
- Conference: used in conference rooms

When users connect these devices to the network, they should automatically get their configuration from server and 
be enabled to make and receive phone calls. They are different physical devices thus they are using different configuration files.
What they have in common is that, when booting, they perform the following HTTP request to the server, putting their MAC 
address in the URL:

```
GET /api/v1/provisioning/aa-bb-cc-11-22-33
```

The server stores a table that contains all the phones in the inventory. If a phone is found in the inventory then its
configuration file should be dynamically generated, according to the phone model configuration format. If a phone is not 
found in the inventory, the server should deny the provisioning request, returning a proper HTTP error code.
As an additional requirement, the system should be able to support new device type provisioning with minimal code/configuration change.

### Default URL Path for H2 Database ###

```
http://localhost:8080/h2-console
```

`Credentials H2 Database from application.properties`

```
spring.datasource.username=sa
spring.datasource.password=password
```
![image](https://github.com/user-attachments/assets/48fb532d-7497-43ab-bd2f-ec72ece5345b)


#### List of Configuration Data from the Device Table ####
![image](https://github.com/user-attachments/assets/0ff0e055-269b-48b7-9817-862aebed0789)


#### Testing the Provisioning Application Server with Request command ####
![image](https://github.com/user-attachments/assets/2f1cc2ad-86a8-4e1b-ab0c-38e36787ec31)
