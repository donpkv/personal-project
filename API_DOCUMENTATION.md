# Career OS - Complete API Documentation

## 📋 Table of Contents
1. [API Overview](#api-overview)
2. [Authentication](#authentication)
3. [Core APIs](#core-apis)
4. [AI-Powered APIs](#ai-powered-apis)
5. [Social Learning APIs](#social-learning-apis)
6. [Mentorship APIs](#mentorship-apis)
7. [Certification APIs](#certification-apis)
8. [Job Market APIs](#job-market-apis)
9. [Analytics APIs](#analytics-apis)
10. [Error Handling](#error-handling)
11. [Rate Limiting](#rate-limiting)
12. [SDK Examples](#sdk-examples)

## 🌐 API Overview

**Base URL**: `https://api.career-os.com/api/v1`
**Development URL**: `http://localhost:8080/api/v1`

### API Characteristics
- **RESTful Design**: Standard HTTP methods and status codes
- **JSON Format**: All requests and responses in JSON
- **JWT Authentication**: Stateless token-based authentication
- **Rate Limited**: Prevents abuse with configurable limits
- **Versioned**: API versioning for backward compatibility
- **CORS Enabled**: Cross-origin requests supported
- **OpenAPI 3.0**: Complete Swagger documentation

### HTTP Status Codes
- `200 OK` - Successful request
- `201 Created` - Resource created successfully
- `204 No Content` - Successful request with no response body
- `400 Bad Request` - Invalid request parameters
- `401 Unauthorized` - Authentication required or invalid
- `403 Forbidden` - Access denied
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource conflict (e.g., duplicate email)
- `429 Too Many Requests` - Rate limit exceeded
- `500 Internal Server Error` - Server error

## 🔐 Authentication

### JWT Token Authentication
All protected endpoints require a valid JWT token in the Authorization header.

**Header Format:**
```
Authorization: Bearer <jwt_token>
```

### Authentication Endpoints

#### POST /auth/login
Authenticate user and receive JWT token.

**Request:**
```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "email": "user@example.com",
      "fullName": "John Doe",
      "role": "USER"
    }
  }
}
```

#### POST /auth/register
Register new user account.

**Request:**
```json
{
  "email": "newuser@example.com",
  "password": "securePassword123",
  "fullName": "Jane Smith",
  "acceptTerms": true
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "message": "Account created successfully",
    "emailVerificationSent": true
  }
}
```

#### POST /auth/refresh
Refresh expired JWT token.

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600
  }
}
```

#### POST /auth/logout
Invalidate current JWT token.

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:**
```json
{
  "success": true,
  "message": "Logged out successfully"
}
```

## 🎯 Core APIs

### User Management

#### GET /users/profile
Get current user profile.

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "email": "user@example.com",
    "fullName": "John Doe",
    "profilePicture": "https://cdn.career-os.com/avatars/user123.jpg",
    "bio": "Software developer passionate about learning",
    "location": "San Francisco, CA",
    "timezone": "America/Los_Angeles",
    "joinedAt": "2024-01-15T10:30:00Z",
    "lastActiveAt": "2024-09-15T14:20:00Z",
    "preferences": {
      "emailNotifications": true,
      "pushNotifications": true,
      "learningReminders": true
    }
  }
}
```

#### PUT /users/profile
Update user profile.

**Request:**
```json
{
  "fullName": "John Smith",
  "bio": "Senior software developer and mentor",
  "location": "New York, NY",
  "timezone": "America/New_York",
  "preferences": {
    "emailNotifications": false,
    "pushNotifications": true,
    "learningReminders": true
  }
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "message": "Profile updated successfully"
  }
}
```

### Skill Management

#### GET /skills
Get all available skills with pagination.

**Query Parameters:**
- `page` (integer): Page number (default: 0)
- `size` (integer): Page size (default: 20)
- `category` (string): Filter by skill category
- `search` (string): Search skills by name

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "skill-123",
        "name": "JavaScript",
        "category": "Programming Languages",
        "description": "Dynamic programming language for web development",
        "difficulty": "INTERMEDIATE",
        "popularity": 95,
        "averageTimeToLearn": 120,
        "relatedSkills": ["TypeScript", "Node.js", "React"]
      }
    ],
    "totalElements": 500,
    "totalPages": 25,
    "currentPage": 0,
    "pageSize": 20
  }
}
```

#### GET /users/skills
Get current user's skills and proficiency levels.

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": "user-skill-123",
      "skill": {
        "id": "skill-123",
        "name": "JavaScript",
        "category": "Programming Languages"
      },
      "proficiencyLevel": "INTERMEDIATE",
      "yearsOfExperience": 2,
      "lastAssessed": "2024-08-15T10:00:00Z",
      "confidenceScore": 75,
      "learningGoal": "ADVANCED",
      "endorsements": 5
    }
  ]
}
```

#### POST /users/skills
Add or update user skill.

**Request:**
```json
{
  "skillId": "skill-123",
  "proficiencyLevel": "INTERMEDIATE",
  "yearsOfExperience": 2,
  "learningGoal": "ADVANCED"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "message": "Skill added successfully",
    "userSkillId": "user-skill-456"
  }
}
```

### Learning Resources

#### GET /learning-resources
Get learning resources with filtering and pagination.

**Query Parameters:**
- `page` (integer): Page number
- `size` (integer): Page size
- `skill` (string): Filter by skill
- `type` (string): Resource type (VIDEO, ARTICLE, COURSE, BOOK)
- `difficulty` (string): Difficulty level
- `free` (boolean): Free resources only

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "resource-123",
        "title": "Advanced JavaScript Concepts",
        "description": "Deep dive into closures, prototypes, and async programming",
        "type": "VIDEO",
        "provider": "TechEd",
        "url": "https://example.com/course/123",
        "duration": 180,
        "difficulty": "ADVANCED",
        "rating": 4.7,
        "reviewCount": 1250,
        "price": 0,
        "skills": ["JavaScript", "Async Programming"],
        "thumbnailUrl": "https://cdn.career-os.com/thumbnails/resource123.jpg"
      }
    ],
    "totalElements": 1000,
    "totalPages": 50,
    "currentPage": 0
  }
}
```

#### POST /learning-resources/{resourceId}/progress
Track learning progress for a resource.

**Request:**
```json
{
  "progressPercentage": 75,
  "timeSpentMinutes": 120,
  "status": "IN_PROGRESS",
  "notes": "Great explanation of closures"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "message": "Progress updated successfully"
  }
}
```

## 🤖 AI-Powered APIs

### Skill Recommendations

#### POST /ai/skill-recommendations
Get personalized skill recommendations based on user profile and goals.

**Request:**
```json
{
  "currentRole": "Frontend Developer",
  "targetRole": "Full Stack Developer",
  "timeframe": "6_MONTHS",
  "focusAreas": ["Backend Development", "Database Design"],
  "learningStyle": "HANDS_ON"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "recommendations": [
      {
        "skill": {
          "id": "skill-456",
          "name": "Node.js",
          "category": "Backend Frameworks"
        },
        "priority": "HIGH",
        "reasoning": "Essential for full-stack JavaScript development",
        "estimatedLearningTime": 60,
        "marketDemand": 85,
        "salaryImpact": 15000,
        "prerequisites": ["JavaScript"],
        "suggestedResources": [
          {
            "id": "resource-789",
            "title": "Node.js Complete Guide",
            "type": "COURSE",
            "provider": "TechEd"
          }
        ]
      }
    ],
    "learningPath": {
      "id": "path-123",
      "title": "Frontend to Full Stack Journey",
      "estimatedDuration": "6 months",
      "totalSkills": 5
    },
    "aiInsights": "Based on current market trends, focusing on Node.js and database skills will significantly improve your full-stack capabilities and increase your market value by approximately $15,000."
  }
}
```

### Resume Analysis

#### POST /ai/resume-analysis
Analyze resume and get AI-powered optimization suggestions.

**Request:**
```json
{
  "resumeText": "John Doe\nSoftware Developer\n...",
  "targetRole": "Senior Software Engineer",
  "targetCompany": "Google",
  "analysisType": "ATS_OPTIMIZATION"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "overallScore": 78,
    "atsScore": 82,
    "sections": {
      "summary": {
        "score": 75,
        "feedback": "Good technical summary, but could be more specific about achievements",
        "suggestions": [
          "Quantify your impact with specific metrics",
          "Include relevant keywords for the target role"
        ]
      },
      "experience": {
        "score": 85,
        "feedback": "Strong technical experience with good progression",
        "suggestions": [
          "Use more action verbs",
          "Add specific technologies and frameworks used"
        ]
      },
      "skills": {
        "score": 70,
        "feedback": "Good skill coverage but missing some trending technologies",
        "suggestions": [
          "Add cloud technologies (AWS, Azure)",
          "Include DevOps tools and practices"
        ]
      }
    },
    "missingKeywords": [
      "microservices",
      "cloud architecture",
      "CI/CD",
      "agile methodology"
    ],
    "improvementPlan": {
      "priority1": "Add quantified achievements to work experience",
      "priority2": "Include missing technical keywords",
      "priority3": "Optimize summary section for ATS parsing"
    },
    "marketAlignment": {
      "score": 80,
      "feedback": "Good alignment with senior engineer roles",
      "salaryRange": {
        "min": 120000,
        "max": 180000,
        "currency": "USD"
      }
    }
  }
}
```

### Learning Path Generation

#### POST /ai/generate-learning-path
Generate personalized learning path using AI.

**Request:**
```json
{
  "targetRole": "Data Scientist",
  "currentSkills": ["Python", "Statistics"],
  "timeAvailable": 10,
  "timeUnit": "HOURS_PER_WEEK",
  "preferredLearningStyle": "PROJECT_BASED",
  "targetCompletionDate": "2025-03-15"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "pathId": "path-456",
    "title": "Python to Data Science Mastery",
    "description": "Complete journey from Python basics to advanced data science",
    "estimatedDuration": "24 weeks",
    "difficultyLevel": "INTERMEDIATE",
    "steps": [
      {
        "id": "step-1",
        "title": "Advanced Python for Data Science",
        "description": "Master pandas, numpy, and data manipulation",
        "stepType": "LEARNING",
        "estimatedHours": 20,
        "resources": [
          {
            "id": "resource-101",
            "title": "Pandas Mastery Course",
            "type": "VIDEO"
          }
        ],
        "assessments": [
          {
            "id": "assessment-201",
            "title": "Python Data Manipulation Quiz",
            "type": "MULTIPLE_CHOICE"
          }
        ]
      }
    ],
    "aiRecommendations": {
      "studySchedule": "3 hours on weekdays, 2.5 hours on weekends",
      "difficultyProgression": "Gradual increase with practice projects",
      "keyMilestones": [
        "Complete first data analysis project by week 8",
        "Build machine learning model by week 16",
        "Portfolio project completion by week 24"
      ]
    }
  }
}
```

## 👥 Social Learning APIs

### Study Groups

#### GET /social/study-groups
Get study groups with filtering options.

**Query Parameters:**
- `page` (integer): Page number
- `size` (integer): Page size
- `category` (string): Group category
- `skill` (string): Related skill
- `privacy` (string): PUBLIC, PRIVATE, INVITE_ONLY

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "group-123",
        "name": "JavaScript Mastery Circle",
        "description": "Weekly discussions on advanced JavaScript topics",
        "category": "PROGRAMMING",
        "privacy": "PUBLIC",
        "memberCount": 45,
        "maxMembers": 100,
        "createdBy": {
          "id": "user-456",
          "fullName": "Sarah Johnson",
          "profilePicture": "https://cdn.career-os.com/avatars/user456.jpg"
        },
        "skills": ["JavaScript", "Node.js", "React"],
        "activityLevel": "HIGH",
        "lastActivity": "2024-09-15T10:30:00Z",
        "isJoined": false
      }
    ],
    "totalElements": 150,
    "totalPages": 8
  }
}
```

