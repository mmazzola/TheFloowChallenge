package mmazzola.thefloowchallenge;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import mmazzola.thefloowchallenge.model.Journey;

public class JourneyAdapter extends RecyclerView.Adapter<JourneyAdapter.ViewHolder> {

    private List<Journey> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout mLayout;
        public ViewHolder(LinearLayout v) {
            super(v);
            mLayout = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public JourneyAdapter(List<Journey> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public JourneyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.journey_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Journey j = mDataset.get(position);
        ((TextView)holder.mLayout.findViewById(R.id.journey_name)).setText("Route: "+position);
        ((TextView)holder.mLayout.findViewById(R.id.journey_startTime)).setText(j.getStartTime());
        ((TextView)holder.mLayout.findViewById(R.id.journey_endTime)).setText(j.getEndTime());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}