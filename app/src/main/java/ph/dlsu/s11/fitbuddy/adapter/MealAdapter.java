package ph.dlsu.s11.fitbuddy.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import ph.dlsu.s11.fitbuddy.R;
import ph.dlsu.s11.fitbuddy.ViewMealActivity;
import ph.dlsu.s11.fitbuddy.model.Meal;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private ArrayList<Meal> mealArrayList;
    private Context context;
    private OnItemClickListener listener;
    private Float totalCalories;
    private Float totalFats;
    private Float totalProtein;
    private Float totalCarbs;
    private Float totalSugar;
    private Float totalSodium;

    private Integer Servings;

    public MealAdapter(Context context, ArrayList<Meal> mealArrayList){
        this.mealArrayList = mealArrayList;
        this.context = context;
    }


    @Override
    public int getItemCount() {
        return mealArrayList.size();
    }



    @Override
    public MealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal, parent, false);
        MealViewHolder mealViewHolder = new MealViewHolder(view);

        return mealViewHolder;
    }

    @Override
    public void onBindViewHolder(final MealViewHolder holder, final int position) {


        totalCalories = Integer.parseInt(mealArrayList.get(position).getServings()) * Float.parseFloat(mealArrayList.get(position).getCaloriesPerServing());
        totalFats = Integer.parseInt(mealArrayList.get(position).getServings()) * Float.parseFloat(mealArrayList.get(position).getFatsPerServing());
        totalProtein = Integer.parseInt(mealArrayList.get(position).getServings()) * Float.parseFloat(mealArrayList.get(position).getProteinPerServing());
        totalCarbs = Integer.parseInt(mealArrayList.get(position).getServings()) * Float.parseFloat(mealArrayList.get(position).getCarbsPerServing());
        totalSugar = Integer.parseInt(mealArrayList.get(position).getServings()) * Float.parseFloat(mealArrayList.get(position).getSugarPerServing());
        totalSodium = Integer.parseInt(mealArrayList.get(position).getServings()) * Float.parseFloat(mealArrayList.get(position).getSodiumPerServing());





        holder.tv_mealName.setText(mealArrayList.get(position).getMealChoice() + " - " + mealArrayList.get(position).getMealName());
        holder.tv_mealInfo.setText(mealArrayList.get(position).getServings() + " servings" + " - "
                + totalCalories.toString()  + " kCal"
                + "\n" + "Fat: " + totalFats.toString() + "g / "
                + "Protein: " + totalProtein.toString() + "g " + "\n" +  "Sugar: " + totalSugar.toString() + "g "
                + "/ Carbs: " + totalCarbs.toString() + "g \n"
                + "Sodium: " + totalSodium.toString() + "g");
        if(mealArrayList.get(position).getImage() != ""){
            Picasso.get().load(mealArrayList.get(position).getImage()).fit().into(holder.iv_item);
        }
        else{
            holder.iv_item.setImageResource(R.drawable.item_no_image);
        }

//        holder.btn_viewOrderList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(v.getContext(), OrderDetailsActivity.class );
//                i.putExtra("Order", orderArrayList.get(position));
//                v.getContext().startActivity(i);
//            }
//        });

//        holder.btn_removeItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                orderArrayList.remove(holder.getAdapterPosition());
//                notifyDataSetChanged();
//            }
//        });
    }

    protected class MealViewHolder extends RecyclerView.ViewHolder {

        TextView tv_mealName;
        TextView tv_mealInfo;
        ImageView iv_item;
        public MealViewHolder(View view){
            super(view);
            tv_mealInfo = view.findViewById(R.id.tv_mealInfo);
            tv_mealName = view.findViewById(R.id.tv_mealName);
            iv_item = view.findViewById(R.id.iv_item);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(view.getContext(), ViewMealActivity.class);
                    intent.putExtra("ChosenMeal", mealArrayList.get(position));
                    view.getContext().startActivity(intent);



                }
            });
        }

    }
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