#### POST /social/study-groups
Create new study group.

**Request:**
```json
{
  "name": "Python Data Science Study Group",
  "description": "Weekly meetings to discuss data science projects and techniques",
  "category": "DATA_SCIENCE",
  "privacy": "PUBLIC",
  "maxMembers": 50,
  "skills": ["Python", "Machine Learning", "Data Analysis"],
  "meetingSchedule": "Every Tuesday 7 PM EST"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "groupId": "group-789",
    "message": "Study group created successfully",
    "inviteCode": "PY-DS-2024"
  }
}
```

#### POST /social/study-groups/{groupId}/join
Join a study group.

**Response:**
```json
{
  "success": true,
  "data": {
    "message": "Successfully joined the study group",
    "membershipId": "membership-123"
  }
}
```

### Group Posts & Discussions

#### GET /social/study-groups/{groupId}/posts
Get posts from a study group.

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "post-123",
        "author": {
          "id": "user-456",
          "fullName": "Mike Chen",
          "profilePicture": "https://cdn.career-os.com/avatars/user456.jpg"
        },
        "content": "Just completed a great project on neural networks. Happy to share the code!",
        "postType": "DISCUSSION",
        "createdAt": "2024-09-15T14:30:00Z",
        "likeCount": 12,
        "commentCount": 5,
        "isLiked": false,
        "attachments": [
          {
            "type": "CODE",
            "url": "https://github.com/user/neural-network-project",
            "title": "Neural Network Implementation"
          }
        ]
      }
    ]
  }
}
```

#### POST /social/study-groups/{groupId}/posts
Create new post in study group.

**Request:**
```json
{
  "content": "Looking for study partners for the upcoming AWS certification. Anyone interested?",
  "postType": "QUESTION",
  "tags": ["AWS", "Certification", "Study Partner"]
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "postId": "post-456",
    "message": "Post created successfully"
  }
}
```

## 🤝 Mentorship APIs

### Mentor Discovery

#### GET /mentorship/mentors
Discover available mentors with filtering.

**Query Parameters:**
- `skills` (string): Comma-separated skill names
- `industry` (string): Industry preference
- `experience` (integer): Minimum years of experience
- `rating` (float): Minimum rating
- `availability` (string): IMMEDIATE, WITHIN_WEEK, WITHIN_MONTH

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "mentor-123",
        "user": {
          "id": "user-789",
          "fullName": "Dr. Emily Rodriguez",
          "profilePicture": "https://cdn.career-os.com/avatars/user789.jpg",
          "title": "Senior Data Scientist at Google"
        },
        "bio": "10+ years in data science with expertise in ML, AI, and team leadership",
        "expertiseAreas": ["Machine Learning", "Data Science", "Python", "Leadership"],
        "industries": ["Technology", "Healthcare", "Finance"],
        "yearsOfExperience": 12,
        "hourlyRate": 150,
        "rating": 4.9,
        "totalReviews": 47,
        "totalMentees": 23,
        "availability": "WITHIN_WEEK",
        "languages": ["English", "Spanish"],
        "timezone": "America/Los_Angeles"
      }
    ]
  }
}
```

