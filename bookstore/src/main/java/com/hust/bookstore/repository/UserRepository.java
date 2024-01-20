package com.hust.bookstore.repository;

import com.hust.bookstore.entity.User;
import com.hust.bookstore.enumration.UserType;
import com.hust.bookstore.repository.projection.StatUserProjection;
import com.hust.bookstore.repository.projection.UserProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    @Query("select u.type as type, count(u) as count from User u group by u.type")
    List<StatUserProjection> statisticUser();

    @Query("select u " +
            "from User u " +
            "where "+
            " (coalesce(:types) is null or u.type in :types) ")
    Page<User> searchUsers(List<UserType> types, Pageable pageable);

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
