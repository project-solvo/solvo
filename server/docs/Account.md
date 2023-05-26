## Accounts

### GET `/api/account/{uid}/avatar`

#### Response 404

- Avatar of user {uid} not found

#### Response 200

- `File`: Avatar file of user {uid}

### PUT `/api/account/{uid}/newAvatar`

#### Request

- `File`: File of new avatar

#### Response 401

- Do not have permission to change avatar

#### Response 200

```json5
{
    "url": ".." // Url of new avatar uploaded
}
```