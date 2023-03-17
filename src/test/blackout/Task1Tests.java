package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import unsw.blackout.BlackoutController;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

@TestInstance(value = Lifecycle.PER_CLASS)
public class Task1Tests {
    @Test
    public void testExample() {
        // Task 1
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 3 devices
        // 2 devices are in view of the satellite
        // 1 device is out of view of the satellite

        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(340));
            controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(30));
            controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(180));
            controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(330));
        });

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1"), controller.listSatelliteIds());
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceA", "DeviceB", "DeviceC"), controller.listDeviceIds());

        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(340), 100 + RADIUS_OF_JUPITER,
                "StandardSatellite"), controller.getInfo("Satellite1"));

        assertEquals(new EntityInfoResponse("DeviceA", Angle.fromDegrees(30), RADIUS_OF_JUPITER, "HandheldDevice"),
                controller.getInfo("DeviceA"));
        assertEquals(new EntityInfoResponse("DeviceB", Angle.fromDegrees(180), RADIUS_OF_JUPITER, "LaptopDevice"),
                controller.getInfo("DeviceB"));
        assertEquals(new EntityInfoResponse("DeviceC", Angle.fromDegrees(330), RADIUS_OF_JUPITER, "DesktopDevice"),
                controller.getInfo("DeviceC"));
    }

    @Test
    public void testremovesuccuessfully() {
        // Task 1
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 3 devices and deletes them
        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(340));
            controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(30));
            controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(180));
            controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(330));
        });

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1"), controller.listSatelliteIds());
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceA", "DeviceB", "DeviceC"), controller.listDeviceIds());

        assertDoesNotThrow(() -> {
            controller.removeDevice("DeviceA");
            controller.removeDevice("DeviceB");
            controller.removeDevice("DeviceC");
            controller.removeSatellite("Satellite1");
        });

        assertListAreEqualIgnoringOrder(Arrays.asList(), controller.listSatelliteIds());
        assertListAreEqualIgnoringOrder(Arrays.asList(), controller.listDeviceIds());
    }

    @Test
    public void basicFileSupport() {
        // Task 1
        BlackoutController controller = new BlackoutController();

        // Creates 1 device and add some files to it
        assertDoesNotThrow(() -> {
            controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(330));
        });
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceC"), controller.listDeviceIds());
        assertEquals(new EntityInfoResponse("DeviceC", Angle.fromDegrees(330), RADIUS_OF_JUPITER, "DesktopDevice"),
                controller.getInfo("DeviceC"));
        assertDoesNotThrow(() -> {
            controller.addFileToDevice("DeviceC", "Hello World", "My first file!");
        });

        Map<String, FileInfoResponse> expected = new HashMap<>();
        expected.put("Hello World",
                new FileInfoResponse("Hello World", "My first file!", "My first file!".length(), true));
        assertEquals(
                new EntityInfoResponse("DeviceC", Angle.fromDegrees(330), RADIUS_OF_JUPITER, "DesktopDevice", expected),
                controller.getInfo("DeviceC"));
    }

    @Test
    public void createdifferenttypesofDevice() {
        BlackoutController controller = new BlackoutController();
        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(340));
            controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(30));
            controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(180));
            controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(330));
        });

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1"), controller.listSatelliteIds());
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceA", "DeviceB", "DeviceC"), controller.listDeviceIds());

    }

    @Test
    public void checkDuplicate_id() {
        BlackoutController controller = new BlackoutController();
        assertThrows(Exception.class, () -> {
            controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(340));
            controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(30));
            controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(180));
        });

    }

    @Test
    public void checkDuplicateafterdelete_id() {
        BlackoutController controller = new BlackoutController();

        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(340));
            controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(30));
            controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(180));
            controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(330));
            controller.removeDevice("DeviceA");
            controller.removeSatellite("Satellite1");
        });

        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(340));
            controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(30));
        });

    }

    @Test
    public void check_samename_difftype() {
        BlackoutController controller = new BlackoutController();

        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(340));
            controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(30));
            controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(180));
            controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(330));
        });

        assertThrows(Exception.class, () -> {
            controller.createSatellite("Satellite1", "RelaySatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(340));
        });
        assertThrows(Exception.class, () -> {
            controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(180));
        });

    }

    @Test
    public void remove_twice_error() {
        BlackoutController controller = new BlackoutController();

        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(340));
            controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(30));
            controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(180));
            controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(330));
        });

        Exception exception = assertThrows(Exception.class, () -> {
            controller.removeDevice("DeviceA");
            controller.removeDevice("DeviceA");
        });
        String expectedMessage = "There is no this device";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        Exception exception2 = assertThrows(Exception.class, () -> {
            controller.removeSatellite("Satellite1");
            controller.removeSatellite("Satellite1");
        });
        String expectedMessage2 = "There is no this satellite";
        String actualMessage2 = exception2.getMessage();
        assertEquals(expectedMessage2, actualMessage2);
    }

    @Test
    public void addfile_todevice() {
        // can't add file to a non existing device
        BlackoutController controller = new BlackoutController();

        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(340));
            controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(30));
        });

        assertDoesNotThrow(() -> {
            controller.addFileToDevice("DeviceA", "file1", "hi, this is file 1");
        });

        // duplicate
        Exception exception = assertThrows(Exception.class, () -> {
            controller.addFileToDevice("DeviceA", "file1", "different file 1");
        });
        String expectedMessage = "filename must be unique";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void delet_and_readd() {
        // can't add file to a non existing device
        BlackoutController controller = new BlackoutController();

        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(340));
            controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(30));
        });

        assertDoesNotThrow(() -> {
            controller.addFileToDevice("DeviceA", "file1", "hi, this is file 1");
            controller.removeDevice("DeviceA");
            controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(30));
            controller.addFileToDevice("DeviceA", "file1", "hi, this is file 1");
        });

    }

    @Test
    public void test_getinfo_fordifftypess() {
        // can't add file to a non existing device
        BlackoutController controller = new BlackoutController();

        assertDoesNotThrow(() -> {
            controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(340));
            controller.createSatellite("Satellite2", "RelaySatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(130));
            controller.createSatellite("Satellite3", "TeleportingSatellite", 100 + RADIUS_OF_JUPITER,
                    Angle.fromDegrees(70));
            controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(30));
            controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(180));
            controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(330));
        });
        assertEquals(
                "EntityInfoResponse [files={}, height=69911.0, id=DeviceA, positionRadians=0.5235987755982988, positionDegrees=29.999999999999996, type=HandheldDevice]",
                controller.getInfo("DeviceA").toString());

        assertEquals(
                "EntityInfoResponse [files={}, height=69911.0, id=DeviceB, positionRadians=3.141592653589793, positionDegrees=180.0, type=LaptopDevice]",
                controller.getInfo("DeviceB").toString());

        assertEquals(
                "EntityInfoResponse [files={}, height=69911.0, id=DeviceC, positionRadians=5.759586531581287, positionDegrees=330.0, type=DesktopDevice]",
                controller.getInfo("DeviceC").toString());

        assertEquals(
                "EntityInfoResponse [files={}, height=70011.0, id=Satellite1, positionRadians=5.934119456780721, positionDegrees=340.0, type=StandardSatellite]",
                controller.getInfo("Satellite1").toString());
        assertEquals(
                "EntityInfoResponse [files={}, height=70011.0, id=Satellite2, positionRadians=2.2689280275926285, positionDegrees=130.0, type=RelaySatellite]",
                controller.getInfo("Satellite2").toString());

        assertEquals(
                "EntityInfoResponse [files={}, height=70011.0, id=Satellite3, positionRadians=1.2217304763960306, positionDegrees=70.0, type=TeleportingSatellite]",
                controller.getInfo("Satellite3").toString());
    }

    @Test
    public void getinfo_whenhasfile() {
        // can't add file to a non existing device
        BlackoutController controller = new BlackoutController();

        assertDoesNotThrow(() -> {
            controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(30));
            controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(180));
            controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(330));
            controller.addFileToDevice("DeviceA", "file1", "hi, this is file 1");
            controller.addFileToDevice("DeviceB", "file2", "hi, this is file 2");
            controller.addFileToDevice("DeviceC", "file3", "hi, this is file 3");
        });
        assertEquals(
                "EntityInfoResponse [files={file1=FileInfoResponse [data=hi, this is file 1, fileSize=18, filename=file1, isFileComplete=true]}, height=69911.0, id=DeviceA, positionRadians=0.5235987755982988, positionDegrees=29.999999999999996, type=HandheldDevice]",
                controller.getInfo("DeviceA").toString());

        assertEquals(
                "EntityInfoResponse [files={file2=FileInfoResponse [data=hi, this is file 2, fileSize=18, filename=file2, isFileComplete=true]}, height=69911.0, id=DeviceB, positionRadians=3.141592653589793, positionDegrees=180.0, type=LaptopDevice]",
                controller.getInfo("DeviceB").toString());

        assertEquals(
                "EntityInfoResponse [files={file3=FileInfoResponse [data=hi, this is file 3, fileSize=18, filename=file3, isFileComplete=true]}, height=69911.0, id=DeviceC, positionRadians=5.759586531581287, positionDegrees=330.0, type=DesktopDevice]",
                controller.getInfo("DeviceC").toString());

    }

}
