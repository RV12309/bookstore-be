package com.hust.bookstore.repository;

import com.hust.bookstore.entity.OrderDetails;
import com.hust.bookstore.repository.projection.StatOderProjection;
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

    @Query(value = "SELECT YEARWEEK(created_at) AS time, " +
            "COUNT(*) AS totalOrder, " +
            " SUM(total) AS totalAmount " +
            "FROM order_details " +
            "WHERE created_at BETWEEN :startDate AND :endDate " +
            "GROUP BY week_number " +
            "ORDER BY week_number", nativeQuery = true)
    List<StatOderProjection> statisticOrderMonth(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "SELECT DATE_FORMAT(created_at, '%Y-%m') AS time, " +
            "       COUNT(*) AS totalOrder, " +
            "       SUM(total) AS totalAmount " +
            "FROM order_details " +
            "WHERE created_at BETWEEN :startDate AND :endDate " +
            "GROUP BY time " +
            "ORDER BY time", nativeQuery = true)
    List<StatOderProjection> statisticOrderQuater(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "SELECT DATE_FORMAT(created_at, '%Y-%m') AS time, " +
            "COUNT(*) AS totalOrder, " +
            "SUM(total) AS totalAmount " +
            "FROM order_details " +
            "WHERE created_at BETWEEN :startDate AND :endDate " +
            "GROUP BY time " +
            "ORDER BY time", nativeQuery = true)
    List<StatOderProjection> statisticOrderYear(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "SELECT DATE(created_at) AS time, " +
            "COUNT(*) AS totalOrder, " +
            "SUM(total) AS totalAmount " +
            "FROM order_details " +
            "WHERE created_at BETWEEN :startDate AND :endDate " +
            "GROUP BY time " +
            "ORDER BY time", nativeQuery = true)
    List<StatOderProjection> statisticOrderWeek(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "SELECT DATE_FORMAT(created_at, '%Y-%m-%d %H:00:00') AS time, " +
            "COUNT(*) AS totalOrder, " +
            "SUM(total) AS totalAmount " +
            "FROM order_details " +
            "WHERE created_at BETWEEN :startDate AND :endDate " +
            "GROUP BY time " +
            "ORDER BY time", nativeQuery = true)
    List<StatOderProjection> statisticOrderDay(LocalDateTime startDate, LocalDateTime endDate);

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
