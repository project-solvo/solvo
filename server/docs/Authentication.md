## Authentication

### POST `/register`

#### Request

```json5
{
    "username": "",
    "password": []
    // md5
}
```

#### Response 400

- Invalid username
- Username already exists

```json
{
    "success": false,
    "reason": "INVALID_USERNAME"
}
```

#### Response 200

```json
{
    "success": true
}
```

### GET `/register/{username}`

#### Response 200

- Username can be registered 

```json
{
    "validity": true
}
```

- Username already registered

```json
{
    "validity": false
}
```

### POST `/login`

#### Request

```json5
{
    "username": "",
    "password": []
    // md5
}
```

#### Response 200

```json5
{
    "success": true,
    "userInfo": {
        // ...
    },
    "token": "token"
}
```

#### Response 400

- User not found
- Wrong password

```json5
{
    "success": false,
    "reason": ""
}
```
