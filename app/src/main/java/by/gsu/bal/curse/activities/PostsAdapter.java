package by.gsu.bal.curse.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;

import by.gsu.bal.curse.Constants;
import by.gsu.bal.curse.MyRecyclerViewInterface;
import by.gsu.bal.curse.Post;
import by.gsu.bal.curse.R;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {
    Context context;
    ArrayList<Post> posts;
    private final MyRecyclerViewInterface recyclerViewInterface;

    public PostsAdapter(Context context, ArrayList<Post> posts, MyRecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.posts = posts;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @NotNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem;
        if (context.getClass() == MyPostsActivity.class) {
            layoutIdForListItem = R.layout.my_post_card;
        } else {
            layoutIdForListItem = R.layout.post_card;
        }


        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(layoutIdForListItem, parent, false);

        PostViewHolder viewHolder = new PostViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.tvPostTitle.setText(post.getTitle());
        holder.tvPostCity.setText(post.getCity());

        LocalDateTime date = LocalDateTime.parse(post.getPublicationDate(), Constants.ISO8601);
        String dateStr = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(date);
        holder.tvPostDate.setText(dateStr);

        double cost = post.getCost();
        String costStr;
        if (cost == (long) cost) {
            costStr = String.format("%d", (long) cost);
        } else {
            costStr = String.format("%.2f", cost);
        }
        costStr += " р.";
        holder.tvPostCost.setText(costStr);


        Picasso.get().load(post.getImagesURLs().get(0)).into(holder.ivPostCover);

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvPostTitle;
        TextView tvPostCost;
        TextView tvPostCity;
        TextView tvPostDate;
        ImageView ivPostCover;

        public PostViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvPostTitle = itemView.findViewById(R.id.tvPostTitle);
            tvPostCost = itemView.findViewById(R.id.tvPostCost);
            tvPostCity = itemView.findViewById(R.id.tvPostCity);
            tvPostDate = itemView.findViewById(R.id.tvPostDate);
            ivPostCover = itemView.findViewById(R.id.ivPostCover);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onClickOnItem(position);
                    }
                }
            });

            if (context.getClass() == MyPostsActivity.class) {
                ImageButton btnEditPost = itemView.findViewById(R.id.btnEditPost);
                ImageButton btnDeletePost = itemView.findViewById(R.id.btnDeletePost);

                btnEditPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        recyclerViewInterface.editPost(position);
                    }
                });
                btnDeletePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        recyclerViewInterface.deletePost(position);
                    }
                });
            }
        }
    }
}
