package com.example.mycontacts;

import androidx.annotation.NonNull;
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
    /*SimpleItemTouchHelperCallback callback;
    ItemTouchHelper itemTouchHelper;*/
////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        contactsAdapter = new ContactsAdapter(contactArrayList, MainActivity.this);
        recyclerView.setAdapter(contactsAdapter);

        contactsAppDataBase = Room.databaseBuilder(getApplicationContext(), ContactsAppDataBase.class,
                "ContactDB").build();

        loadContacts();
////////////////////
       /* callback = new SimpleItemTouchHelperCallback(contactsAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);*/

       new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

           @Override
           public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
               final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
               final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

              //Contact contact =
               return makeMovementFlags(dragFlags, swipeFlags);

           }

           @Override
           public boolean onMove(@NonNull RecyclerView recyclerView,
                                 @NonNull RecyclerView.ViewHolder viewHolder,
                                 @NonNull RecyclerView.ViewHolder target) {
               contactsAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
               return true;
           }

           //Метод отрабатывает свап влево и удаляет из БД и списка Контакт
           @Override
           public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Contact contact = contactArrayList.get(viewHolder.getAdapterPosition());
                deleteContact(contact);
           }
       }).attachToRecyclerView(recyclerView);

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

                            if (isUpdate && contact != null) {
                                updateContact(firstNameEditText.getText().toString(),
                                        lastNameEditText.getText().toString(),
                                        emailEditText.getText().toString(),
                                        phoneNumberEditText.getText().toString(),
                                        position);
                            } else {
                                createContact(firstNameEditText.getText().toString(),
                                        lastNameEditText.getText().toString(),
                                        emailEditText.getText().toString(),
                                        phoneNumberEditText.getText().toString());
                            }
                        }
                    }
                });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void loadContacts() {

        new GetAllContactAsyncTask().execute();

    }

    private void deleteContact(Contact contact) {

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
            contactArrayList = (ArrayList<Contact>)contactsAppDataBase.getContactDAO().getAllContacts();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            contactsAdapter.setContactArrayList(contactArrayList);
        }
    }

    private class CreateContactAsyncTask extends AsyncTask<Contact, Void, Void> {

        @Override
        protected Void doInBackground(Contact... contacts) {
            //long id = contactsAppDataBase.getContactDAO().addContact(contacts[0]);
            contactsAppDataBase.getContactDAO().addContact(contacts[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadContacts();
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
            loadContacts();
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
            loadContacts();
        }
    }
}
