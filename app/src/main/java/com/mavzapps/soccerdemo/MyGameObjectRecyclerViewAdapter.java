package com.mavzapps.soccerdemo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mavzapps.soccerdemo.GameListFragment.OnListFragmentInteractionListener;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * {@link RecyclerView.Adapter} that can display a {@link GameObject} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyGameObjectRecyclerViewAdapter extends RecyclerView.Adapter<MyGameObjectRecyclerViewAdapter.ViewHolder> {

    private final List<GameObject> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final int ODD_VIEW = 0;
    private final int EVEN_VIEW = 1;

    public MyGameObjectRecyclerViewAdapter(List<GameObject> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return mValues.get(position).getViewType();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == ODD_VIEW)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_gameobject_odd, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_gameobject_even, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        String gameNameStatus = mValues.get(position).getGameName() + " - " +mValues.get(position).getGameStatus();
        holder.mGameStatus.setText(gameNameStatus);
        holder.mHomeScore.setText(String.valueOf(mValues.get(position).getHomeTeamScore()));
        holder.mAwayScore.setText(String.valueOf(mValues.get(position).getAwayTeamScore()));
        holder.mHomeName.setText(mValues.get(position).getHomeTeamName());
        holder.mAwayName.setText(mValues.get(position).getAwayTeamName());

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
        String date = format.format(mValues.get(position).getStartDate());
        String location = mValues.get(position).getGameLocation() + " - " + date;

        holder.mLocationTime.setText(location);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mGameStatus;
        public final TextView mHomeScore;
        public final TextView mHomeName;
        public final TextView mAwayScore;
        public final TextView mAwayName;
        public final TextView mLocationTime;
        public GameObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mGameStatus = view.findViewById(R.id.gameStatus);
            mHomeScore = view.findViewById(R.id.homeScore);
            mAwayScore = view.findViewById(R.id.awayScore);
            mHomeName = view.findViewById(R.id.homeName);
            mAwayName = view.findViewById(R.id.awayName);
            mLocationTime = view.findViewById(R.id.locationTime);
        }

        @Override
        public String toString() {
            return super.toString() + "'" + mHomeName.getText() + " vs " + mAwayName.getText() +"'";
        }
    }
}
