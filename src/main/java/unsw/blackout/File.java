package unsw.blackout;

import unsw.blackout.Devicie.Device;
import unsw.blackout.Satellite.Satellite;

public class File {
    private String filename;
    private String content;
    private int size;
    private boolean istransiting_send = false;
    private boolean istransiting_rec = false;
    private int shouldbesize;
    private Device fromdevice = null;
    private Satellite fromSatellite = null;
    private int howmanyseningto = 0;

    public File(String filename, String content) {
        this.filename = filename;
        this.content = content;
        if (content == null) {
            this.size = 0;
        } else {
            this.size = content.length();
        }
    }

    public int getHowmanyseningto() {
        return howmanyseningto;
    }

    public void addHowmanyseningto() {
        this.howmanyseningto = this.howmanyseningto + 1;
    }

    public void subHowmanyseningto() {
        this.howmanyseningto = this.howmanyseningto - 1;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setContent(String content) {
        this.content = content;
        this.size = content.length();
    }

    public void addContent(String inputcontent) {
        this.content = content + inputcontent;
        this.size = this.content.length();
    }

    public int getshouldbesize() {
        return shouldbesize;
    }

    public void setshouldbesize(int input) {
        this.shouldbesize = input;
    }

    public String getFilename() {
        return filename;
    }

    public String getContent() {
        return content;
    }

    public int getSize() {
        return size;
    }

    public boolean isIstransiting_send() {
        return istransiting_send;
    }

    public void setIstransiting_send(boolean istransiting_send) {
        this.istransiting_send = istransiting_send;
    }

    public boolean isIstransiting_rec() {
        return istransiting_rec;
    }

    public void setIstransiting_rec(boolean istransiting_rec) {
        this.istransiting_rec = istransiting_rec;
    }

    public Device getFromdevices() {
        return fromdevice;
    }

    public Satellite getFromsatellite() {
        return fromSatellite;
    }

    public void setFromdevices(Device de) {
        fromdevice = de;
    }

    public void setFromsatellite(Satellite sa) {
        fromSatellite = sa;
    }

}
