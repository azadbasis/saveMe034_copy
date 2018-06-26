package com.nanosoft.bd.saveme.phoneBook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.nanosoft.bd.saveme.activity.Operations;
import com.nanosoft.bd.saveme.R;
import com.nanosoft.bd.saveme.database.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nanosoft on 30/05/2016.
 */
public class CustomAdapter extends ArrayAdapter<Contact> implements Filterable {
    private List<Contact> contacts;
    private Context context;
    private Filter contactFilter;
    private List<Contact> origContactList;

    DatabaseManager manager;
    Contact contact;


    public CustomAdapter(Context context, List<Contact> contacts) {
        super(context, R.layout.custom_contact_list, contacts);
        this.context = context;
        this.contacts = contacts;
        this.origContactList = contacts;
        manager = new DatabaseManager(getContext());
    }

    /* *********************************
     * We use the holder pattern
	 * It makes the view faster and avoid finding the component
	 * **********************************/
    static class ViewHolder {
        TextView nameTv;
        TextView phoneTv;
        CheckBox checkBox;
    }

    /*search item*/
    public int getCount() {
        return contacts.size();
    }

    public Contact getItem(int position) {
        return contacts.get(position);
    }

    public long getItemId(int position) {
        return contacts.get(position).hashCode();
    }

    /*end search item*/

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        View v = convertView;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.custom_contact_list, null);

            viewHolder = new ViewHolder();
            TextView tvn = (TextView) v.findViewById(R.id.contactName);
            TextView TvPhn = (TextView) v.findViewById(R.id.contactNumber);
            CheckBox checkBox = (CheckBox) v.findViewById(R.id.listItemCheckBox);

            viewHolder.nameTv = tvn;
            viewHolder.phoneTv = TvPhn;
            viewHolder.checkBox = checkBox;
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();
                    contacts.get(getPosition).selected = buttonView.isChecked();
                }
            });

            viewHolder.checkBox.setTag(position);
            v.setTag(viewHolder);


        } else {
            viewHolder = (ViewHolder) v.getTag();
        }


        contact = contacts.get(position);
        viewHolder.nameTv.setText(contact.getContactName());
        viewHolder.phoneTv.setText("" + contact.getContactPhoneNumber());
      //  viewHolder.checkBox.setChecked(contacts.get(position).selected);






        int bitForCheckBoxVisibility = Operations.getIntegerSharedPreference(context,"bitForCheckBoxVisibility",0);
        if (bitForCheckBoxVisibility==0) {
            viewHolder.checkBox.setVisibility(View.GONE);
        }else {
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            String cbState =manager.getCheckBoxStatus(position+1).getCheckBoxStatus();  // contacts.get(position).getCheckBoxStatus();

            if (cbState.equals("1")) {
                viewHolder.checkBox.setChecked(true);
            } else {
                viewHolder.checkBox.setChecked(false);
            }


            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewHolder.checkBox.isChecked()) {
                        contact = new Contact("1");
                        // int productId = contacts.get(position).getId();
                        manager.updateCheckBoxState(position+1, contact);
//                    Toast.makeText(getContext(), "Database " + manager.getCheckBoxStatus(position+1).getCheckBoxStatus(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getContext(), "Position "+position, Toast.LENGTH_SHORT).show();
                    } else {
                        contact = new Contact("0");
                        //  int productId = contacts.get(position).getId();
                        manager.updateCheckBoxState(position+1, contact);
                    }
                }
            });
        }








        return v;
    }


    /*search code*/

    public void resetData() {
        contacts = origContactList;
    }


    	/*
     * We create our filter
	 */

    @Override
    public Filter getFilter() {
        if (contactFilter == null)
            contactFilter = new ContactFilter();

        return contactFilter;
    }

    /*search code*/

    public class ContactFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = origContactList;
                results.count = origContactList.size();
            } else {
                // We perform filtering operation
                List<Contact> nContactList = new ArrayList<Contact>();

                for (Contact c : contacts) {
                    if (c.getContactName().toUpperCase().contains(constraint.toString().toUpperCase()))
                        nContactList.add(c);
                }

                results.values = nContactList;
                results.count = nContactList.size();


            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {


            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                contacts = (List<Contact>) results.values;
                notifyDataSetChanged();
            }


        }
    }


}
