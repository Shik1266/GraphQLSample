package com.example.graphqlsample;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.MutationTestMutation;
import com.example.ViewerTestQuery;
import com.example.type.ReactionContent;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class GraphQLJava extends AppCompatActivity {

    private Context mContext;
    private TextView tvLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphql);

        mContext = this;
        tvLogin = findViewById(R.id.tvLogin);
        //OKHttp 설정, github 토큰 설정
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().header("Authorization",
                        "bearer f9c11f215aca6858e43272ec892c1ba68e27d007").method(chain.request().method(), chain.request().body()).build();
                return chain.proceed(request);
            }
        }).build();

        //ApolloClinet 설정, github endpoint 설정
        ApolloClient apolloClient = ApolloClient.builder().serverUrl("https://api.github.com/graphql").okHttpClient(okHttpClient).build();


        //mutate sample
        //github에 등록된 issue에 이모지 reaction을 남긴다 (하트,웃음 등)
        apolloClient.mutate(
                new MutationTestMutation("MDU6SXNzdWU2MjgzNjM4NzM", ReactionContent.HEART)
        ).enqueue(new ApolloCall.Callback<MutationTestMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<MutationTestMutation.Data> response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "mutation success", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e("khs", "onResponse Mutate");
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "mutation onFailure", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e("khs", "onFailure Mutate");
            }
        });

        //query sample
        //헤더에 보낸 토큰에 대한 로그인된 아이디를 가져온다.
        apolloClient.query(new ViewerTestQuery()).enqueue(new ApolloCall.Callback<ViewerTestQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<ViewerTestQuery.Data> response) {
                if (response.getData() != null && response.getData().getViewer() != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvLogin.setText(response.getData().getViewer().getLogin());
                        }
                    });

                }
                Log.e("khs", "onResponse viewer" + response.getData().getViewer().getLogin());
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

            }
        });
    }
}
