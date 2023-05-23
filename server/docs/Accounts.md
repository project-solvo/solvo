## Accounts

### POST `/accounts/register`

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

### POST `/accounts/login`

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
