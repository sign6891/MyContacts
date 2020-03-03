package com.example.mycontacts.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//Создание полей таблицы
@Entity(tableName = "contacts")
public class Contact {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "contact_id")
    private long id;

    @ColumnInfo(name = "contact_first_name")
    private String firstName;

    @ColumnInfo(name = "contact_last_name")
    private String lastName;

    @ColumnInfo(name = "contact_email")
    private String email;

    @ColumnInfo(name = "contact_phone_number")
    private String phoneNumber;


    public Contact(long id, String firstName, String lastName, String email, String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
