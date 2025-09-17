package com.careeros.repository;

import com.careeros.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Skill entity
 */
@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {

    /**
     * Find skill by name
     */
    Optional<Skill> findByName(String name);

    /**
     * Find skills by category
     */
    List<Skill> findByCategory(String category);

    /**
     * Find skills by name containing (case insensitive)
     */
    List<Skill> findByNameContainingIgnoreCase(String name);

    /**
     * Find skills by category and subcategory
     */
    List<Skill> findByCategoryAndSubcategory(String category, String subcategory);

    /**
     * Find all categories
     */
    @Query("SELECT DISTINCT s.category FROM Skill s ORDER BY s.category")
    List<String> findAllCategories();

    /**
     * Find subcategories by category
     */
    @Query("SELECT DISTINCT s.subcategory FROM Skill s WHERE s.category = :category ORDER BY s.subcategory")
    List<String> findSubcategoriesByCategory(@Param("category") String category);

    /**
     * Find trending skills (most popular)
     */
    @Query("SELECT s FROM Skill s JOIN UserSkill us ON s.id = us.skill.id GROUP BY s ORDER BY COUNT(us) DESC")
    List<Skill> findTrendingSkills();

    /**
     * Find skills with high market demand
     */
    @Query("SELECT s FROM Skill s WHERE s.marketDemand >= :minDemand ORDER BY s.marketDemand DESC")
    List<Skill> findHighDemandSkills(@Param("minDemand") Double minDemand);

    /**
     * Search skills by keywords
     */
    @Query("SELECT s FROM Skill s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.category) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Skill> searchByKeyword(@Param("keyword") String keyword);

    /**
     * Find related skills by category
     */
    @Query("SELECT s FROM Skill s WHERE s.category = :category AND s.id != :excludeId")
    List<Skill> findRelatedSkills(@Param("category") String category, @Param("excludeId") UUID excludeId);

    /**
     * Check if skill exists by name
     */
    boolean existsByName(String name);
}
