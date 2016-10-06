package com.example.shangyang.to_dolist;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainActivity extends ListActivity implements AdapterView.OnItemLongClickListener {

    private EditText taskTitle;
    private EditText taskDescription;
    private Button addButton;
    private List<Map<String, String>> pairs;
    SimpleAdapter adapter;
    int REQUEST_EXTERNAL_STORAGE = 1;
    String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private CheckBox checkDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskTitle = (EditText) findViewById(R.id.taskTitle);
        taskDescription = (EditText) findViewById(R.id.taskDescription);
        addButton = (Button) this.findViewById(R.id.addButton);
        pairs = new ArrayList<Map<String, String>>();


        getListView().setOnItemLongClickListener(this);


       /* String start = read();

        if(start != "[]")
        {
            Map<String, String> map = new HashMap<String, String>();
            String[] newstr = start.split("=");

            for(int i = 1; i < newstr.length; i = i + 2)
            {
                map.put("title", newstr[i]);
                map.put("description", newstr[i+1]);
                pairs.add(map);
                adapter = new SimpleAdapter(getApplicationContext(), pairs,R.layout.list_item, new String[]{"title", "description"},
                        new int[]{R.id.titleTextView, R.id.descTextView});
            }
        }*/




        int permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<String, String>();

                map.put("title", taskTitle.getText().toString());
                map.put("description", taskDescription.getText().toString());
                pairs.add(map);

                String result = pairs.toString();
                write(result);

                adapter = new SimpleAdapter(getApplicationContext(), pairs,R.layout.list_item, new String[]{"title", "description"},
                        new int[]{R.id.titleTextView, R.id.descTextView})
                {
                    public View getView(final int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        checkDelete = (CheckBox) view.findViewById(R.id.checkDelete);
                        checkDelete.setChecked(false);
                        checkDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pairs.remove(position);
                                adapter.notifyDataSetChanged();
                                write(pairs.toString());
                            }
                        });
                        return view;
                    }
                };
                setListAdapter(adapter);
            }
        }
        );
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        pairs.remove(position);
        adapter.notifyDataSetChanged();
        write(pairs.toString());
        return false;
    }



    private void write(String content){
        try {
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){

                File sdCardDir = Environment.getExternalStorageDirectory();
                File targetFile = new File(sdCardDir.getCanonicalPath() + "/taskList.txt");

                FileOutputStream fout = new FileOutputStream(targetFile);
                byte[] bytes = content.getBytes();

                fout.write(bytes);
                fout.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String read()
    {
        String allText = "";
        try {
            File sdCardDir = Environment.getExternalStorageDirectory();
            File targetFile = new File(sdCardDir.getCanonicalPath() + "/taskList.txt");
            Scanner scan = new Scanner(targetFile);

            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                allText += line;

            }
            scan.close();
            } catch (IOException e) {
            e.printStackTrace();
        }
        return allText;

    }

}
