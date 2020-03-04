package com.example.mycontacts;

/*public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private ContactsAdapter contactsAdapter;
    private ArrayList<Contact> contactArrayList = new ArrayList<>();

    public SimpleItemTouchHelperCallback(ContactsAdapter adapter) {
        contactsAdapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        contactsAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;

    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        contactsAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
}*/
