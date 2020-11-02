package com.upgrad.FoodOrderingApp.service.entity;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "customer")
@NamedQueries(
        {
                @NamedQuery(name = "customerByContactNumber", query = "select c from CustomerEntity c where c.contactNumber =:contactNumber"),
                @NamedQuery(name = "userByEmail", query = "select c from CustomerEntity c where c.email =:email"),
                @NamedQuery(name = "userByUuid", query = "select c from CustomerEntity c where c.uuid = :uuid"),
                @NamedQuery(name = "deleteUserById", query = "delete from CustomerEntity c where c.uuid = :uuid")
        }
)
public class CustomerEntity implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "firstname")
    @NotNull
    @Size(max = 30)
    private String firstname;

    @Column(name = "lastname")
    @Size(max = 30)
    private String lastname;

    @Column(name = "email")
    @NotNull
    @Size(max = 50)
    private String email;

    @Column(name = "contactNumber")
    @Size(max = 30)
    private String contactNumber;

    @Column(name = "password")
    @NotNull
    @Size(max = 255)
    private String password;

    @Column(name = "salt")
    @NotNull
    @Size(max = 200)
    private String salt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstName(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastName(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }



}
