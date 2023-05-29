# Web Pages

This document illustrates how the web pages can be accessed.

| Page Name               | Path                                                                                         | Example                                     | Web Module           |
|-------------------------|----------------------------------------------------------------------------------------------|---------------------------------------------|----------------------|
| Home (courses)          | `/`                                                                                          |                                             | `web-pages-home`     |
| Auth (login / register) | `/auth`                                                                                      |                                             | `web-pages-auth`     |
| Course (TODO)           | `/courses/{courseCode}`                                                                      | `/courses/50006`                            | `web-pages-course`   |
| Article (TODO)          | `/courses/{courseCode}/articles/{articleCode}`                                               | `/courses/50006/articles/2022`              | `web-pages-article`  |
| Question                | `/courses/{courseCode}/articles/{articleCode}/questions/{questionCode}`                      | `/courses/50006/articles/2022/questions/1a` | `web-pages-question` |
| Answer                  | `/courses/{courseCode}/articles/{articleCode}/questions/{questionCode}/answers/{answerCode}` | `/courses/50006/articles/2022/questions/1a` | `web-pages-question` |

