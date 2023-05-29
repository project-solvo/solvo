## Content

### GET `/api/images/{resourceId}`

#### Response 404

- Image with given resource ID not found

#### Response 200

- `File`: File of the requested image

### GET `/api/courses`

#### Response 200

- `List<Course>`: list of all courses

### GET `/api/courses/{courseCode}`

#### Response 404

- Course with given code does not exist

#### Response 200

- `List<String>`: list of all articles (possibly none) in that course

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