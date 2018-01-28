package com.example.vivad.app

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.view.Gravity
import android.widget.Toast
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.example.vivad.app.MainActivity
import com.example.vivad.app.business.UserBusiness
import kotlinx.android.synthetic.main.activity_login.*
import android.view.View.OnKeyListener
import android.view.View
import android.view.KeyEvent
import android.widget.EditText
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.interceptors.validatorResponseInterceptor
import com.google.gson.JsonParser
import org.jetbrains.anko.*


data class ResponseData(
        val success: Boolean,
        val message: String,
        val user: User?
){
    class Deserializer: ResponseDeserializable<ResponseData> {
        override fun deserialize(content: String): ResponseData? = Gson().fromJson(content, ResponseData::class.java)
    }
}

data class User(
        val first_name: String,
        val last_name: String,
        val email: String,
        val location: String,
        val avaliable: String,
        val gender: String,
        val birthday: String,
        var role: String,
        val created: String,
        val last_login: String
){
    class Deserializer: ResponseDeserializable<User> {
        override fun deserialize(content: String): User? = Gson().fromJson(content, User::class.java)
    }
}


class LoginActivity : AppCompatActivity() {

    private lateinit var mUserBusiness: UserBusiness

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mUserBusiness = UserBusiness(this)

        val email = mUserBusiness.getStoredString("UserEmail")
        if (email != "") {
            val intent = Intent(this, MainActivity::class.java )
            startActivity(intent)
            finish()
        }

        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
//        Color.valueOf(
        shape.setColor(Color.argb(51,255,255,255))
        shape.cornerRadius = 80F

        val buttonAccessShape = GradientDrawable()
        buttonAccessShape.shape = GradientDrawable.RECTANGLE
        buttonAccessShape.setColor(ResourcesCompat.getColor(getResources(), R.color.colorAccessBlue,null))
        buttonAccessShape.cornerRadius = 80F



        userText.setBackground(shape)
        passwordText.setBackground(shape)
        access_button.setBackground(buttonAccessShape)

        passwordText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                login()
                return@OnKeyListener true
            }
            false
        })

        password_forgot.setOnClickListener {

            var emailEditText: EditText? = null

            alert("Esqueceu sua senha?") {
                title = "Esqueceu sua senha?"
                positiveButton("Enviar") {
                    sendRecoveryPassword(emailEditText?.text.toString())
                    }
                customView {

                    verticalLayout {
                        textView {
                            text = "Coloque seu endereço de e-mail cadastrado para enviarmos sua recuperação de senha"
                            textSize = 16f
                            gravity = Gravity.CENTER
                        }

                        emailEditText = editText {

                        }

                        padding = dip(20)
                    }
                }
            }.show()

        }

        access_button.setOnClickListener {

            login()

        }

    }

    private fun login(){
        val email = userText.text.toString()
        val password = passwordText.text.toString()

        val list = listOf("email" to email,"password" to password)
        val loading = indeterminateProgressDialog("This a progress dialog")
        loading.show()
        "http://painelgm7club.com.br/user/signin".httpPost(list).responseObject(ResponseData.Deserializer()) { request, response, result ->
            val (data, err) = result
            loading.hide()
            if (data != null) {
                if (data.user != null) {
                    if (data.user.role == "place") {
                        mUserBusiness.saveUser(email, password)

                        val intent = Intent(this, MainActivity::class.java)
                        finish()
                        startActivity(intent)
                        toast("Bem vindo ${data.user.first_name}")
                    } else {
                        toast("Usuário não tem permissão de acesso.")
                    }
                }
            } else {
                if (response.data.isEmpty()) {
                    toast("Erro. Verifique sua conexão.")
                } else {
                    val json = JsonParser().parse(String(response.data)).asJsonObject
                    if (json.has("message")) {
                        toast(json["message"].asString)
                    } else {
                        toast("Erro.")
                    }

                }
            }
        }
    }

    private fun sendRecoveryPassword(email: String) {
        val list = listOf("email" to email)

        "http://painelgm7club.com.br/user/forgot".httpPost(list).responseObject(ResponseData.Deserializer()) { request, response, result ->
            val (data, err) = result

            if (data != null) {
                if (data.success) {
                    toast("Enviado!")
                } else {
                    toast(data.message)
                }

            } else {
                if (response.data.isEmpty()) {
                    toast("Erro. Verifique sua conexão.")
                } else {
                    val json = JsonParser().parse(String(response.data)).asJsonObject
                    if (json.has("message")) {
                        toast(json["message"].asString)
                    } else {
                        toast("Erro.")
                    }

                }

            }
        }
    }

}

