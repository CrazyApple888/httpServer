package ru.nsu.fit.g19202.isachenko;

public class Header {

    private String date = "";
    private String serverName = "";
    private String lastModified = "";
    private String contentType = "";
    private String contentLength = "";


    public String getHeaderString() {
        return contentType
                + date
                + lastModified
                + contentLength
                + serverName + "\r\n";
    }

    public static class HeaderBuilder {
        private final Header header;

        public HeaderBuilder() {
            header = new Header();
        }

        public HeaderBuilder setDate(String date) {
            header.date = "Date: " + date + "\r\n";
            return this;
        }

        public HeaderBuilder setServerName(String serverName) {
            header.serverName = "Server: " + serverName + "\r\n";
            return this;
        }

        public HeaderBuilder setLastModified(String lastModified) {
            header.lastModified = "Last-modified: " + lastModified + "\r\n";
            return this;
        }

        public HeaderBuilder setContentType(String contentType) {
            header.contentType = "Content-Type: " + contentType + "\r\n";
            return this;
        }

        public HeaderBuilder setContentLength(String contentLength) {
            header.contentLength = "Content-Length: " + contentLength + "\r\n";
            return this;
        }

        public Header getHeader() {
            return header;
        }
    }
}
