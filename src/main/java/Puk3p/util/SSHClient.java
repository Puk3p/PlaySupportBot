package Puk3p.util;

import com.jcraft.jsch.*;

import java.io.InputStream;
import java.util.Properties;

public class SSHClient {

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public SSHClient(String host, int port, String username, String password) {
        if (host == null || username == null || password == null) {
            throw new IllegalArgumentException("Host, username și password nu pot fi null.");
        }

        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String executeCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("Comanda nu poate fi null sau goală.");
        }

        String wrappedCommand = "screen -S Survival -X stuff '" + command + "\\n'";
        Session session = null;
        ChannelExec channel = null;

        try {
            session = createSession();
            session.connect();
            System.out.println("[SSHClient] Conectat la serverul SSH: " + host);

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(wrappedCommand);
            channel.setErrStream(System.err);

            try (InputStream in = channel.getInputStream()) {
                channel.connect();
                System.out.println("[SSHClient] Execut comanda: " + command);

                byte[] buffer = new byte[1024];
                StringBuilder output = new StringBuilder();
                int bytesRead;

                while ((bytesRead = in.read(buffer)) != -1) {
                    output.append(new String(buffer, 0, bytesRead));
                }

                System.out.println("[SSHClient] Comanda executată cu succes.");
                return output.toString().trim();
            }
        } catch (Exception e) {
            throw new RuntimeException("Eroare la executarea comenzii SSH: " + e.getMessage(), e);
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    public String executePasswordChangeCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("Comanda nu poate fi null sau goală.");
        }

        String wrappedCommand = "screen -S Lobby2 -X stuff '" + command + "\\n'";
        Session session = null;
        ChannelExec channel = null;

        try {
            session = createSession();
            session.connect();
            System.out.println("[SSHClient] Conectat la serverul SSH: " + host);

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(wrappedCommand);
            channel.setErrStream(System.err);

            try (InputStream in = channel.getInputStream()) {
                channel.connect();
                System.out.println("[SSHClient] Execut comanda: " + command);

                byte[] buffer = new byte[1024];
                StringBuilder output = new StringBuilder();
                int bytesRead;

                while ((bytesRead = in.read(buffer)) != -1) {
                    output.append(new String(buffer, 0, bytesRead));
                }

                System.out.println("[SSHClient] Comanda de schimbare parolă executată cu succes.");
                return output.toString().trim();
            }
        } catch (Exception e) {
            throw new RuntimeException("Eroare la executarea comenzii SSH pentru schimbarea parolei: " + e.getMessage(), e);
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }



    private Session createSession() throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        session.setPassword(password);
        session.setConfig(getDefaultConfig());
        return session;
    }

    private Properties getDefaultConfig() {
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        return config;
    }
}
