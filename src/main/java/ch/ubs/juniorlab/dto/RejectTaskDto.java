package ch.ubs.juniorlab.dto;

public class RejectTaskDto {
    private String firstName;
    private String lastName;
    private String reason;

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
