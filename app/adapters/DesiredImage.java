package ua.in.danilichev.imagesearcher.app.adapters;

import android.os.Parcel;
import android.os.Parcelable;

public class DesiredImage implements Parcelable {

    private String imageTitle;
    private String imageUrl;

    public DesiredImage(String imageTitle, String imageUrl) {
        this.imageTitle = imageTitle;
        this.imageUrl = imageUrl;
    }

    public String getImageTitle() { return imageTitle; }

    public String getImageUrl() { return imageUrl; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(imageTitle);
        parcel.writeString(imageUrl);
    }

    public static final Parcelable.Creator<DesiredImage> CREATOR =
            new Parcelable.Creator<DesiredImage>() {

        public DesiredImage createFromParcel(Parcel in) {
            return new DesiredImage(in);
        }

        public DesiredImage[] newArray(int size) {
            return new DesiredImage[size];
        }
    };

    private DesiredImage(Parcel parcel) {
        imageTitle = parcel.readString();
        imageUrl = parcel.readString();
    }
}
