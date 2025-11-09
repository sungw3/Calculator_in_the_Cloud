import java.io.*;
import java.net.*;


public class Config {
    private String host = "localhost";
    private int port = 1234;


    public Config(String path) {
        File f = new File(path);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#") || line.isEmpty()) continue;
                if (line.contains(":")) {
                    String[] parts = line.split(":" , 2);
                    host = parts[0];
                    port = Integer.parseInt(parts[1]);
                }
            }
        } catch (Exception e) {
        // ignore, keep defaults
        }
    }

    public String getHost(){return host;}
    public int getPort(){return port;}
}