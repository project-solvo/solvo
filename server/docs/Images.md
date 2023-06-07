## Content

### GET `/api/images/{resourceId}`

#### Response 404

- Image with given resource ID not found

#### Response 200

- `File`: File of the requested image

### POST `/api/images/upload`

#### Request

- `File`: File of image to be uploaded

#### Response 401

- User unauthorized

#### Response 200

```json5
{
    url: ".." // Url of image uploaded
}
```
