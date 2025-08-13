package com.gastrogeniusai.infrastructure.repository;

import com.gastrogeniusai.domain.entity.User;
import com.gastrogeniusai.domain.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * Provides custom query methods for user management and authentication.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     * 
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email address.
     * 
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by username or email (for login).
     * 
     * @param username the username to search for
     * @param email    the email to search for
     * @return Optional containing the user if found
     */
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);

    /**
     * Checks if a username already exists.
     * 
     * @param username the username to check
     * @return true if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Checks if an email already exists.
     * 
     * @param email the email to check
     * @return true if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Finds all users with a specific role.
     * 
     * @param role     the role to filter by
     * @param pageable pagination information
     * @return page of users with the specified role
     */
    Page<User> findByRole(UserRole role, Pageable pageable);

    /**
     * Finds all enabled users.
     * 
     * @param pageable pagination information
     * @return page of enabled users
     */
    Page<User> findByEnabledTrue(Pageable pageable);

    /**
     * Finds all disabled users.
     * 
     * @param pageable pagination information
     * @return page of disabled users
     */
    Page<User> findByEnabledFalse(Pageable pageable);

    /**
     * Finds users created after a specific date.
     * 
     * @param date     the date to filter from
     * @param pageable pagination information
     * @return page of users created after the date
     */
    Page<User> findByCreatedAtAfter(LocalDateTime date, Pageable pageable);

    /**
     * Finds users who have logged in after a specific date.
     * 
     * @param date     the date to filter from
     * @param pageable pagination information
     * @return page of users with recent login
     */
    Page<User> findByLastLoginAfter(LocalDateTime date, Pageable pageable);

    /**
     * Finds users who have never logged in.
     * 
     * @param pageable pagination information
     * @return page of users who have never logged in
     */
    Page<User> findByLastLoginIsNull(Pageable pageable);

    /**
     * Searches users by username, email, first name, or last name containing the
     * given text.
     * 
     * @param searchTerm the text to search for
     * @param pageable   pagination information
     * @return page of matching users
     */
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchByTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Counts users by role.
     * 
     * @param role the role to count
     * @return number of users with the specified role
     */
    long countByRole(UserRole role);

    /**
     * Counts enabled users.
     * 
     * @return number of enabled users
     */
    long countByEnabledTrue();

    /**
     * Counts users created after a specific date.
     * 
     * @param date the date to filter from
     * @return number of users created after the date
     */
    long countByCreatedAtAfter(LocalDateTime date);

    /**
     * Updates a user's last login timestamp.
     * 
     * @param userId    the user ID
     * @param lastLogin the new last login timestamp
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :lastLogin WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId, @Param("lastLogin") LocalDateTime lastLogin);

    /**
     * Updates a user's enabled status.
     * 
     * @param userId  the user ID
     * @param enabled the new enabled status
     */
    @Modifying
    @Query("UPDATE User u SET u.enabled = :enabled WHERE u.id = :userId")
    void updateEnabledStatus(@Param("userId") Long userId, @Param("enabled") boolean enabled);

    /**
     * Gets users with the most recipes.
     * 
     * @param limit the maximum number of users to return
     * @return list of users ordered by recipe count
     */
    @Query("SELECT u FROM User u LEFT JOIN u.recipes r GROUP BY u ORDER BY COUNT(r) DESC")
    List<User> findTopUsersByRecipeCount(Pageable pageable);

    /**
     * Finds users with no recipes.
     * 
     * @param pageable pagination information
     * @return page of users without recipes
     */
    @Query("SELECT u FROM User u WHERE u.recipes IS EMPTY")
    Page<User> findUsersWithoutRecipes(Pageable pageable);

    /**
     * Finds inactive users (no login for specified days).
     * 
     * @param daysAgo  the number of days to look back
     * @param pageable pagination information
     * @return page of inactive users
     */
    @Query("SELECT u FROM User u WHERE u.lastLogin < :cutoffDate OR u.lastLogin IS NULL")
    Page<User> findInactiveUsers(@Param("cutoffDate") LocalDateTime cutoffDate, Pageable pageable);
}
