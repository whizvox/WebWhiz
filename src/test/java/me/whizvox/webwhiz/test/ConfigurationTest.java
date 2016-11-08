package me.whizvox.webwhiz.test;

import me.whizvox.webwhiz.common.Configuration;

import java.io.File;
import java.io.IOException;

public class ConfigurationTest {

    public static void main(String[] args) throws IOException {

        Configuration config = new Configuration(new File("C:\\Users\\Neil\\dev\\TESTING\\test.json"));
        config.load();
        System.out.println(config.getString("yexd", "ijhabsdjhasd"));
        config.getFloat("yexd2", 91823.1f);
        config.save();

    }

}
