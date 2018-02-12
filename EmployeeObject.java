package application.data;

public class EmployeeObject {

  private String firstName;
  private String lastName;
  private int employeeId;
  private int age;

  public static class Builder {

    private String firstName = "Unsub";
    private String lastName = "Unsub";
    private int employeeId = 0;
    private int age = 0;

    public Builder firstName(String val) {
      firstName = val;
      return this;
    }

    public Builder lastName(String val) {
      lastName = val;
      return this;
    }

    public Builder employeeId(int val) {
      employeeId = val;
      return this;
    }

    public Builder age(int val) {
      age = val;
      return this;
    }

    public EmployeeObject build() {
      return new EmployeeObject(this);
    }
  }

  private EmployeeObject(Builder builder) {
    firstName = builder.firstName;
    lastName = builder.lastName;
    employeeId = builder.employeeId;
    age = builder.age;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public int getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(int employeeId) {
    this.employeeId = employeeId;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }
}
