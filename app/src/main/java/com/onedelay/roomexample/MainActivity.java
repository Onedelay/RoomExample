package com.onedelay.roomexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.onedelay.roomexample.database.AppDatabase;
import com.onedelay.roomexample.database.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AppDatabase database;

    private TextView mTextView;
    private EditText mEditTextName;
    private EditText mEditTextAge;
    private EditText mEditTextPhone;

    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = AppDatabase.getInstance(getBaseContext());

        mTextView = findViewById(R.id.textView);
        mEditTextName = findViewById(R.id.name);
        mEditTextAge = findViewById(R.id.age);
        mEditTextPhone = findViewById(R.id.phone);

        findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = mEditTextName.getText().toString();
                final int age = Integer.parseInt(mEditTextAge.getText().toString());
                final String phone = mEditTextPhone.getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        database.userDao().insertAll(new User(name, age, phone));
                    }
                }).start();
                println("데이터가 추가되었습니다.");
            }
        });

        users = new ArrayList<>();
        findViewById(R.id.queryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                 * 아래와 같이 직접 쿼리 메소드를 호출하면 다음과 같은 오류가 뜨며 어플이 종료된다.
                 * List<User> users = database.userDao().selectAll();
                 * java.lang.IllegalStateException: Cannot access database on the main thread since it may potentially lock the UI for a long period of time.
                 */

                /*
                 * 메인스레드를 직접 접근해도 다음과 같은 오류가 뜨며 어플이 종료된다.
                 * runOnUiThread(new Runnable() {
                 *   @Override
                 *   public void run() {
                 *       users = database.userDao().selectAll();
                 *   }
                 * });
                 * java.lang.IllegalStateException: Cannot access database on the main thread since it may potentially lock the UI for a long period of time.
                 */

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        users = database.userDao().selectAll();
                    }
                }).start();

                /*
                 * 첫번째 데이터를 추가하자 마자 조회를 누르면 데이터가 없다고 나온다.
                 * 근데 두번째 데이터부터는 추가하자 마자 조회가 된다.
                 * 왜그런지 모르겠다.
                 */
                if(users.size() == 0){
                    println("데이터가 없습니다.");
                } else {
                    println("데이터를 조회합니다.");
                    User user;
                    for(int i=0; i<users.size(); i++){
                        user = users.get(i);
                        println(String.format(getString(R.string.output_format), i+1, user.getName(), user.getAge(), user.getPhone()));
                    }
                }
            }
        });

    }

    private void println(String s) {
        mTextView.append(s+"\n");
    }
}
