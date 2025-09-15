import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/theme/app_colors.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/custom_search_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/widgets/error_message.dart';
import '../models/study_group.dart';
import '../providers/study_groups_provider.dart';
import '../widgets/study_group_card.dart';
import '../widgets/create_group_fab.dart';

class StudyGroupsScreen extends ConsumerStatefulWidget {
  const StudyGroupsScreen({super.key});

  @override
  ConsumerState<StudyGroupsScreen> createState() => _StudyGroupsScreenState();
}

class _StudyGroupsScreenState extends ConsumerState<StudyGroupsScreen>
    with TickerProviderStateMixin {
  late TabController _tabController;
  final TextEditingController _searchController = TextEditingController();
  String _searchQuery = '';

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 4, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    _searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CustomAppBar(
        title: 'Study Groups',
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications_outlined),
            onPressed: () => context.push('/notifications'),
          ),
        ],
        bottom: TabBar(
          controller: _tabController,
          isScrollable: true,
          labelColor: AppColors.primary,
          unselectedLabelColor: AppColors.textSecondary,
          indicatorColor: AppColors.primary,
          tabs: const [
            Tab(text: 'Discover'),
            Tab(text: 'My Groups'),
            Tab(text: 'Featured'),
            Tab(text: 'Recent'),
          ],
        ),
      ),
      body: Column(
        children: [
          // Search Bar
          Padding(
            padding: const EdgeInsets.all(16),
            child: CustomSearchBar(
              controller: _searchController,
              hintText: 'Search study groups...',
              onChanged: (value) {
                setState(() {
                  _searchQuery = value;
                });
              },
            ),
          ),
          
          // Tab Content
          Expanded(
            child: TabBarView(
              controller: _tabController,
              children: [
                _buildDiscoverTab(),
                _buildMyGroupsTab(),
                _buildFeaturedTab(),
                _buildRecentTab(),
              ],
            ),
          ),
        ],
      ),
      floatingActionButton: const CreateGroupFAB(),
    );
  }

  Widget _buildDiscoverTab() {
    final studyGroupsAsync = ref.watch(discoverStudyGroupsProvider(_searchQuery));
    
    return studyGroupsAsync.when(
      data: (groups) => _buildGroupsList(groups),
      loading: () => const LoadingIndicator(),
      error: (error, stack) => ErrorMessage(
        message: 'Failed to load study groups',
        onRetry: () => ref.refresh(discoverStudyGroupsProvider(_searchQuery)),
      ),
    );
  }

  Widget _buildMyGroupsTab() {
    final myGroupsAsync = ref.watch(myStudyGroupsProvider);
    
    return myGroupsAsync.when(
      data: (groups) => groups.isEmpty
          ? _buildEmptyState(
              icon: Icons.group_outlined,
              title: 'No Groups Yet',
              subtitle: 'Join or create your first study group to get started!',
            )
          : _buildGroupsList(groups),
      loading: () => const LoadingIndicator(),
      error: (error, stack) => ErrorMessage(
        message: 'Failed to load your groups',
        onRetry: () => ref.refresh(myStudyGroupsProvider),
      ),
    );
  }

  Widget _buildFeaturedTab() {
    final featuredGroupsAsync = ref.watch(featuredStudyGroupsProvider);
    
    return featuredGroupsAsync.when(
      data: (groups) => _buildGroupsList(groups),
      loading: () => const LoadingIndicator(),
      error: (error, stack) => ErrorMessage(
        message: 'Failed to load featured groups',
        onRetry: () => ref.refresh(featuredStudyGroupsProvider),
      ),
    );
  }

  Widget _buildRecentTab() {
    final recentGroupsAsync = ref.watch(recentStudyGroupsProvider);
    
    return recentGroupsAsync.when(
      data: (groups) => _buildGroupsList(groups),
      loading: () => const LoadingIndicator(),
      error: (error, stack) => ErrorMessage(
        message: 'Failed to load recent groups',
        onRetry: () => ref.refresh(recentStudyGroupsProvider),
      ),
    );
  }

  Widget _buildGroupsList(List<StudyGroup> groups) {
    if (groups.isEmpty) {
      return _buildEmptyState(
        icon: Icons.search_outlined,
        title: 'No Groups Found',
        subtitle: 'Try adjusting your search or create a new group',
      );
    }

    return RefreshIndicator(
      onRefresh: () async {
        ref.invalidate(discoverStudyGroupsProvider(_searchQuery));
        ref.invalidate(myStudyGroupsProvider);
        ref.invalidate(featuredStudyGroupsProvider);
        ref.invalidate(recentStudyGroupsProvider);
      },
      child: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: groups.length,
        itemBuilder: (context, index) {
          final group = groups[index];
          return Padding(
            padding: const EdgeInsets.only(bottom: 16),
            child: StudyGroupCard(
              group: group,
              onTap: () => context.push('/study-groups/${group.id}'),
            ),
          );
        },
      ),
    );
  }

  Widget _buildEmptyState({
    required IconData icon,
    required String title,
    required String subtitle,
  }) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(32),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              icon,
              size: 80,
              color: AppColors.textSecondary,
            ),
            const SizedBox(height: 24),
            Text(
              title,
              style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                color: AppColors.textPrimary,
                fontWeight: FontWeight.w600,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 12),
            Text(
              subtitle,
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: AppColors.textSecondary,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 32),
            ElevatedButton.icon(
              onPressed: () => context.push('/study-groups/create'),
              icon: const Icon(Icons.add),
              label: const Text('Create Group'),
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.primary,
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(
                  horizontal: 24,
                  vertical: 12,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
