
package model;
/**
 *
 * @author JeanLuc
 */


import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;
// The Client doesn't strictly need JPA annotations, but keeping them avoids confusion
import jakarta.persistence.*;

public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private LocalDate dob;
    private String gender;
    private String email;


    private Set<Account> accounts;

    public Customer() {}


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Set<Account> getAccounts() { return accounts; }
    public void setAccounts(Set<Account> accounts) { this.accounts = accounts; }

    // Helper for printing
    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}