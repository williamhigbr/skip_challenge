package br.com.williamhigino.skipchallenge.screens.orders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.williamhigino.skipchallenge.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

public class OrdersAdapter extends RecyclerView.Adapter {

    private List<OrderModel> items;
    Consumer<OrderModel> clickConsumer;

    public OrdersAdapter(Consumer<OrderModel> clickConsumer) {
        this.clickConsumer = clickConsumer;
        this.items = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_order, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        OrderModel item = items.get(position);

        //sets elements
        itemViewHolder.nameText.setText(""+item.storeId);
        itemViewHolder.dateText.setText(item.date.toString());
        itemViewHolder.priceText.setText(item.formattedPrice());
        itemViewHolder.statusText.setText(item.formattedStatus());
    }

    public void setItems(List<OrderModel> orderModels) {
        items.clear();
        items.addAll(orderModels);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.holder_card_view)
        CardView holderCardView;
        @BindView(R.id.name_text)
        TextView nameText;
        @BindView(R.id.date_text)
        TextView dateText;
        @BindView(R.id.price_text)
        TextView priceText;
        @BindView(R.id.status_text)
        TextView statusText;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            holderCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    OrderModel item = items.get(position);
                    if(clickConsumer != null) {
                        try {
                            clickConsumer.accept(item);
                        } catch (Exception e) {
                            Log.e("ProductsAdapter", "Exception: " + e.getMessage(), e);
                        }
                    }
                }
            });
        }

    }
}
