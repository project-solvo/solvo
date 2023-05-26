## Uploads

### PUT `/upload/article`

#### Request

`ArticleUpstream`:

```json5
{
  content: "This is some description of the paper",
  anonymity: true, // true or false

  name: "Fancy Paper Name",
  course: {
    code: "50000",
    name: "CourseName"
  },
  termYear: "2021-22",
    
  questions: [
    {
      content: "What is 1 + 1?",
      anonymity: true, // usually same as article anonymity
      index: "1a"
    },
    {
      content: "What is 1 + 2?",
      anonymity: true,
      index: "1b"
    }, 
      // ...
  ],
}
```

#### Response 401

- User unauthorized

#### Response 400

- Article upload failed due to invalid attributes of Article (like question index too long)

#### Response 200

- `UUID`: COID of uploaded article


### PUT `/upload/answer`

#### Request

`AnswerUpstream`:

```json5
{
    content: "1 + 1 = 2",
    anonymity: true, // true or false
    
    question: 123, // COID of question
}
```

#### Response 401

- User unauthorized

#### Response 400

- Answer upload failed due to invalid attributes of Answer (like question COID not exist)

#### Response 200

- `UUID`: COID of uploaded answer

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