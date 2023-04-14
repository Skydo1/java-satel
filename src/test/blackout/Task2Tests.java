package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.blackout.FileTransferException;
import unsw.response.models.FileInfoResponse;
import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.Arrays;

import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

@TestInstance(value = Lifecycle.PER_CLASS)
public class Task2Tests {
    @Test
    public void testpostion_change_aftersimulate() {
        BlackoutController controller = new BlackoutController();
        
            controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(320));
            controller.createSatellite("Satellite2", "TeleportingSatellite", 1000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(177));
            controller.createSatellite("Satellite3", "RelaySatellite", 1000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(310));

        controller.simulate();
        assertEquals(
                "EntityInfoResponse [files={}, height=70911.0, id=Satellite1, positionRadians=5.549798145310935, positionDegrees=317.9800108758485, type=StandardSatellite]",
                controller.getInfo("Satellite1").toString());
        assertEquals(
                "EntityInfoResponse [files={}, height=70911.0, id=Satellite2, positionRadians=3.1033349604583313, positionDegrees=177.8079956496606, type=TeleportingSatellite]",
                controller.getInfo("Satellite2").toString());
        assertEquals(
                "EntityInfoResponse [files={}, height=70911.0, id=Satellite3, positionRadians=5.38936740453987, positionDegrees=308.78800652550916, type=RelaySatellite]",
                controller.getInfo("Satellite3").toString());
    }

    @Test
    public void checkteleport_thengodifferetn_direct() {
        BlackoutController controller = new BlackoutController();
        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite1", "TeleportingSatellite", 1000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(179.8));
        });
        controller.simulate(1);
        // the postion is no at 0
        assertEquals(
                "EntityInfoResponse [files={}, height=70911.0, id=Satellite1, positionRadians=0.0, positionDegrees=0.0, type=TeleportingSatellite]",
                controller.getInfo("Satellite1").toString());
        assertDoesNotThrow(() -> controller.createSatellite("Satellite2", "TeleportingSatellite",
                1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0)));
        controller.simulate();
        assertEquals(
                "EntityInfoResponse [files={}, height=70911.0, id=Satellite1, positionRadians=6.269083122751218, positionDegrees=359.1920043503394, type=TeleportingSatellite]",
                controller.getInfo("Satellite1").toString());
        assertEquals(
                "EntityInfoResponse [files={}, height=70911.0, id=Satellite2, positionRadians=0.014102184428367954, positionDegrees=0.8079956496605932, type=TeleportingSatellite]",
                controller.getInfo("Satellite2").toString());
    }

    @Test
    public void realy_direction_check() {
        BlackoutController controller = new BlackoutController();
        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite1", "RelaySatellite", 1000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(160));
            controller.createSatellite("Satellite2", "RelaySatellite", 1000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(345));
            controller.createSatellite("Satellite3", "RelaySatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(32));
            controller.createSatellite("Satellite4", "RelaySatellite", 1000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(148));
        });
        controller.simulate(3);
        // the postion is no at 0
        assertEquals(
                "EntityInfoResponse [files={}, height=70911.0, id=Satellite1, positionRadians=2.7290669732632713, positionDegrees=156.3640195765273, type=RelaySatellite]",
                controller.getInfo("Satellite1").toString());
        assertEquals(
                "EntityInfoResponse [files={}, height=70911.0, id=Satellite2, positionRadians=6.084845749308092, positionDegrees=348.63598042347263, type=RelaySatellite]",
                controller.getInfo("Satellite2").toString());
        assertEquals(
                "EntityInfoResponse [files={}, height=70911.0, id=Satellite3, positionRadians=0.6219651905658413, positionDegrees=35.635980423472674, type=RelaySatellite]",
                controller.getInfo("Satellite3").toString());
        assertEquals(
                "EntityInfoResponse [files={}, height=70911.0, id=Satellite4, positionRadians=2.519627463023952, positionDegrees=144.36401957652734, type=RelaySatellite]",
                controller.getInfo("Satellite4").toString());
    }

    @Test
    public void relay_in_between() {
        BlackoutController controller = new BlackoutController();
        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite1", "RelaySatellite", 1000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(188));
        });

        controller.simulate(1);
        assertEquals(
                "EntityInfoResponse [files={}, height=70911.0, id=Satellite1, positionRadians=3.2600657171067877, positionDegrees=186.78800652550913, type=RelaySatellite]",
                controller.getInfo("Satellite1").toString());

        controller.simulate(41);
        // the postion is no at 0
        assertEquals(
                "EntityInfoResponse [files={}, height=70911.0, id=Satellite1, positionRadians=2.4773944813323654, positionDegrees=141.94424796934615, type=RelaySatellite]",
                controller.getInfo("Satellite1").toString());

        controller.simulate(45);
        assertEquals(
                "EntityInfoResponse [files={}, height=70911.0, id=Satellite1, positionRadians=3.217759163821684, positionDegrees=184.36401957652734, type=RelaySatellite]",
                controller.getInfo("Satellite1").toString());

    }

    @Test
    public void testEntitiesInRange() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to
        // download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(320));
            controller.createSatellite("Satellite2", "StandardSatellite", 1000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(315));
            controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
            controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));
            controller.createDevice("DeviceD", "HandheldDevice", Angle.fromDegrees(180));
            controller.createSatellite("Satellite3", "StandardSatellite", 2000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(175));
        });
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceC", "Satellite2"),
                controller.communicableEntitiesInRange("Satellite1"));
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceB", "DeviceC", "Satellite1"),
                controller.communicableEntitiesInRange("Satellite2"));
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2"), controller.communicableEntitiesInRange("DeviceB"));

        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceD"), controller.communicableEntitiesInRange("Satellite3"));
    }

    @Test
    public void Desktop_canbe_in_stand() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to
        // download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite1", "StandardSatellite", 11000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(92));
            controller.createDevice("DeviceA", "Desktop", Angle.fromDegrees(104));
            controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(79));
        });
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceB"), controller.communicableEntitiesInRange("Satellite1"));
    }

    @Test
    public void testifRelaywork() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to
        // download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        assertDoesNotThrow(() -> {
            controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(240));
            controller.createDevice("Deviceb", "DesktopDevice", Angle.fromDegrees(240.3));
            controller.createSatellite("Satellite3", "StandardSatellite", 11000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(175));
        });
        assertListAreEqualIgnoringOrder(Arrays.asList(), controller.communicableEntitiesInRange("Satellite3"));

        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite1", "RelaySatellite", 21000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(230));
        });
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite3", "DeviceA", "Deviceb"),
                controller.communicableEntitiesInRange("Satellite1"));

        // check real still can't let standard connect to Desktop
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1"), controller.communicableEntitiesInRange("Deviceb"));

    }

    @Test
    public void device_sendto_satellite() {
        // just some of them... you'll have to test the rest
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to
        // download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        String msg = "Hey";
        byte[] array = new byte[81];
        byte[] array2 = new byte[71];
        String msg1 = new String(array, Charset.forName("UTF-8"));
        String msg2 = new String(array2, Charset.forName("UTF-8"));
        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite1", "StandardSatellite", 5000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(320));
            controller.createSatellite("Satellite2", "RelaySatellite", 5000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(320));
            controller.createSatellite("Satellite3", "TeleportingSatellite", 5000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(320));

            controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(310));
            controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(312));
            controller.addFileToDevice("DeviceA", "FileAlpha", msg);
            controller.addFileToDevice("DeviceB", "FileAlpha3", msg);

            controller.addFileToDevice("DeviceA", "FileAlpha2", msg1);
            controller.addFileToDevice("DeviceA", "FileAlpha4", msg2);
        });

        // can't send larger bytes
        assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
                () -> controller.sendFile("FileAlpha2", "DeviceA", "Satellite1"));

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceA", "Satellite1"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        // send from another device
        assertThrows(FileTransferException.VirtualFileNoBandwidthException.class,
                () -> controller.sendFile("FileAlpha3", "DeviceB", "Satellite1"));

        // double send to satel
        assertThrows(FileTransferException.VirtualFileNoBandwidthException.class,
                () -> controller.sendFile("FileAlpha", "DeviceA", "Satellite1"));
        // send the one hasn't be transfed yet
        controller.simulate(1);
        assertThrows(FileTransferException.VirtualFileNotFoundException.class,
                () -> controller.sendFile("FileAlpha", "Satellite1", "DeviceB"));

        // can't send to relay
        assertThrows(FileTransferException.VirtualFileNoBandwidthException.class,
                () -> controller.sendFile("FileAlpha", "DeviceA", "Satellite2"));

        // can't send larger bytes
        assertThrows(FileTransferException.VirtualFileNoBandwidthException.class,
                () -> controller.sendFile("FileAlpha2", "DeviceA", "Satellite1"));

    }

    @Test
    public void out_ofrange_work() {
        // just some of them... you'll have to test the rest
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to
        // download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        String msg = "Hey";
        byte[] array = new byte[71];
        byte[] array2 = new byte[31];
        String msg1 = new String(array, Charset.forName("UTF-8"));
        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite1", "StandardSatellite", 5000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(320));
            controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(310));
            controller.addFileToDevice("DeviceA", "hi", msg1);
            controller.createSatellite("Satellite2", "TeleportingSatellite", 5000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(320));
            controller.sendFile("hi", "DeviceA", "Satellite1");
            controller.sendFile("hi", "DeviceA", "Satellite2");
        });

        controller.simulate(40);

        assertEquals(
                "EntityInfoResponse [files={}, height=74911.0, id=Satellite1, positionRadians=4.250136171025246, positionDegrees=243.51486495563844, type=StandardSatellite]",
                controller.getInfo("Satellite1").toString());
        assertEquals(
                "EntityInfoResponse [files={}, height=74911.0, id=Satellite2, positionRadians=5.762567507390509, positionDegrees=330.170797332699, type=TeleportingSatellite]",
                controller.getInfo("Satellite2").toString());

    }

    @Test
    public void teleport_duringsending() {
        // just some of them... you'll have to test the rest
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to
        // download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        String msg = "Hettlot";
        String msg2 = "tsotmantsyt";

        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite1", "TeleportingSatellite", 31000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(179.7));
            controller.createSatellite("Satellite2", "TeleportingSatellite", 31000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(175));
            controller.createSatellite("Satellite3", "StandardSatellite", 31000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(175));

            controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(179));
            controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(179));

            controller.addFileToDevice("DeviceA", "hi", msg);
            controller.addFileToDevice("DeviceA", "hii", msg2);

            controller.sendFile("hi", "DeviceA", "Satellite1");
            controller.sendFile("hii", "DeviceA", "Satellite2");

        });

        controller.simulate(1);

        assertEquals(
                "EntityInfoResponse [files={}, height=100911.0, id=Satellite1, positionRadians=0.0, positionDegrees=0.0, type=TeleportingSatellite]",
                controller.getInfo("Satellite1").toString());
        assertEquals(
                "EntityInfoResponse [files={hi=FileInfoResponse [data=Helo, fileSize=4, filename=hi, isFileComplete=true], hii=FileInfoResponse [data=tsotmantsyt, fileSize=11, filename=hii, isFileComplete=true]}, height=69911.0, id=DeviceA, positionRadians=3.12413936106985, positionDegrees=179.0, type=LaptopDevice]",
                controller.getInfo("DeviceA").toString());
        assertEquals(
                "EntityInfoResponse [files={hii=FileInfoResponse [data=tsotmantsyt, fileSize=11, filename=hii, isFileComplete=true]}, height=100911.0, id=Satellite2, positionRadians=3.0642359134187513, positionDegrees=175.5677852713092, type=TeleportingSatellite]",
                controller.getInfo("Satellite2").toString());

    }

    @Test
    public void otherteleporting() {
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to
        // download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        String msg2 = "tsotmantsyt";

        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite2", "TeleportingSatellite", 31000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(179));
            controller.createSatellite("Satellite3", "StandardSatellite", 31000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(179));

            controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(179));
            controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(179));

            controller.addFileToDevice("DeviceA", "hii", msg2);

            controller.sendFile("hii", "DeviceA", "Satellite2");

        });

        controller.simulate(1);

        assertDoesNotThrow(() -> controller.sendFile("hii", "Satellite2", "DeviceB"));
        assertDoesNotThrow(() -> controller.sendFile("hii", "Satellite2", "Satellite3"));
        assertDoesNotThrow(() -> controller.createSatellite("Satellite1", "TeleportingSatellite",
                31000 + RADIUS_OF_JUPITER, Angle.fromDegrees(179.7)));

        controller.simulate(1);

        assertEquals(
                "EntityInfoResponse [files={hii=FileInfoResponse [data=somansy, fileSize=7, filename=hii, isFileComplete=true]}, height=69911.0, id=DeviceB, positionRadians=3.12413936106985, positionDegrees=179.0, type=LaptopDevice]",
                controller.getInfo("DeviceB").toString());
        assertEquals(
                "EntityInfoResponse [files={hii=FileInfoResponse [data=somansy, fileSize=7, filename=hii, isFileComplete=true]}, height=100911.0, id=Satellite3, positionRadians=3.074590748926476, positionDegrees=176.16107364345402, type=StandardSatellite]",
                controller.getInfo("Satellite3").toString());
        assertEquals(
                "EntityInfoResponse [files={}, height=100911.0, id=Satellite1, positionRadians=0.0, positionDegrees=0.0, type=TeleportingSatellite]",
                controller.getInfo("Satellite1").toString());

    }

    @Test
    public void otherteleporting2() {
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to
        // download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        String msg2 = "tsotmantsyt";

        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite2", "TeleportingSatellite", 31000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(178.8));
            controller.createSatellite("Satellite3", "StandardSatellite", 31000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(178.8));

            controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(179));
            controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(179));

            controller.addFileToDevice("DeviceA", "hii", msg2);

            controller.sendFile("hii", "DeviceA", "Satellite2");

        });

        controller.simulate(1);

        assertDoesNotThrow(() -> controller.sendFile("hii", "Satellite2", "DeviceB"));
        assertDoesNotThrow(() -> controller.sendFile("hii", "Satellite2", "Satellite3"));
        assertDoesNotThrow(() -> controller.createSatellite("Satellite1", "TeleportingSatellite",
                31000 + RADIUS_OF_JUPITER, Angle.fromDegrees(179.7)));

        controller.simulate(2);

        assertEquals(
                "EntityInfoResponse [files={hii=FileInfoResponse [data=tsotmansy, fileSize=9, filename=hii, isFileComplete=true]}, height=69911.0, id=DeviceB, positionRadians=3.12413936106985, positionDegrees=179.0, type=LaptopDevice]",
                controller.getInfo("DeviceB").toString());
        assertEquals(
                "EntityInfoResponse [files={hii=FileInfoResponse [data=tsomansy, fileSize=8, filename=hii, isFileComplete=true]}, height=100911.0, id=Satellite3, positionRadians=3.0463257843508007, positionDegrees=174.54161046518104, type=StandardSatellite]",
                controller.getInfo("Satellite3").toString());

    }

    @Test
    public void testSomeExceptionsForSend() {
        // just some of them... you'll have to test the rest
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to
        // download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        String msg = "Hettlo";
        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite1", "StandardSatellite", 5000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(320));
            controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
            controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));
            controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        });
        assertThrows(FileTransferException.VirtualFileNotFoundException.class,
                () -> controller.sendFile("NonExistentFile", "DeviceC", "Satellite1"));

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
        controller.simulate(msg.length() * 2);
        assertThrows(FileTransferException.VirtualFileAlreadyExistsException.class,
                () -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
    }

    @Test
    public void testMovement() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to
        // download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        assertDoesNotThrow(() -> controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER,
                Angle.fromDegrees(340)));
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(340), 100 + RADIUS_OF_JUPITER,
                "StandardSatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(337.95), 100 + RADIUS_OF_JUPITER,
                "StandardSatellite"), controller.getInfo("Satellite1"));
    }

    @Test
    public void testExample() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to
        // download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        String msg = "Hey";
        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(320));
            controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
            controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));
            controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        });

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        controller.simulate(msg.length() * 2);
        assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "Satellite1", "DeviceB"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
                controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

        controller.simulate(msg.length());
        assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true),
                controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

        // Hints for further testing:
        // - What about checking about the progress of the message half way through?
        // - Device/s get out of range of satellite
        // ... and so on.
    }

    @Test
    public void testRelayMovement() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to
        // download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        assertDoesNotThrow(() -> controller.createSatellite("Satellite1", "RelaySatellite", 100 + RADIUS_OF_JUPITER,
                Angle.fromDegrees(180)));
        // moves in negative direction
        assertEquals(
                new EntityInfoResponse("Satellite1", Angle.fromDegrees(180), 100 + RADIUS_OF_JUPITER, "RelaySatellite"),
                controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(178.77), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(177.54), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(176.31), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));

        controller.simulate(5);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(170.18), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate(24);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(140.72), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));
        // edge case
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(139.49), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));
        // coming back
        controller.simulate(1);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(140.72), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate(5);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(146.85), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));
    }

    @Test
    public void testTeleportingMovement() {
        // Test for expected teleportation movement behaviour
        BlackoutController controller = new BlackoutController();
        assertDoesNotThrow(() -> controller.createSatellite("Satellite1", "TeleportingSatellite",
                10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0)));

        controller.simulate();
        Angle clockwiseOnFirstMovement = controller.getInfo("Satellite1").getPosition();
        controller.simulate();
        Angle clockwiseOnSecondMovement = controller.getInfo("Satellite1").getPosition();
        assertTrue(clockwiseOnSecondMovement.compareTo(clockwiseOnFirstMovement) == 1);

        // It should take 250 simulations to reach theta = 180.
        // Simulate until Satellite1 reaches theta=180
        controller.simulate(250);

        // Verify that Satellite1 is now at theta=0
        assertTrue(controller.getInfo("Satellite1").getPosition().toDegrees() % 360 == 0);
    }
}
