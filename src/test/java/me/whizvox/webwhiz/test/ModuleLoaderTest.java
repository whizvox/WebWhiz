package me.whizvox.webwhiz.test;

import me.whizvox.webwhiz.core.WebWhiz;
import me.whizvox.webwhiz.core.WebWhizConfiguration;

public class ModuleLoaderTest extends WebWhiz {

    public ModuleLoaderTest(WebWhizConfiguration config) {
        super(config);
    }

    public static void main(String[] args) throws Exception {

        ModuleLoaderTest app = new ModuleLoaderTest(WebWhizConfiguration.DEFAULT);
        app.getModuleLoader().load();

    }

}
