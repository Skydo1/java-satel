package unsw.blackout.Devicie;

import unsw.utils.Angle;

public class LaptopDevice extends Device {
    public static int range = 100000;

    public LaptopDevice(String did, Angle dangle) {
        super(did, dangle, range);
    }

}
