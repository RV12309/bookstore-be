package com.hust.bookstore.repository;

import com.hust.bookstore.entity.OrderDetails;
import com.hust.bookstore.repository.projection.StatOderTypeProjection;
import com.hust.bookstore.repository.projection.StatRevenueProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetails, Long> {
    Page<OrderDetails> findAllByUserId(Long id, Pageable pageable);

    Page<OrderDetails> findAllBySellerId(Long id, Pageable pageable);

    @Query(value = "select count(*) as totalOrder, " +
            "created_at as time from order_details " +
            "where created_at between ?1 and ?2 group by week(created_at)", nativeQuery = true)
    List<StatOderTypeProjection> statisticOrderMonth(LocalDateTime from, LocalDateTime to);

    @Query(value = "select count(*) as totalOrder, " +
            "created_at as time from order_details " +
            "where created_at between ?1 and ?2 group by month(created_at)", nativeQuery = true)
    List<StatOderTypeProjection> statisticOrderQuater(LocalDateTime from, LocalDateTime to);

    @Query(value = "select count(*) as totalOrder, " +
            "created_at as time from order_details " +
            "where created_at between ?1 and ?2 group by month(created_at)", nativeQuery = true)
    List<StatOderTypeProjection> statisticOrderYear(LocalDateTime from, LocalDateTime to);

    @Query(value = "select count(*) as totalOrder, " +
            "created_at as time from order_details " +
            "where created_at between ?1 and ?2 group by day(created_at)", nativeQuery = true)
    List<StatOderTypeProjection> statisticOrderWeek(LocalDateTime from, LocalDateTime to);

    @Query(value = "select count(*) as totalOrder, " +
            "created_at as time from order_details " +
            "where created_at between ?1 and ?2 group by time(created_at)", nativeQuery = true)
    List<StatOderTypeProjection> statisticOrderDay(LocalDateTime from, LocalDateTime to);

    @Query(value = "select sum(o.total) as totalAmount, " +
            "created_at as time from order_details o " +
            "where created_at between ?1 and ?2 group by day(created_at)", nativeQuery = true)
    List<StatRevenueProjection> statisticRevenueMonth(LocalDateTime from, LocalDateTime to);

    @Query(value = "select sum(o.total) as totalAmount, " +
            "created_at as time from order_details o " +
            "where created_at between ?1 and ?2 group by month(created_at)", nativeQuery = true)
    List<StatRevenueProjection> statisticRevenueQuater(LocalDateTime from, LocalDateTime to);

    @Query(value = "select sum(o.total) as totalAmount, " +
            "created_at as time from order_details o " +
            "where created_at between ?1 and ?2 group by month(created_at)", nativeQuery = true)
    List<StatRevenueProjection> statisticRevenueYear(LocalDateTime from, LocalDateTime to);

    @Query(value = "select sum(o.total) as totalAmount, " +
            "created_at as time from order_details o " +
            "where created_at between ?1 and ?2 group by day(created_at)", nativeQuery = true)
    List<StatRevenueProjection> statisticRevenueWeek(LocalDateTime from, LocalDateTime to);

    @Query(value = "select sum(o.total) as totalAmount, " +
            "created_at as time from order_details o " +
            "where created_at between ?1 and ?2 group by time(created_at)", nativeQuery = true)
    List<StatRevenueProjection> statisticRevenueOrderDay(LocalDateTime from, LocalDateTime to);
}
