package io.github.akndmr.yummio.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.akndmr.yummio.R;
import io.github.akndmr.yummio.model.Ingredient;
import io.github.akndmr.yummio.utils.ConstantsUtil;

/**
 * Created by Akın DEMİR on 23.06.2018.
 */
public class RecipeDetailsAdapter extends RecyclerView.Adapter<RecipeDetailsAdapter.RecipeDetailsViewHolder>{

    private final Context mContext;
    private final List<Ingredient> mIngredientList;

    public RecipeDetailsAdapter(Context context, List<Ingredient> ingredientList) {
        this.mContext = context;
        this.mIngredientList = ingredientList;
    }

    public class RecipeDetailsViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.iv_unit_icon)
        ImageView unitIcon;

        @Nullable
        @BindView(R.id.tv_ingredient_name)
        TextView ingredientName;

        @Nullable
        @BindView(R.id.tv_unit_number)
        TextView unitNumber;

        @Nullable
        @BindView(R.id.tv_ingredient_number)
        TextView ingredientRowNumber;

        @Nullable
        @BindView(R.id.tv_unit_long_name)
        TextView ingredientUnitLongName;

        @Nullable
        @BindView(R.id.iv_ingredient_checked)
        ImageView ingredientChecked;

      public RecipeDetailsViewHolder(View v) {
            super(v);
            ButterKnife.bind(this,v);
        }
    }

    @NonNull
    @Override
    public RecipeDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.single_ingredient_card, parent, false);

        return new RecipeDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecipeDetailsViewHolder holder, int position) {
        Ingredient ingredient = mIngredientList.get(position);

        holder.ingredientName.setText(ingredient.getIngredient());
        holder.unitNumber.setText(String.valueOf(ingredient.getQuantity()));
        holder.ingredientRowNumber.setText(String.valueOf(position+1));

        String measure = ingredient.getMeasure();
        Log.d("UNIT_NO MEASURE: ", String.valueOf(measure));
        int unitNo = 0;

        for(int i=0; i < ConstantsUtil.units.length; i++){
            if(measure.equals(ConstantsUtil.units[i])){
                unitNo = i;
                Log.d("UNIT_NO FOR: ", String.valueOf(unitNo));
                break;
            }
        }
        int unitIcon = ConstantsUtil.unitIcons[unitNo];
        Log.d("UNIT_NO: ", String.valueOf(unitIcon));
        String unitLongName = ConstantsUtil.unitName[unitNo];

        holder.unitIcon.setImageResource(unitIcon);
        holder.ingredientUnitLongName.setText(unitLongName);

        final boolean isChecked = false;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.ingredientChecked.getVisibility() == View.GONE){
                    holder.ingredientChecked.setVisibility(View.VISIBLE);
                }
                else{
                    holder.ingredientChecked.setVisibility(View.GONE);
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return mIngredientList.size();
    }
}

