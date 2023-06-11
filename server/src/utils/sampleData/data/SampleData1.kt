package org.solvo.server.utils.sampleData.data

import io.ktor.http.*
import org.solvo.model.api.communication.ReactionKind
import org.solvo.server.modules.AuthDigest
import org.solvo.server.utils.sampleData.builder.SampleDataBuilder

fun SampleDataBuilder.sampleData1() {
    val alex = user("Alex", AuthDigest("alex123"))
    val bob = user("Bob", AuthDigest("bob456"))
    val carol = user("Carol", AuthDigest("carol789"))

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
            termYear("2022")
            question("1a.i)") {
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
            question("1a.ii)") {
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
            question("1a.iii)") {
                content { "![some image](${image1a.url})" }
                anonymous()
                answer(alex) {
                    content("""
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
                    """.trimIndent())
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
            questions(questionsList)
        }
    }
    course("50002", "Software Engineering Design")
    course("50003", "Models of Computation")
    course("50004", "Operating Systems")
    course("50005", "Networks and Communications")
    course("50006", "Compilers")
    course("50008", "Probability and Statistics")
    course("50009", "Symbolic Reasoning")
}