package com.example.mycontacts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycontacts.Data.ContactsAppDataBase;
import com.example.mycontacts.Model.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ContactsAdapter contactsAdapter;
    private ArrayList<Contact> contactArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ContactsAppDataBase contactsAppDataBase;
/////////////
    SimpleItemTouchHelperCallback callback;
    ItemTouchHelper itemTouchHelper;
////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);

        contactsAppDataBase = Room.databaseBuilder(getApplicationContext(), ContactsAppDataBase.class,
                "ContactDB").build();

        new GetAllContactAsyncTask().execute();

        contactsAdapter = new ContactsAdapter(this, contactArrayList, MainActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contactsAdapter);
////////////////////
        callback = new SimpleItemTouchHelperCallback(contactsAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
////////////////////


        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAndEditContacts(false, null, -1);
            }
        });
    }

    public void addAndEditContacts(final boolean isUpdate, final Contact contact, final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.layout_add_contact, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(view);

        TextView newContactTitle = view.findViewById(R.id.newContactTitle);
        final EditText firstNameEditText = view.findViewById(R.id.firstNameEditText);
        final EditText lastNameEditText = view.findViewById(R.id.lastNameEditText);
        final EditText emailEditText = view.findViewById(R.id.emailEditText);
        final EditText phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText);

        newContactTitle.setText(!isUpdate ? "Add Contact" : "Edit Contact");

        if (isUpdate && contact != null) {
            firstNameEditText.setText(contact.getFirstName());
            lastNameEditText.setText(contact.getLastName());
            emailEditText.setText(contact.getEmail());
            phoneNumberEditText.setText(contact.getPhoneNumber());
        }

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(isUpdate ? "Update" : "Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton(isUpdate ? "Delete" : "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isUpdate) {
                    deleteContact(contact, position);
                } else {
                    dialog.cancel();
                }
            }
        });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(firstNameEditText.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter First Name!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(lastNameEditText.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter Last Name", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(emailEditText.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(phoneNumberEditText.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                if (isUpdate && contact != null) {
                    updateContact(firstNameEditText.getText().toString(), lastNameEditText.getText().toString(),
                            emailEditText.getText().toString(), phoneNumberEditText.getText().toString(), position);
                } else {
                    createContact(firstNameEditText.getText().toString(), lastNameEditText.getText().toString(),
                            emailEditText.getText().toString(), phoneNumberEditText.getText().toString());
                }
            }
        });
    }

    private void deleteContact(Contact contact, int position) {

        contactArrayList.remove(position);
        new DeleteContactAsyncTask().execute(contact);

    }

    private void updateContact(String firstName, String lastName, String email, String phoneNumber, int position) {
        Contact contact = contactArrayList.get(position);

        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setEmail(email);
        contact.setPhoneNumber(phoneNumber);

        new UpdateContactAsyncTask().execute(contact);
        contactArrayList.set(position, contact);
    }

    private void createContact(String firstName, String lastName, String email, String phoneNumber) {

        new CreateContactAsyncTask().execute(new Contact(0, firstName, lastName, email, phoneNumber));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private class GetAllContactAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            contactArrayList.addAll(contactsAppDataBase.getContactDAO().getAllContacts());
            //contactArrayList = (ArrayList<Contact>)contactsAppDataBase.getContactDAO().getAllContacts();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            contactsAdapter.notifyDataSetChanged();
        }
    }

    private class CreateContactAsyncTask extends AsyncTask<Contact, Void, Void> {

        @Override
        protected Void doInBackground(Contact... contacts) {
            long id = contactsAppDataBase.getContactDAO().addContact(contacts[0]);
            Contact contact = contactsAppDataBase.getContactDAO().getContact(id);

            if (contact != null) {
                contactArrayList.add(0, contact);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            contactsAdapter.notifyDataSetChanged();
        }
    }

    private class UpdateContactAsyncTask extends AsyncTask<Contact, Void, Void> {

        @Override
        protected Void doInBackground(Contact... contacts) {
            contactsAppDataBase.getContactDAO().updateContact(contacts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            contactsAdapter.notifyDataSetChanged();
        }
    }

    private class DeleteContactAsyncTask extends AsyncTask<Contact, Void, Void> {

        @Override
        protected Void doInBackground(Contact... contacts) {
            contactsAppDataBase.getContactDAO().deleteContact(contacts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            contactsAdapter.notifyDataSetChanged();
        }
    }
}
