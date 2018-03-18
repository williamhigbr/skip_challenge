package br.com.williamhigino.skipchallenge.screens.products;

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

public class ProductsAdapter extends RecyclerView.Adapter {

    private List<ProductModel> items;
    Consumer<ProductModel> clickConsumer;

    public ProductsAdapter(Consumer<ProductModel> clickConsumer) {
        this.clickConsumer = clickConsumer;
        this.items = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_product, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        ProductModel item = items.get(position);

        //sets elements
        itemViewHolder.nameText.setText(item.name);
        itemViewHolder.descriptionText.setText(item.description);
        itemViewHolder.priceText.setText(item.formattedPrice());
    }

    public void addItems(List<ProductModel> productModels) {
        items.addAll(productModels);
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
        @BindView(R.id.description_text)
        TextView descriptionText;
        @BindView(R.id.price_text)
        TextView priceText;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            holderCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    ProductModel item = items.get(position);
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
