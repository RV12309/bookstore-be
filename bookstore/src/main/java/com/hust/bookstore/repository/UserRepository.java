package com.hust.bookstore.repository;

import com.hust.bookstore.dto.request.SearchUserRequest;
import com.hust.bookstore.entity.User;
import com.hust.bookstore.enumration.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    @Query("select u.type, count(u) from User u group by u.type")
    Map<UserType, Long> statisticUser();

    @Query("select u from User u " +
            "left join Account a on u.id = a.userId " +
            "where u.type = :#{#input.getType()} " +
            "and (:#{#input.getUsername()} is null or a.username like :#{#input.getUsername()}) " +
            "and (:#{#input.getEmail()} is null or u.email like :#{#input.getEmail()}) " +
            "and (:#{#input.getPhone()} is null or u.phone like :#{#input.getPhone()}) " +
            "and (:#{#input.getType()} is null or u.type = :#{#input.getType()}) " +
            "order by u.id desc")
    Page<User> searchUsers(SearchUserRequest input, Pageable pageable);

    @Query("""
            select u.accountId as id, u.name as name
            from User u
            where u.accountId in :accountIds
            """)
    List<SellerProjection> findAllByAccountIdIn(List<Long> accountIds);

    @Query("""
            select accountId as id, name as name
            from User
            where accountId = :accountId
            """)
    Optional<SellerProjection> findSeller(Long accountId);
}
