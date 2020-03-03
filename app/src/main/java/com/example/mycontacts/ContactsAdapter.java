package com.example.mycontacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycontacts.Model.Contact;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {

    private ArrayList<Contact> contactArrayList;
    private Context context;
    private MainActivity mainActivity;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView firstNameTextView;
        private TextView lastNameTextView;
        private TextView emailTextView;
        private TextView phoneNumberTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            firstNameTextView = itemView.findViewById(R.id.firstNameTextView);
            lastNameTextView = itemView.findViewById(R.id.lastNameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            phoneNumberTextView = itemView.findViewById(R.id.phoneNumberTextView);
        }
    }

    public ContactsAdapter(Context context, ArrayList<Contact> contacts, MainActivity mainActivity) {
        this.context = context;
        this.contactArrayList = contacts;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        final Contact contact = contactArrayList.get(position);

        holder.firstNameTextView.setText(contact.getFirstName());
        holder.lastNameTextView.setText(contact.getLastName());
        holder.emailTextView.setText(contact.getEmail());
        holder.phoneNumberTextView.setText(contact.getPhoneNumber());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.addAndEditContacts(true, contact, position);
            }
        });
    }

    public void onItemDismiss(int position) {
        contactArrayList.remove(position);
        //mainActivity.deleteContact(contact, position);
        notifyItemRemoved(position);
    }

    public void onItemMove(int fromPosition, int toPosition) {
        Contact prev = contactArrayList.remove(fromPosition);
        contactArrayList.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public int getItemCount() {
        return contactArrayList.size();
    }


}