#### POST /mentorship/find-matches
Get AI-powered mentor matches.

**Request:**
```json
{
  "goals": ["Career transition to data science", "Technical skill development"],
  "preferredIndustry": "Technology",
  "experienceLevel": "INTERMEDIATE",
  "communicationStyle": "DIRECT",
  "sessionFrequency": "WEEKLY",
  "budget": 100,
  "minCompatibilityScore": 0.7
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "matches": [
      {
        "mentor": {
          "id": "mentor-123",
          "user": {
            "fullName": "Dr. Emily Rodriguez",
            "title": "Senior Data Scientist"
          }
        },
        "compatibilityScore": 0.92,
        "matchReasons": [
          "Excellent skill alignment with your learning goals",
          "Extensive industry experience (12 years)",
          "Highly rated by previous mentees (4.9/5.0)",
          "Good availability for regular mentoring sessions"
        ],
        "aiExplanation": "Dr. Rodriguez is an exceptional match for your data science transition goals. Her expertise in machine learning and proven track record of mentoring career changers makes her ideal for your journey."
      }
    ],
    "totalMatches": 5
  }
}
```

### Mentorship Requests

#### POST /mentorship/requests
Request mentorship from a specific mentor.

**Request:**
```json
{
  "mentorId": "mentor-123",
  "message": "Hi Dr. Rodriguez, I'm transitioning to data science and would love your guidance on building ML skills and portfolio projects.",
  "goals": [
    "Learn advanced machine learning techniques",
    "Build a strong data science portfolio",
    "Prepare for data scientist interviews"
  ],
  "preferredSchedule": "Weekly 1-hour sessions, preferably weekday evenings",
  "expectedDuration": "3_MONTHS"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "requestId": "request-456",
    "message": "Mentorship request sent successfully",
    "estimatedResponse": "within 48 hours"
  }
}
```

