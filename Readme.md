MySQL Setup
======================
- Please go to DatabaseConnection.java file check for the settings.
1. Create a database "bookstore" in your MySQL server. (DatabaseConnection.java line 8)
2. Modify the user and password as your MySQL account in DatabaseConnection.java (line 9-10). Or you can create a user and password as DatabaseConnection.java in your MySQL server.

Bookstore Application
======================

This is a simple console-based Java application for a bookstore management system.

Requirements
------------
- Java Development Kit (JDK) installed on your system
- MySQL Connector/J JAR file (mysql-connector-java-8.0.32.jar) located in the 'lib' directory

Compile and Run the Application
-------------------------------

For Linux and macOS:

1. Open a terminal and navigate to the project directory (the directory containing the 'src' and 'lib' folders).

2. Run following command to clean working directory

   `rm -rf out`

3. Compile the Java application using the following command:

   `javac -d out src/com/bookstore/*.java src/com/bookstore/models/*.java -cp local_datafiles/`

4. Run the Java application using the following command:

   `java -cp $FULLPATH/mysql-connector-java-8.0.32.jar:out com.bookstore.Main`

For Windows:

1. Open a command prompt and navigate to the project directory (the directory containing the 'src' and 'lib' folders).

2. Run following command to clean working directory

   `rd /s /q out`

3. Compile the Java application using the following command:

   `javac -d out src/com/bookstore/*.java src/com/bookstore/models/*.java -cp local_datafiles/`

4. Run the Java application using the following command:

   `java -cp $FULLPATH\mysql-connector-java-8.0.32.jar;out com.bookstore.Main`

That's it! You should now see the bookstore application running in your terminal or command prompt.