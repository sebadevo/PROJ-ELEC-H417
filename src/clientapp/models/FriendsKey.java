package clientapp.models;

public class FriendsKey {
    private final String key;
    private final String friendName;

    public FriendsKey(String key, String username) {
        this.key = key;
        this.friendName = username;
    }

    public String getKey() {
        return key;
    }

    public String getFriendName() {
        return friendName;
    }

}
