# AttendEase API Contracts

This document contains the REST API endpoints, request payloads, and response structures exposed by the FastAPI backend for client consumption (Android/Mobile App).

All endpoints are prefixed with `/api/v1`.

---

## 1. Authentication Endpoints

### Login
* **URL**: `/auth/login`
* **Method**: `POST`
* **Headers**: `Content-Type: application/json`
* **Request Payload**:
  ```json
  {
    "email": "user@example.com",
    "password": "securepassword"
  }
  ```
* **Response (200 OK)**:
  ```json
  {
    "access_token": "jwt-token-string",
    "refresh_token": "jwt-token-string",
    "user": {
      "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "role": "LECTURER", // "ADMIN", "LECTURER", or "STUDENT"
      "full_name": "Dr. John Doe",
      "email": "user@example.com"
    }
  }
  ```

### Refresh Token
* **URL**: `/auth/refresh`
* **Method**: `POST`
* **Request Payload**:
  ```json
  {
    "refresh_token": "jwt-token-string"
  }
  ```
* **Response (200 OK)**:
  Same as Login response structure (generates a new token pair).

### Student Self-Registration
* **URL**: `/auth/register`
* **Method**: `POST`
* **Request Payload**:
  ```json
  {
    "email": "student@example.com",
    "password": "securepassword",
    "full_name": "Jane Smith",
    "student_id": "STU12345",
    "matric_number": "ENG1802999",
    "department_id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "level": "300"
  }
  ```
* **Response (210 Created)**:
  Same as Login response structure (logs the student in automatically).

### Get Current User Profile
* **URL**: `/auth/me`
* **Method**: `GET`
* **Headers**: `Authorization: Bearer <access_token>`
* **Response (200 OK)**:
  ```json
  {
    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "role": "STUDENT",
    "full_name": "Jane Smith",
    "email": "student@example.com"
  }
  ```

---

## 2. Dashboard Endpoints

### Admin Dashboard Overview
* **URL**: `/dashboard/admin`
* **Method**: `GET`
* **Headers**: `Authorization: Bearer <access_token>`
* **Response (200 OK)**:
  ```json
  {
    "total_students": 150,
    "total_lecturers": 25,
    "total_courses": 45,
    "active_sessions": 2
  }
  ```

### Lecturer Dashboard Overview
* **URL**: `/dashboard/lecturer`
* **Method**: `GET`
* **Headers**: `Authorization: Bearer <access_token>`
* **Response (200 OK)**:
  ```json
  {
    "full_name": "Dr. John Doe",
    "assigned_courses": 3,
    "total_sessions": 24,
    "active_sessions": [
      {
        "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        "course_code": "CS301",
        "course_title": "Data Structures & Algorithms",
        "session_code": "AB12CD",
        "expires_at": "2026-06-24T18:00:00Z"
      }
    ],
    "courses": [
      {
        "course_assignment_id": "4fa85f64-5717-4562-b3fc-2c963f66afa7",
        "course_code": "CS301",
        "course_title": "Data Structures & Algorithms",
        "credit_units": 3
      }
    ]
  }
  ```

### Student Dashboard Overview
* **URL**: `/dashboard/student`
* **Method**: `GET`
* **Headers**: `Authorization: Bearer <access_token>`
* **Response (200 OK)**:
  ```json
  {
    "full_name": "Jane Smith",
    "attendance_percentage": 85.5,
    "present_count": 17,
    "total_count": 20,
    "recent_attendance": [
      {
        "course_code": "CS301",
        "course_title": "Data Structures & Algorithms",
        "session_date": "2026-06-24",
        "check_in_time": "2026-06-24T14:05:00Z",
        "status": "PRESENT"
      }
    ]
  }
  ```

---

## 3. Course Assignments

### List Course Assignments
* **URL**: `/course-assignments/`
* **Method**: `GET`
* **Headers**: `Authorization: Bearer <access_token>`
* **Response (200 OK)**:
  ```json
  [
    {
      "id": "4fa85f64-5717-4562-b3fc-2c963f66afa7",
      "course_id": "5fa85f64-5717-4562-b3fc-2c963f66afa8",
      "lecturer_id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "academic_session_id": "6fa85f64-5717-4562-b3fc-2c963f66afa9",
      "created_at": "2026-06-24T12:00:00Z",
      "updated_at": "2026-06-24T12:00:00Z"
    }
  ]
  ```

---

## 4. Attendance Session Management (Lecturer Side)

