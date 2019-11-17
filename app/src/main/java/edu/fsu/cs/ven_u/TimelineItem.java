package edu.fsu.cs.ven_u;

public class TimelineItem {
    private String mTitle;
    private String mVisibility;

    public TimelineItem(String title, String visibility) {
        mTitle = title;
        mVisibility = visibility;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getVisibility() {
        return mVisibility;
    }
}
