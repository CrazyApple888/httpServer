package ru.nsu.fit.g19202.isachenko;

import java.util.Date;

public class HttpException extends Exception {

    private final String serverName;

    public HttpException(String msg, String serverName) {
        super(msg);
        this.serverName = serverName;
    }

    @Override
    public String getMessage() {
        String content = "<html><body><h1>" + super.getMessage() + "</h1></body></html>";
        var header = new Header.HeaderBuilder()
                .setDate(new Date().toString())
                .setServerName(serverName)
                .setLastModified(new Date().toString())
                .setContentLength(Integer.toString(content.length()))
                .setContentType("text/html")
                .getHeader()
                .getHeaderString();
        return "HTTP/1.1 " + super.getMessage() + "\r\n" + header + content;
    }
}
