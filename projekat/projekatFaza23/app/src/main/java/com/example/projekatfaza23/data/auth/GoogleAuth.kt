package com.example.projekatfaza23.data.auth

import android.content.Context
import android.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.projekatfaza23.R
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import java.security.SecureRandom
import com.example.projekatfaza23.data.repository.GoogleProfileRepository


class GoogleAuth(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)

    suspend fun GoogleSignIn(activityContext: Context) : UserProfile?{
        val nonce = generateRandomNonce()
        val googleIdOption : GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.google_client_id))
            .setNonce(nonce)
            .build()

        val request : GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager.getCredential(
                request = request,
                context = activityContext
            )
            val googleTokenCredentialData = GoogleIdTokenCredential.createFrom(result.credential.data)
            Log.d("test##", "slika: ${googleTokenCredentialData.profilePictureUri}")


            val profile = UserProfile(
                name = googleTokenCredentialData.givenName ?: throw Exception("Google Login failed: Name missing"),
                lastName = googleTokenCredentialData.familyName ?: throw Exception("Google Login failed"),
                email = googleTokenCredentialData.id,
                profilePictureURL = googleTokenCredentialData.profilePictureUri,
                //TODO nema broja telefona!
                phoneNumber = null,
                idToken = googleTokenCredentialData.idToken
            )
            UserManager.saveUser(profile)
            return profile
        } catch (e: GetCredentialException){
            Log.e("GoogleAuth", "${e.message}")
            return null
        } catch (e: GoogleIdTokenParsingException){
            Log.w("GoogleAuth", "Parsing error: ${e.message}")
            return null
        } catch (e: Exception){
            Log.e("GoogleAuth", "Unknown mistake: ${e.message}")
            return null
        }
    }

    private fun generateRandomNonce(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }
}

