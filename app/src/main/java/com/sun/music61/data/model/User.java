package com.sun.music61.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;

public final class User implements Parcelable {

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @NonNull
    private String mId;

    @NonNull
    private String mUsername;

    private String mAvatarUrl;

    interface JSONKey {
        String ID = "id";
        String USERNAME = "username";
        String AVATAR_URL = "avatar_url";
    }

    public User(@NonNull String id, @NonNull String username) {
        mId = id;
        mUsername = username;
    }

    private User(Parcel in) {
        mId = Objects.requireNonNull(in.readString());
        mUsername = Objects.requireNonNull(in.readString());
        mAvatarUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mUsername);
        parcel.writeString(mAvatarUrl);
    }

    public User(JSONObject obj) throws JSONException {
        mId = obj.getString(JSONKey.ID);
        mUsername = obj.getString(JSONKey.USERNAME);
        mAvatarUrl = obj.getString(JSONKey.AVATAR_URL);
    }

    public String getId() {
        return mId;
    }

    @NonNull
    public String getUsername() {
        return mUsername;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }
}
