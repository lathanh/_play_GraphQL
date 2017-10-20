package net.lathanh.play.graphql;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.github.ViewerQuery;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import javax.annotation.Nonnull;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by rlathanh on 2017-10-12.
 */
public class MainActivity extends AppCompatActivity {

  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.activity_main);


    OkHttpClient httpClient = new OkHttpClient.Builder()
        .addInterceptor(new Interceptor() {
          @Override
          public okhttp3.Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder().method(original.method(), original.body());
            builder.header("Authorization", "bearer 3c0a2de927fcffb91198f4cccf3b45d8ed69c343");
            return chain.proceed(builder.build());
          }
        })
        .build();

    ApolloClient apolloClient = ApolloClient.builder()
        .serverUrl("https://api.github.com/graphql")
        .okHttpClient(httpClient)
        .build();

    apolloClient.query(ViewerQuery.builder().build()).enqueue(new ApolloCall.Callback<ViewerQuery.Data>() {
      @Override
      public void onResponse(@Nonnull Response<ViewerQuery.Data> response) {
        final String login = response.data().viewer().login();
        Log.v("ViewerQuery",
            "onResponse. hasErrors=" + response.hasErrors() +
            "; viewer.login=" + login);
        if (response.hasErrors()) {
          for (Error error : response.errors()) {
            Log.v("ViewerQuery", "error: " + error.message());
          }
        }
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {

        }

        MainActivity.this.runOnUiThread(new Runnable() {
          @Override public void run() {
            TextView text1 = findViewById(android.R.id.text1);
            text1.setText(getString(R.string.hello_person, login));
          }
        });
      }

      @Override
      public void onCompleted() {
        super.onCompleted();
      }

      @Override
      public void onFailure(@Nonnull ApolloException e) {
        Log.d("ViewerQuery", "onFailure.", e);
      }
    });
  }



}
