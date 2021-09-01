package ru.nsu.fit.g19202.isachenko;


public class Main {
    public static void main(String[] args) throws Throwable {
        HttpServer server = new HttpServer("idrew" ,777);
        server.start();
    }
}
