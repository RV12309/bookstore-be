package com.hust.bookstore.repository;

import com.hust.bookstore.entity.OrderDetails;
import com.hust.bookstore.repository.projection.StatOderProjection;
import com.hust.bookstore.repository.projection.StatRevenueProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetails, Long> {
    Page<OrderDetails> findAllByUserIdOrderByCreatedAtDesc(Long id, Pageable pageable);

    Page<OrderDetails> findAllBySellerIdOrderByCreatedAtDesc(Long id, Pageable pageable);

    @Query(value = "WITH RECURSIVE date_range AS ( " +
            "    SELECT :startDate AS date_value " +
            "    UNION " +
            "    SELECT DATE_ADD(date_value, INTERVAL 1 week) " +
            "    FROM date_range " +
            "    WHERE date_value < :endDate " +
            ") " +
            "SELECT dr.date_value AS time, " +
            "       COUNT(od.id) AS totalOrder, " +
            "       COALESCE(SUM(od.total), 0) AS totalAmount " +
            "FROM date_range dr " +
            "LEFT JOIN order_details od ON YEARWEEK(od.created_at) = YEARWEEK(dr.date_value ) " +
            "AND (od.seller_id = :id OR :id IS NULL) " +
            "GROUP BY time " +
            "ORDER BY time", nativeQuery = true)
    List<StatOderProjection> statisticOrderMonth(LocalDateTime startDate, LocalDateTime endDate, Long id);

    @Query(value = "WITH RECURSIVE month_range AS ( " +
            "    SELECT DATE_FORMAT(:startDate, '%Y-%m-%d') AS month_value " +
            "    UNION " +
            "    SELECT DATE_FORMAT(DATE_ADD(month_value, INTERVAL 1 MONTH), '%Y-%m-%d') " +
            "    FROM month_range " +
            "    WHERE month_value < DATE_FORMAT(:endDate, '%Y-%m-%d') " +
            ") " +
            "SELECT mr.month_value AS time, " +
            "       COUNT(*) AS totalOrder, " +
            "       COALESCE(SUM(total), 0) AS totalAmount " +
            "FROM month_range mr " +
            "LEFT JOIN order_details od ON month(od.created_at, '%Y-%m-%d') = month(mr.month_value )" +
            "WHERE od.created_at BETWEEN :startDate AND :endDate " +
            "AND (od.seller_id = :id OR :id IS NULL) " +
            "GROUP BY time " +
            "ORDER BY time", nativeQuery = true)
    List<StatOderProjection> statisticOrderQuater(LocalDateTime startDate, LocalDateTime endDate, Long id);

    @Query(value = "WITH RECURSIVE month_range AS ( " +
            "    SELECT DATE_FORMAT(:startDate, '%Y-%m-%d') AS month_value " +
            "    UNION " +
            "    SELECT DATE_FORMAT(DATE_ADD(month_value, INTERVAL 1 MONTH), '%Y-%m-%d') " +
            "    FROM month_range " +
            "    WHERE month_value < DATE_FORMAT(:endDate, '%Y-%m-%d') " +
            ") " +
            "SELECT mr.month_value AS time, " +
            "       COUNT(*) AS totalOrder, " +
            "       COALESCE(SUM(total), 0) AS totalAmount " +
            "FROM month_range mr " +
            "LEFT JOIN order_details od ON month(od.created_at, '%Y-%m-%d') = month(mr.month_value ) " +
            "WHERE od.created_at BETWEEN :startDate AND :endDate " +
            "AND (od.seller_id = :id OR :id IS NULL) " +
            "GROUP BY time " +
            "ORDER BY time", nativeQuery = true)
    List<StatOderProjection> statisticOrderYear(LocalDateTime startDate, LocalDateTime endDate, Long id);

    @Query(value = "WITH RECURSIVE date_range AS ( " +
            "    SELECT :startDate AS date_value " +
            "    UNION " +
            "    SELECT DATE_ADD(date_value, INTERVAL 1 DAY) " +
            "    FROM date_range " +
            "    WHERE date_value < :endDate " +
            ") " +
            "SELECT dr.date_value AS time, " +
            "       COUNT(od.id) AS totalOrder, " +
            "       COALESCE(SUM(od.total), 0) AS totalAmount " +
            "FROM date_range dr " +
            "LEFT JOIN order_details od ON DATE(od.created_at) = dr.date_value " +
            "AND (od.seller_id = :id OR :id IS NULL) " +
            "GROUP BY time " +
            "ORDER BY time", nativeQuery = true)
    List<StatOderProjection> statisticOrderWeek(LocalDateTime startDate, LocalDateTime endDate, Long id);

//    @Query(value = "SELECT DATE_FORMAT(created_at, '%Y-%m-%d %H:00:00') AS time, " +
//            "COUNT(*) AS totalOrder, " +
//            "COALESCE(SUM(total), 0) AS totalAmount " +
//            "FROM order_details " +
//            "WHERE created_at BETWEEN :startDate AND :endDate " +
//            "AND (seller_id = :id OR :id IS NULL) " +
//            "GROUP BY time " +
//            "ORDER BY time", nativeQuery = true)
    @Query(value = "WITH RECURSIVE month_range AS ( " +
            "    SELECT DATE_FORMAT(:startDate, '%Y-%m-%d %H:00:00') AS month_value " +
            "    UNION " +
            "    SELECT DATE_FORMAT(DATE_ADD(month_value, INTERVAL 1 hour), '%Y-%m-%d %H:00:00') " +
            "    FROM month_range " +
            "    WHERE month_value < DATE_FORMAT(:endDate, '%Y-%m-%d %H:00:00') " +
            ") " +
            "SELECT mr.month_value AS time, " +
            "       COUNT(*) AS totalOrder, " +
            "       COALESCE(SUM(total), 0) AS totalAmount " +
            "FROM month_range mr " +
            "LEFT JOIN order_details od ON hour(od.created_at, '%Y-%m-%d %H:00:00') = hour(mr.month_value ) " +
            "WHERE od.created_at BETWEEN :startDate AND :endDate " +
            "AND (od.seller_id = :id OR :id IS NULL) " +
            "GROUP BY time " +
            "ORDER BY time", nativeQuery = true)
    List<StatOderProjection> statisticOrderDay(LocalDateTime startDate, LocalDateTime endDate, Long id);

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
