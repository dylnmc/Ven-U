package edu.fsu.cs.ven_u;

public class TimelineItem {
    private String mTitle;
    private String mVisibility;
    private String mCreator;
    private String mDescription;

    public TimelineItem(String title, String visibility, String creator, String description) {
        mTitle = title;
        mVisibility = visibility;
        mCreator = creator;
        mDescription = description;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getVisibility() {
        return mVisibility;
    }

    public String getCreator() {
        return mCreator;
    }

    public String getDescription() {
        return mDescription;
    }
}
