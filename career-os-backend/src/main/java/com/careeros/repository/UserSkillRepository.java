package com.careeros.repository;

import com.careeros.entity.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for UserSkill entity
 */
@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, UUID> {

    /**
     * Find all skills for a specific user
     */
    List<UserSkill> findByUserId(UUID userId);

    /**
     * Find user skill by user ID and skill ID
     */
    UserSkill findByUserIdAndSkillId(UUID userId, UUID skillId);

    /**
     * Find skills by proficiency level for a user
     */
    List<UserSkill> findByUserIdAndProficiencyLevel(UUID userId, UserSkill.ProficiencyLevel proficiencyLevel);

    /**
     * Find users with a specific skill
     */
    List<UserSkill> findBySkillId(UUID skillId);

    /**
     * Find users with specific skill and minimum proficiency level
     */
    @Query("SELECT us FROM UserSkill us WHERE us.skill.id = :skillId AND us.proficiencyLevel >= :minLevel")
    List<UserSkill> findBySkillIdAndMinProficiencyLevel(@Param("skillId") UUID skillId, 
                                                        @Param("minLevel") UserSkill.ProficiencyLevel minLevel);

    /**
     * Count skills by proficiency level for a user
     */
    @Query("SELECT COUNT(us) FROM UserSkill us WHERE us.user.id = :userId AND us.proficiencyLevel = :level")
    Long countByUserIdAndProficiencyLevel(@Param("userId") UUID userId, 
                                         @Param("level") UserSkill.ProficiencyLevel level);

    /**
     * Find top skills by user count
     */
    @Query("SELECT us.skill.name, COUNT(us) as userCount FROM UserSkill us GROUP BY us.skill.name ORDER BY userCount DESC")
    List<Object[]> findTopSkillsByUserCount();

    /**
     * Find skills that need improvement for a user (beginner/intermediate level)
     */
    @Query("SELECT us FROM UserSkill us WHERE us.user.id = :userId AND us.proficiencyLevel IN ('BEGINNER', 'INTERMEDIATE')")
    List<UserSkill> findSkillsNeedingImprovement(@Param("userId") UUID userId);

    /**
     * Find users with expertise in specific skills (advanced/expert level)
     */
    @Query("SELECT us FROM UserSkill us WHERE us.skill.name IN :skillNames AND us.proficiencyLevel IN ('ADVANCED', 'EXPERT')")
    List<UserSkill> findExpertsInSkills(@Param("skillNames") List<String> skillNames);

    /**
     * Delete user skill by user ID and skill ID
     */
    void deleteByUserIdAndSkillId(UUID userId, UUID skillId);

    /**
     * Find user skill by user ID and skill name
     */
    @Query("SELECT us FROM UserSkill us WHERE us.user.id = :userId AND us.skill.name = :skillName")
    UserSkill findByUserIdAndSkillName(@Param("userId") UUID userId, @Param("skillName") String skillName);
}
