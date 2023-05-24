## Accounts

### GET `/account/{uid}/avatar`

#### Response 404

- Avatar of user {uid} not found

#### Response 200

- Respond with the avatar file of user {uid}

### PUT `/account/{uid}/newAvatar`

#### Request

- File of new avatar

#### Response 403

- Do not have permission to change avatar

#### Response 200

- Url of new avatar uploaded

```json5
{
    "url": ".."
}
```