#### GET /mentorship/requests
Get user's mentorship requests (sent and received).

**Query Parameters:**
- `type` (string): SENT, RECEIVED, ALL
- `status` (string): PENDING, ACCEPTED, DECLINED

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": "request-456",
      "type": "SENT",
      "mentor": {
        "id": "mentor-123",
        "user": {
          "fullName": "Dr. Emily Rodriguez",
          "profilePicture": "https://cdn.career-os.com/avatars/user789.jpg"
        }
      },
      "message": "Hi Dr. Rodriguez, I'm transitioning to data science...",
      "status": "PENDING",
      "requestedAt": "2024-09-14T10:00:00Z",
      "goals": ["Learn ML techniques", "Build portfolio", "Interview prep"]
    }
  ]
}
```

### Mentorship Sessions

#### POST /mentorship/sessions
Schedule a mentorship session.

**Request:**
```json
{
  "mentorId": "mentor-123",
  "scheduledTime": "2024-09-20T15:00:00Z",
  "duration": 60,
  "topic": "Machine Learning Project Review",
  "sessionType": "VIDEO_CALL",
  "agenda": [
    "Review current ML project",
    "Discuss model optimization techniques",
    "Plan next learning steps"
  ]
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "sessionId": "session-789",
    "message": "Session scheduled successfully",
    "meetingLink": "https://meet.career-os.com/session/789",
    "calendarEvent": {
      "icsUrl": "https://api.career-os.com/calendar/session-789.ics"
    }
  }
}
```

## 🏆 Certification APIs

### Certificate Generation

#### POST /certificates/generate
Generate a digital certificate for skill or path completion.

**Request:**
```json
{
  "type": "SKILL_PROFICIENCY",
  "skillName": "Advanced JavaScript",
  "score": 92,
  "assessmentId": "assessment-123",
  "completionDate": "2024-09-15T14:30:00Z"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "certificateId": "CERT-JS2024-ABC123",
    "verificationHash": "sha256:a1b2c3d4e5f6...",
    "pdfUrl": "https://certificates.career-os.com/CERT-JS2024-ABC123.pdf",
    "verificationUrl": "https://career-os.com/verify/CERT-JS2024-ABC123",
    "blockchainHash": "0x1234567890abcdef...",
    "sharingCredentials": {
      "linkedInUrl": "https://www.linkedin.com/profile/add?startTask=CERTIFICATION_NAME&name=Advanced%20JavaScript%20Proficiency&organizationName=Career%20OS",
      "credentialId": "CERT-JS2024-ABC123"
    }
  }
}
```

### Certificate Validation

#### GET /certificates/verify/{certificateId}
Verify certificate authenticity.

**Query Parameters:**
- `hash` (string): Verification hash for additional security

**Response:**
```json
{
  "success": true,
  "data": {
    "isValid": true,
    "certificateId": "CERT-JS2024-ABC123",
    "recipient": {
      "name": "John Doe",
      "profileUrl": "https://career-os.com/profile/user123"
    },
    "skillName": "Advanced JavaScript",
    "issuer": "Career OS",
    "issuedAt": "2024-09-15T14:30:00Z",
    "expiresAt": "2027-09-15T14:30:00Z",
    "score": 92,
    "verificationDetails": {
      "blockchainVerified": true,
      "hashMatches": true,
      "notRevoked": true
    }
  }
}
```

#### GET /users/certificates
Get user's certificates.

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": "cert-123",
      "certificateId": "CERT-JS2024-ABC123",
      "skillName": "Advanced JavaScript",
      "certificateType": "SKILL_PROFICIENCY",
      "issuer": "Career OS",
      "issuedAt": "2024-09-15T14:30:00Z",
      "expiresAt": "2027-09-15T14:30:00Z",
      "score": 92,
      "pdfUrl": "https://certificates.career-os.com/CERT-JS2024-ABC123.pdf",
      "verificationUrl": "https://career-os.com/verify/CERT-JS2024-ABC123",
      "isPublic": true,
      "sharingEnabled": true
    }
  ]
}
```

