
package ch.epfl.javions;

import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

public class Crc24Test {
    @Test
    public void crc24WorksOnTrivialInput1() {
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        String mS = "8D392AE499107FB5C00439";
        String cS = "035DB8";
        int c = Integer.parseInt(cS, 16); // 0x035DB8

        byte[] mAndC = HexFormat.of().parseHex(mS + cS);
        assertEquals(0, crc24.crc(mAndC));

        byte[] mOnly = HexFormat.of().parseHex(mS);
        assertEquals(c, crc24.crc(mOnly));
    }

    @Test
    public void crc24WorksOnTrivialInput2() {
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        String mS = "8D4D2286EA428867291C08";
        String cS = "EE2EC6";
        int c = Integer.parseInt(cS, 16); // 0xEE2EC6

        byte[] mAndC = HexFormat.of().parseHex(mS + cS);
        assertEquals(0, crc24.crc(mAndC));

        byte[] mOnly = HexFormat.of().parseHex(mS);
        assertEquals(c, crc24.crc(mOnly));
    }

    @Test
    public void crc24WorksOnTrivialInput3() {
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        String mS = "8D3950C69914B232880436";
        String cS = "BC63D3";
        int c = Integer.parseInt(cS, 16); // 0xBC63D3

        byte[] mAndC = HexFormat.of().parseHex(mS + cS);
        assertEquals(0, crc24.crc(mAndC));

        byte[] mOnly = HexFormat.of().parseHex(mS);
        assertEquals(c, crc24.crc(mOnly));
    }

    @Test
    public void crc24WorksOnTrivialInput4() {
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        String mS = "8D4B17E399893E15C09C21";
        String cS = "9FC014";
        int c = Integer.parseInt(cS, 16); // 0x9FC014

        byte[] mAndC = HexFormat.of().parseHex(mS + cS);
        assertEquals(0, crc24.crc(mAndC));

        byte[] mOnly = HexFormat.of().parseHex(mS);
        assertEquals(c, crc24.crc(mOnly));
    }

    @Test
    public void crc24WorksOnTrivialInput5() {
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        String mS = "8D4B18F4231445F2DB63A0";
        String cS = "DEEB82";
        int c = Integer.parseInt(cS, 16); // 0xDEEB82

        byte[] mAndC = HexFormat.of().parseHex(mS + cS);
        assertEquals(0, crc24.crc(mAndC));

        byte[] mOnly = HexFormat.of().parseHex(mS);
        assertEquals(c, crc24.crc(mOnly));
    }

    @Test
    public void crc24WorksOnTrivialInput6() {
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        String mS = "8D495293F82300020049B8";
        String cS = "111203";
        int c = Integer.parseInt(cS, 16); // 0x111203

        byte[] mAndC = HexFormat.of().parseHex(mS + cS);
        assertEquals(0, crc24.crc(mAndC));

        byte[] mOnly = HexFormat.of().parseHex(mS);
        assertEquals(c, crc24.crc(mOnly));
    }
}

