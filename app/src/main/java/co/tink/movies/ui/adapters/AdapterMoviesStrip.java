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

public class AdapterMoviesStrip extends RecyclerView.Adapter<AdapterMoviesStrip.ViewHolder> {

  private Context context;
  private List<Movie> list;
  private FragmentMoviesStrip fragment;
  private LinearLayoutManager layoutManager;
  private int selectedPosition = -1;

  private final int VIEW_TYPE_ITEM = 0;
  private final int VIEW_TYPE_LOADING = 1;

  @Override
  public void onViewAttachedToWindow(AdapterMoviesStrip.ViewHolder holder) {
    super.onViewAttachedToWindow(holder);
    if (getItemViewType(holder.getAdapterPosition()) == VIEW_TYPE_LOADING) {
      fragment.moviesStrip();
    }

  }


  public AdapterMoviesStrip(Context context, FragmentMoviesStrip fragment, LinearLayoutManager layoutManager, List<Movie> list) {
    this.context = context;
    this.fragment = fragment;
    this.list = list;
    this.layoutManager = layoutManager;
  }

  @Override
  public AdapterMoviesStrip.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    if (viewType == VIEW_TYPE_ITEM) {
      View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_movie_card, viewGroup, false);
      return new AdapterMoviesStrip.ViewHolder(view);
    } else if (viewType == VIEW_TYPE_LOADING) {
      View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_movie_card_loading, viewGroup, false);
      return new AdapterMoviesStrip.ViewHolder(view);
    }
    return null;
  }

  public int getItemCount() {
    return (null != list ? list.size() + 1 : 1);
  }

  @Override
  public int getItemViewType(int position) {
    return position == list.size() ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
  }

  @Override
  public void onBindViewHolder(final AdapterMoviesStrip.ViewHolder holder, int i) {
    final View itemView = holder.itemView;

    itemView.setScaleX(1f);
    itemView.setScaleY(1f);

    if (getItemViewType(i) == VIEW_TYPE_ITEM) {

      final Movie movie = list.get(holder.getAdapterPosition());

      holder.movieTitle.setText(movie.getTitle());
      Glide.with(context).load(movie.getImage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.moviePoster);
      holder.movieRating.setText(String.valueOf(movie.getRating()));
      holder.movieYear.setText(String.valueOf(movie.getReleaseYear()));

      if (holder.getAdapterPosition() == selectedPosition) {
        Animation anim_out = AnimationUtils.loadAnimation(context, R.anim.scale_out_tv);
        itemView.startAnimation(anim_out);
        anim_out.setFillAfter(true);
      }

      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

          Glide
              .with(context)
              .load(movie.getImage())
              .asBitmap()
              .diskCacheStrategy(DiskCacheStrategy.SOURCE)
              .skipMemoryCache(true)
              .into(new SimpleTarget<Bitmap>(100, 100) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                  fragment.setBlurImage(resource);
                }
              });

          notifyItemChanged(selectedPosition);
          selectedPosition = holder.getAdapterPosition();
          Animation anim_in = AnimationUtils.loadAnimation(context, R.anim.scale_in_tv);
          itemView.startAnimation(anim_in);
          anim_in.setFillAfter(true);
          layoutManager.scrollToPosition(selectedPosition);
        }
      });

    }

  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    protected TextView movieTitle;
    protected ImageView moviePoster;
    protected TextView movieRating;
    protected TextView movieYear;

    public ViewHolder(View view) {
      super(view);
      this.movieTitle = view.findViewById(R.id.movie_title);
      this.moviePoster = view.findViewById(R.id.movie_poster);
      this.movieRating = view.findViewById(R.id.movie_rating);
      this.movieYear = view.findViewById(R.id.movie_year);
    }
  }

}