## 💼 Job Market APIs

### Job Recommendations

#### POST /jobs/recommendations
Get personalized job recommendations.

**Request:**
```json
{
  "location": "San Francisco, CA",
  "remoteOnly": false,
  "jobTitle": "Software Engineer",
  "salaryMin": 120000,
  "experienceLevel": "MID_LEVEL",
  "skills": ["JavaScript", "React", "Node.js"],
  "companySize": "MEDIUM"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "recommendedJobs": [
      {
        "id": "job-123",
        "title": "Senior Software Engineer",
        "company": "TechCorp Inc.",
        "location": "San Francisco, CA",
        "salary": {
          "min": 140000,
          "max": 180000,
          "currency": "USD"
        },
        "jobType": "FULL_TIME",
        "isRemote": false,
        "description": "Join our team building next-generation web applications...",
        "requiredSkills": ["JavaScript", "React", "Node.js", "PostgreSQL"],
        "experienceLevel": "SENIOR",
        "postedDate": "2024-09-10T09:00:00Z",
        "applicationDeadline": "2024-10-10T23:59:59Z",
        "matchScore": 0.89,
        "matchReasons": [
          "Strong skill alignment (89% match)",
          "Salary meets your expectations",
          "Company culture fits your preferences"
        ]
      }
    ],
    "totalJobs": 47,
    "aiInsights": "Based on your skills and preferences, you're well-positioned for senior roles in the Bay Area. Consider adding cloud technologies to increase your opportunities by 25%.",
    "skillGaps": ["AWS", "Docker", "Kubernetes"],
    "marketInsights": {
      "averageSalary": 155000,
      "demandTrend": "HIGH",
      "competitionLevel": "MODERATE"
    }
  }
}
```

