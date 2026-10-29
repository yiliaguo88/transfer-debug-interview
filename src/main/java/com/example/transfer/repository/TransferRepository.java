package com.example.transfer.repository;

import com.example.transfer.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    @Query(value = "SELECT * FROM transfer_record ORDER BY id LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Transfer> findHistoryPage(@Param("offset") long offset, @Param("limit") int limit);
}