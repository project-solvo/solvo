## Uploads

### PUT `/upload/article`

#### Request

```json5
{
    coid: null,
    auther: null,
    content: "..",
    anonymity: true, // true or false
    course: { code: "50000", name: "CourseName" },
    termYear: "..",
    questions: [/* q1, q2, .. */],
    comments: []
}
```

#### Response 401

- User unauthorized

#### Response 400

- Article upload failed due to invalid attributes of Article

#### Response 200

```json5
{
    coid: 123, // allocated COID
    auther: null, // null if anonymous, otherwise the poster
    content: "..",
    anonymity: true, // true or false
    course: { code: "50000", name: "CourseName" },
    termYear: "..",
    questions: [/* q1, q2, .. */],
    comments: []
}
```

### PUT `/upload/answer`

#### Request

```json5
{
    coid: null,
    auther: null,
    content: "..",
    anonymity: true, // true or false
    question: 124, // COID of question
    comments: []
}
```

#### Response 401

- User unauthorized

#### Response 400

- Answer upload failed due to invalid attributes of Answer (like question COID not exist)

#### Response 200

```json5
{
    coid: 125, // allocated COID
    auther: null, // null if anonymous, otherwise the poster
    content: "..",
    anonymity: true, // true or false
    questions: 124,
    comments: []
}
```

### PUT `/upload/image`

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