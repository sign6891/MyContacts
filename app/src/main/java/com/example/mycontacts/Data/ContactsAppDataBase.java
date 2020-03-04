package com.example.mycontacts.Data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.mycontacts.Contact;

@Database(entities = {Contact.class}, version = 1)
public abstract class ContactsAppDataBase extends RoomDatabase {

    public abstract ContactDAO getContactDAO();

}
