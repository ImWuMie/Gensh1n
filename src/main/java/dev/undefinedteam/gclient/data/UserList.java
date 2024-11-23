package dev.undefinedteam.gclient.data;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class UserList {
    @SerializedName("u")
    public List<User> users = new ArrayList<>();

    @AllArgsConstructor
    public static class User {
        @SerializedName("i")
        public String gameName;
        @SerializedName("c")
        public String nameColor;
        @SerializedName("iu")
        public String gameUUid;
        @SerializedName("g")
        public String group;
        @SerializedName("n")
        public String name;
    }
}
