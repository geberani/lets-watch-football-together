package com.ranicorp.letswatchfootballtogether.ui.signIn

import android.app.Activity
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ranicorp.letswatchfootballtogether.BuildConfig
import com.ranicorp.letswatchfootballtogether.databinding.FragmentSignInBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        oneTapClient = Identity.getSignInClient(requireActivity())
        activityResultLauncher = setActivityResultLauncher(oneTapClient)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        setSignInRequest()
        binding.btnSignInWithGoogle.setOnClickListener {
            onClick()
        }
    }

    private fun setActivityResultLauncher(signInClient: SignInClient): ActivityResultLauncher<IntentSenderRequest> {
        return registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val credential = signInClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    if (idToken != null) {
                        firebaseAuthWithGoogle(idToken)
                    } else {
                        Log.d("TAG", "No ID token!")
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            } else {
                oneTapClient.signOut()
            }
        }
    }

    private fun setSignInRequest() {
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    private fun onClick() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(requireActivity()) { result ->
                try {
                    activityResultLauncher.launch(
                        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("TAG", "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(requireActivity()) { e ->
                legacySignIn()
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val googleUid = auth.currentUser?.uid
                    if (googleUid != null) {
                        findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToSettingFragment(googleUid))
                    }
                } else {
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun legacySignIn() {
        val request = GetSignInIntentRequest.builder()
            .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
            .build()
        Identity.getSignInClient(requireActivity())
            .getSignInIntent(request)
            .addOnSuccessListener { result ->
                try {
                    activityResultLauncher.launch(
                        IntentSenderRequest.Builder(result.intentSender).build()
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("TAG", "Google Sign-in failed")
                }
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "legacySignIn: ", e)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}