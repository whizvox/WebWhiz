package me.whizvox.webwhiz.test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VarTest {

    public static void main(String[] args) {

        Map<String, String> map = new HashMap<>();
        map.put("module1", "mod1");
        map.put("module2", "{module1}/mod2");
        map.put("module3", "{module2}");
        map.put("module4", "@{module2}");
        map.put("module5", "{module6}");
        map.put("module6", "{module5}");
        map.put("module7", "{module5}");

        map.keySet().forEach(s -> System.out.println(s + ": " + resolve(map, s)));
    }

    private static Pattern PATTERN = Pattern.compile("\\{(.+?)}");

    private static String resolve(Map<String, String> map, String id) {
        return resolve(map, id, id, 0);
    }

    private static String resolve(Map<String, String> map, String moduleId, String origModuleId, int iteration) {
        if (iteration > 12) {
            return getCleanPath(map.get(origModuleId));
        }
        String path = getCleanPath(map.get(moduleId));
        if (path.startsWith("@")) {
            return path.substring(1);
        }
        Matcher matcher = PATTERN.matcher(path);
        if (!matcher.find()) {
            return path;
        }
        String otherModuleId = matcher.group(1);
        return matcher.replaceFirst(resolve(map, otherModuleId, origModuleId, ++iteration));
    }

    private static String getCleanPath(String path) {
        path = path.trim();
        if (path.length() > 0) {
            if (path.charAt(0) == '/') {
                path = path.substring(1);
            }
            if (path.length() > 1 && path.charAt(path.length() - 1) == '/') {
                path = path.substring(0, path.length() - 2);
            }
        }
        return path;
    }

}
