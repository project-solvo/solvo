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

### GET `/api/courses`

#### Response 200

- `List<Course>`: list of all courses
- 
### POST `/api/courses/new`

#### Request

- `Course`: Course to add

#### Response 401

- User unauthorized

#### Response 400

- Bad Course format

#### Response 200

- `Int`: ID of course

### GET `/api/courses/{courseCode}`

#### Response 404

- Course with given code does not exist

#### Response 200

- `Course`: information of the course

### GET `/api/courses/{courseCode}/articles`

#### Response 404

- Course with given code does not exist

#### Response 200

- `List<ArticleDownstream>`: list of all articles (possibly none) in that course

### POST `/api/courses/{courseCode}/articles/upload`

#### Request

`ArticleUpstream`:

```json5
{
  content: "This is some description of the paper",
  anonymity: true, // true or false

  name: "Fancy Paper Name",
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

### GET `/api/courses/{courseCode}/articles/{articleName}`

#### Response 404

- Course or term does not exist, or such article is not found

#### Response 200

- `ArticleDownstream`:

```json5
{
    coid: 120,  // allocated article COID
    auther: null,  // null if anonymous, otherwise the author
    content: "This is some description of the paper",
    anonymity: true,  // true or false
    likes: 10,
    dislikes: 0,


    name: "Fancy Paper Name",
    course: {
        code: "50000",
        name: "CourseName"
    },
    termYear: "2021-22",
    
    questionIndexes: ["1a", "1b", "2a", "2b", "3"],
    comments: [/* List of comments */],
    stars: 3,
    views: 130,
}
```

### GET `/api/courses/{courseCode}/articles/{articleName}/questions/{questionIndex}`

#### Response 404

- Course or term does not exist, or such article or question is not found

#### Response 200

- `QuestionDownstream`:

```json5
{
    coid: 123,  // allocated question COID
    auther: {
        id: 234, 
        username: "Jerry",
        avatarUrl: null
    },   // null if anonymous, otherwise the author
    content: "What is 1 + 1?",
    anonymity: false,  // true or false
    likes: 10,
    dislikes: 0,

    index: "1a",
    article: 120,  // COID of its paper
    answers: [
        {
            coid: 12345,  // allocated answer COID
            auther: null,   // null if anonymous, otherwise the author
            content: "1 + 1 = 2",
            anonymity: true,  // true or false
            likes: 10,
            
            question: 123,  // COID of its question
            comments: [/* List of comments */],
            upVotes: 10,
            downVotes: 0
        },
        // ...
    ],
    comments: [/* List of comments */],
}
```

### GET `/api/comments/get/{coid}`

#### Response 404

- Comment with given coid not found

#### Response 200

- `CommentDownstream`:
- 
```json5
{
    coid: 250,  // allocated comment COID
    auther: {
        id: 234, 
        username: "Jerry",
        avatarUrl: null
    },   // null if anonymous, otherwise the author
    content: "blah",
    anonymity: false,  // true or false
    likes: 10,
    dislikes: 0,

    parent: 123,  // parent coid
    pinned: false,
    previewSubComments: [
        {
            author: {
                id: 234,
                username: "Jerry",
                avatarUrl: null
            },
            content: "Some addition..."
        },
        {
            author: null,
            content: "I disagree..."
        },
    ], // simplified content of up to 3 sub-comments
    allSubCommentIds: [251, 254, 258, 259, 260, /*...*/]  // coid of all sub-comments
}
```


### GET `/api/comments/get/{coid}/reactions`

#### Response 200

- `List<Reaction>`: List of reactions of the comment. 
- If user token is provided in the header, each `Reaction` contains whether the user has make this kind of reaction on this content
- Return an empty list if there's no reaction or given coid does not exist


### POST `/api/comments/post/{parentId}`, `/api/comments/post/{parentId}/asAnswer`

- if `/asAnswer` is used, the comment is uploaded as an answer to a question

#### Request

`CommentUpstream`:

```json5
{
  content: "I am making a comment",
  anonymity: true, // true or false
}
```

#### Response 401

- User unauthorized

#### Response 400

- Bad comment format

#### Response 200

- `UUID`: COID of uploaded comment

### POST `/api/comments/post/{coid}/reaction`

#### Request

`ReactionKind`: type of reaction

#### Response 401

- User unauthorized

#### Response 400

- Bad request format

#### Response 200

- Successfully posted reaction