package com.example.projectiiapp.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.FirebaseDatabase

class AuthViewModel: ViewModel(){
    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

//    private val _currentUserId = MutableLiveData<String?>(null)
//    val currentUserId: LiveData<String?> = _currentUserId

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference


    init {
        checkCurrentUser()
//        setAuthState()
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {task ->
                if(task.isSuccessful){
                    val userId = firebaseAuth.currentUser?.uid ?: ""
                    verifyUserRecord(userId) { exists ->
                        if (exists) {
                            _authState.value = AuthState.Success(userId)
//                            _currentUserId.value = userId
                        } else {
                            _authState.value = AuthState.Error("Tài khoản không tồn tại trong hệ thống")
                            firebaseAuth.signOut()
                        }
                    }
                }else{
                    val errorMessage = when (task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> "Email hoặc mật khẩu không đúng"
                        else -> "Đăng nhập thất bại: ${task.exception?.message ?: "Lỗi không xác định"}"
                    }
                    _authState.value = AuthState.Error(errorMessage)
                }
            }

    }

    fun signUp(email: String, password: String) {
        _authState.value = AuthState.Loading
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    val userId = firebaseAuth.currentUser?.uid ?: ""
                    createUserRecord(userId, email) { success ->
                        if (success) {
                            _authState.value = AuthState.Success(userId)
//                            _currentUserId.value = userId
                        } else {
                            _authState.value = AuthState.Error("Lỗi khi tạo hồ sơ người dùng")
                            firebaseAuth.currentUser?.delete()
                        }
                    }
                }else{
                    val errorMessage = when (task.exception) {
                        is FirebaseAuthWeakPasswordException -> "Mật khẩu quá yếu"
                        is FirebaseAuthUserCollisionException -> "Email đã được đăng ký"
                        else -> "Đăng ký thất bại: ${task.exception?.message ?: "Lỗi không xác định"}"
                    }
                    _authState.value = AuthState.Error(errorMessage)
                }
            }
    }

    fun logout() {
        firebaseAuth.signOut()
        _authState.value = AuthState.LoggedOut
//        _currentUserId.value = null
    }
    private fun checkCurrentUser() {
        firebaseAuth.currentUser?.let { user ->
            verifyUserRecord(user.uid) { exists ->
                if (exists) {
                    _authState.value = AuthState.Success(user.uid)
//                    _currentUserId.value = user.uid
                } else {
                    // User exists in Auth but not in DB, need to recreate record
                    createUserRecord(user.uid, user.email ?: "") { success ->
                        if (success) {
                            _authState.value = AuthState.Success(user.uid)
                        } else {
                            _authState.value = AuthState.Error("Lỗi đồng bộ tài khoản")
                            firebaseAuth.signOut()
                        }
                    }
                }
            }
        }
    }
    private fun verifyUserRecord(userId: String, callback: (Boolean) -> Unit) {
        database.child("users").child(userId).get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.exists())
            }
            .addOnFailureListener {
                Log.v("$it", "Error creating user record")
                callback(false)
            }
    }
    private fun createUserRecord(userId: String, email: String, callback: (Boolean) -> Unit) {
        val userData = hashMapOf<String, Any>(
            "email" to email,
            "created_at" to System.currentTimeMillis(),
            "devices" to hashMapOf<String, Boolean>()
        )
        database.child("users").child(userId).setValue(userData)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                Log.v("$it", "Error creating user record")
                callback(false)
            }
    }
}