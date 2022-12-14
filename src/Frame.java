package src;

public class Frame {

    public final int tag;
    public final String username;
    public final int x;
    public final int y;
    public final int r;
    public final byte[] data;

    public final RewardList rewardList;

    public final LocationList locationList;

    /**
     * Creates a <code>Frame</code> containing the specified information.
     * @param tag this frame's type.
     * @param username the user who sent this frame.
     * @param x
     * @param y
     * @param r
     * @param data
     */
    public Frame(int tag, String username, int x, int y, int r, byte[] data) {
        this.tag = tag;
        this.username = username;
        this.x = x;
        this.y = y;
        this.r = r;
        this.data = data;
        this.rewardList = null;
        this.locationList = null;
    }

    public Frame(int tag, RewardList rewardList) {
        this.tag = tag;
        this.rewardList = rewardList;
        this.username = "";
        this.x = 0;
        this.y = 0;
        this.r = 0;
        this.data = null;
        this.locationList = null;

    }

    public Frame(int tag, LocationList locationList) {
        this.tag = tag;
        this.rewardList = null;
        this.username = "";
        this.x = 0;
        this.y = 0;
        this.r = 0;
        this.data = null;
        this.locationList = locationList;

    }
}
