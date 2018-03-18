package br.com.williamhigino.skipchallenge.screens.cart;

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

public class OrderItemsAdapter extends RecyclerView.Adapter {

    private List<OrderItemModel> items;
    Consumer<OrderItemModel> clickConsumer;

    public OrderItemsAdapter(Consumer<OrderItemModel> clickConsumer) {
        this.clickConsumer = clickConsumer;
        this.items = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_order_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        OrderItemModel item = items.get(position);

        //sets elements
        itemViewHolder.nameText.setText(item.product.name);
        itemViewHolder.quantityText.setText("" + item.quantity);
        itemViewHolder.priceText.setText(item.formattedPrice());
    }

    public void setItems(List<OrderItemModel> orderItemModels) {
        items.clear();
        items.addAll(orderItemModels);
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
        @BindView(R.id.quantity_text)
        TextView quantityText;
        @BindView(R.id.price_text)
        TextView priceText;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            holderCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    OrderItemModel item = items.get(position);
                    if(clickConsumer != null) {
                        try {
                            clickConsumer.accept(item);
                        } catch (Exception e) {
                            Log.e("OrderItemsId", "Exception: " + e.getMessage(), e);
                        }
                    }
                }
            });
        }

    }
}
