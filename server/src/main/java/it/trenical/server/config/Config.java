package it.trenical.server.config;

public class Config {

    public final Database database = new Database();
    public final Server server = new Server();
    public final Logic logic = new Logic();

    public static class Database {
        public String path = "./TreniCal.db";
    }

    public static class Server {
        public int port = 8008;
        public int threadNumber = 4;
    }

    public static class Logic {

        public Price price = new Price();

        public static class Price {
            public float distanceMultiplier = 0.05f;
            public float businessMultiplier = 1.3f;
        }
    }
}
