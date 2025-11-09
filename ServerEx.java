import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServerEx {
    private ServerSocket serverSocket;
    private ExecutorService pool;

    public ServerEx(int port, int threads) throws IOException {
        serverSocket = new ServerSocket(port);
        pool = Executors.newFixedThreadPool(threads);
        System.out.println("Server listening on port " + port);
    }

    public void start() {
        try {
            while (true) {
                Socket client = serverSocket.accept();
                pool.submit(new ClientHandler(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 1234;
        int threads = 8;
        if (args.length >= 1)
            port = Integer.parseInt(args[0]);
        if (args.length >= 2)
            threads = Integer.parseInt(args[1]);
        ServerEx s = new ServerEx(port, threads);
        s.start();
    }

    static class ClientHandler implements Runnable {
        private Socket sock;

        public ClientHandler(Socket s) {
            this.sock = s;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty())
                        continue;
                    String resp = process(line);
                    out.write(resp + "\n");
                    out.flush();
                }

            } catch (Exception e) {
                // 로그
            } finally {
                try {
                    sock.close();
                } catch (IOException ignored) {

                }
            }
        }

        private String process(String line) {
            // expected: CALC ADD 10 20
            try {
                String[] toks = line.split("\\s+");
                int idx = 0;
                String cmd = toks[idx++];
                if (!cmd.equalsIgnoreCase("CALC")) {
                    return String.format("Error occurred: %s %d %s", Protocol.TYPE_ERR, Protocol.ERR_INVALID_OPCODE,
                            "INVALID_CMD");
                }
                if (idx >= toks.length)
                    return String.format("Error occurred: %s %d %s", Protocol.TYPE_ERR, Protocol.ERR_INVALID_OPCODE, "NO_OPCODE");
                String op = toks[idx++].toUpperCase();
                if (!(op.equals("ADD") || op.equals("SUB") || op.equals("MUL") || op.equals("DIV"))) {
                    return String.format("Error occurred: %s %d %s", Protocol.TYPE_ERR, Protocol.ERR_INVALID_OPCODE, "UNKNOWN_OP");
                }
                // expect exactly 2 args
                if (toks.length - idx != 2) {
                    return String.format("Error occurred: %s %d %s", Protocol.TYPE_ERR, Protocol.ERR_ARG_COUNT, "ARG_COUNT");
                }
                double a, b;
                try {
                    a = Double.parseDouble(toks[idx++]);
                    b = Double.parseDouble(toks[idx++]);
                } catch (NumberFormatException nfe) {
                    return String.format("Error occurred: %s %d %s", Protocol.TYPE_ERR, Protocol.ERR_INVALID_NUMBER, "BAD_NUMBER");
                }
                if (op.equals("DIV") && b == 0.0) {
                    return String.format("Error occurred: %s %d %s", Protocol.TYPE_ERR, Protocol.ERR_DIV_BY_ZERO, "DIV_BY_ZERO");
                }
                double result = 0.0;
                switch (op) {
                    case "ADD":
                        result = a + b;
                        break;
                    case "SUB":
                        result = a - b;
                        break;
                    case "MUL":
                        result = a * b;
                        break;
                    case "DIV":
                        result = a / b;
                        break;
                }
                // strip .0 for integers
                String outVal = (result == Math.rint(result)) ? String.valueOf((long) result) : String.valueOf(result);
                return String.format("The result of %s is %s", op, outVal);

            } catch (Exception ex) {
                return String.format("Error occurred: %s %d %s", Protocol.TYPE_ERR, Protocol.ERR_INTERNAL, "INTERNAL");
            }
        }
    }
}