### Market Analytics

#### GET /jobs/market-insights
Get job market insights for specific role and location.

**Query Parameters:**
- `jobTitle` (string): Job title to analyze
- `location` (string): Geographic location
- `skills` (string): Comma-separated skills to analyze

**Response:**
```json
{
  "success": true,
  "data": {
    "jobTitle": "Software Engineer",
    "location": "San Francisco, CA",
    "analysisDate": "2024-09-15T12:00:00Z",
    "totalJobPostings": 1247,
    "recentJobPostings": 89,
    "salaryInsights": {
      "average": 155000,
      "median": 150000,
      "min": 120000,
      "max": 220000,
      "currency": "USD"
    },
    "topHiringCompanies": [
      "Google",
      "Meta",
      "Airbnb",
      "Uber",
      "Stripe"
    ],
    "topRequiredSkills": [
      "JavaScript",
      "Python",
      "React",
      "Node.js",
      "AWS",
      "Docker",
      "Kubernetes"
    ],
    "experienceLevelDistribution": {
      "ENTRY_LEVEL": 15,
      "MID_LEVEL": 45,
      "SENIOR_LEVEL": 35,
      "LEAD_LEVEL": 5
    },
    "remoteWorkPercentage": 68,
    "demandTrend": "INCREASING",
    "competitionLevel": "HIGH"
  }
}
```

#### GET /jobs/skill-demand
Analyze skill demand in the job market.

**Query Parameters:**
- `skills` (string): Comma-separated skills to analyze
- `location` (string): Geographic location
- `timeframe` (string): LAST_MONTH, LAST_3_MONTHS, LAST_6_MONTHS

