package unsw.blackout.Devicie;

import java.util.ArrayList;

import unsw.utils.Angle;
import unsw.blackout.File;
import unsw.blackout.FileTransferException;
import unsw.blackout.Satellite.Satellite;
import unsw.blackout.Satellite.TeleportingSatellite;
import unsw.blackout.FileTransferException.VirtualFileAlreadyExistsException;
import java.util.List;

public abstract class Device {
    private String Did;
    private Angle Dangle;
    private int range;
    private ArrayList<File> files = new ArrayList<File>();

    public Device(String did, Angle dangle, int newrange) {
        Did = did;
        Dangle = dangle;
        range = newrange;
    }

    public String getDid() {
        return Did;
    }

    public int getRange() {
        return range;
    }

    public Angle getDangle() {
        return Dangle;
    }

    public boolean isHasfile() {
        if (files.isEmpty()) {
            return false;
        }
        return true;
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public void addfile(File fe) {
        files.add(fe);
    }

    public void creatfile(String filename, String content) {
        files.add(new File(filename, content));
    }

    public void sfile(String filename, Satellite sa) throws FileTransferException {
        for (File f : files) {
            if (f.getFilename().equals(filename)) {
                try {
                    sa.receiving(f, this, null);
                } catch (FileTransferException error) {
                    throw error;
                }

            }
        }
    }

    public void reciving(File gotfile, Satellite fromSa) throws FileTransferException {
        boolean alreadhere = false;
        File recfile = new File(gotfile.getFilename(), "");
        recfile.setshouldbesize(gotfile.getSize());
        recfile.setFromsatellite(fromSa);
        for (File f : files) {
            if (gotfile.getFilename().equals(f.getFilename())) {
                alreadhere = true;
            }
        }
        if (alreadhere) {
            // re direct now rate
            int sendingfilesrate = 0;
            for (File f : fromSa.getFiles()) {
                if (f.isIstransiting_send()) {
                    sendingfilesrate = sendingfilesrate + f.getHowmanyseningto();
                }
            }
            fromSa.setNowsendingrate(fromSa.getoriginsendrate() / sendingfilesrate);
            throw new VirtualFileAlreadyExistsException(gotfile.getFilename());
        }
        gotfile.setIstransiting_send(true);
        gotfile.addHowmanyseningto();
        recfile.setIstransiting_rec(true);
        this.addfile(recfile);

    }

    public void transiting(File f, List<String> rangelist) {
        boolean outofrange = false;
        String transferingcontent = null;
        int assuming_size = 0;
        boolean teleported = false;
        Satellite fromSa = f.getFromsatellite();
        int nowrate = fromSa.getNowsendingrate();
        ArrayList<String> containlist = new ArrayList<String>(rangelist);

        // if is from telport and transported
        if (fromSa instanceof TeleportingSatellite) {
            if (fromSa.getSangle().compareTo(Angle.fromDegrees(0)) == 0) {
                // it teleported
                for (File file : fromSa.getFiles()) {
                    if (f.getFilename().equals(file.getFilename())) {
                        transferingcontent = file.getContent().substring(f.getSize());
                    }
                }
                transferingcontent = transferingcontent.replaceAll("t", "");
                transferingcontent = transferingcontent.replaceAll("T", "");
                f.addContent(transferingcontent);
                f.getFromsatellite().transitdone(f.getFilename());
                f.setIstransiting_rec(false);
                f.setshouldbesize(f.getSize());
                teleported = true;
            }
        }

        if (!containlist.contains(this.Did) && !teleported) {
            this.files.remove(f);
            f.getFromsatellite().transitdone(f.getFilename());
            outofrange = true;
        }

        if (!outofrange && !teleported) {
            for (File file : fromSa.getFiles()) {
                if (f.getFilename().equals(file.getFilename())) {
                    transferingcontent = file.getContent();
                    assuming_size = file.getSize();
                }
            }
            if ((assuming_size - f.getSize()) <= nowrate) {
                f.setContent(transferingcontent);
                f.getFromsatellite().transitdone(f.getFilename());
                f.setIstransiting_rec(false);
            } else {
                f.addContent(transferingcontent.substring(f.getSize(), nowrate + f.getSize()));
                if (assuming_size == f.getSize()) {
                    f.getFromsatellite().transitdone(f.getFilename());
                    f.setIstransiting_rec(false);
                }
            }

        }
    }

    public void transitdone(String filename) {
        for (File f : files) {
            if (f.getFilename().equals(filename)) {
                f.setIstransiting_send(false);
                f.subHowmanyseningto();
            }
        }
    }
}
