### POST `/api/comments/{coid}/comment`, `/api/comments/{coid}/answer`

- if posting to `/asAnswer`, the comment is uploaded as an answer to a question

#### Request

`CommentUpstream`:

```json5
{
    content: "I am making a comment",
    anonymity: true,
    // true or false
}
```

#### Response 401

- User unauthorized

#### Response 400

- Bad comment format

#### Response 200

- `UUID`: COID of uploaded comment

## Reactions

### GET `/api/comments/{coid}/reactions`

#### Response 200

- `List<Reaction>`: List of reactions of the comment.
- If user token is provided in the header, each `Reaction` contains whether the user has make this kind of reaction on this content
- Return an empty list if there's no reaction or given coid does not exist

### POST `/api/comments/{coid}/reactions/new`

#### Request

`ReactionKind`: type of reaction

#### Response 401

- User unauthorized

#### Response 400

- Bad request format

#### Response 200

- Successfully posted reaction
