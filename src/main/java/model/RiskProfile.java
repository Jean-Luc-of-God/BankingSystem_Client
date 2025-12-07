package model;

/**
 *
 * @author JeanLuc
 */

import java.io.Serializable;
import jakarta.persistence.*;


@Entity
@Table(name = "risk_profile")
public class RiskProfile implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @Enumerated(EnumType.STRING)
    private ERiskType type;

    @OneToOne
    @JoinColumn(name = "cust_id")
    private Customer customer;

    public RiskProfile() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public ERiskType getType() { return type; }
    public void setType(ERiskType type) { this.type = type; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}