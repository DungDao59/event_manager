package group_3.util;

import group_3.dao.EventDAO;
import group_3.dao.PresenterDAO;
import group_3.dao.ScheduleDAO;
import group_3.dao.SessionDAO;
import group_3.dao.TicketDAO;
import group_3.dao.impl.EventDAOImpl;
import group_3.dao.impl.EventDAOInMemory;
import group_3.dao.impl.PresenterDAOImpl;
import group_3.dao.impl.PresenterDAOInMemory;
import group_3.dao.impl.ScheduleDAOImpl;
import group_3.dao.impl.SessionDAOImpl;
import group_3.dao.impl.SessionDAOInMemory;
import group_3.dao.impl.TicketDAOImpl;
import group_3.dao.impl.TicketDAOInMemory;

/**
 * @author Group 3
 *
 * Simple provider to switch between real DAO implementations and in-memory mocks.
 * Toggle via env var MOCK_DATA=true or JVM arg -DmockData=true.
 */
public final class DaoProvider {
    private DaoProvider() {}

    private static Boolean MOCK_MODE;

    private static boolean isMockMode() {
        if (MOCK_MODE != null) return MOCK_MODE;
        String env = System.getenv("MOCK_DATA");
        boolean fromEnv = env != null && (env.equalsIgnoreCase("true") || env.equalsIgnoreCase("1") || env.equalsIgnoreCase("yes") || env.equalsIgnoreCase("y"));
        boolean fromProp = Boolean.getBoolean("mockData");
        MOCK_MODE = fromEnv || fromProp;
        System.out.println("[DaoProvider] MOCK_DATA env=" + env + ", mockData prop=" + fromProp + ", final MOCK_MODE=" + MOCK_MODE);
        return MOCK_MODE;
    }

    public static EventDAO getEventDAO() {
        return isMockMode() ? new EventDAOInMemory() : new EventDAOImpl();
    }

    public static SessionDAO getSessionDAO() {
        return isMockMode() ? new SessionDAOInMemory() : new SessionDAOImpl();
    }

    public static TicketDAO getTicketDAO() {
        return isMockMode() ? new TicketDAOInMemory() : new TicketDAOImpl();
    }

    public static PresenterDAO getPresenterDAO() {
        return isMockMode() ? new PresenterDAOInMemory() : new PresenterDAOImpl();
    }

    public static ScheduleDAO getScheduleDAO() {
        return new ScheduleDAOImpl();
    }
}

