package application.main;

import application.data.EmployeeObject;
import javafx.util.Builder;

public class ApplicationMain {

  public static void main(String arg[]) {

    EmployeeObject.Builder bl = new EmployeeObject.Builder();

    bl.firstName("Arnab");
    bl.employeeId(123);
    bl.age(23);
    bl.lastName("Bhattcharjee");

    EmployeeObject emp = bl.build();

    System.out.println("Employee name is " + emp.getFirstName() + " " + emp.getLastName());
    System.out.println("Employee id is " + emp.getEmployeeId());
    System.out.println("Employee age is " + emp.getAge());


  }

}