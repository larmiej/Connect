package com.example.nfc_intro_project;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by edmondcotterell on 2016-04-21.
 */

public class CardListAdapter extends SelectableAdapter<CardListAdapter.ViewHolder> {
    private ArrayList<Card> mDataset;
    private final Context mContext;
    public SparseBooleanArray selectedItems;
    private ViewHolder.ClickListener clickListener;
    private OnCompleteListener mListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        // each data item is just a string in this case
        private CardView cardView;
        private TextView companyNameView;
        private TextView phoneNumberView;
        private TextView companyEmailView;
        private RelativeLayout cardLayout;
        private ViewHolder.ClickListener listener;

        public ViewHolder(CardView v, ClickListener listener) {
            super(v);
            cardView = v;
            companyNameView = (TextView) v.findViewById(R.id.Company_name);
            phoneNumberView = (TextView) v.findViewById(R.id.Phone_number);
            companyEmailView = (TextView) v.findViewById(R.id.Company_email);
            cardLayout = (RelativeLayout)v.findViewById(R.id.card_layout_sl);
            this.listener = listener;

            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClicked(getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {

            if (listener != null) {
                return listener.onItemLongClicked(getPosition());
            }
            return false;
        }

        public interface ClickListener {
            public void onItemClicked(int position);
            public boolean onItemLongClicked(int position);
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public CardListAdapter(ArrayList<Card> myDataset, Context mContext, ViewHolder.ClickListener clickListener) {
        mDataset = myDataset;
        this.mContext = mContext;
        selectedItems = new SparseBooleanArray();
        this.clickListener = clickListener;

        //implement oncomplete listener to let main activity know when list empty
        try {
            this.mListener = (OnCompleteListener)mContext;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(mContext.toString() + " must implement OnCompleteListener");
        }
    }

    // Create new views (invoked by the cardView manager)
    @Override
    public CardListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_business_card, parent, false);
        ViewHolder vh = new ViewHolder((CardView) v, clickListener);
        return vh;
    }

    // Replace the contents of a view (invoked by the cardView manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        Card listItem = mDataset.get(position);

        //set list name
        TextView listName = (TextView) holder.cardView.findViewById(R.id.Contact_name);
        listName.setText(listItem.getName().toUpperCase());

        TextView listeEmail= (TextView) holder.cardView.findViewById(R.id.Company_email);
        listeEmail.setText(listItem.getEmail());

        TextView listNumber = (TextView) holder.cardView.findViewById(R.id.Phone_number);
        listNumber.setText(listItem.getNumber());

        TextView listCompanyName = (TextView) holder.cardView.findViewById(R.id.Company_name);
        listCompanyName.setText(listItem.getCompany());

        // Highlight the item if it's selected
        if(isSelected(position))
            holder.cardLayout.setBackgroundColor(mContext.getResources().getColor(R.color.select));
        else
            holder.cardLayout.setBackgroundColor(mContext.getResources().getColor(R.color.unselect));

    }

    // Return the size of your dataset (invoked by the cardView manager)
    @Override
    public int getItemCount() {

        //tell main activity to do something each
        // time the list count is updated
        mListener.onComplete(mDataset.size());
        return mDataset.size();
    }

    public void removeItem(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    public int getDataSetSize()
    {
        return mDataset.size();
    }

    //just to let the main activity noe when the list is empty
    public static interface OnCompleteListener {
        public abstract void onComplete(int dataSetSize);
    }

    public void removeItems(List<Integer> positions) {
        // Reverse-sort the list
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        // Split the list in ranges
        while (!positions.isEmpty()) {
            if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1)) {
                    ++count;
                }

                if (count == 1) {
                    removeItem(positions.get(0));
                } else {
                    removeRange(positions.get(count - 1), count);
                }

                for (int i = 0; i < count; ++i) {
                    positions.remove(0);
                }
            }
        }
    }

    private void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i) {
            mDataset.remove(positionStart);
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }
}