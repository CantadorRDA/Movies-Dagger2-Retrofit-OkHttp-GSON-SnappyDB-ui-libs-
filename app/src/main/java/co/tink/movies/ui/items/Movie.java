package co.tink.movies.ui.items;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Cantador on 09.09.17.
 */

public class Movie implements Serializable {

  private String Title;
  private String Image;
  private double Rating;
  @SerializedName("releaseYear")
  private int ReleaseYear;
  private String[] Genre;

  public Movie(){}

  public Movie(
      String Title,
      String Image,
      double Rating,
      int ReleaseYear,
      String[] Genre
  ) {
    this.Title = Title;
    this.Image = Image;
    this.Rating = Rating;
    this.ReleaseYear = ReleaseYear;
    this.Genre = Genre;
  }

  public String getTitle() {
    return Title;
  }

  public String getImage() {
    return Image;
  }

  public double getRating() {
    return Rating;
  }

  public int getReleaseYear() {
    return ReleaseYear;
  }

  public String[] getGenres() {
    return Genre;
  }

  public void setTitle(String Title) {
    this.Title = Title;
  }

  public void setImage(String Image) {
    this.Image = Image;
  }

  public void setRating(double Rating) {
    this.Rating = Rating;
  }

  public void setReleaseYear(int ReleaseYear) {
    this.ReleaseYear = ReleaseYear;
  }

  public void setGenres(String[] Genre) {
    this.Genre = Genre;
  }

}
