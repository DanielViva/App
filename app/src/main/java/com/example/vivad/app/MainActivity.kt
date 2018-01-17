package com.example.vivad.app

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*

data class ResponseData(
        val user: User
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
        val created: String,
        val last_login: String
){
    class Deserializer: ResponseDeserializable<User> {
        override fun deserialize(content: String): User? = Gson().fromJson(content, User::class.java)
    }
}


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.setColor(Color.argb(0.2F,1f,1f,1f))
        shape.cornerRadius = 80F

        val buttonAcessShape = GradientDrawable()
        buttonAcessShape.shape = GradientDrawable.RECTANGLE
        buttonAcessShape.setColor(Color.BLACK)
        buttonAcessShape.cornerRadius = 80F

        val buttonFacebookShape = GradientDrawable()
        buttonFacebookShape.shape = GradientDrawable.RECTANGLE
        buttonFacebookShape.setColor(Color.BLUE)
        buttonFacebookShape.cornerRadius = 80F


        userText.setBackground(shape)
        passwordText.setBackground(shape)

        facebook_access.setBackground(buttonFacebookShape)
        access_button.setBackground(buttonAcessShape)



        access_button.setOnClickListener {

            val email = userText.text.toString()
            val password = passwordText.text.toString()

            val list = listOf("email" to email,"password" to password)

            "http://painelgm7club.com.br/user/signin".httpPost(list).responseObject(ResponseData.Deserializer()) { request, response, result ->
                val (data, err) = result

                println(data)
                if (data != null) {
                    Toast.makeText(this@MainActivity, data.user.first_name, Toast.LENGTH_LONG).show()
                }
            }

        }

//                    .responseJson { request, response, result ->
//                result.fold(success = { json ->
//
//                    val name = json.obj()["user"] as? Map<String, *>
//                    if (name !=  null) {
//                       Toast.makeText(this@MainActivity,"logado",Toast.LENGTH_LONG)
//                        }else {
//                        Toast.makeText(this@MainActivity,"não logado",Toast.LENGTH_LONG)
//                    }
//
//
//
//
//                }, failure = { error ->
//                    Log.e("qdp error", error.toString())
//                })
//            }

//        }
        facebook_access.setOnClickListener {



        }


    }


}
