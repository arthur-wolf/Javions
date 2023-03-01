package ch.epfl.javions;

/**
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */

public final class Crc24 {

    public static final int GENERATOR = 0xFF409;
    private final byte[] table;

    public Crc24(int generator){
       // Crc24 crc = new Crc24(Crc24.GENERATOR);
        this.table = buildTable();
    }

    //dont pass the test
    public int crc(byte[] bytes) {
        int crc = 0;
        for (byte o : bytes) {
            //need to change crc >> 16 i guess
            crc = ((crc << 8) | o) ^ table[(crc >> 16) & 0xFF];
            System.out.println("crc" + crc);
        }
        return crc & 0xFFFFFF;
    }
    private static int crc_bitwise(int generator, byte [] bytes){
        int crc = 0;
        int[] table = new int[]{0, generator};
        for (byte b : bytes) {
            //look good to me
            crc = ((crc << 1) | b) ^ (table[crc >> 23]);
            System.out.println("crc_bitwise " + crc);
        }
        return crc & 0xFFFFFF;

    }
    //look good to me
    private static byte[]  buildTable(){
        byte[] table = new byte[256];
        for (int i = 0; i < 256; i++) {
            table[i] = (byte) crc_bitwise(GENERATOR, new byte[]{(byte) i});
        }
        return table;

    }


}