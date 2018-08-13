package com.bakingapp.lacourt.bakingapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bakingapp.lacourt.bakingapp.R;
import com.bakingapp.lacourt.bakingapp.model.RecipesResponse;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private final List<RecipesResponse> recipesResponseList;

    private final RecipeOnClickHandler recipeOnClickHandler;

    public interface RecipeOnClickHandler{
        void onClick(RecipesResponse recipe);
    }

    public RecipeAdapter(List<RecipesResponse> recipesResponses, RecipeOnClickHandler recipeOnClickHandler){
        this.recipesResponseList = recipesResponses;
        this.recipeOnClickHandler = recipeOnClickHandler;
    }

    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.recipe_list_item, parent, false);

        return new ViewHolder(view);

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvRecipeName;
        public TextView tvRecipeIngredientsSample;
        public RelativeLayout layoutRecipeListItem;

        public ViewHolder(View view) {
            super(view);

            layoutRecipeListItem = (RelativeLayout) view.findViewById(R.id.layout_recipe_list_item);
            tvRecipeName = (TextView) view.findViewById(R.id.tv_recipe_name);
            tvRecipeIngredientsSample = (TextView) view.findViewById(R.id.tv_recipe_ingredients_sample);

        }
    }

    @Override
    public void onBindViewHolder(RecipeAdapter.ViewHolder holder, final int position) {

        final RecipesResponse recipe = recipesResponseList.get(position);

        for(int i = 0; i < 4; i++){

            holder.tvRecipeIngredientsSample.append("-" + recipe.getIngredients().get(i).getIngredient() + "\n");
        }

        holder.tvRecipeIngredientsSample.append("...");

        holder.tvRecipeName.setText(recipe.getName());

        holder.layoutRecipeListItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                recipeOnClickHandler.onClick(recipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipesResponseList.size();
    }
}