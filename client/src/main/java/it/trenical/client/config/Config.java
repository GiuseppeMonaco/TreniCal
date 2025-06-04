package it.trenical.client.config;

public class Config {

    public final Server server = new Server();

    public static class Server {
        public String address = "localhost";
        public int port = 8008;
    }

}
