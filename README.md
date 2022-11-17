# REST Chat application with Server-sent-event Emitter (Sse Emitter)

# Maven Project 

## Contributors

* ### Velislav Angelov

## Functionalities
1. User Controller 
    * User registration
    * Get All Users
    * Delete a User 
2. Message Controller
    * Send a message to another User
    * Get all pending messages
    * Login 
    * Logout
## Description
* This REST API provides a simple way to send a message to another User with the help of a Sse Emitter. On top of that, if the receiver is offline, your sent messages will be put in a pending state. Then, when the receiver checks his pending messages, they will be deleted.
## To run the application you need:
* IntelliJ IDEA
* Java 8 
* Oracle Database