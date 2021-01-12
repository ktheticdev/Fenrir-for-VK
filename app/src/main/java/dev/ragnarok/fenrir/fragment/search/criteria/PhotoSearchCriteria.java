package dev.ragnarok.fenrir.fragment.search.criteria;

import android.os.Parcel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import dev.ragnarok.fenrir.R;
import dev.ragnarok.fenrir.fragment.search.options.SimpleGPSOption;
import dev.ragnarok.fenrir.fragment.search.options.SimpleNumberOption;
import dev.ragnarok.fenrir.fragment.search.options.SpinnerOption;

public final class PhotoSearchCriteria extends BaseSearchCriteria {

    public static final int KEY_SORT = 1;
    public static final int KEY_RADIUS = 2;
    public static final int KEY_GPS = 3;

    public static final Creator<PhotoSearchCriteria> CREATOR = new Creator<PhotoSearchCriteria>() {
        @Override
        public PhotoSearchCriteria createFromParcel(Parcel in) {
            return new PhotoSearchCriteria(in);
        }

        @Override
        public PhotoSearchCriteria[] newArray(int size) {
            return new PhotoSearchCriteria[size];
        }
    };

    public PhotoSearchCriteria(String query) {
        super(query);

        SpinnerOption sort = new SpinnerOption(KEY_SORT, R.string.sorting, true);
        sort.available = new ArrayList<>(2);
        sort.available.add(new SpinnerOption.Entry(0, R.string.likes));
        sort.available.add(new SpinnerOption.Entry(1, R.string.by_date_added));
        appendOption(sort);

        appendOption(new SimpleNumberOption(KEY_RADIUS, R.string.radius, true, 5000));
        appendOption(new SimpleGPSOption(KEY_GPS, R.string.gps, true));
    }

    private PhotoSearchCriteria(Parcel in) {
        super(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @NotNull
    @Override
    public PhotoSearchCriteria clone() throws CloneNotSupportedException {
        return (PhotoSearchCriteria) super.clone();
    }
}