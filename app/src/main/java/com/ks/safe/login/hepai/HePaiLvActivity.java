package com.ks.safe.login.hepai;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ks.safe.login.ChineseUtils;
import com.ks.safe.login.R;

public class HePaiLvActivity extends AppCompatActivity {
    EditText name1;
    EditText name2;
    Button compute;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_he_pai_lv);
        name1 = (EditText) findViewById(R.id.name1);
        name2 = (EditText) findViewById(R.id.name2);
        compute = (Button) findViewById(R.id.compute);
        result = (TextView) findViewById(R.id.result);
        compute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str1 = name1.getText().toString();
                String str2 = name2.getText().toString();
                char[] n1 = str1.toCharArray();
                char[] n2 = str2.toCharArray();
                String temp = "";
                for (int i = 0; i < n1.length; i++) {
                    temp += ChineseUtils.getStrokeCount(n1[i]) + ",";
                }
                for (int i = 0; i < n2.length; i++) {
                    temp += ChineseUtils.getStrokeCount(n2[i]) + ",";
                }
                if (temp.endsWith(",")) {
                    temp = temp.substring(0, temp.length() - 1);
                }
                String[] ss = temp.split(",");
                while (ss.length > 2) {
                    String[] tt = new String[ss.length - 1];
                    for (int i = 0; i < ss.length - 1; i++) {
                        int num = Integer.parseInt(ss[i]) + Integer.parseInt(ss[i + 1]);
                        tt[i] = num % 10 + "";
                    }
                    ss = tt;
                }
                result.setText("合拍率：" + ss[0] + ss[1] + "%");
            }
        });
    }
}
