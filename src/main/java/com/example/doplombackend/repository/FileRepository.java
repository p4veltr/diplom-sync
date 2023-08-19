package com.example.doplombackend.repository;

import com.example.doplombackend.model.storage.FileEntry;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntry, Long> {
    Optional<FileEntry> findByFileNameEquals(String fileName);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update FileEntry file  set file.fileName = :filename  where file.id = :id")
    void updateFile(@Param("filename") String fileName, @Param("id") Long id);
}
