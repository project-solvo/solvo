package org.solvo.server.utils.sampleData.data

import io.ktor.http.*
import org.solvo.model.api.communication.ReactionKind
import org.solvo.model.utils.UserPermission
import org.solvo.server.modules.AuthDigest
import org.solvo.server.utils.sampleData.builder.SampleDataBuilder

fun SampleDataBuilder.sampleData1() {
    val admin = user("Admin", AuthDigest("admin")) {
        permit(UserPermission.ROOT)
    }

    val alex = user("Alex", AuthDigest("alex")) {
        permit(UserPermission.OPERATOR)
        avatar("./test-resources/avatar1.png", ContentType.Image.PNG)
    }
    val bob = user("Bob", AuthDigest("bob"))
    val carol = user("Carol", AuthDigest("carol"))
    val david = user("David", AuthDigest("david"))
    val evan = user("Evan", AuthDigest("evan"))

    val image1a = image("./test-resources/Algorithm-2022-1a.png", alex, ContentType.Image.PNG)
    val answerImage1 = image("./test-resources/Answer-Image-1.png", alex, ContentType.Image.PNG)
    val sharedContent1a = sharedContent {
        "![some image](${image1a.url})"
    }

    val questionsList = listOf("1a", "1b", "1c", "1d", "2a", "2b", "2c")

    course("50001", "Algorithm Design and Analysis") {
        article("Paper_2022", alex) {
            content("My content")
            anonymous()
            displayName("Paper 2022")
            question("1a.i") {
                content { "![some image](${image1a.url})" }
                anonymous()
                answer(alex) {
                    content {
                        """ 
                            ```
                            import Data.Array 
                            fromHive :: [[Int]] -> Array (Int, Int) Int 
                            fromHive hive = array ((0, 0), (size, size)) compact 
                                where 
                                    size = length hive – 1 
                                    compact = [((i, j), bees) | 
                                       (i, row) <- zip [0..] hive, 
                                       (j, bees) <- zip [0..] (extend (size + 1) row)] 
                            extend m xs = take m ${'$'} (xs ++ repeat 0) 
                            ```
                            ![some image](${answerImage1.url})
                        """.trimIndent()
                    }
                    reaction(alex) {
                        react(ReactionKind.PLUS_ONE)
                    }
                    reaction(bob) {
                        react(ReactionKind.PLUS_ONE)
                        react(ReactionKind.HEART)
                    }
                    reaction(carol) {
                        react(ReactionKind.PLUS_ONE)
                        react(ReactionKind.ROCKET)
                    }
                }
                answer(bob) {
                    content(
                        """
                        Here's a one liner: 
                        ```
                        fromHive lss = let n = length lss in array ((0,0), (n,n)) [((I,j),e)|(ls,I) <-zip lss [0..], (e,j) <- zip ls [0..]] 
                        ```
                    """.trimIndent()
                    )
                    comment(alex) {
                        content("great answer")
                    }
                    reaction(alex) {
                        react(ReactionKind.PLUS_ONE)
                        react(ReactionKind.SMILE)
                    }
                    reaction(carol) {
                        react(ReactionKind.PLUS_ONE)
                    }
                }
                answer(carol) {
                    content("Haha!")
                    reaction(alex) {
                        react(ReactionKind.THINKING)
                    }
                    reaction(bob) {
                        react(ReactionKind.THINKING)
                        react(ReactionKind.EYES)
                    }
                }
            }
            question("1a.ii") {
                content { "![some image](${image1a.url})" }
                anonymous()
                answer(alex) {
                    content(
                        """ 
                            ```
                            bees :: Array (Int, Int) Int -> Int 
                            bees hiveArr 
                              = helper hiveArr 0 0 - hiveArr ! (0,0) 
                              where 
                                helper :: Int -> Int -> Int 
                                helper i j  
                                  | i == snd (bounds hiveArr)  
                                    = hiveArr ! (i, j) 
                                  | otherwise 
                                    = hiveArr ! (i, j) 
                                      + min (helper hiveArr (i + 1) j) (helper hiveArr (i  + 1) (j + 1)) 
                            ```
                            -- Complexity: O(2^n) 
                        """.trimIndent()
                    )
                }
                answer(bob) {
                    content(
                        """ 
                            An alternative answer:
                            ```
                            bees :: [[Int]] -> Int 
                            bees hive 
                              = helper (length hive – 1, 0) 
                              where 
                                hiveArr = fromHive hive 
                                helper :: (Int, Int) -> Int 
                                helper (0, _) = 0 
                                helper (i, j) = min ( 
                                    (calc (i – 1, j)), -- up and left 
                                    (calc (i – 1, j + 1)), -- up and right 
                                  where 
                                    calc (i', j’) = hiveArr ! (i', j’) + helper (i', j')
                             ```
                            -- Complexity: O(2^n) 
                        """.trimIndent()
                    )
                }
            }
            question("1a.iii") {
                content { "![some image](${image1a.url})" }
                anonymous()
                answer(alex) {
                    content(
                        """
                        ```
                        bees' :: Array (Int, Int) Int -> Int 
                        bees' hiveArr = table ! (size, size) - hiveArr ! (0,0) 
                          where 
                            table :: Array (Int, Int) Int 
                            table = tabulate ((0,0), (size, size)) (uncurry memo) 
                            memo :: Int -> Int -> Int 
                            memo i j 
                              | i == size = table ! (i, j) 
                              | otherwise = table ! (i, j) 
                                            + min (table ! (i + 1, j)) (table ! (i + 1, j + 1)) 
                            size = snd (bounds hiveArr) 
                        ```
                        -- Complexity: O(mn) where m = height of hive, n = max width of hive. 
                    """.trimIndent()
                    )
                }
            }
            question("1b") {
                content { "Haha..!" }
                anonymous()
                answer(alex) {
                    content("Short answer")
                }
            }
            question("2a") {
                content("### 10 * 25 + 1 = ?")
                anonymous()
                answer(bob) {
                    content(
                        """
                        I believe it's 251. 
                        Calculations: 
                        ```math
                        10 * 25 + 1 = 250 + 1 =  251
                        ```
                        Smarter calculations:
                        ```math
                        10 * 25 + 1 = \sum_{i=1}^{10} 25 + 1 = 251
                        ```
                        Even smarter calculations:
                        ```math
                        10 * 25 + 1 = 25 + 25 + 25 + 25 + 25 + 25 + 25 + 25 + 25 + 25 + 1 = 251
                        ```
                    """.trimIndent()
                    )
                }
            }
            comment(bob) {
                content("I am commenting an article!")
                anonymous()
            }
        }
        article("Paper_2021", alex) {
            content("My content")
            anonymous()
            displayName("Paper 2021")
            termYear("2021")
            defaultQuestions(questionsList)
        }
    }
    course("50002", "Software Engineering Design") {
        article("Paper_2022", alex) {
            termYear("2022")
            question("2a") {
                content(
                    """
                ## Video Streaming
                
                Look at the code under the folder Q2 (attached below), which contains a few different classes forming part of a system for streaming movies, and a couple of simple tests.
                
                ### 2a
                The `MediaLibrary` could be very large if it contains a large index of movie recommendations. Update the code using an appropriate design pattern to prevent more than one instance of this class being created.
                
                ```java
                package ic.doc;

                import static ic.doc.movies.Certification.PARENTAL_GUIDANCE;
                import static ic.doc.movies.Certification.TWELVE_A;
                import static java.util.Collections.EMPTY_LIST;

                import ic.doc.awards.Oscar;
                import ic.doc.movies.Actor;
                import ic.doc.movies.Genre;
                import ic.doc.movies.Movie;
                import java.util.List;
                import java.util.Set;
                import java.util.stream.Collectors;

                public class MediaLibrary {

                  // This code is simplified for the purposes of the exam - you can imagine in real life
                  // this class would be much bigger and more sophisticated, with much more data.

                  private final List<Movie> topMovies;

                  public MediaLibrary() {
                    topMovies =
                        List.of(
                            new Movie(
                                "Jurassic Park",
                                "A pragmatic paleontologist touring an almost complete theme park on an "
                                    + "island in Central America is tasked with protecting a couple of kids after "
                                    + "a power failure causes the park's cloned dinosaurs to run loose.",
                                2342384,
                                List.of(
                                    new Actor("Richard Attenborough"),
                                    new Actor("Laura Dern"),
                                    new Actor("Sam Neill"),
                                    new Actor("Jeff Goldblum")),
                                Set.of(Genre.ADVENTURE),
                                List.of(Oscar.forBest("Visual Effects")),
                                PARENTAL_GUIDANCE),
                            new Movie(
                                "No Time To Die",
                                "Another installment of the James Bond franchise",
                                1342365,
                                List.of(new Actor("Daniel Craig")),
                                Set.of(Genre.ACTION, Genre.ADVENTURE),
                                EMPTY_LIST,
                                TWELVE_A));
                  }

                  public List<Movie> recommendedMoviesFor(User user) {

                    // A sophisticated ML algorithm runs and then recommends...

                    if (user.isChild()) {
                      return topMovies.stream().filter(Movie::isSuitableForChildren).collect(Collectors.toList());
                    } else {
                      return topMovies;
                    }
                  }
                }
                ```
            """.trimIndent()
                )

                thought(alex) {
                    content(
                        """
                        The class is huge and does not contain shared data, it can therefore be refactored using the `Singleton` pattern. 
                    """.trimIndent()
                    )
                    reaction(carol) {
                        react(ReactionKind.HEART)
                        react(ReactionKind.PLUS_ONE)
                    }
                    reaction(david) {
                        react(ReactionKind.PLUS_ONE)
                    }
                }

                answer(bob) {
                    content(
                        """
                        I used the Singleton pattern. Note that the static class `InstanceHolder` ensures the instance is initialized once. 
                        
                        ```java
                        package ic.doc;

                        import static ic.doc.movies.Certification.PARENTAL_GUIDANCE;
                        import static ic.doc.movies.Certification.TWELVE_A;
                        import static java.util.Collections.EMPTY_LIST;

                        import ic.doc.awards.Oscar;
                        import ic.doc.movies.Actor;
                        import ic.doc.movies.Genre;
                        import ic.doc.movies.Movie;
                        import java.util.List;
                        import java.util.Set;
                        import java.util.stream.Collectors;

                        public final class MediaLibrary {

                          private static final class InstanceHolder {
                            private static final MediaLibrary instance = new MediaLibrary();
                          }

                          public static MediaLibrary getInstance() {
                            return InstanceHolder.instance;
                          }

                          // This code is simplified for the purposes of the exam - you can imagine in real life
                          // this class would be much bigger and more sophisticated, with much more data.

                          private final List<Movie> topMovies;

                          private MediaLibrary() {
                            topMovies =
                                List.of(
                                    new Movie(
                                        "Jurassic Park",
                                        "A pragmatic paleontologist touring an almost complete theme park on an "
                                            + "island in Central America is tasked with protecting a couple of kids after "
                                            + "a power failure causes the park's cloned dinosaurs to run loose.",
                                        2342384,
                                        List.of(
                                            new Actor("Richard Attenborough"),
                                            new Actor("Laura Dern"),
                                            new Actor("Sam Neill"),
                                            new Actor("Jeff Goldblum")),
                                        Set.of(Genre.ADVENTURE),
                                        List.of(Oscar.forBest("Visual Effects")),
                                        PARENTAL_GUIDANCE),
                                    new Movie(
                                        "No Time To Die",
                                        "Another installment of the James Bond franchise",
                                        1342365,
                                        List.of(new Actor("Daniel Craig")),
                                        Set.of(Genre.ACTION, Genre.ADVENTURE),
                                        EMPTY_LIST,
                                        TWELVE_A));
                          }

                          public List<Movie> recommendedMoviesFor(User user) {

                            // A sophisticated ML algorithm runs and then recommends...

                            if (user.isChild()) {
                              return topMovies.stream().filter(Movie::isSuitableForChildren).collect(Collectors.toList());
                            } else {
                              return topMovies;
                            }
                          }
                        }
                        ```
                    """.trimIndent()
                    )
                    reaction(carol) {
                        react(ReactionKind.EYES)
                        react(ReactionKind.THINKING)
                    }
                }

                thought(carol) {
                    content(
                        """
                        I think we can also do this. (less code)
                        
                        ```java
                        public final class MediaLibrary {
                          private static MediaLibrary instance = new MediaLibrary();

                          private MediaLibrary() {
                            // ...
                          }
                        }
                        ```
                    """.trimIndent()
                    )
                    reaction(carol) {
                        react(ReactionKind.THINKING)
                    }
                    comment(bob) {
                        content("This works, but the instance is initialized even if it may not be needed.")
                        reaction(alex) { react(ReactionKind.PLUS_ONE) }
                    }
                }

                answer(carol) {
                    content(
                        """
                        Alternative:
                        
                        ```java
                        public final class MediaLibrary {
                          private static MediaLibrary instance = null;
                          
                          public static synchronized MediaLibrary getInstance() {
                            if (instance == null) {
                              instance = new MediaLibrary();
                            }
                            return instance;
                          }

                          private MediaLibrary() {
                            // ...
                          }
                        }
                        ```
                    """.trimIndent()
                    )
                    reaction(carol) {
                        react(ReactionKind.PLUS_ONE)
                    }
                    reaction(bob) {
                        react(ReactionKind.PLUS_ONE)
                    }
                    comment(bob) {
                        content(
                            """
                            Can be improved so that in most times we don't require a monitor (via `synchronized`)
                            ```java
                            
                            private static volatile MediaLibrary instance = null;
                            public static MediaLibrary getInstance() {
                              if (instance == null) {
                                synchronized(MediaLibrary.class) {
                                  if (instance == null) {
                                    instance = new MediaLibrary();
                                  }
                                }
                              }
                              return instance;
                            }
                            ```
                        """.trimIndent()
                        )
                        reaction(alex) { react(ReactionKind.THINKING) }
                    }
                }
            }

        }
    }
    course("50003", "Models of Computation")
    course("50004", "Operating Systems") {
        article("Paper_2022", alex) {
            content("2022")
            anonymous()
            displayName("Paper 2022")
            termYear("2022")
            question("1a.i") {
                content(
                    """
                    1a An operating system (OS) supports kernel-level threads with preemptive scheduling. User programs can use a mutual exclusion lock (mutex) m of type struct pthread mutex t with the following three functions:
                    
                    pthread_mutex_init (& m )
                    pthread_mutex_lock (& m )
                    pthread_mutex_unlock (& m )
                    
                    i) Briefly explain the purpose of synchronisation primitives in operating systems and give three examples of synchronisation primitives.
                """.trimIndent()
                )
                answer(bob) {
                    content(
                        """
                        I think synchronisation primitives are designed to save memory.
                        Example: RAID 3
                    """.trimIndent()
                    )
                }
            }
        }
    }

    course("50005", "Networks and Communications")
    course("50006", "Compilers")
    course("50008", "Probability and Statistics")
}