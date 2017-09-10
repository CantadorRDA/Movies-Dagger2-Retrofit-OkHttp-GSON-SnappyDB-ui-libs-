package co.tink.movies.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.List;

import co.tink.movies.R;
import co.tink.movies.ui.fragments.FragmentMoviesStrip;
import co.tink.movies.ui.items.Movie;

/**
 * Created by Cantador on 09.09.17.
 */

public class AdapterGenres extends RecyclerView.Adapter<AdapterGenres.ViewHolder> {

  private Context context;
  private List<String> list;
  private FragmentMoviesStrip fragment;
  private LinearLayoutManager layoutManager;
  private RecyclerView parentRecycler;
  private int selectedPosition = -1;

  public AdapterGenres(Context context, FragmentMoviesStrip fragment, LinearLayoutManager layoutManager, List<String> list, RecyclerView parentRecycler) {
    this.context = context;
    this.fragment = fragment;
    this.list = list;
    this.layoutManager = layoutManager;
    this.parentRecycler = parentRecycler;
  }

  @Override
  public AdapterGenres.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_genre, viewGroup, false);
    return new AdapterGenres.ViewHolder(view);
  }

  public int getItemCount() {
    return list.size();
  }

  @Override
  public void onBindViewHolder(final AdapterGenres.ViewHolder holder, int i) {
    final View itemView = holder.itemView;

    final String genre = list.get(holder.getAdapterPosition());
    if (selectedPosition == holder.getAdapterPosition()) {
      itemView.setSelected(false);
    }

    holder.genreRowText.setText(genre);

    holder.genreRowText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (selectedPosition!=-1) {
          parentRecycler.findViewHolderForAdapterPosition(selectedPosition).itemView.setSelected(false);
        }
//        notifyItemChanged(selectedPosition);
        selectedPosition = holder.getAdapterPosition();
        layoutManager.scrollToPosition(holder.getAdapterPosition());
        fragment.setGenre(genre);
        holder.genreRowText.setSelected(true);
      }
    });

  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    protected TextView genreRowText;

    public ViewHolder(View view) {
      super(view);
      this.genreRowText = view.findViewById(R.id.genre_row_text);
    }
  }

}
