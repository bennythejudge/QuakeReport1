package android.example.com.quakereport1;

/**
 * Created by neo on 09/03/2018.
 */

public class EarthQuake {
    private String mMagn;
    private String mLoc;
    private Long mTime;

    public EarthQuake (String magnitude, String location, Long time) {
        mMagn = magnitude;
        mLoc = location;
        mTime = time;
    }

    public String getMagnitude () {
        return mMagn;
    }
    public String getLocation () {
        return mLoc;
    }
    public Long getTime() {
        return mTime;
    }
}
