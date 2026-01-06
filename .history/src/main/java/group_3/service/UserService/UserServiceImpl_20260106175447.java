package group_3.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import group_3.dao.PersonDAO;
import group_3.model.Person;
import group_3.model.enums.Role;
import group_3.service.SystemHistoryService.SystemHistoryService;
import group_3.service.SystemHistoryService.SystemHistoryServiceImpl;

/**
 * Implementation of UserService for managing all Person types.
 * Provides CRUD operations for Attendees, Presenters, and generic Person objects.
 * 
 * @author Group21
 */
public class UserServiceImpl implements UserService {
    private final PersonDAO personDAO;
    private final SystemHistoryService historyService;
    
    // In-memory set of banned user IDs (since schema cannot be modified)
    // In production, this could be stored in a separate table or Redis cache
    private static final Set<Integer> bannedUsers = new HashSet<>();

    public UserServiceImpl(PersonDAO personDao){
        this.personDAO = personDao;
        this.historyService = new SystemHistoryServiceImpl();
    }

    public UserServiceImpl(PersonDAO personDao, SystemHistoryService historyService){
        this.personDAO = personDao;
        this.historyService = historyService;
    }

    @Override
    public Person createPerson(Person person){
        if(person == null){
            throw new IllegalArgumentException("Person must not be null");
        }

        if(person.getUsername() == null || person.getUsername().isBlank()){
            throw new IllegalArgumentException("Username must not be empty");
        }

        if(personDAO.findByUsername(person.getUsername()).isPresent()){
            throw new IllegalArgumentException("Username already exists: " + person.getUsername());
        }

        personDAO.create(person);
        
        historyService.logAction(null, "USER_CREATED", 
            String.format("{\"userId\": %d, \"username\": \"%s\"}", person.getId(), person.getUsername()));
        
        return person;
    }

    @Override
    public Optional<Person> getUserById(int userId){
        if(userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        return personDAO.findById(userId);
    }

    @Override
    public Optional<Person>getUserByUsername(String username){
        if(username == null || username.isBlank()){
            throw new IllegalArgumentException("Username must not be empty");
        }

        return personDAO.findByUsername(username);
    }

    @Override
    public List<Person> getAllUsers(){
        return personDAO.findAll();
    }

    @Override
    public void updateUser(Person person){
        if (person == null || person.getId() <= 0){
            throw new IllegalArgumentException("Invalid person data");
        }
        Optional<Person> existing = personDAO.findById(person.getId());

        if(existing.isEmpty()){
            throw new IllegalArgumentException("User not found with ID: " + person.getId());
        }

        personDAO.update(person);
        
        historyService.logAction(person.getId(), "USER_UPDATED", 
            String.format("{\"userId\": %d, \"username\": \"%s\"}", person.getId(), person.getUsername()));
    }

    @Override
    public void deleteUser(int userId){
        if(userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        Optional<Person> existing = personDAO.findById(userId);

        if(existing.isEmpty()) {
            throw new IllegalArgumentException(
                    "User not found with ID " + userId
            );
        }

        personDAO.delete(userId);
        bannedUsers.remove(userId); // Remove from banned list if deleted
        
        historyService.logAction(userId, "USER_DELETED", 
            String.format("{\"userId\": %d}", userId));
    }

    @Override
    public boolean userExists(String username){
        if(username == null || username.isBlank()){
            return false;
        }

        return personDAO.findByUsername(username).isPresent();
    }

    // ======================= System Admin Functions =======================

    @Override
    public boolean banUser(int userId, String reason) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        Optional<Person> userOpt = personDAO.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        Person user = userOpt.get();
        
        // Cannot ban system admins
        if (user.getRole() == Role.SYSTEM_ADMIN) {
            throw new IllegalArgumentException("Cannot ban a System Admin");
        }

        if (bannedUsers.contains(userId)) {
            return false; // Already banned
        }

        bannedUsers.add(userId);
        
        historyService.logAction(userId, "USER_BANNED", 
            String.format("{\"userId\": %d, \"username\": \"%s\", \"reason\": \"%s\"}", 
                userId, user.getUsername(), reason != null ? reason : "No reason provided"));
        
        return true;
    }

    @Override
    public boolean unbanUser(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        Optional<Person> userOpt = personDAO.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        if (!bannedUsers.contains(userId)) {
            return false; // Not banned
        }

        bannedUsers.remove(userId);
        
        Person user = userOpt.get();
        historyService.logAction(userId, "USER_UNBANNED", 
            String.format("{\"userId\": %d, \"username\": \"%s\"}", userId, user.getUsername()));
        
        return true;
    }

    @Override
    public boolean isUserBanned(int userId) {
        return bannedUsers.contains(userId);
    }

    @Override
    public boolean assignRole(int userId, Role newRole) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        if (newRole == null) {
            throw new IllegalArgumentException("Role must not be null");
        }

        Optional<Person> userOpt = personDAO.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        Person user = userOpt.get();
        Role oldRole = user.getRole();
        
        if (oldRole == newRole) {
            return false; // No change needed
        }

        // Note: This requires creating a new Person subclass instance based on the new role
        // For simplicity, we'll log the action. Full implementation would need to migrate user data.
        historyService.logAction(userId, "ROLE_CHANGED", 
            String.format("{\"userId\": %d, \"username\": \"%s\", \"oldRole\": \"%s\", \"newRole\": \"%s\"}", 
                userId, user.getUsername(), oldRole, newRole));
        
        return true;
    }

    @Override
    public List<Person> getUsersByRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role must not be null");
        }
        return personDAO.findAll().stream()
                .filter(p -> p.getRole() == role)
                .collect(Collectors.toList());
    }

    @Override
    public List<Person> getBannedUsers() {
        return personDAO.findAll().stream()
                .filter(p -> bannedUsers.contains(p.getId()))
                .collect(Collectors.toList());
    }
}