### Open Attendance Session
* **URL**: `/attendance-sessions/`
* **Method**: `POST`
* **Headers**: `Authorization: Bearer <access_token>`
* **Request Payload**:
  ```json
  {
    "course_assignment_id": "4fa85f64-5717-4562-b3fc-2c963f66afa7",
    "session_date": "2026-06-24", // Optional. Defaults to today.
    "duration_minutes": 60, // Optional. Defaults to 60.
    "session_code": "AB12CD", // Optional. Auto-generated if omitted.
    "geofencing_enabled": true, // Optional. Default is false.
    "latitude": 6.5244, // Optional. Required if geofencing is enabled.
    "longitude": 3.3792, // Optional. Required if geofencing is enabled.
    "radius_meters": 50 // Optional. Default is 50.
  }
  ```
* **Response (201 Created)**:
  ```json
  {
    "id": "7fa85f64-5717-4562-b3fc-2c963f66afb0",
    "course_assignment_id": "4fa85f64-5717-4562-b3fc-2c963f66afa7",
    "session_date": "2026-06-24",
    "start_time": "2026-06-24T17:00:00Z",
    "expires_at": "2026-06-24T18:00:00Z",
    "session_code": "AB12CD",
    "status": "ACTIVE",
    "geofencing_enabled": true,
    "latitude": 6.5244,
    "longitude": 3.3792,
    "radius_meters": 50,
    "created_at": "2026-06-24T17:00:00Z",
    "updated_at": "2026-06-24T17:00:00Z"
  }
  ```

### Close Attendance Session
* **URL**: `/attendance-sessions/{session_id}/close`
* **Method**: `POST`
* **Headers**: `Authorization: Bearer <access_token>`
* **Response (200 OK)**:
  ```json
  {
    "id": "7fa85f64-5717-4562-b3fc-2c963f66afb0",
    "status": "CLOSED",
    "updated_at": "2026-06-24T17:35:00Z"
  }
  ```

### Get Session Checked-In Students (Register List)
* **URL**: `/attendance-sessions/{session_id}/records`
* **Method**: `GET`
* **Headers**: `Authorization: Bearer <access_token>`
* **Response (200 OK)**:
  ```json
  [
    {
      "id": "8fa85f64-5717-4562-b3fc-2c963f66afb1",
      "session_id": "7fa85f64-5717-4562-b3fc-2c963f66afb0",
      "student_id": "8fa85f64-5717-4562-b3fc-2c963f66afc2",
      "check_in_time": "2026-06-24T17:05:00Z",
      "latitude": 6.5243,
      "longitude": 3.3791,
      "status": "PRESENT",
      "created_at": "2026-06-24T17:05:00Z"
    }
  ]
  ```

---

## 5. Attendance Operations (Student Side)

### Student Check-In
* **URL**: `/attendance/check-in`
* **Method**: `POST`
* **Headers**: `Authorization: Bearer <access_token>`
* **Request Payload**:
  ```json
  {
    "session_code": "AB12CD",
    "latitude": 6.5243, // Optional. Required if geofencing is enabled.
    "longitude": 3.3791 // Optional. Required if geofencing is enabled.
  }
  ```
* **Response (201 Created)**:
  ```json
  {
    "id": "8fa85f64-5717-4562-b3fc-2c963f66afb1",
    "session_id": "7fa85f64-5717-4562-b3fc-2c963f66afb0",
    "student_id": "8fa85f64-5717-4562-b3fc-2c963f66afc2",
    "check_in_time": "2026-06-24T17:05:00Z",
    "latitude": 6.5243,
    "longitude": 3.3791,
    "status": "PRESENT",
    "created_at": "2026-06-24T17:05:00Z"
  }
  ```

### Get My Attendance History
* **URL**: `/attendance/me`
* **Method**: `GET`
* **Headers**: `Authorization: Bearer <access_token>`
* **Response (200 OK)**:
  ```json
  [
    {
      "id": "8fa85f64-5717-4562-b3fc-2c963f66afb1",
      "session_id": "7fa85f64-5717-4562-b3fc-2c963f66afb0",
      "student_id": "8fa85f64-5717-4562-b3fc-2c963f66afc2",
      "check_in_time": "2026-06-24T17:05:00Z",
      "latitude": 6.5243,
      "longitude": 3.3791,
      "status": "PRESENT",
      "created_at": "2026-06-24T17:05:00Z"
    }
  ]
  ```
