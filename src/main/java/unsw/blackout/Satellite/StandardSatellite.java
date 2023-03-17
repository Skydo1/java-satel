package unsw.blackout.Satellite;

import unsw.utils.Angle;

public class StandardSatellite extends Satellite {
    /*
     * private int velocity = 2500; private int range = 150000; private boolean
     * desktop = false;
     */
    public static int velocity = 2500;
    public static int range = 150000;
    public static int filecanstore = 3;
    public static int bytescanstore = 80;
    public static int recrate = 1;
    public static int sendrate = 1;
    public static boolean desktop = false;


    private Angle newangle = Angle.fromRadians(super.getVelocity() / super.getHeight());

    public StandardSatellite(String sID, double height, Angle sangle) {
        super(sID, height, sangle, velocity, range, desktop, filecanstore, bytescanstore, recrate, sendrate);
    }

    public void move() {
        if (super.getSangle().subtract(newangle).compareTo(Angle.fromDegrees(0)) != 1) {
            super.setSangle(Angle.fromDegrees(360).subtract(newangle));
        } else {
            super.setSangle(super.getSangle().subtract(newangle));
        }
    }

}
