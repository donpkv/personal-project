import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:timeago/timeago.dart' as timeago;

import '../../../core/theme/app_colors.dart';
import '../../../core/widgets/skill_chip.dart';
import '../models/study_group.dart';

class StudyGroupCard extends StatelessWidget {
  final StudyGroup group;
  final VoidCallback? onTap;
  final bool showJoinButton;

  const StudyGroupCard({
    super.key,
    required this.group,
    this.onTap,
    this.showJoinButton = true,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
      ),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Header with cover image and basic info
              Row(
                children: [
                  // Cover Image
                  Container(
                    width: 60,
                    height: 60,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(8),
                      color: AppColors.surface,
                    ),
                    child: group.coverImageUrl != null
                        ? ClipRRect(
                            borderRadius: BorderRadius.circular(8),
                            child: CachedNetworkImage(
                              imageUrl: group.coverImageUrl!,
                              fit: BoxFit.cover,
                              placeholder: (context, url) => Container(
                                color: AppColors.surface,
                                child: const Icon(
                                  Icons.group,
                                  color: AppColors.textSecondary,
                                ),
                              ),
                              errorWidget: (context, url, error) => Container(
                                color: AppColors.surface,
                                child: const Icon(
                                  Icons.group,
                                  color: AppColors.textSecondary,
                                ),
                              ),
                            ),
                          )
                        : const Icon(
                            Icons.group,
                            size: 32,
                            color: AppColors.textSecondary,
                          ),
                  ),
                  
                  const SizedBox(width: 12),
                  
                  // Group Info
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          children: [
                            Expanded(
                              child: Text(
                                group.name,
                                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                                  fontWeight: FontWeight.w600,
                                  color: AppColors.textPrimary,
                                ),
                                maxLines: 1,
                                overflow: TextOverflow.ellipsis,
                              ),
                            ),
                            if (group.isFeatured)
                              Container(
                                padding: const EdgeInsets.symmetric(
                                  horizontal: 6,
                                  vertical: 2,
                                ),
                                decoration: BoxDecoration(
                                  color: AppColors.accent.withOpacity(0.1),
                                  borderRadius: BorderRadius.circular(4),
                                ),
                                child: Text(
                                  'FEATURED',
                                  style: Theme.of(context).textTheme.labelSmall?.copyWith(
                                    color: AppColors.accent,
                                    fontWeight: FontWeight.w600,
                                  ),
                                ),
                              ),
                          ],
                        ),
                        
                        const SizedBox(height: 4),
                        
