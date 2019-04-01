package com.sun.music61.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.sun.music61.util.CommonUtils;
import java.util.Objects;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

public final class Track implements Parcelable {

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    @NonNull
    private String mId;
    @NonNull
    private String mDuration;
    @NonNull
    private String mGenre;
    private String mTitle;
    private String mDescription;
    private String mOriginalFormat;
    private long mOriginalContentSize;
    @NonNull
    private User mUser;
    private String mArtworkUrl;
    private String mStreamUrl;
    private String mDownloadUrl;
    private boolean mDownloadAble;
    private String mWaveFormUrl;
    private boolean isDownloaded;

    interface JSONKey {
        String ID = "id";
        String DURATION = "duration";
        String GENRE = "genre";
        String TITLE = "title";
        String DESCRIPTION = "description";
        String ORIGINAL_FORMAT = "original_format";
        String ORIGINAL_CONTENT_SIZE = "original_content_size";
        String USER = "user";
        String ART_WORK_URL = "artwork_url";
        String STREAM_URL = "stream_url";
        String DOWNLOAD_URL = "download_url";
        String DOWNLOAD_ABLE = "downloadable";
        String WAVE_FROM_URL = "waveform_url";
    }

    private Track(Parcel in) {
        mId = Objects.requireNonNull(in.readString());
        mDuration = Objects.requireNonNull(in.readString());
        mGenre = Objects.requireNonNull(in.readString());
        mTitle = in.readString();
        mDescription = in.readString();
        mOriginalFormat = in.readString();
        mOriginalContentSize = in.readLong();
        mUser = Objects.requireNonNull(in.readParcelable(getClass().getClassLoader()));
        mArtworkUrl = in.readString();
        mStreamUrl = in.readString();
        mDownloadUrl = in.readString();
        mDownloadAble = in.readByte() != 0;
        mWaveFormUrl = in.readString();
        isDownloaded = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mDuration);
        parcel.writeString(mGenre);
        parcel.writeString(mTitle);
        parcel.writeString(mDescription);
        parcel.writeString(mOriginalFormat);
        parcel.writeLong(mOriginalContentSize);
        parcel.writeParcelable(mUser, i);
        parcel.writeString(mArtworkUrl);
        parcel.writeString(mStreamUrl);
        parcel.writeString(mDownloadUrl);
        parcel.writeByte((byte) (mDownloadAble ? 1 : 0));
        parcel.writeString(mWaveFormUrl);
        parcel.writeByte((byte) (isDownloaded ? 1 : 0));
    }

    public Track(JSONObject obj) throws JSONException {
        mId = obj.getString(JSONKey.ID);
        mDuration = obj.getString(JSONKey.DURATION);
        mGenre = obj.getString(JSONKey.GENRE);
        mTitle = obj.getString(JSONKey.TITLE);
        mDescription = obj.getString(JSONKey.DESCRIPTION);
        mOriginalFormat = obj.getString(JSONKey.ORIGINAL_FORMAT);
        mOriginalContentSize = obj.getLong(JSONKey.ORIGINAL_CONTENT_SIZE);
        mUser = new User(obj.getJSONObject(JSONKey.USER));
        mArtworkUrl = obj.getString(JSONKey.ART_WORK_URL);
        mStreamUrl = obj.getString(JSONKey.STREAM_URL).concat(CommonUtils.AUTHORIZED_SERVER);
        mDownloadUrl = obj.getString(JSONKey.DOWNLOAD_URL);
        mDownloadAble = obj.getBoolean(JSONKey.DOWNLOAD_ABLE);
        mWaveFormUrl = obj.getString(JSONKey.WAVE_FROM_URL);
    }

    public Track(@NonNull String id, @NonNull String duration, String title, String artist,
            String artworkUrl, String path) {
        mId = id;
        mDuration = duration;
        mTitle = title;
        mUser = new User(UUID.randomUUID().toString(), artist);
        mArtworkUrl = artworkUrl;
        mStreamUrl = path;
    }

    public String getId() {
        return mId;
    }

    public String getDuration() {
        return mDuration;
    }

    @NonNull
    public String getGenre() {
        return mGenre;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getOriginalFormat() {
        return mOriginalFormat;
    }

    public long getOriginalContentSize() {
        return mOriginalContentSize;
    }

    @NonNull
    public User getUser() {
        return mUser;
    }

    public String getArtworkUrl() {
        return mArtworkUrl;
    }

    public String getStreamUrl() {
        return mStreamUrl;
    }

    public String getDownloadUrl() {
        return mDownloadUrl;
    }

    public boolean isDownloadAble() {
        return mDownloadAble;
    }

    public String getWaveFormUrl() {
        return mWaveFormUrl;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }
}
