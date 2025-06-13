package ch.ubs.juniorlab.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Gpn", nullable = false, unique = true, length = 20)
    private String gpn;

    @Column(name = "Name", nullable = false, length = 255)
    private String name;

    @Column(name = "Email", nullable = false, length = 255)
    private String email;

    @Column(name = "Prename", length = 45)
    private String prename;

    // Standard-Konstruktor
    public Person() {
    }

    // Optional: Konstruktor mit Parametern
    public Person(String gpn, String name, String email, String prename) {
        this.gpn = gpn;
        this.name = name;
        this.email = email;
        this.prename = prename;
    }

    // Getter und Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGpn() {
        return gpn;
    }

    public void setGpn(String gpn) {
        this.gpn = gpn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPrename() {
        return prename;
    }

    public void setPrename(String prename) {
        this.prename = prename;
    }
}