                        Row(
                          children: [
                            Icon(
                              _getCategoryIcon(group.category),
                              size: 14,
                              color: AppColors.textSecondary,
                            ),
                            const SizedBox(width: 4),
                            Text(
                              _getCategoryName(group.category),
                              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                                color: AppColors.textSecondary,
                              ),
                            ),
                            const SizedBox(width: 8),
                            Icon(
                              Icons.people_outline,
                              size: 14,
                              color: AppColors.textSecondary,
                            ),
                            const SizedBox(width: 4),
                            Text(
                              '${group.memberCount} members',
                              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                                color: AppColors.textSecondary,
                              ),
                            ),
                          ],
                        ),
                      ],
                    ),
                  ),
                ],
              ),
              
              const SizedBox(height: 12),
              
              // Description
              if (group.description != null && group.description!.isNotEmpty)
                Text(
                  group.description!,
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppColors.textPrimary,
                  ),
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
              
              const SizedBox(height: 12),
              
              // Skills Tags
              if (group.focusSkills.isNotEmpty)
                Wrap(
                  spacing: 6,
                  runSpacing: 6,
                  children: group.focusSkills.take(3).map((skill) {
                    return SkillChip(
                      label: skill,
                      size: SkillChipSize.small,
                    );
                  }).toList(),
                ),
              
              const SizedBox(height: 16),
              
              // Footer with stats and actions
              Row(
                children: [
                  // Privacy indicator
                  Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 8,
                      vertical: 4,
                    ),
                    decoration: BoxDecoration(
                      color: _getPrivacyColor(group.privacyType).withOpacity(0.1),
                      borderRadius: BorderRadius.circular(4),
                    ),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Icon(
                          _getPrivacyIcon(group.privacyType),
                          size: 12,
                          color: _getPrivacyColor(group.privacyType),
                        ),
                        const SizedBox(width: 4),
                        Text(
                          _getPrivacyLabel(group.privacyType),
                          style: Theme.of(context).textTheme.labelSmall?.copyWith(
                            color: _getPrivacyColor(group.privacyType),
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                      ],
                    ),
                  ),
                  
                  const SizedBox(width: 8),
                  
                  // Activity indicator
                  Text(
                    'Active ${timeago.format(group.updatedAt)}',
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: AppColors.textSecondary,
                    ),
                  ),
                  
                  const Spacer(),
                  
                  // Join button
                  if (showJoinButton && !group.isUserMember)
                    ElevatedButton(
                      onPressed: group.canJoin ? () => _joinGroup(context) : null,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: AppColors.primary,
                        foregroundColor: Colors.white,
                        padding: const EdgeInsets.symmetric(
                          horizontal: 16,
                          vertical: 8,
                        ),
                        minimumSize: Size.zero,
                        tapTargetSize: MaterialTapTargetSize.shrinkWrap,
                      ),
                      child: Text(
                        group.privacyType == StudyGroupPrivacy.private
                            ? 'Request'
                            : 'Join',
                        style: const TextStyle(fontSize: 12),
                      ),
                    ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  IconData _getCategoryIcon(StudyGroupCategory category) {
    switch (category) {
      case StudyGroupCategory.programming:
        return Icons.code;
      case StudyGroupCategory.webDevelopment:
        return Icons.web;
      case StudyGroupCategory.mobileDevelopment:
        return Icons.phone_android;
      case StudyGroupCategory.dataScience:
        return Icons.analytics;
      case StudyGroupCategory.aiMachineLearning:
        return Icons.psychology;
      case StudyGroupCategory.cloudComputing:
        return Icons.cloud;
      case StudyGroupCategory.cybersecurity:
        return Icons.security;
      case StudyGroupCategory.devops:
        return Icons.settings;
      case StudyGroupCategory.uiUxDesign:
        return Icons.design_services;
      case StudyGroupCategory.projectManagement:
        return Icons.manage_accounts;
      default:
        return Icons.group;
    }
  }

  String _getCategoryName(StudyGroupCategory category) {
    switch (category) {
      case StudyGroupCategory.programming:
        return 'Programming';
      case StudyGroupCategory.webDevelopment:
        return 'Web Development';
      case StudyGroupCategory.mobileDevelopment:
        return 'Mobile Development';
      case StudyGroupCategory.dataScience:
        return 'Data Science';
      case StudyGroupCategory.aiMachineLearning:
        return 'AI & ML';
      case StudyGroupCategory.cloudComputing:
        return 'Cloud Computing';
      case StudyGroupCategory.cybersecurity:
        return 'Cybersecurity';
      case StudyGroupCategory.devops:
        return 'DevOps';
      case StudyGroupCategory.uiUxDesign:
        return 'UI/UX Design';
      case StudyGroupCategory.projectManagement:
        return 'Project Management';
      default:
        return 'General';
    }
  }

  IconData _getPrivacyIcon(StudyGroupPrivacy privacy) {
    switch (privacy) {
      case StudyGroupPrivacy.public:
        return Icons.public;
      case StudyGroupPrivacy.private:
        return Icons.lock;
      case StudyGroupPrivacy.restricted:
        return Icons.verified_user;
    }
  }

  String _getPrivacyLabel(StudyGroupPrivacy privacy) {
    switch (privacy) {
      case StudyGroupPrivacy.public:
        return 'Public';
      case StudyGroupPrivacy.private:
        return 'Private';
      case StudyGroupPrivacy.restricted:
        return 'Restricted';
    }
  }

  Color _getPrivacyColor(StudyGroupPrivacy privacy) {
    switch (privacy) {
      case StudyGroupPrivacy.public:
        return AppColors.accent;
      case StudyGroupPrivacy.private:
        return AppColors.warning;
      case StudyGroupPrivacy.restricted:
        return AppColors.primary;
    }
  }

  void _joinGroup(BuildContext context) {
    // TODO: Implement join group functionality
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('Joining ${group.name}...'),
        backgroundColor: AppColors.accent,
      ),
    );
  }
}
