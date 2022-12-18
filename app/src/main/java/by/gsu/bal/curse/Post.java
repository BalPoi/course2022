package by.gsu.bal.curse;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Post implements Parcelable {
    public String id;
    public String title;
    public String description;
    public String city;
    public String publicationDate;
    public double cost;
    public String contacts;
    public String userID;

    public ArrayList<String> imagesURLs;


    public Post() {
    }

    public Post(String id, String title, String description, String city,
                String publicationDate, String cost, String contacts, String userID, ArrayList<String> imagesURLs) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.city = city;
        this.publicationDate = publicationDate;
        try {
            this.cost = Double.parseDouble(cost);
        } catch (RuntimeException e) {
            this.cost = 0d;
        }
        this.contacts = contacts;
        this.userID = userID;
        this.imagesURLs = imagesURLs;
    }

    public Post(Parcel  in) {
        this.id = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.city = in.readString();
        this.publicationDate = in.readString();
        this.cost = in.readDouble();
        this.contacts = in.readString();
        this.userID = in.readString();

        this.imagesURLs = new ArrayList<>();
        in.readList(imagesURLs, String.class.getClassLoader());
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }



    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<String> getImagesURLs() {
        return imagesURLs;
    }

    public void setImagesURLs(ArrayList<String> imagesURLs) {
        this.imagesURLs = imagesURLs;
    }

    @Override
    public String toString() {
        return "Post{" +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", city='" + city + '\'' +
                ", publicationDate='" + publicationDate + '\'' +
                ", cost=" + cost +
                ", contacts='" + contacts + '\'' +
                ", userID='" + userID + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(city);
        parcel.writeString(publicationDate);
        parcel.writeDouble(cost);
        parcel.writeString(contacts);
        parcel.writeString(userID);
        parcel.writeList(imagesURLs);
    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

}
