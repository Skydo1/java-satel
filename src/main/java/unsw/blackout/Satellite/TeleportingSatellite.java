package unsw.blackout.Satellite;

import unsw.utils.Angle;
public class TeleportingSatellite extends Satellite {
    /*
     * private int velocity = 1000; private int range = 200000; private boolean
     * desktop = true;
     */
    public static int velocity = 1000;
    public static int range = 200000;
    public static int filecanstore = 1000;
    public static int bytescanstore = 200;
    public static int recrate = 15;
    public static int sendrate = 10;
    public static boolean desktop = true;

    private boolean antidirect = true;
    private Angle newangle = Angle.fromRadians(super.getVelocity() / super.getHeight());

    public TeleportingSatellite(String sID, double height, Angle sangle) {
        super(sID, height, sangle, velocity, range, desktop, filecanstore, bytescanstore, recrate, sendrate);
    }

    @Override
    // remebr to test if the satellies created at 0 degeree
    public void move() {
        boolean teleported = false;
        Angle check180 = Angle.fromDegrees(180);
        if (((super.getSangle().add(newangle).compareTo(check180) != -1 && antidirect == true)
                || (super.getSangle().subtract(newangle).compareTo(check180) != 1 && antidirect != true))
                && super.getSangle().compareTo(Angle.fromDegrees(0)) != 0) {
            // teleport happende change direct
            teleported = true;
        }
        if (teleported) {
            this.antidirect = !antidirect;
            super.setSangle(new Angle());
        } else {
            if (antidirect) {
                super.setSangle(super.getSangle().add(newangle));
            } else {
                if (super.getSangle().compareTo(Angle.fromDegrees(0)) == 0) {
                    super.setSangle(Angle.fromDegrees(360).subtract(newangle));
                } else {
                    super.setSangle(super.getSangle().subtract(newangle));
                }
            }
        }
    }

}
