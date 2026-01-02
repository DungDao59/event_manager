package group_3.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import group_3.dao.PresenterDAO;
import group_3.model.Presenter;
import group_3.util.MockData;

/** In-memory implementation of PresenterDAO for mock mode. */
public class PresenterDAOInMemory implements PresenterDAO {
    
    @Override
    public int create(Presenter presenter) {
        int id = MockData.PRESENTER_SEQ.incrementAndGet();
        // Create a new Presenter with the generated ID instead of trying to set it
        Presenter newPresenter = new Presenter(id, presenter.getUsername(), presenter.getPasswordHash(), 
            presenter.getFullName(), presenter.getDateOfBirth(), presenter.getContactInformation(),
            presenter.getPresenterRole(), presenter.getStatistics());
        MockData.PRESENTERS.put(id, newPresenter);
        return id;
    }

    @Override
    public Optional<Presenter> findById(int personId) {
        return Optional.ofNullable(MockData.PRESENTERS.get(personId));
    }

    @Override
    public Optional<Presenter> findByUsername(String username) {
        return MockData.PRESENTERS.values().stream()
                .filter(p -> p.getUsername() != null && p.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public List<Presenter> findAll() {
        return new ArrayList<>(MockData.PRESENTERS.values());
    }

    @Override
    public void update(Presenter presenter) {
        MockData.PRESENTERS.put(presenter.getId(), presenter);
    }

    @Override
    public void delete(int personId) {
        MockData.PRESENTERS.remove(personId);
    }

    @Override
    public void updateStatistics(int personId, String statistics) {
        Presenter p = MockData.PRESENTERS.get(personId);
        if (p != null) {
            p.setStatistics(statistics);
        }
    }

    @Override
    public void updatePresenterRole(int personId, String presenterRole) {
        Presenter p = MockData.PRESENTERS.get(personId);
        if (p != null) {
            p.setPresenterRole(presenterRole);
        }
    }
}
