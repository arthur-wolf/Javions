package ch.epfl.javions.aircraft;

import java.util.Objects;

/**
 * Collects the constant data of an aircraft
 *
 * @param registration           the aircraft's registration
 * @param typeDesignator         the aircraft's typeDesignator
 * @param model                  the aircraft's model
 * @param description            the aircraft's description
 * @param wakeTurbulenceCategory the aircraft's wake turbulenceCategory
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public record AircraftData(AircraftRegistration registration,
                           AircraftTypeDesignator typeDesignator,
                           String model,
                           AircraftDescription description,
                           WakeTurbulenceCategory wakeTurbulenceCategory) {

    /**
     * Validates the aircraft's data
     *
     * @param registration           the aircraft's registration
     * @param typeDesignator         the aircraft's typeDesignator
     * @param model                  the aircraft's model
     * @param description            the aircraft's description
     * @param wakeTurbulenceCategory the aircraft's wake turbulenceCategory
     */
    public AircraftData {
        Objects.requireNonNull(registration);
        Objects.requireNonNull(typeDesignator);
        Objects.requireNonNull(model);
        Objects.requireNonNull(description);
        Objects.requireNonNull(wakeTurbulenceCategory);
    }
}
