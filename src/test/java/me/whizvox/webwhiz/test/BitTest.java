package me.whizvox.webwhiz.test;

public class BitTest {

    public static void main(String[] args) {

        int i = 240;
        byte b = (byte) (i);

        System.out.println(b);
        System.out.println(b & 0xff);

        System.out.println(1 + (Byte.MAX_VALUE << 1));
        System.out.println((byte) -1);
    }

}
