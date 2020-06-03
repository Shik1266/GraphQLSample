package com.example.graphqlsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity(),View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnKt.setOnClickListener(this)
        btnJava.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnKt -> {
                startActivity<GraphQLKt>()
            }
            R.id.btnJava -> {
                startActivity<GraphQLJava>()
            }
        }
    }
}
