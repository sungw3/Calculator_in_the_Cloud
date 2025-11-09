import java.io.*;
import java.net.*;
import java.util.*;

public class ClientEx {
    public static void main(String[] args) {
        String cfg = "server_info.dat";
        Config c = new Config(cfg);
        try (Socket sock = new Socket(c.getHost(), c.getPort());
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
            Scanner sc = new Scanner(System.in)) 
        {
            System.out.println("Connected to " + c.getHost() + ":" + c.getPort());
            System.out.println("Hello,");
            System.out.println("I will calculate two numbers for you.");
            System.out.println("Choose from <ADD><SUB><DIV><MUL> and two numbers on a line.");
            System.out.println("Enter commands like: CALC ADD 10 20");
        while (true) {
                System.out.print("Enter: ");
                String line = sc.nextLine();
                if (line == null) break;
                line = line.trim();
                if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) break;
                out.write(line + "\n");
                out.flush();
                String resp = in.readLine();
                if (resp == null) break;
                System.out.println("Server: " + resp);
            }

        } catch (Exception e) {
            System.err.println("Failed to connect: " + e.getMessage());
        }
    }
}