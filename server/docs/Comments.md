### GET `/api/comments/{coid}`

#### Response 404

- Comment with given coid not found

#### Response 200

- `CommentDownstream`:
-

```json5
{
    coid: 250,
    // allocated comment COID
    auther: {
        id: 234,
        username: "Jerry",
        avatarUrl: null
    },
    // null if anonymous, otherwise the author
    content: "blah",
    anonymity: false,
    // true or false
    likes: 10,
    dislikes: 0,
    parent: 123,
    // parent coid
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
    ],
    // simplified content of up to 3 sub-comments
    allSubCommentIds: [
        251,
        254,
        258,
        259,
        260,
        /*...*/
    ]
    // coid of all sub-comments
}
```
