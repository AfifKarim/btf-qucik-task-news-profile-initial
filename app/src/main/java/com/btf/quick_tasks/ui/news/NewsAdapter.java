package com.btf.quick_tasks.ui.news;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btf.quick_tasks.R;
import com.btf.quick_tasks.dataBase.model.ArticleResponseModel;
import com.btf.quick_tasks.dataBase.model.NewsResponseModel;
import com.bumptech.glide.Glide;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private final List<ArticleResponseModel> articleList;
    private final Context context;

    public NewsAdapter(Context context, List<ArticleResponseModel> articleList) {
        this.context = context;
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        ArticleResponseModel article = articleList.get(position);

        // Set text with null checks
        holder.title.setText(article.getTitle() != null ? article.getTitle() : "No Title");
        holder.description.setText(article.getDescription() != null ? article.getDescription() : "No Description");
        holder.author.setText(article.getAuthor() != null ? article.getAuthor() : "Unknown Author");
        holder.source.setText(article.getSource() != null && article.getSource().getName() != null ? article.getSource().getName() : "Unknown Source");
        holder.date.setText(article.getPublishedAt() != null ? article.getPublishedAt() : "N/A");
        holder.content.setText(article.getContent() != null ? article.getContent() : "No Content");

        // Load image with Glide
        Glide.with(context)
                .load(article.getUrlToImage())
                .placeholder(R.drawable.placeholder) // Replace with your placeholder
                .error(R.drawable.nophotoavailable) // Fallback if image fails
                .into(holder.image);

        // Open article URL in browser on click
        holder.itemView.setOnClickListener(v -> {
            if (article.getUrl() != null && !article.getUrl().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title, description, author, source, date, content;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.newsImage);
            title = itemView.findViewById(R.id.newsTitle);
            description = itemView.findViewById(R.id.newsDescription);
            author = itemView.findViewById(R.id.newsAuthor);
            source = itemView.findViewById(R.id.newsSource);
            date = itemView.findViewById(R.id.newsDate);
            content = itemView.findViewById(R.id.newsContent);
        }
    }
}