package ch.epfl.javions;


import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.gui.AircraftStateManager;
import ch.epfl.javions.gui.ObservableAircraftState;
import javafx.collections.ObservableSet;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/*
java --enable-preview \
        -cp out/production/Javions/ \
        --module-path /Users/arthur/epfl/ba2/cs108/javafx-sdk-20/lib \
        --add-modules javafx.controls \
        ch.epfl.javions.TextUI
*/

/*
java --enable-preview `
     -cp out/production/Javions/ `
     --module-path C:\Users\oussa\Videos\Documents\Bureau\INF_BA2\PPOO\javafx-sdk-20\lib `
     --add-modules javafx.controls `
     ch.epfl.javions.TextUI

 */
public class TextUI {
    private static AircraftDatabase getDatabase() {
        // Try to get the database from the resources
        URL aircraftURL = TextUI.class.getResource("/aircraft.zip");
        if (aircraftURL != null) {
            String aircraftFileName = URLDecoder.decode(aircraftURL.getFile(), UTF_8);
            return new AircraftDatabase(aircraftFileName);
        }
        throw new IllegalStateException("Could not find aircraft.zip in resources");
    }

    public static void main(String[] args) {
        AircraftStateManager manager = new AircraftStateManager(getDatabase());
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(new FileInputStream("resources/messages_20230318_0915.bin")))) {
            byte[] bytes = new byte[RawMessage.LENGTH];
            System.out.println("OACI    Indicatif      Immat.  Modèle                        Longitude   Latitude   Alt.  Vit.\n" + "――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――");
            while (true) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                Message message1 = MessageParser.parse(new RawMessage(timeStampNs, message));
                if (message1 != null) {
                    /*if(message1.timeStampNs() < (System.nanoTime() - startTime)){
                        Thread.sleep((long) (((System.nanoTime() - startTime) - message1.timeStampNs())/(9E6)));
                    }*/
                    manager.updateWithMessage(message1);
                    manager.purge();
                }
                ObservableSet<ObservableAircraftState> states = manager.states();
                List<ObservableAircraftState> listStates = new ArrayList<>(List.copyOf(states));
                listStates.sort(new AddressComparator());
                String CSI = "\u001B[";
                String CLEAR_SCREEN = CSI + "2J";
                Thread.sleep(100);
                System.out.print(CLEAR_SCREEN);
                System.out.print(CSI + ";H");
                for (ObservableAircraftState state : listStates) {
                    System.out.printf("%5s %10s %10s %32s  %f6 %6f %5f %5f %1s \n",
                            state.address().string(), Objects.isNull(state.callSignProperty().get()) ? " " : state.callSignProperty().get().string(),
                            state.aircraftData().registration().string(), state.aircraftData().model(),
                            Units.convertTo(state.positionProperty().get().longitude(), Units.Angle.DEGREE), Units.convertTo(state.positionProperty().get().latitude(), Units.Angle.DEGREE),
                            state.altitudeProperty().get(), Units.convertTo(state.velocityProperty().get(), Units.Speed.KILOMETER_PER_HOUR), findArrow(Units.convertTo(state.trackOrHeadingProperty().get(), Units.Angle.DEGREE)));
                }
            }
        } catch (EOFException e) { /* nothing to do */ } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static class AddressComparator implements Comparator<ObservableAircraftState> {
        @Override
        public int compare(ObservableAircraftState o1,
                           ObservableAircraftState o2) {
            String s1 = o1.address().string();
            String s2 = o2.address().string();
            return s1.compareTo(s2);
        }
    }

    private static String findArrow(double trackOrHeading) {
        if ((0 <= trackOrHeading && trackOrHeading <= 22.5) || (337.5 <= trackOrHeading && trackOrHeading <= 360)) {
            return "↑";
        }
        if (22.5 < trackOrHeading && trackOrHeading <= 67.5) {
            return "↗";
        }
        if (67.5 < trackOrHeading && trackOrHeading <= 112.5) {
            return "→";
        }
        if (112.5 < trackOrHeading && trackOrHeading <= 157.5) {
            return "↘";
        }
        if (157.5 < trackOrHeading && trackOrHeading <= 202.5) {
            return "↓";
        }
        if (202.5 < trackOrHeading && trackOrHeading <= 247.5) {
            return "↙";
        }
        if (247.5 < trackOrHeading && trackOrHeading <= 292.5) {
            return "←";
        }
        if (292.5 < trackOrHeading && trackOrHeading <= 337.5) {
            return "↖";
        }
        return "";
    }
}