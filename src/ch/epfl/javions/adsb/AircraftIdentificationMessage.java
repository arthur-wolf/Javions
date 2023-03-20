package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category, CallSign callSign) {

    public AircraftIdentificationMessage {
        Objects.requireNonNull(icaoAddress);
        Objects.requireNonNull(callSign);
        Preconditions.checkArgument(timeStampNs >= 0);
    }

    public static AircraftIdentificationMessage of(RawMessage rawMessage) {
        long payload = rawMessage.payload();
        //todo
        return null;
    }
}
