package ch.epfl.javions.adsb;
import ch.epfl.javions.Units;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CprDecoderTest {

    @Test
    public void testDecodePosition() {

        var x0 = Math.scalb(111600d, -17);
        var y0 = Math.scalb(94445d, -17);
        var x1 = Math.scalb(108865d, -17);
        var y1 = Math.scalb(77558d, -17);
        var p = CprDecoder.decodePosition(x0, y0, x1, y1, 0);
        Assertions.assertEquals(7.476062346249819, Units.convert((p.longitudeT32()), Units.Angle.T32, Units.Angle.DEGREE), 1e-10);
        Assertions.assertEquals(46.323349038138986, Units.convert((p.latitudeT32()), Units.Angle.T32, Units.Angle.DEGREE), 1e-10);
        System.out.println("Longitude: " + Units.convert((p.longitudeT32()), Units.Angle.T32, Units.Angle.DEGREE));
        System.out.println("Latitude: " + Units.convert((p.latitudeT32()), Units.Angle.T32, Units.Angle.DEGREE));

    }

}
