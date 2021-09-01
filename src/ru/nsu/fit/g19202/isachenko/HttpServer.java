package ru.nsu.fit.g19202.isachenko;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class HttpServer {

    private final ServerSocket serverSocket;
    private final String serverName;
    private final List<String> types = new ArrayList<>();
    private final List<String> methods = new ArrayList<>();

    private String requiredFile;
    private boolean isConnected = false;

    public HttpServer(String serverName, int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.serverName = serverName;
        //--------------------------------------------------------
        types.add("text/plain");
        types.add("text/html");
        types.add("image/jpeg");

        methods.add("GET");
    }

    public void start() throws IOException {
        Socket socket = null;
        while (true) {
            try {
                if (socket == null || !isConnected)
                {
                    socket = serverSocket.accept();
                    isConnected = true;
                }
                parseInput(socket.getInputStream());
                writeResponse(socket.getOutputStream());
                if (!isConnected) {
                    socket.getInputStream().close();
                    socket.getOutputStream().close();
                    socket.close();
                    socket = null;
                }
            } catch (HttpException e) {
                socket.getOutputStream().write(e.getMessage().getBytes());
            } catch (Exception e) {
                isConnected = false;
                if (socket != null) {
                    socket.close();
                }
                socket = null;
            }
        }
    }

    private void parseInput(InputStream socketInput) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socketInput));
        var requestLine = reader.readLine();
        if (requestLine == null) {
            throw new Exception("Client disconnected");
        }
        if (requestLine.isEmpty()) {
            throw new Exception("Empty request");
        }
        var request = requestLine.split(" ");
        System.err.println(Arrays.toString(request));
        String requestMethod = request[0];
        if (!methods.contains(requestMethod)) {
            throw new HttpException("405 Method Not Allowed", serverName);
        }
        requiredFile = new StringBuilder(request[1]).deleteCharAt(0).toString();

        List<String> acceptTypes = new ArrayList<>();
        boolean isAcceptParsed = false;
        boolean isConnectionParsed = false;
        do {
            var parseBuffer = reader.readLine();
            if (parseBuffer.contains("Accept: ")) {
                acceptTypes = Arrays.asList(parseBuffer.split("[: ,;]+"));
                isAcceptParsed = true;
            }
            if (parseBuffer.contains("Connection: ")) {
                if (parseBuffer.contains("close")) {
                    isConnected = false;
                }
                isConnectionParsed = true;
            }
        } while (!isAcceptParsed || !isConnectionParsed);
        System.out.println(acceptTypes);
        if (acceptTypes.contains("*/*")) {
            return;
        }
        var reqType = getType(requiredFile);
        for (var t : acceptTypes) {
            if (t.contains(reqType)) {
                return;
            }
        }
        throw new Exception("Wrong type");
    }

    private void writeResponse(OutputStream os) throws HttpException, IOException {
        File file = new File(requiredFile);
        if (!file.exists()) {
            throw new HttpException("404 Not Found", serverName);
        }
        String headerString = new Header.HeaderBuilder()
                .setDate(new Date().toString())
                .setServerName(serverName)
                .setLastModified(new Date(file.lastModified()).toString())
                .setContentLength(Long.toString(file.length()))
                .setContentType(getType(requiredFile))
                .getHeader()
                .getHeaderString();

        FileInputStream fis = new FileInputStream(file);
        int symbol;
        os.write("HTTP/1.1 200 OK\r\n".getBytes());
        os.write(headerString.getBytes());
        while ((symbol = fis.read()) != -1) {
            os.write((byte) symbol);
        }
    }

    private String getType(String path) {
        StringBuilder typeBuilder = new StringBuilder();
        for (var i = path.length() - 1; i > 0; i--) {
            if (path.charAt(i) == '.')
                break;
            typeBuilder.append(path.charAt(i));
        }
        var type = typeBuilder.reverse().toString().toLowerCase();
        for (var t : types) {
            if (t.contains(type))
                return t;
        }
        return "";
    }

}
