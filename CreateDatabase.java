
import java.sql.*;

public class CreateDatabase {
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String Username = "root";
    private static final String Password = "R@jan12#";

    private static Connection connection;

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, Username, Password);
            System.out.println("Database connected sucessfully");

            Statement state = connection.createStatement();
            String sql = "CREATE DATABASE IF NOT EXISTS libraryMSdb";
            state.executeUpdate(sql);
            System.out.println("Database Created Sucessfully...");

            state.executeUpdate("use LibraryMSdb");

            sql = "create table if not exists Members (" +
                    "Username varchar(50) primary key," +
                    "Mname varchar(100) not null," +
                    "Password varchar(8) not null," +
                    "MPhone varchar(10) not null check(MPhone regexp '^[0-9]{10}$')," +
                    "Address text not null," +
                    "Joining_Date date default(current_date)" + ")";
            state.executeUpdate(sql);
            System.out.println("Member Tanbles created sucessfully.....");

            sql = "create table if not exists Books (" +
                    "Book_Id int auto_increment primary key," +
                    "Book_Name varchar(100) not null," +
                    "Author varchar(50) not null," +
                    "Price decimal(10,2) not null," +
                    "Total_Quantity int not null," +
                    "Available_Quantity int not null," +
                    "Added_by varchar(50)," +
                    "foreign key (Added_By) references members(Username)" + ")";
            state.executeUpdate(sql);
            System.out.println("Books table created sucessfully...");

            sql = "create table if not exists Students(" +
                    "Stu_Id int auto_increment primary key," +
                    "Stu_Name varchar(100) not null," +
                    "Stu_Phone varchar(10) not null check (Stu_Phone regexp '^[0-9]{10}$')" + ")";
            state.executeUpdate(sql);
            System.out.println("Student table created sucessfully....");

            sql = "create table if not exists Issue (" +
            "Issue_Id int auto_increment primary key," +
            "Book_Id int not null," +
            "Stu_Id int not null," +
            "Issued_By varchar(50) not null," +
            "Issued_Date date default (Current_date)," +
            "Returned_Date date," +
            "Penalty int default 0," +
            "foreign key (Book_Id) references Books(Book_Id)," +
            "foreign key (Stu_Id) references Students(Stu_Id)," + 
            "foreign key (Issued_By) references Members(Username)"+")";
            state.executeUpdate(sql);
            System.out.println("Issue table created....");
            
            // state.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Database error" + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Driver error:" + e.getMessage());
        }
    }
}
