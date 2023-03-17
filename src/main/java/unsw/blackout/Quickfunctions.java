package unsw.blackout;

import unsw.blackout.Satellite.Satellite;
import unsw.response.models.EntityInfoResponse;

import java.util.ArrayList;
import java.util.List;

import unsw.blackout.Devicie.DesktopDevice;
import unsw.blackout.Devicie.Device;
import unsw.utils.MathsHelper;
import java.util.Map;
import unsw.response.models.FileInfoResponse;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;
import java.util.HashMap;
import unsw.blackout.Satellite.RelaySatellite;

public class Quickfunctions {
    private ArrayList<Device> devices = new ArrayList<Device>();
    private ArrayList<Satellite> satellites = new ArrayList<Satellite>();

    public Quickfunctions(ArrayList<Device> devices, ArrayList<Satellite> satellites) {
        this.devices = devices;
        this.satellites = satellites;
    }

    public EntityInfoResponse getinfos(String id) {
        for (Device de : devices) {
            if (de.getDid().equals(id)) {
                if (de.isHasfile()) {
                    Map<String, FileInfoResponse> putinvalue = new HashMap<>();
                    ArrayList<File> filesIn = de.getFiles();
                    for (File file : filesIn) {
                        if (file.isIstransiting_rec()) {
                            putinvalue.put(file.getFilename(), new FileInfoResponse(file.getFilename(),
                                    file.getContent(), file.getshouldbesize(), false));
                        } else {
                            putinvalue.put(file.getFilename(),
                                    new FileInfoResponse(file.getFilename(), file.getContent(), file.getSize(), true));
                        }
                    }
                    return new EntityInfoResponse(de.getDid(), de.getDangle(), RADIUS_OF_JUPITER,
                            de.getClass().getSimpleName(), putinvalue);
                } else {
                    return new EntityInfoResponse(de.getDid(), de.getDangle(), RADIUS_OF_JUPITER,
                            de.getClass().getSimpleName());
                }
            }
        }
        for (Satellite sa : satellites) {
            if (sa.getsID().equals(id)) {
                if (sa.isHasfile()) {
                    Map<String, FileInfoResponse> putinvalue = new HashMap<>();
                    ArrayList<File> filesIn = sa.getFiles();
                    for (File file : filesIn) {
                        if (file.isIstransiting_rec()) {
                            putinvalue.put(file.getFilename(), new FileInfoResponse(file.getFilename(),
                                    file.getContent(), file.getshouldbesize(), false));
                        } else {
                            putinvalue.put(file.getFilename(),
                                    new FileInfoResponse(file.getFilename(), file.getContent(), file.getSize(), true));
                        }
                    }
                    return new EntityInfoResponse(sa.getsID(), sa.getSangle(), sa.getHeight(),
                            sa.getClass().getSimpleName(), putinvalue);
                } else {
                    return new EntityInfoResponse(sa.getsID(), sa.getSangle(), sa.getHeight(),
                            sa.getClass().getSimpleName());
                }
            }
        }

        return null;
    }

