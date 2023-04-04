package unsw.blackout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.CopyOnWriteArrayList;


import unsw.blackout.Devicie.DesktopDevice;
import unsw.blackout.Devicie.Device;
import unsw.blackout.Devicie.HandheldDevice;
import unsw.blackout.Devicie.LaptopDevice;
import unsw.blackout.FileTransferException.VirtualFileNotFoundException;
import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;
import unsw.blackout.Satellite.RelaySatellite;
import unsw.blackout.Satellite.Satellite;
import unsw.blackout.Satellite.StandardSatellite;
import unsw.blackout.Satellite.TeleportingSatellite;

public class BlackoutController {
    private ArrayList<Device> Devices = new ArrayList<Device> ();
    private ArrayList<Satellite> satellites = new ArrayList<Satellite> ();
    private ArrayList<String> Ids = new ArrayList<String>();
    private ArrayList<String> filenames = new ArrayList<String>();

    public void createDevice(String deviceId, String type, Angle position) {
        // TODO: Task 1a)
        for(String id: Ids){
            if(id == deviceId){
                System.out.println("Id must be unique");
            }
        }
        Ids.add(deviceId);


        switch (type) {
        case "HandheldDevice":
            Devices.add(new HandheldDevice(deviceId, position));
            break;
        case "LaptopDevice":
            Devices.add(new LaptopDevice(deviceId, position));
            break;
        case "DesktopDevice":
            Devices.add(new DesktopDevice(deviceId, position));
            break;    
        default:
            break;
        }

    }

    public void removeDevice(String deviceId){
        // TODO: Task 1b)
        // the user can do this , just double check
        if(Devices.isEmpty() || !Ids.stream().anyMatch(id -> id.equals(deviceId))){
            System.out.println("There is no this device");
        }
        
        for(Device de: Devices){
            if(de.getDid().equals(deviceId)){
                Ids.remove(de.getDid());
                for(File f: de.getFiles()){
                    filenames.remove(f.getFilename());
                }
                Devices.remove(de);
                break;
            }
        }
        

    }

    public void createSatellite(String satelliteId, String type, double height, Angle position){
        // TODO: Task 1c)

        for(String id: Ids){
            if(id.equals(satelliteId)){
                System.out.println("Id must be unique");
            }
        }
        Ids.add(satelliteId);


        switch (type) {
        case "StandardSatellite":
            satellites.add(new StandardSatellite(satelliteId, height, position));
            break;
        case "RelaySatellite":
            satellites.add(new RelaySatellite(satelliteId, height, position));
            break;
        case "TeleportingSatellite":
            satellites.add(new TeleportingSatellite(satelliteId, height, position));
            break;    
        default:
            break;
        }



    }

    public void removeSatellite(String satelliteId) {
        // TODO: Task 1d)
        // the user can do this , just double check
        boolean check = false;

        if(satellites.isEmpty() || !Ids.stream().anyMatch(id -> id.equals(satelliteId))){
            System.out.println("There is no this satellite");
        }

        for(Satellite sa: satellites){
            //why is .equal better than ==?????? == doesn't works here even they are the same
            if(sa.getsID().equals(satelliteId)){
                check = true;
                Ids.remove(sa.getsID());
                satellites.remove(sa);
                break;
            }
            
        }
       if(check == false){
        System.out.println("shouldn't reach here");
       }

    }

    public List<String> listDeviceIds() {
        // TODO: Task 1e)
        List<String> devicelist = Devices.stream().map(de ->(de.getDid())).collect(Collectors.toList());
        return devicelist;
    }

    public List<String> listSatelliteIds() {
        // TODO: Task 1f)
        List<String> satellitelist = satellites.stream().map(sa ->(sa.getsID())).collect(Collectors.toList());
        return satellitelist;
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        // TODO: Task 1g)
        for(String file: filenames){
            if(file.equals(filename)){
                System.out.println("filename must be unique");
            }
        }

        filenames.add(filename);
        for(Device de: Devices){
            if(de.getDid().equals(deviceId)){
                de.creatfile(filename, content);
            }
        }
    }

