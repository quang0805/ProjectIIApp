package com.example.projectiiapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel: ViewModel(){
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    private val firebaseAuth = FirebaseAuth.getInstance()


    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {task ->
                if(task.isSuccessful){
                    _authState.value = AuthState.Success(firebaseAuth.currentUser?.uid ?: "")
                }else{
                    _authState.value = AuthState.Error(task.exception?.message ?: "Login failed! Please try again.")
                }
            }
    }

    fun signUp(email: String, password: String) {
        _authState.value = AuthState.Loading
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                _authState.value = if (it.isSuccessful) {
                    AuthState.Success(firebaseAuth.currentUser?.uid ?: "")
                } else {
                    AuthState.Error(it.exception?.message ?: "Sign up failed")
                }
            }
    }

    fun logout() {
        firebaseAuth.signOut()
        _authState.value = AuthState.LoggedOut
    }
}