package com.careeros.repository;

import com.careeros.entity.Resume;
import com.careeros.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Resume repository interface
 */
@Repository
public interface ResumeRepository extends JpaRepository<Resume, UUID> {

    List<Resume> findByUserOrderByUpdatedAtDesc(User user);

    List<Resume> findByUserAndStatus(User user, Resume.ResumeStatus status);

    Optional<Resume> findByUserAndIsPrimaryTrue(User user);

    @Query("SELECT r FROM Resume r WHERE r.user = :user AND r.status = :status ORDER BY r.updatedAt DESC")
    List<Resume> findUserResumesByStatus(@Param("user") User user, @Param("status") Resume.ResumeStatus status);

    @Query("SELECT r FROM Resume r WHERE r.user.id = :userId ORDER BY r.updatedAt DESC")
    List<Resume> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT r FROM Resume r WHERE r.atsScore >= :minScore ORDER BY r.atsScore DESC")
    List<Resume> findByAtsScoreGreaterThanEqual(@Param("minScore") Integer minScore);

    @Query("SELECT COUNT(r) FROM Resume r WHERE r.user = :user")
    Long countByUser(@Param("user") User user);

    @Query("SELECT r FROM Resume r WHERE r.title LIKE %:searchTerm% OR r.professionalSummary LIKE %:searchTerm%")
    List<Resume> searchResumes(@Param("searchTerm") String searchTerm);

    @Query("SELECT AVG(r.atsScore) FROM Resume r WHERE r.user = :user AND r.atsScore IS NOT NULL")
    Double getAverageAtsScoreByUser(@Param("user") User user);
}
