# Voltrent API Documentation

Tài liệu này mô tả các API của hệ thống Voltrent.

## Base URL

`http://localhost:8080/api`

## Authentication

Hầu hết các endpoint yêu cầu JWT token trong header `Authorization` dạng Bearer.

`Authorization: Bearer <token>`

---

## Auth API

Base path: `/auth`

### Đăng ký (Register)

- **Endpoint:** `POST /auth/register`
- **Description:** Đăng ký tài khoản mới.
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

### Đăng nhập (Login)

- **Endpoint:** `POST /auth/login`
- **Description:** Đăng nhập và nhận JWT token.
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
      "id": "string",
      "email": "string",
      "fullname": "string",
      "phone": "string",
      "role": "USER"
    }
  }
}
```

### Làm mới token (Refresh Token)

- **Endpoint:** `POST /auth/refresh`
- **Description:** Làm mới access token bằng refresh token.
- **Request Body:**

```json
{
  "refreshToken": "string"
}
```

- **Response:**

```json
{
  "code": 200,
  "message": "Token refreshed successfully",
  "data": {
    "accessToken": "string",
    "refreshToken": "string",
    "tokenType": "Bearer"
  }
}
```

### Đăng xuất (Logout)

- **Endpoint:** `POST /auth/logout`
- **Description:** Đăng xuất, thu hồi refresh token.
- **Authentication:** Required.
- **Response:**

```json
{
  "code": 200,
  "message": "Logout successfully"
}
```

---

## Booking API

Base path: `/bookings`

### Tạo booking (Create Booking)

- **Endpoint:** `POST /bookings`
- **Description:** Tạo booking mới.
- **Authentication:** Required.
- **Request Body:**

```json
{
  "vehicleId": "string",
  "startTime": "string (yyyy-MM-dd HH:mm:ss)",
  "endTime": "string (yyyy-MM-dd HH:mm:ss)"
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
    "status": "PENDING",
    "totalAmount": 0
  }
}
```

### Lịch sử booking (Get My Bookings)

- **Endpoint:** `GET /bookings/my`
- **Description:** Lấy danh sách booking của người dùng hiện tại.
- **Authentication:** Required.
- **Response:**

```json
{
  "code": 200,
  "message": "Danh sách booking của bạn",
  "data": [
    {
      "bookingId": "string",
      "vehicleId": "string",
      "vehicleName": "string",
      "startTime": "string",
      "endTime": "string",
      "status": "PENDING",
      "totalAmount": 0
    }
  ]
}
```

---

## Vehicle API

Base path: `/vehicles`

### Thêm xe (Add Vehicle)

- **Endpoint:** `POST /vehicles`
- **Description:** Thêm xe mới (MANAGER).
- **Authentication:** Required (MANAGER).
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
    "id": "string",
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

### Danh sách xe của tôi (Get My Vehicles)

- **Endpoint:** `GET /vehicles/my`
- **Description:** Lấy danh sách xe của người dùng hiện tại.
- **Authentication:** Required.
- **Response:**

```json
{
  "status": "success",
  "message": "Danh sách phương tiện của bạn",
  "data": [
    {
      "id": "string",
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

### Chi tiết xe (Get Vehicle Detail)

- **Endpoint:** `GET /vehicles/{id}`
- **Description:** Lấy thông tin chi tiết xe.
- **Response:**

```json
{
  "status": "success",
  "message": "Thông tin chi tiết xe",
  "data": {
    "id": "string",
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

### Cập nhật xe (Update Vehicle)

- **Endpoint:** `PUT /vehicles/{id}`
- **Description:** Cập nhật thông tin xe (MANAGER).
- **Authentication:** Required (MANAGER).
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
    "id": "string",
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

### Xóa xe (Delete Vehicle)

- **Endpoint:** `DELETE /vehicles/{id}`
- **Description:** Xóa xe (MANAGER).
- **Authentication:** Required (MANAGER).
- **Response:**

```json
{
  "status": "success",
  "message": "Xóa phương tiện thành công",
  "data": {}
}
```

### Tìm kiếm xe (Search Vehicles)

- **Endpoint:** `GET /vehicles/search`
- **Description:** Tìm kiếm xe khả dụng.
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
      "id": "string",
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

### Tạm dừng xe (Pause Vehicle)

- **Endpoint:** `PUT /vehicles/{id}/pause`
- **Description:** Tạm dừng xe (MANAGER).
- **Authentication:** Required (MANAGER).
- **Response:**

```json
{
  "status": "success",
  "message": "Tạm dừng xe thành công",
  "data": {
    "id": "string",
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

### Tiếp tục xe (Resume Vehicle)

- **Endpoint:** `PUT /vehicles/{id}/resume`
- **Description:** Tiếp tục xe (MANAGER).
- **Authentication:** Required (MANAGER).
- **Response:**

```json
{
  "status": "success",
  "message": "Tiếp tục xe thành công",
  "data": {
    "id": "string",
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
