package unsw.blackout.Satellite;

import unsw.utils.Angle;

public class RelaySatellite extends Satellite {
    /*
     * private int velocity = 1500; private int range = 300000; private boolean
     * desktop = true;
     */
    public static int velocity = 1500;
    public static int range = 300000;
    public static int nofile = -1;
    public static boolean desktop = true;


    private boolean anticlockdire = false;
    private Angle newangle = Angle.fromRadians(super.getVelocity() / super.getHeight());

    public RelaySatellite(String sID, double height, Angle sangle) {
        super(sID, height, sangle, velocity, range, desktop, nofile, nofile, nofile, nofile);
    }

    @Override
    public void move() {
        Angle currentangle = super.getSangle();

        if (currentangle.compareTo(Angle.fromDegrees(345)) == -1
                && currentangle.compareTo(Angle.fromDegrees(190)) != -1) {
            this.anticlockdire = false;
        } else if (currentangle.compareTo(Angle.fromDegrees(345)) != -1
                || currentangle.compareTo(Angle.fromDegrees(140)) != 1) {
            this.anticlockdire = true;
        }
        if (!anticlockdire) {
            super.setSangle(currentangle.subtract(newangle));
        } else {
            if (currentangle.add(newangle).compareTo(Angle.fromDegrees(360)) != -1) {
                currentangle = currentangle.subtract(Angle.fromDegrees(360));
            }
            super.setSangle(currentangle.add(newangle));
        }
    }

}
