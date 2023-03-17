package unsw.blackout.Satellite;

import java.util.ArrayList;
import unsw.utils.Angle;
import unsw.blackout.File;
import unsw.blackout.FileTransferException;
import unsw.blackout.Devicie.Device;
import java.util.List;

import unsw.blackout.FileTransferException.*;

public abstract class Satellite {
    private String sID;
    private double height;
    private Angle sangle;
    private int velocity;
    private int range;
    private boolean desktop;
    private int filecanstore;
    private int bytescanstore;
    private int recrate;
    private int sendrate;

    private int nowrate;
    private int nowsendingrate;
    private ArrayList<File> files = new ArrayList<File>();

    public Satellite(String sID, double height, Angle sangle, int vel, int ra, boolean dType, int fs, int bs, int rc,
            int sr) {
        this.sID = sID;
        this.height = height;
        this.sangle = sangle;
        this.velocity = vel;
        this.range = ra;
        this.desktop = dType;
        this.bytescanstore = bs;
        this.recrate = rc;
        this.sendrate = sr;
        this.filecanstore = fs;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

    public int getVelocity() {
        return velocity;
    }

    public boolean isDesktop() {
        return desktop;
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public void addfile(File newfile) {
        this.files.add(newfile);
    }

    public int getRange() {
        return range;
    }

    public String getsID() {
        return sID;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Angle getSangle() {
        return sangle;
    }

    public void setSangle(Angle newangle) {
        this.sangle = newangle;
    }

    public int getNowrate() {
        return nowrate;
    }

    public boolean isHasfile() {
        if (files.isEmpty()) {
            return false;
        }
        return true;
    }

    public abstract void move();

    public void transitdone(String filename) {
        for (File f : files) {
            if (f.getFilename().equals(filename)) {
                f.setIstransiting_send(false);
                f.subHowmanyseningto();
            }
        }
    }

    public int getNowsendingrate() {
        return nowsendingrate;
    }

    public int getoriginsendrate() {
        return sendrate;
    }

    public void setNowsendingrate(int rate) {
        this.nowsendingrate = rate;
    }

    public void sendfile(String filename, Device de, Satellite sa) throws FileTransferException {
        int sendingfilesrate = 0;
        for (File f : files) {
            if (f.isIstransiting_send()) {
                sendingfilesrate = sendingfilesrate + f.getHowmanyseningto();
            }
        }
        if (sendingfilesrate + 1 > sendrate) {
            throw new VirtualFileNoBandwidthException("satellite has not enough bandwidth to send the file");
        } else {
            sendingfilesrate = sendingfilesrate + 1;
            nowsendingrate = sendrate / sendingfilesrate;
        }

        for (File f : files) {
            if (f.getFilename().equals(filename)) {
                if (f.isIstransiting_rec()) {
                    // is not finishing recving
                    throw new VirtualFileNotFoundException(filename);
                }
                try {
                    if (de != null) {
                        de.reciving(f, this);
                    } else {
                        sa.receiving(f, null, this);
                    }
                } catch (FileTransferException error) {
                    throw error;
                }

            }
        }
    }

    public void receiving(File gotFile, Device fromthis, Satellite inputsate) throws FileTransferException {
        int recband = 0;
        int nowbytes = 0;
        boolean alreadhere = false;
        File recfile = new File(gotFile.getFilename(), "");
        recfile.setshouldbesize(gotFile.getSize());
        if (filecanstore == -1) {
            throw new VirtualFileNoBandwidthException("Relayatellite has no bandwidth");
        }

        if (fromthis != null) {
            recfile.setFromdevices(fromthis);
        } else {
            recfile.setFromsatellite(inputsate);
        }
        for (File f : files) {
            if (gotFile.getFilename().equals(f.getFilename())) {
                alreadhere = true;
            }

            nowbytes = nowbytes + f.getSize();
            if (f.isIstransiting_rec()) {
                recband++;
            }
        }
        recband = recband + 1;
        nowbytes = nowbytes + gotFile.getSize();
        if (recband > recrate) {
            throw new VirtualFileNoBandwidthException("satellite has not enough bandwidth");
        } else {
            nowrate = recrate / recband;
        }
        if (alreadhere) {
            if (inputsate != null) {
                int sendingfilesrate = 0;
                for (File f : inputsate.getFiles()) {
                    if (f.isIstransiting_send()) {
                        sendingfilesrate = sendingfilesrate + f.getHowmanyseningto();
                    }
                }
                inputsate.setNowsendingrate(inputsate.getoriginsendrate() / sendingfilesrate);
            } else if (inputsate == null && recband > 1) {
                recband = recrate / nowrate;
                recband = recband - 1;
                nowrate = recrate / recband;
            }
            throw new VirtualFileAlreadyExistsException(gotFile.getFilename());
        }

        if (files.size() == filecanstore) {
            throw new VirtualFileNoStorageSpaceException("Max Files Reached");
        }

        if (nowbytes > bytescanstore) {
            throw new VirtualFileNoStorageSpaceException("Max Storage Reached");
        }

        recfile.setIstransiting_rec(true);
        gotFile.setIstransiting_send(true);
        gotFile.addHowmanyseningto();
        addfile(recfile);
    }

    public void transiting(File f, List<String> rangelist) {
        int assuming_size = 0;
        String transferingcontent = null;
        boolean outofrange = false;
        int userate = 0;
        boolean isde = false;
        boolean teleported = false;
        if (f.getFromdevices() != null) {
            Device fromDe = f.getFromdevices();
            for (File file : fromDe.getFiles()) {
                if (f.getFilename().equals(file.getFilename())) {
                    transferingcontent = file.getContent();
                    assuming_size = file.getSize();
                }
                userate = nowrate;
                isde = true;
            }
        } else {
            Satellite fromSa = f.getFromsatellite();
            for (File file : fromSa.getFiles()) {
                if (f.getFilename().equals(file.getFilename())) {
                    transferingcontent = file.getContent();
                    assuming_size = file.getSize();
                }
            }
            if (nowrate <= fromSa.getNowsendingrate()) {
                userate = nowrate;
            } else {
                userate = fromSa.getNowsendingrate();
            }
        }
        if (!isde && (f.getFromsatellite() instanceof TeleportingSatellite || this instanceof TeleportingSatellite)) {
            Satellite fromSa = f.getFromsatellite();
            if (fromSa.getSangle().compareTo(Angle.fromDegrees(0)) == 0) {
                // it teleported
                for (File file : fromSa.getFiles()) {
                    if (f.getFilename().equals(file.getFilename())) {
                        transferingcontent = file.getContent().substring(f.getSize());
                    }
                }
                transferingcontent = transferingcontent.replace("t", "");
                transferingcontent = transferingcontent.replace("T", "");
                f.addContent(transferingcontent);
                f.getFromsatellite().transitdone(f.getFilename());
                f.setIstransiting_rec(false);
                f.setshouldbesize(f.getSize());
                teleported = true;
            }
        } else if (isde && this instanceof TeleportingSatellite) {

            if (sangle.compareTo(Angle.fromDegrees(0)) == 0) {
                Device fromDe = f.getFromdevices();
                for (File file : fromDe.getFiles()) {
                    if (f.getFilename().equals(file.getFilename())) {
                        transferingcontent = file.getContent();
                        transferingcontent = transferingcontent.replaceAll("t", "");
                        transferingcontent = transferingcontent.replaceAll("T", "");
                        file.setContent(transferingcontent);
                        teleported = true;
                    }
                }
                files.remove(f);
                f.getFromdevices().transitdone(f.getFilename());
            }

        }

        if (!rangelist.contains(sID) && !teleported) {
            files.remove(f);
            outofrange = true;
            if (isde) {
                f.getFromdevices().transitdone(f.getFilename());
            } else {
                f.getFromsatellite().transitdone(f.getFilename());
            }
        }
        if (!outofrange && !teleported) {
            if ((assuming_size - f.getSize()) <= userate) {
                f.setContent(transferingcontent);
                f.setIstransiting_rec(false);
                if (isde) {
                    f.getFromdevices().transitdone(f.getFilename());
                } else {
                    f.getFromsatellite().transitdone(f.getFilename());
                }
            } else {
                f.addContent(transferingcontent.substring(f.getSize(), userate + f.getSize()));
                if (assuming_size == f.getSize()) {
                    f.setIstransiting_rec(false);
                    if (isde) {
                        f.getFromdevices().transitdone(f.getFilename());
                    } else {
                        f.getFromsatellite().transitdone(f.getFilename());
                    }
                }
            }

        }

    }

}
