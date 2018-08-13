package com.bakingapp.lacourt.bakingapp.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bakingapp.lacourt.bakingapp.R;
import com.bakingapp.lacourt.bakingapp.model.Step;

import java.util.ArrayList;
import java.util.List;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {

    private StepOnClickHandler stepOnClickHandler;

    private ArrayList<Step> steps;

    public StepsAdapter(StepOnClickHandler stepOnClickHandler, List<Step> steps){
        this.stepOnClickHandler = stepOnClickHandler;
        this.steps = new ArrayList<>();
        this.steps.addAll(steps);
    }

    public static class StepsViewHolder extends RecyclerView.ViewHolder{

        public TextView tvShortDescription;
        public TextView tvDescription;
        public TextView tvTapForVideo;
        public LinearLayout stepLayout;

        public StepsViewHolder(View view) {
            super(view);
            tvShortDescription = (TextView) view.findViewById(R.id.tv_short_description);
            tvDescription = (TextView) view.findViewById(R.id.tv_description);
            tvTapForVideo = (TextView) view.findViewById(R.id.tv_tap_for_video);
            stepLayout = (LinearLayout) view.findViewById(R.id.step_layout);
        }
    }

    @Override
    public StepsAdapter.StepsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.step_layout, parent, false);

        return new StepsViewHolder(view);

    }

    @Override
    public void onBindViewHolder(StepsAdapter.StepsViewHolder holder, final int position) {

        if(position == 0) {
            holder.tvShortDescription.setText(R.string.recipe_introduction);
            holder.tvDescription.setText(R.string.recipe_introduction_description);
            holder.tvDescription.setGravity(Gravity.CENTER);
            holder.tvTapForVideo.setTextColor(Color.parseColor("#5183d1"));
            holder.tvTapForVideo.setText(R.string.tap_for_video);
        } else {
            holder.tvShortDescription.setText(steps.get(position).getShortDescription());
            holder.tvDescription.setText(steps.get(position).getDescription());

            if(steps.get(position).getVideoURL().isEmpty()) {
                holder.tvTapForVideo.setTextColor(Color.parseColor("#9b9b9b"));
                holder.tvTapForVideo.setText(R.string.no_video_to_show);
            } else {
                holder.tvTapForVideo.setTextColor(Color.parseColor("#5183d1"));
                holder.tvTapForVideo.setText(R.string.tap_for_video);
            }

        }
        holder.stepLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                stepOnClickHandler.onClick(steps, position);
                Log.d("test", "test" + steps + position + stepOnClickHandler);
            }
        });

    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public interface StepOnClickHandler {
        void onClick(ArrayList<Step> steps, int position);
    }

    public void clear() {
        int size = this.steps.size();
        this.steps.clear();
        notifyItemRangeRemoved(0, size);
    }
}
