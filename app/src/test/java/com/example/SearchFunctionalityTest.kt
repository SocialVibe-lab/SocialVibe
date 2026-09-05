package com.example

import com.example.ui.getMockSearchPosts
import com.example.ui.getMockSearchUsers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchFunctionalityTest {

    @Test
    fun `mock users list contains required users`() {
        val users = getMockSearchUsers()
        assertTrue(users.any { it.name == "Sophia Lin" })
        assertTrue(users.any { it.name == "Marcus Vance" })
        assertTrue(users.any { it.name == "Elena Gomez" })
        assertTrue(users.any { it.name == "Jackson" })
    }

    @Test
    fun `searching for sophia returns Sophia Lin case-insensitively`() {
        val users = getMockSearchUsers()
        val query = "sophia"

        val matches = users.filter { user ->
            user.name.contains(query, ignoreCase = true) ||
                user.username.contains(query, ignoreCase = true) ||
                user.bio.contains(query, ignoreCase = true)
        }

        assertTrue(matches.isNotEmpty())
        assertEquals("Sophia Lin", matches.first().name)

        // Uppercase check
        val upperMatches = users.filter { user ->
            user.name.contains("SOPHIA", ignoreCase = true) ||
                user.username.contains("SOPHIA", ignoreCase = true) ||
                user.bio.contains("SOPHIA", ignoreCase = true)
        }
        assertEquals(1, upperMatches.size)
    }

    @Test
    fun `searching for golden returns posts containing golden`() {
        val posts = getMockSearchPosts()
        val query = "golden"

        val matches = posts.filter { post ->
            post.text.contains(query, ignoreCase = true) ||
                post.authorName.contains(query, ignoreCase = true) ||
                post.authorHandle.contains(query, ignoreCase = true)
        }

        assertTrue(matches.isNotEmpty())
        assertTrue(matches.any { it.text.contains("Catching the golden hour vibes today!", ignoreCase = true) })
        assertTrue(matches.any { it.text.contains("Golden hour by the beach.", ignoreCase = true) })
    }

    @Test
    fun `searching for productive returns posts containing Having a productive week`() {
        val posts = getMockSearchPosts()
        val query = "productive"

        val matches = posts.filter { post ->
            post.text.contains(query, ignoreCase = true) ||
                post.authorName.contains(query, ignoreCase = true) ||
                post.authorHandle.contains(query, ignoreCase = true)
        }

        assertTrue(matches.isNotEmpty())
        assertTrue(matches.any { it.text.contains("Having a productive week!", ignoreCase = true) })
    }

    @Test
    fun `searching for non-existent query yields empty results`() {
        val users = getMockSearchUsers()
        val posts = getMockSearchPosts()
        val query = "xyz123"

        val userMatches = users.filter { user ->
            user.name.contains(query, ignoreCase = true) ||
                user.username.contains(query, ignoreCase = true) ||
                user.bio.contains(query, ignoreCase = true)
        }

        val postMatches = posts.filter { post ->
            post.text.contains(query, ignoreCase = true) ||
                post.authorName.contains(query, ignoreCase = true) ||
                post.authorHandle.contains(query, ignoreCase = true)
        }

        assertTrue(userMatches.isEmpty())
        assertTrue(postMatches.isEmpty())
    }
}
