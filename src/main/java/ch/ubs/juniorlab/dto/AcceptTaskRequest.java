package ch.ubs.juniorlab.dto;

public class AcceptTaskRequest {
    private String firstName;
    private String lastName;
    private int gpn;

    // Default constructor
    public AcceptTaskRequest() {
    }

    // Constructor with parameters
    public AcceptTaskRequest(String firstName, String lastName, int gpn) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gpn = gpn;
    }

    // Getters and setters
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
    
    public int getGpn() {
        return gpn;
    }
    
    public void setGpn(int gpn) {
        this.gpn = gpn;
    }
}