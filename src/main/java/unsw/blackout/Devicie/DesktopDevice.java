package unsw.blackout.Devicie;

import unsw.utils.Angle;

public class DesktopDevice extends Device {
    public static int range = 200000;
    public DesktopDevice(String did, Angle dangle) {
        super(did, dangle, range);
    }
    
    
}
