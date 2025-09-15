class AppConfig {
  static const String appName = 'Career OS';
  static const String appVersion = '1.0.0';
  static const String appDescription = 'AI-Powered Skill Development & Job Readiness Platform';
  
  // API Configuration
  static const String baseUrl = 'https://api.career-os.com/api/v1';
  static const String websocketUrl = 'wss://api.career-os.com/ws';
  
  // Development URLs
  static const String devBaseUrl = 'http://localhost:8080/api/v1';
  static const String devWebsocketUrl = 'ws://localhost:8080/ws';
  
  // Feature Flags
  static const bool enableOfflineMode = true;
  static const bool enablePushNotifications = true;
  static const bool enableAnalytics = true;
  static const bool enableSocialFeatures = true;
  static const bool enableAIFeatures = true;
  static const bool enableVideoLearning = true;
  static const bool enableARFeatures = false; // Future feature
  
  // Pagination
  static const int defaultPageSize = 20;
  static const int maxPageSize = 100;
  
  // Cache Configuration
  static const int cacheExpirationHours = 24;
  static const int maxCacheSize = 100; // MB
  
  // Media Configuration
  static const int maxImageSizeMB = 10;
  static const int maxVideoSizeMB = 100;
  static const List<String> supportedImageFormats = ['jpg', 'jpeg', 'png', 'webp'];
  static const List<String> supportedVideoFormats = ['mp4', 'mov', 'avi'];
  
  // Assessment Configuration
  static const int defaultAssessmentTimeLimit = 30; // minutes
  static const int maxAssessmentQuestions = 50;
  static const int passingScore = 70; // percentage
  
  // Social Features
  static const int maxPostLength = 2000;
  static const int maxCommentLength = 500;
  static const int maxGroupMembers = 1000;
  
  // Notifications
  static const String firebaseTopicAll = 'all_users';
  static const String firebaseTopicPremium = 'premium_users';
  
  // Colors
  static const int primaryColorValue = 0xFF3B82F6;
  static const int secondaryColorValue = 0xFF64748B;
  static const int accentColorValue = 0xFF10B981;
  
  // Environment
  static bool get isProduction => const String.fromEnvironment('ENVIRONMENT') == 'production';
  static bool get isDevelopment => const String.fromEnvironment('ENVIRONMENT') == 'development';
  
  static String get currentBaseUrl => isProduction ? baseUrl : devBaseUrl;
  static String get currentWebsocketUrl => isProduction ? websocketUrl : devWebsocketUrl;
}
