package group_3.util;

import group_3.dao.EventDAO;
import group_3.dao.PresenterDAO;
import group_3.dao.ScheduleDAO;
import group_3.dao.SessionDAO;
import group_3.dao.TicketDAO;
import group_3.dao.impl.EventDAOImpl;
import group_3.dao.impl.PresenterDAOImpl;
import group_3.dao.impl.ScheduleDAOImpl;
import group_3.dao.impl.SessionDAOImpl;
import group_3.dao.impl.TicketDAOImpl;

/**
 * @author Group 3
 *
 * Simple provider to get DAO implementations.
 */
public final class DaoProvider {
    private DaoProvider() {}

    public static EventDAO getEventDAO() {
        return new EventDAOImpl();
    }

    public static SessionDAO getSessionDAO() {
        return new SessionDAOImpl();
    }

    public static TicketDAO getTicketDAO() {
        return new TicketDAOImpl();
    }

    public static PresenterDAO getPresenterDAO() {
        return new PresenterDAOImpl();
    }

    public static ScheduleDAO getScheduleDAO() {
        return new ScheduleDAOImpl();
    }
}
