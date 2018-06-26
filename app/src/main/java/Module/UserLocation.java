package Module;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Nanosoft-Android on 8/10/2016.
 */
public class UserLocation {
    public String Name;
    public double Latitude;
    public double Longitue;
    public int smsNo;

    public LatLng getLatLng() {
        return new LatLng(Latitude, Longitue);
    }

    @Override
    public String toString() {
        return Name;
    }

    public int getSmsNo() {
        return smsNo;
    }

    public void setSmsNo(int smsNo) {
        this.smsNo = smsNo;
    }
}
