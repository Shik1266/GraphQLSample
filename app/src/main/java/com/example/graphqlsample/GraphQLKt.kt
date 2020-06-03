package com.example.graphqlsample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.MutationTestMutation
import com.example.ViewerTestQuery
import com.example.type.ReactionContent
import kotlinx.android.synthetic.main.activity_graphql.*
import okhttp3.OkHttpClient
import org.jetbrains.anko.toast

class GraphQLKt : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphql)

        //OKHttp 설정, github 토큰 설정
        var okHttpClient = OkHttpClient().newBuilder()
            .addInterceptor {
                val original = it.request()
                val request = original.newBuilder()
                    .header(
                        "Authorization",
                        "bearer f9c11f215aca6858e43272ec892c1ba68e27d007"
                    )
                    .method(original.method(), original.body())
                    .build()
                it.proceed(request)
            }
            .build()

        //ApolloClinet 설정, github endpoint 설정
        val apolloClient: ApolloClient = ApolloClient.builder()
            .serverUrl("https://api.github.com/graphql")
            .okHttpClient(okHttpClient)
            .build()


        //mutate sample
        //github에 등록된 issue에 이모지 reaction을 남긴다 (하트,웃음 등)
        apolloClient.mutate(
            MutationTestMutation("MDU6SXNzdWU2MjgzNjM4NzM", ReactionContent.HEART)
        ).enqueue(object : ApolloCall.Callback<MutationTestMutation.Data>() {
            override fun onFailure(e: ApolloException) {
                runOnUiThread {
                    toast("mutation onFailure")
                }
                Log.e("khs", "onFailure Mutate")
            }

            override fun onResponse(response: Response<MutationTestMutation.Data>) {
                runOnUiThread {
                    toast("mutation success")
                }
                Log.e("khs", "onResponse Mutate")
            }
        })

        //query sample
        //헤더에 보낸 토큰에 대한 로그인된 아이디를 가져온다.
        apolloClient.query(
            ViewerTestQuery()
        ).enqueue(object : ApolloCall.Callback<ViewerTestQuery.Data>() {
            override fun onFailure(e: ApolloException) {
                Log.e("khs", "onFailure viewer = ${e.toString()}")
            }

            override fun onResponse(response: Response<ViewerTestQuery.Data>) {
                Log.e("khs", "onResponse viewer")
                response.data?.let {
                    runOnUiThread {
                        tvLogin.text = it.viewer.login
                    }
                }
            }
        })
    }
}