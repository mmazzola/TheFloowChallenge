package mmazzola.thefloowchallenge;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import mmazzola.thefloowchallenge.activity.MapActivity;
import mmazzola.thefloowchallenge.model.Journey;

public class JourneyAdapter extends RecyclerView.Adapter<JourneyAdapter.ViewHolder> {

    private MapActivity mActivity;
    private List<Journey> mDataset;
    private boolean clickable = true;
    private ViewHolder selectedViewHolder = null;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout mLayout;
        public ViewHolder(LinearLayout v) {
            super(v);
            mLayout = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public JourneyAdapter(List<Journey> myDataset,MapActivity mActivity) {
        this.mDataset = myDataset;
        this.mActivity = mActivity;
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Journey j = mDataset.get(position);
        ((TextView)holder.mLayout.findViewById(R.id.journey_name)).setText(String.format(mActivity.getString(R.string.route_name),position));
        final TextView startTime = holder.mLayout.findViewById(R.id.journey_startTime);
        startTime.setText(j.getStartTime());
        final TextView endTime = holder.mLayout.findViewById(R.id.journey_endTime);
        endTime.setText(j.getEndTime());
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickable) {
                    LinearLayout parent = (LinearLayout) startTime.getParent();
                    boolean becomingVisible = View.VISIBLE != parent.getVisibility();
                    if(becomingVisible) {
                        if(selectedViewHolder != null){
                            clearSelection();
                        }
                        selectedViewHolder = holder;
                        parent.setVisibility(View.VISIBLE);
                        ((LinearLayout)endTime.getParent()).setVisibility(View.VISIBLE);
                        mActivity.updateMapWithJourney(j);
                    }else{
                        clearSelection();
                    }
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setClickable(boolean clickable){
        this.clickable = clickable;
    }

    private void clearSelection(){
        ((LinearLayout)selectedViewHolder.mLayout.findViewById(R.id.journey_startTime).getParent()).setVisibility(View.GONE);
        ((LinearLayout)selectedViewHolder.mLayout.findViewById(R.id.journey_endTime).getParent()).setVisibility(View.GONE);
        mActivity.clearMap();
        selectedViewHolder = null;
    }

    public void addJourney(Journey j){
        this.mDataset.add(j);
    }


}