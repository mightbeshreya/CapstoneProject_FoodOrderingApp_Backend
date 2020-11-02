package com.upgrad.FoodOrderingApp.service.entity;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "address")
@NamedQueries({
        @NamedQuery(name = "getAddressByUuid", query = "SELECT a from AddressEntity a where a.uuid = :uuid"),
        @NamedQuery(name = "getAddressById", query = "SELECT a from AddressEntity a where a.id = :id")
})
public class AddressEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @Size(max = 200)
    private String uuid;

    @Column(name = "flat_buil_number")
    @Size(max = 255)
    private String flat_buil_number;

    @Column(name = "locality")
    @Size(max = 255)
    private String locality;

    @Column(name = "city")
    @Size(max = 30)
    private String city;

    @Column(name = "pincode")
    @Size(max = 30)
    private String pincode;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private StateEntity state_id;

    @Column(name = "active")
    private Integer active;

    public AddressEntity(
            String uuid,
            String flatBuilNo,
            String locality,
            String city,
            String pincode,
            StateEntity state) {
        this.uuid = uuid;
        this.flat_buil_number = flatBuilNo;
        this.locality = locality;
        this.city = city;
        this.pincode = pincode;
        this.state_id = state;
    }

    public AddressEntity() {}

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

    public String getFlat_buil_number() {
        return flat_buil_number;
    }

    public void setFlatBuilNo(String flat_buil_number) {
        this.flat_buil_number = flat_buil_number;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public StateEntity getState() {
        return state_id;
    }

    public void setState(StateEntity state_id) {
        this.state_id = state_id;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }
}
