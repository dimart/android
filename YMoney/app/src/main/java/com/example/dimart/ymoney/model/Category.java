package com.example.dimart.ymoney.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Dmitrii Petukhov on 8/20/15.
 */
public class Category implements Parcelable {
    public Integer id;
    public String title;
    public List<Category> subs;

    private Category(Parcel in) {
        // FIFO.
        id = in.readInt();
        title = in.readString();
        in.readList(subs, null);
    }

    public Category(int id, String title, List<Category> subs) {
        this.id = id;
        this.title = title;
        this.subs = subs;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        // FIFO.
        if (id != null) out.writeInt(id);
        out.writeString(title);
        if (subs != null) out.writeList(subs);
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public String toString() {
        String result = "";
        result += "Category: '" + title + "' ";
        if (id != null) {
            result += "id=" + id + " ";
        }
        if (subs != null && !subs.isEmpty()) {
            for (Category c : subs) {
                result = result + "\n\t";
                result += c.toString();
            }
        }
        return result;
    }
}
