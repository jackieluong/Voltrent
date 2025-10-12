# Voltrent API Documentation

This document provides the documentation for the Voltrent API.

## Base URL

`http://localhost:8080`

## Authentication

Most endpoints require a JWT token to be included in the `Authorization` header as a Bearer token.

`Authorization: Bearer <token>`

---

## Auth API

Base path: `/api/auth`

### Register

- **Endpoint:** `POST /register`
- **Description:** Register a new user.
- **Request Body:**

```json
{
  "fullname": "string",
  "email": "string",
  "password": "string",
  "phone": "string"
}
```

- **Response:**

```json
{
  "code": 200,
  "message": "Register successfully",
  "data": {
    "email": "string",
    "fullname": "string",
    "phone": "string"
  }
}
```

### Login

- **Endpoint:** `POST /login`
- **Description:** Login a user and get a JWT token.
- **Request Body:**

```json
{
  "email": "string",
  "password": "string"
}
```

- **Response:**

```json
{
  "code": 200,
  "message": "Login successfully",
  "data": {
    "accessToken": "string",
    "refreshToken": "string",
    "tokenType": "Bearer",
    "user": {
      "email": "string",
      "fullname": "string",
      "phone": "string",
      "role": "USER"
    }
  }
}
```

---

## Booking API

Base path: `/api/bookings`

### Create Booking

- **Endpoint:** `POST /`
- **Description:** Create a new booking.
- **Authentication:** Required.
- **Request Body:**

```json
{
  "vehicle_id": "string",
  "start_time": "string (yyyy-MM-dd HH:mm:ss)",
  "end_time": "string (yyyy-MM-dd HH:mm:ss)"
}
```

- **Response:**

```json
{
  "code": 200,
  "message": "Create booking successfully",
  "data": {
    "bookingId": "string",
    "vehicleId": "string",
    "status": "string",
    "totalAmount": 0
  }
}
```

---

## Vehicle API

Base path: `/vehicles`

### Add Vehicle

- **Endpoint:** `POST /`
- **Description:** Add a new vehicle.
- **Authentication:** Required (MANAGER role).
- **Request Body:**

```json
{
  "name": "string",
  "type": "string",
  "pricePerHour": 0,
  "imageUrl": "string"
}
```

- **Response:**

```json
{
  "status": "success",
  "message": "Xe đã được tạo thành công",
  "data": {
    "id": 0,
    "ownerId": "string",
    "ownerEmail": "string",
    "name": "string",
    "type": "string",
    "pricePerHour": 0,
    "imageUrl": "string",
    "status": "AVAILABLE"
  }
}
```

### Get My Vehicles

- **Endpoint:** `GET /my`
- **Description:** Get the vehicles of the current user.
- **Authentication:** Required.
- **Response:**

```json
{
  "status": "success",
  "message": "Danh sách phương tiện của bạn",
  "data": [
    {
      "id": 0,
      "ownerId": "string",
      "ownerEmail": "string",
      "name": "string",
      "type": "string",
      "pricePerHour": 0,
      "imageUrl": "string",
      "status": "AVAILABLE"
    }
  ]
}
```

### Update Vehicle

- **Endpoint:** `PUT /{id}`
- **Description:** Update a vehicle.
- **Authentication:** Required (MANAGER role).
- **Request Body:**

```json
{
  "name": "string",
  "type": "string",
  "pricePerHour": 0,
  "imageUrl": "string"
}
```

- **Response:**

```json
{
  "status": "success",
  "message": "Cập nhật phương tiện thành công",
  "data": {
    "id": 0,
    "ownerId": "string",
    "ownerEmail": "string",
    "name": "string",
    "type": "string",
    "pricePerHour": 0,
    "imageUrl": "string",
    "status": "AVAILABLE"
  }
}
```

### Delete Vehicle

- **Endpoint:** `DELETE /{id}`
- **Description:** Delete a vehicle.
- **Authentication:** Required (MANAGER role).
- **Response:**

```json
{
  "status": "success",
  "message": "Xóa phương tiện thành công",
  "data": {}
}
```

### Search Vehicles

- **Endpoint:** `GET /search`
- **Description:** Search for available vehicles.
- **Query Parameters:**
  - `type` (string, optional)
  - `priceMin` (number, optional)
  - `priceMax` (number, optional)
- **Response:**

```json
{
  "status": "success",
  "message": "Danh sách xe tìm thấy",
  "data": [
    {
      "id": 0,
      "ownerId": "string",
      "ownerEmail": "string",
      "name": "string",
      "type": "string",
      "pricePerHour": 0,
      "imageUrl": "string",
      "status": "AVAILABLE"
    }
  ]
}
```

### Pause Vehicle

- **Endpoint:** `PUT /{id}/pause`
- **Description:** Pause a vehicle.
- **Authentication:** Required (MANAGER role).
- **Response:**

```json
{
  "status": "success",
  "message": "Tạm dừng xe thành công",
  "data": {
    "id": 0,
    "ownerId": "string",
    "ownerEmail": "string",
    "name": "string",
    "type": "string",
    "pricePerHour": 0,
    "imageUrl": "string",
    "status": "PAUSED"
  }
}
```

### Resume Vehicle

- **Endpoint:** `PUT /{id}/resume`
- **Description:** Resume a vehicle.
- **Authentication:** Required (MANAGER role).
-- **Response:**

```json
{
  "status": "success",
  "message": "Tiếp tục xe thành công",
  "data": {
    "id": 0,
    "ownerId": "string",
    "ownerEmail": "string",
    "name": "string",
    "type": "string",
    "pricePerHour": 0,
    "imageUrl": "string",
    "status": "AVAILABLE"
  }
}
```