    public List<String> checkrange(Device nowde, Satellite nowsa, boolean isde, boolean issa) {
        ArrayList<String> comlist = new ArrayList<String>();
        ArrayList<String> checkdup = new ArrayList<String>();
        if (isde) {
            for (Satellite sa : satellites) {
                if ((nowde instanceof DesktopDevice) && !sa.isDesktop()) {
                    continue;
                }
                if (MathsHelper.isVisible(sa.getHeight(), sa.getSangle(), nowde.getDangle())) {
                    if (MathsHelper.getDistance(sa.getHeight(), sa.getSangle(), nowde.getDangle()) <= nowde
                            .getRange()) {
                        if (sa instanceof RelaySatellite) {
                            comlist.add(sa.getsID());
                            comlist.addAll(relaycaseInrage(sa, comlist));
                        } else {
                            comlist.add(sa.getsID());
                        }
                    }
                }
            }
        } else if (issa) {
            for (Device de : devices) {
                if ((de instanceof DesktopDevice) && !nowsa.isDesktop()) {
                    continue;
                } else {
                    if (MathsHelper.isVisible(nowsa.getHeight(), nowsa.getSangle(), de.getDangle())) {
                        if (MathsHelper.getDistance(nowsa.getHeight(), nowsa.getSangle(), de.getDangle()) <= nowsa
                                .getRange()) {
                            comlist.add(de.getDid());
                        }
                    }
                }
            }

            for (Satellite sa : satellites) {
                if (nowsa.getsID().equals(sa.getsID())) {
                    continue;
                }
                if (MathsHelper.isVisible(nowsa.getHeight(), nowsa.getSangle(), sa.getHeight(), sa.getSangle())) {
                    if (MathsHelper.getDistance(nowsa.getHeight(), nowsa.getSangle(), sa.getHeight(),
                            sa.getSangle()) <= nowsa.getRange()) {
                        if (sa instanceof RelaySatellite) {
                            comlist.add(sa.getsID());
                            comlist.addAll(relaycaseInrage(sa, comlist));
                        } else {
                            comlist.add(sa.getsID());
                        }
                    }
                }
            }

        }

        for (String element : comlist) {
            if (!checkdup.contains(element)) {
                checkdup.add(element);
            }
        }
        comlist = checkdup;

        // device, duplicate and , type isdesk
        if (isde) {
            for (Device de : devices) {
                if (comlist.contains(de.getDid())) {
                    comlist.remove(de.getDid());
                }
            }
            for (Satellite sa : satellites) {
                if (nowde instanceof DesktopDevice && !sa.isDesktop()) {
                    comlist.remove(sa.getsID());
                }
            }
        } else if (issa) {
            if (!nowsa.isDesktop()) {
                for (Device de : devices) {
                    if (comlist.contains(de.getDid()) && de instanceof DesktopDevice) {
                        comlist.remove(de.getDid());
                    }
                }
            }
            if (comlist.contains(nowsa.getsID())) {
                comlist.remove(nowsa.getsID());
            }

        }

        for (String element : comlist) {
            if (!checkdup.contains(element)) {
                checkdup.add(element);
            }
        }

        return comlist;

    }

    public List<String> relaycaseInrage(Satellite nowsa, ArrayList<String> precomlist) {
        ArrayList<String> comlist = new ArrayList<String>();
        comlist.addAll(precomlist);
        for (Device de : devices) {
            if ((de instanceof DesktopDevice) && !nowsa.isDesktop()) {
                continue;
            } else {
                if (MathsHelper.isVisible(nowsa.getHeight(), nowsa.getSangle(), de.getDangle())) {
                    if (MathsHelper.getDistance(nowsa.getHeight(), nowsa.getSangle(), de.getDangle()) <= nowsa
                            .getRange()) {
                        comlist.add(de.getDid());
                    }
                }
            }
        }

        for (Satellite sa : satellites) {
            if (nowsa.getsID().equals(sa.getsID())) {
                continue;
            }
            if (MathsHelper.isVisible(nowsa.getHeight(), nowsa.getSangle(), sa.getHeight(), sa.getSangle())) {
                if (MathsHelper.getDistance(nowsa.getHeight(), nowsa.getSangle(), sa.getHeight(),
                        sa.getSangle()) <= nowsa.getRange()) {
                    if (sa instanceof RelaySatellite) {
                        // System.out.println(precomlist);

                        if (precomlist.contains(sa.getsID()) && nowsa instanceof RelaySatellite) {
                            continue;
                        }
                        comlist.add(sa.getsID());
                        comlist.addAll(relaycaseInrage(sa, comlist));
                    } else {
                        comlist.add(sa.getsID());
                    }
                }
            }
        }

        return comlist;
    }
}
