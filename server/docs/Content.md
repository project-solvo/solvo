## Content

### GET `/images/{resourceId}`

#### Response 404

- Image with given resource ID not found

#### Response 200

- `File`: File of the requested image

### GET `/courses`

#### Response 200

- `List<Course>`: list of all courses

### GET `/courses/{courseCode}`

#### Response 404

- Course with given code does not exist

#### Response 200

- `List<String>`: list of all possible terms of articles in that course

### GET `/courses/{courseCode}/{termName}`

#### Response 404

- Course or term does not exist

#### Response 200

- `List<Article>`: list of all articles (possibly none) of given course at given term

### GET `/courses/{courseCode}/{termName}/{articleName}`

#### Response 404

- Course or term does not exist, or such article is not found

#### Response 200

- `List<Question>`: list of questions of the article found 

### GET `/courses/{courseCode}/{termName}/{articleName}/{questionIndex}`

#### Response 404

- Course or term does not exist, or such article or question is not found

#### Response 200

```json5
{
  coid: 123,  // allocated COID
  auther: null,  // null if anonymous, otherwise the author
  content: "..",
  anonymity: true,  // true or false
  index: "1a",
  answers: [/* List of answers with their comments */],
  comments: [/* List of comments */]
}
```