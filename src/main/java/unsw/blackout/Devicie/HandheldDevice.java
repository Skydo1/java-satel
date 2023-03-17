package unsw.blackout.Devicie;

import unsw.utils.Angle;

public class HandheldDevice extends Device {
    public static int range = 50000;

    public HandheldDevice(String did, Angle dangle) {
        super(did, dangle, range);
    }

}
