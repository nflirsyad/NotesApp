package com.nflirsyad.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_STORAGE=100;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this,InsertAndViewActivity.class);
                Map<String, Object> data = (Map<String, Object>)adapterView.getAdapter().getItem(i);
                intent.putExtra("filename",data.get("name").toString());
                Toast.makeText(MainActivity.this, "You clicked" + data.get("name"), Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, Object> data = (Map<String, Object>)adapterView.getAdapter().getItem(i);

                tampilkanDialogKonfirmasiHapusCatatan(data.get("name").toString());
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT>=23){
            if (periksaIzinPenyimpanan()){
                ambilListFilePadaFolder();
            }
        }else{
            ambilListFilePadaFolder();
        }
    }

    public boolean periksaIzinPenyimpanan(){
        if (Build.VERSION.SDK_INT>=23){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED){
                return true;
            }else {
                ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE);
                return false;
                }
            }else{
                return false;
            }
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE_STORAGE:
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    ambilListFilePadaFolder();
                }
                break;
        }
    }

    private void ambilListFilePadaFolder(){
        String path = Environment.getExternalStorageDirectory().toString()+"/project1";
        File directory = new File(path);
        if (directory.exists()){
            File[] files = directory.listFiles();
            String[] filenames =new String[files.length];
            String[] dateCreated = new String[files.length];
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
            ArrayList<Map<String,String>> itemDataList = new ArrayList<>();
            for (int i = 0; i<files.length;i++){
                filenames[i] = files[i].getName();
                Date lastModDate = new Date(files[i].lastModified());
                dateCreated[i] = simpleDateFormat.format(lastModDate);
                Map<String,String> itemMap = new HashMap<>();
                itemMap.put("name",filenames[i]);
                itemMap.put("date",dateCreated[i]);
                itemDataList.add(itemMap);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, itemDataList,
                    android.R.layout.simple_list_item_2,
                    new String[]{"name","date"},new int[]{android.R.id.text1,android.R.id.text1});
            listView.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_tambah:
                Intent intent = new Intent(this,InsertAndViewActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void tampilkanDialogKonfirmasiHapusCatatan(final String filename){
        new AlertDialog.Builder(this)
                .setTitle("Hapus Catatan ini ?")
                .setMessage("Apakah anda yakin ingin menghapus catatan" + filename +"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        hapusFile(filename);
                    }
                })
                .setNegativeButton(android.R.string.no,null).show();
    }

    void hapusFile(String filename){
        String path = Environment.getExternalStorageDirectory().toString() + "/project1";
        File file = new File(path,filename);
        if (file.exists()){
            file.delete();
        }
        ambilListFilePadaFolder();
    }

}