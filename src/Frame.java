package src;

public class Frame {

    public final int tag;
    public final String username;
    public final int x;
    public final int y;
    public final int r;
    public final byte[] data;

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
    }
}
