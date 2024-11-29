package io.github.jakubherr.gitfit.data

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.postgrest.Postgrest

class Supabase {
    private val supabase = createSupabaseClient(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseKey
    ) {
        install(Auth)
        install(Postgrest)
        //install other modules
    }

    private val auth = supabase.auth

    // TODO
    //  return result?
    //  empty user and password get handled by backend
    suspend fun registerUser(userEmail: String, userPassword: String) = try {
        auth.signUpWith(Email) {
            email = userEmail
            password = userPassword
        }
    } catch (e: HttpRequestException) {
        println("Failed to register user")
    }

    suspend fun signIn(userEmail: String, userPassword: String) = try {
        auth.signInWith(Email) {
            email = userEmail
            password = userPassword
        }
    } catch (e: HttpRequestException) {
        println("Failed to sign in user")
    }

    suspend fun signOut() = auth.signOut()
}