    public EntityInfoResponse getInfo(String id) {
        // TODO: Task 1h)
        boolean check = false;
        for(String Id: Ids){
            if(Id.equals(id)){
                check = true;
            }
        }
        if(check == false){
            return null;
        }

        Quickfunctions info = new Quickfunctions(Devices, satellites);
        return info.getinfos(id);
        
    }

    public void simulate() {
        // TODO: Task 2a)
        // avoid bug
        for(Satellite sa: satellites){
            sa.move();
        }

        for (Satellite sa : satellites) {
            // create a CopyOnWriteArrayList to hold the files
            CopyOnWriteArrayList<File> files = new CopyOnWriteArrayList<>();
            // add files to the list
            files.addAll(sa.getFiles());
            for (File f : files) {
                if (f.isIstransiting_rec()) {
                    if (f.getFromdevices() == null) {
                        sa.transiting(f, communicableEntitiesInRange(f.getFromsatellite().getsID()));
                    } else {
                        sa.transiting(f, communicableEntitiesInRange(f.getFromdevices().getDid()));
                    }
                }
            }
        }
    
        for(Device de: Devices){
             // create a CopyOnWriteArrayList to hold the files
            CopyOnWriteArrayList<File> files = new CopyOnWriteArrayList<>();
            // add files to the list
            files.addAll(de.getFiles());
            for(File f: files){
                if(f.isIstransiting_rec()){
                    de.transiting(f, communicableEntitiesInRange(f.getFromsatellite().getsID()));
                }
            }
        }
    }

    /**
     * Simulate for the specified number of minutes.
     * You shouldn't need to modify this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    public List<String> communicableEntitiesInRange(String id) {
        // TODO: Task 2 b)
        Device nowde = null;
        Satellite nowsa = null;
        boolean isde = false;
        boolean issa = false;
        boolean check = false;
        for(String Id: Ids){
            if(Id.equals(id)){
                check = true;
            }
        }
        if(check == false){
            return null;
        }

        for(Device de: Devices){
            if(de.getDid().equals(id)){
                nowde = de;
                isde = true;
            }
        }

        for(Satellite sa: satellites){
            if(sa.getsID().equals(id)){
                nowsa = sa;
                issa = true;
            }
        }
        Quickfunctions com = new Quickfunctions(Devices, satellites);
        return com.checkrange(nowde, nowsa, isde, issa);

    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        // TODO: Task 2 c)
        boolean check = true;
        for(String file: filenames){
            if(file.equals(fileName)){
                check = false;
            }
        }
        if(check == true){
            throw new VirtualFileNotFoundException(fileName);
        }
        boolean isde = false;
        Device fromD = null;
        Device toD = null;
        Satellite fromS = null;
        Satellite toS = null;
        for(Device de: Devices){
            if(de.getDid().equals(fromId)){
                fromD = de;
                isde = true;
            }
            else if(de.getDid().equals(toId)){
                toD = de;
            }
        }

        for(Satellite sa: satellites){
            if(sa.getsID().equals(fromId)){
                fromS = sa;
            }
            else if(sa.getsID().equals(toId)){
                toS = sa;
            }
        }

        if(isde){
            // device can only send to satellites
            fromD.sfile(fileName, toS);
        }
        else{
            if(toD != null){
                fromS.sendfile(fileName, toD, null);
            }
            else{
                fromS.sendfile(fileName, null, toS);
            }
        }


    }

    public void createDevice(String deviceId, String type, Angle position, boolean isMoving) throws Exception{
        createDevice(deviceId, type, position);
        // TODO: Task 3
    }

    public void createSlope(int startAngle, int endAngle, int gradient) {
        // TODO: Task 3
        // If you are not completing Task 3 you can leave this method blank :)
    }


/* 
    public static void main(String[] args) throws Exception{
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(340));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(30));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(180));
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(330));

        System.out.println(controller.listDeviceIds());

        controller.removeDevice("DeviceA");
        controller.removeDevice("DeviceB"); 
        System.out.println(controller.listDeviceIds());
        
        controller.removeDevice("DeviceC");
        controller.removeSatellite("Satellite1");

       
    }*/
}

