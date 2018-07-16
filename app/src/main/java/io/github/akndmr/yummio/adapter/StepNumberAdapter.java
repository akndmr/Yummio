package io.github.akndmr.yummio.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.akndmr.yummio.R;
import io.github.akndmr.yummio.model.Step;

/**
 * Created by Akın DEMİR on 20.06.2018.
 */
public class StepNumberAdapter extends RecyclerView.Adapter<StepNumberAdapter.StepNumberHolder>{

    private final Context mContext;
    private final ArrayList<Step> mStepArrayList;
    public OnStepClick mOnStepClick;
    private int rowNo = -1;

    public StepNumberAdapter(Context context, ArrayList<Step> stepArrayList, OnStepClick onStepClick) {
        this.mContext = context;
        this.mStepArrayList = stepArrayList;
        this.mOnStepClick = onStepClick;
    }

    public class StepNumberHolder extends RecyclerView.ViewHolder {

       @Nullable
       @BindView(R.id.tv_step_number_tablet)
       TextView stepNumber;

        @Nullable
        @BindView(R.id.tv_step_title_tablet)
        TextView stepTitle;

        public StepNumberHolder(View v) {
            super(v);
            ButterKnife.bind(this,v);
        }
    }

    @NonNull
    @Override
    public StepNumberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.single_step_number, parent, false);

        return new StepNumberHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final StepNumberHolder holder, int position) {

        holder.stepTitle.setText(mStepArrayList.get(position).getShortDescription());
        holder.stepNumber.setText(String.valueOf(position+1));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnStepClick.onStepClick(holder.getAdapterPosition());
                rowNo = holder.getAdapterPosition();
                notifyDataSetChanged();
                Toast.makeText(mContext,"TIKLANDI", Toast.LENGTH_SHORT).show();
            }
        });

        if(rowNo == position){
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
        }
        else if(position == 0){
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
        }
        else
        {
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryLight));
        }
    }

    @Override
    public int getItemCount() {
        return mStepArrayList.size();
    }


    public interface OnStepClick {
          void onStepClick(int position);
    }
}
