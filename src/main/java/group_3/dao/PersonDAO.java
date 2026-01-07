package group_3.dao;

import group_3.model.Person;
import java.util.Optional;
import java.util.List;

/**
 * @author Group 3
 *
 * DAO interface defining operations for accessing and managing Person data.
 */


public interface PersonDAO {

    void create(Person person);

    Optional<Person> findById(int id);

    Optional<Person> findByUsername(String username);

    List<Person> findAll();

    void update(Person person);

    void delete(int id);
}