**Response:**
```json
{
  "success": true,
  "data": {
    "location": "San Francisco, CA",
    "timeframe": "LAST_3_MONTHS",
    "analysisDate": "2024-09-15T12:00:00Z",
    "skillAnalysis": {
      "JavaScript": {
        "demandCount": 1850,
        "averageSalary": 145000,
        "growthRate": 12.5,
        "trend": "INCREASING"
      },
      "React": {
        "demandCount": 1420,
        "averageSalary": 150000,
        "growthRate": 18.3,
        "trend": "RAPIDLY_INCREASING"
      },
      "Node.js": {
        "demandCount": 980,
        "averageSalary": 155000,
        "growthRate": 15.7,
        "trend": "INCREASING"
      }
    },
    "trendingSkills": [
      "TypeScript",
      "GraphQL",
      "Next.js",
      "Kubernetes",
      "Terraform"
    ],
    "decliningSkills": [
      "jQuery",
      "AngularJS",
      "PHP 5"
    ]
  }
}
```

## 📊 Analytics APIs

### User Analytics

#### GET /analytics/user/dashboard
Get comprehensive user analytics dashboard.

**Response:**
```json
{
  "success": true,
  "data": {
    "userId": "user-123",
    "generatedAt": "2024-09-15T14:30:00Z",
    "learningAnalytics": {
      "totalLearningHours": 245,
      "currentStreak": 7,
      "longestStreak": 21,
      "completedPaths": 3,
      "inProgressPaths": 2,
      "averageProgress": 68.5
    },
    "skillAnalytics": {
      "totalSkills": 12,
      "skillLevelDistribution": {
        "BEGINNER": 3,
        "INTERMEDIATE": 6,
        "ADVANCED": 2,
        "EXPERT": 1
      },
      "recentlyImprovedSkills": 4,
      "skillCategoryDistribution": {
        "Programming Languages": 5,
        "Frameworks": 4,
        "Tools": 3
      }
    },
    "performanceAnalytics": {
      "averageAssessmentScore": 84.2,
      "performanceTrend": 2.3,
      "strongAreas": ["JavaScript", "React"],
      "improvementAreas": ["Database Design", "System Architecture"]
    },
    "goalAnalytics": {
      "totalGoals": 5,
      "achievedGoals": 3,
      "goalAchievementRate": 60
    },
    "socialAnalytics": {
      "studyGroupsJoined": 2,
      "mentoringSessions": 8,
      "peerInteractionScore": 75
    },
    "careerAnalytics": {
      "certificatesEarned": 3,
      "jobReadinessScore": 82
    }
  }
}
```

### Learning Path Analytics

#### GET /analytics/learning-paths/{pathId}/effectiveness
Get learning path effectiveness analysis.

**Response:**
```json
{
  "success": true,
  "data": {
    "pathId": "path-123",
    "pathTitle": "Full Stack JavaScript Development",
    "analysisDate": "2024-09-15T14:30:00Z",
    "enrollmentStats": {
      "totalEnrollments": 1250,
      "completionRate": 73.5,
      "averageCompletionDays": 89,
      "dropoutRate": 26.5
    },
    "stepAnalytics": [
      {
        "stepId": "step-1",
        "stepTitle": "JavaScript Fundamentals",
        "completionRate": 95.2,
        "averageTime": 12.5,
        "difficultyRating": 2.3,
        "satisfactionScore": 4.6
      }
    ],
    "userFeedback": {
      "averageRating": 4.7,
      "totalReviews": 847,
      "satisfactionScore": 4.6,
      "recommendationRate": 89.2
    },
    "improvementSuggestions": [
      "Add more practical projects in the middle section",
      "Provide additional support for database concepts",
      "Include more real-world examples"
    ]
  }
}
```

### Predictive Analytics

#### GET /analytics/user/success-prediction
Get AI-powered success prediction for the user.

**Response:**
```json
{
  "success": true,
  "data": {
    "userId": "user-123",
    "predictionDate": "2024-09-15T14:30:00Z",
    "successProbability": 85.7,
    "engagementScore": 78.3,
    "learningVelocity": 4.2,
    "consistencyScore": 82.1,
    "riskFactors": [
      "Decreased activity in the last 2 weeks",
      "Struggling with advanced topics in current path"
    ],
    "recommendations": [
      "Schedule regular study sessions to maintain consistency",
      "Consider requesting mentorship for challenging topics",
      "Join study groups for peer support"
    ],
    "projectedOutcomes": {
      "currentPath": {
        "estimatedCompletion": "2024-12-15",
        "successProbability": 87.2
      },
      "skillDevelopment": {
        "nextSkillMilestone": "Advanced React",
        "estimatedTimeToAchieve": 45
      },
      "careerReadiness": {
        "jobReadyScore": 82,
        "estimatedTimeToJobReady": 60
      }
    }
  }
}
```

## ⚠️ Error Handling

### Standard Error Response Format
All API errors follow a consistent format:

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid request parameters",
    "details": [
      {
        "field": "email",
        "message": "Email format is invalid"
      },
      {
        "field": "password",
        "message": "Password must be at least 8 characters"
      }
    ],
    "timestamp": "2024-09-15T14:30:00Z",
    "path": "/api/v1/auth/register",
    "requestId": "req-123456"
  }
}
```

### Common Error Codes
- `VALIDATION_ERROR` - Request validation failed
- `AUTHENTICATION_REQUIRED` - Valid authentication token required
- `ACCESS_DENIED` - Insufficient permissions
- `RESOURCE_NOT_FOUND` - Requested resource doesn't exist
- `RESOURCE_CONFLICT` - Resource already exists or conflicts
- `RATE_LIMIT_EXCEEDED` - Too many requests
- `EXTERNAL_SERVICE_ERROR` - Third-party service unavailable
- `INTERNAL_SERVER_ERROR` - Unexpected server error

## 🚦 Rate Limiting

### Rate Limit Headers
All responses include rate limiting information:

```
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1694789400
X-RateLimit-Window: 3600
```

### Rate Limits by Endpoint Type
- **Authentication**: 10 requests per minute
- **User Management**: 100 requests per hour
- **Learning Content**: 500 requests per hour
- **AI Services**: 50 requests per hour
- **Social Features**: 200 requests per hour
- **Analytics**: 100 requests per hour

### Rate Limit Exceeded Response
```json
{
  "success": false,
  "error": {
    "code": "RATE_LIMIT_EXCEEDED",
    "message": "Too many requests. Please try again later.",
    "retryAfter": 60,
    "limit": 50,
    "window": 3600
  }
}
```

## 🛠️ SDK Examples

### JavaScript/TypeScript SDK

```typescript
import { CareerOSClient } from '@career-os/sdk';

const client = new CareerOSClient({
  baseUrl: 'https://api.career-os.com/api/v1',
  apiKey: 'your-api-key'
});

// Authentication
const authResult = await client.auth.login({
  email: 'user@example.com',
  password: 'password'
});

// Get skill recommendations
const recommendations = await client.ai.getSkillRecommendations({
  currentRole: 'Frontend Developer',
  targetRole: 'Full Stack Developer'
});

// Join study group
await client.social.joinStudyGroup('group-123');

// Get user analytics
const analytics = await client.analytics.getUserDashboard();
```

### Python SDK

```python
from career_os import CareerOSClient

client = CareerOSClient(
    base_url='https://api.career-os.com/api/v1',
    api_key='your-api-key'
)

# Authentication
auth_result = client.auth.login(
    email='user@example.com',
    password='password'
)

# Generate learning path
learning_path = client.ai.generate_learning_path(
    target_role='Data Scientist',
    current_skills=['Python', 'Statistics'],
    time_available=10
)

# Get job recommendations
jobs = client.jobs.get_recommendations(
    location='San Francisco, CA',
    job_title='Data Scientist'
)
```

### cURL Examples

```bash
# Login
curl -X POST https://api.career-os.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password"
  }'

# Get user profile
curl -X GET https://api.career-os.com/api/v1/users/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get skill recommendations
curl -X POST https://api.career-os.com/api/v1/ai/skill-recommendations \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "currentRole": "Frontend Developer",
    "targetRole": "Full Stack Developer"
  }'
```

This comprehensive API documentation provides complete coverage of all Career OS platform APIs, enabling developers to integrate with our intelligent career development ecosystem effectively.
