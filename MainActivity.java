package com.example.cis700;

import android.media.MediaRecorder;


import java.io.IOException;

import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;
import android.os.Handler;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;

import android.content.pm.PackageManager;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static final int RequestPermissionCode = 1;

    private static final String TAG = "MainActivity";
    private static String fileName = null;

    private SensorManager sensorManager;
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private TextView mTextViewResult;
    private RequestQueue mQueue;
    private String hora;
    Sensor acclereometer,mGyro,mMagno,mTemp,mPressure,mHumid,mlight;
    TextView xval,yval,zval,gxval,gyval,gzval,mxval,myval,mzval,temp,pressure,light,humi;
    EditText subId;
    Button save,start;
    boolean pressed_flag;
    MediaRecorder mediaRecorder;
    JsonObjectRequest request;


    private String name;

    StringBuffer Api_Time = new StringBuffer();

    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xval=(TextView)findViewById(R.id.x_val);
        yval=(TextView)findViewById(R.id.y_val);
        zval=(TextView)findViewById(R.id.z_val);
        gxval=(TextView)findViewById(R.id.gx_val);
        gyval=(TextView)findViewById(R.id.gy_val);
        gzval=(TextView)findViewById(R.id.gz_val);
        mxval=(TextView)findViewById(R.id.mx_val);
        myval=(TextView)findViewById(R.id.my_val);
        mzval=(TextView)findViewById(R.id.mz_val);
        temp=(TextView)findViewById(R.id.t_val);
        pressure=(TextView)findViewById(R.id.p_val);
        light=(TextView)findViewById(R.id.l_val);
        humi=(TextView)findViewById(R.id.h_val);
        subId=(EditText)findViewById(R.id.subID);
        save=(Button)findViewById(R.id.savebtn);
        start=(Button)findViewById(R.id.strbtn);
        mediaRecorder = new MediaRecorder();


        // TIME

        mQueue = Volley.newRequestQueue(this);


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PackageManager.PERMISSION_GRANTED);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressed_flag = false;
                Toast.makeText(getBaseContext(), "Data saved", Toast.LENGTH_LONG).show();

               mediaRecorder.stop();
             mediaRecorder.release();
               mediaRecorder = null;
                write_csv(Api_Time,subId.getText().toString());


            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressed_flag=true;
                Toast.makeText(getBaseContext(), "Data Recording", Toast.LENGTH_LONG).show();
                jsonParse();


                name = subId.getText().toString();
               fileName = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
                fileName += "/Audio_"+name+".3gp";
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setOutputFile(fileName);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

                try {
                    mediaRecorder.prepare();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }

               mediaRecorder.start();


            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acclereometer =sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGyro =sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mMagno =sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mTemp =sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mlight =sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mPressure =sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mHumid =sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);


       // mQueue = Volley.newRequestQueue(this);



        if(acclereometer!=null){
            Log.d(TAG, "onCreate: init sensor services");
            sensorManager.registerListener(MainActivity.this,acclereometer,(SensorManager.SENSOR_DELAY_NORMAL));
            int test = SensorManager.SENSOR_DELAY_NORMAL;
            Log.d(TAG, "onCreate: registered sensor services");
        }
        else{
            xval.setText("sensor not available");
            yval.setText("sensor not available");
            zval.setText("sensor not available");

        }
        if(mMagno!=null){
            Log.d(TAG, "onCreate: init sensor services");
            sensorManager.registerListener(MainActivity.this,mMagno,SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: registered sensor services");
        }
        else{
            mxval.setText("sensor not available");
            myval.setText("sensor not available");
            mzval.setText("sensor not available");

        }
        if(mGyro!=null){
            Log.d(TAG, "onCreate: init sensor services");
            sensorManager.registerListener(MainActivity.this,mGyro,SensorManager.SENSOR_DELAY_NORMAL);
            int numerito = SensorManager.SENSOR_DELAY_NORMAL;
            Log.d(TAG, "onCreate: registered sensor services");
        }
        else{
            gxval.setText("sensor not available");
            gyval.setText("sensor not available");
            gzval.setText("sensor not available");

        }
        if(mHumid!=null){
            Log.d(TAG, "onCreate: init sensor services");
            sensorManager.registerListener(MainActivity.this,mHumid,SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: registered sensor services");
        }
        else{
            humi.setText("sensor not available");

        }
        if(mlight!=null){
            Log.d(TAG, "onCreate: init sensor services");
            sensorManager.registerListener(MainActivity.this,mlight,SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: registered sensor services");
        }
        else{
            light.setText("sensor not available");

        }
        if(mPressure!=null){
            Log.d(TAG, "onCreate: init sensor services");
            sensorManager.registerListener(MainActivity.this,mPressure,SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: registered sensor services");
        }
        else{
            pressure.setText("sensor not available");

        }
        if(mTemp!=null){
            Log.d(TAG, "onCreate: init sensor services");
            sensorManager.registerListener(MainActivity.this,mTemp,SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: registered sensor services");
        }
        else{
            temp.setText("sensor not available");

        }


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor= event.sensor;


        if(sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION){
            //Log.d(TAG, "AccelerometerSensorChanged: X:"+event.values[0]+" Y:"+event.values[1]+" Z:"+event.values[2]);
            xval.setText(""+event.values[0]);
            yval.setText(""+event.values[1]);
            zval.setText(""+event.values[2]);

            if(pressed_flag){
                Log.d(TAG, "AccelerometerSensorChanged: X:"+event.values[0]+" Y:"+event.values[1]+" Z:"+event.values[2]);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                String format = simpleDateFormat.format(new Date());
                setSomeVariable(subId.getText().toString());
                String entry = format+","+xval.getText().toString() + "," + yval.getText().toString() + "," + zval.getText().toString() +"\n";
                if(subId.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Enter Subject ID", Toast.LENGTH_SHORT).show();
                }
                else{

                    try {

                        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
                        String fileName = "Accelerometer_"+subId.getText().toString()+".csv";
                        String filePath = baseDir + File.separator + fileName;


                        File file = new File(filePath);
                        FileOutputStream f = new FileOutputStream(file, true);


                        try {
                            f.write(entry.getBytes());
                            f.flush();
                            f.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                }
            }

        }
        if(sensor.getType()==Sensor.TYPE_GYROSCOPE){

           Log.d(TAG, "GyroSensorChanged: X:"+event.values[0]+" Y:"+event.values[1]+" Z:"+event.values[2]);
            gxval.setText(""+event.values[0]);
            gyval.setText(""+event.values[1]);
            gzval.setText(""+event.values[2]);

            if(pressed_flag){



                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                String format = simpleDateFormat.format(new Date());
                String entry = format+","+gxval.getText().toString() + "," + gyval.getText().toString() + "," + gzval.getText().toString() + "\n";
                if(subId.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Enter Subject ID", Toast.LENGTH_SHORT).show();
                }
                else{

                    try {


                        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
                        String fileName = "Gyro_"+subId.getText().toString()+".csv";
                        String filePath = baseDir + File.separator + fileName;

                        File file = new File(filePath);
                        FileOutputStream f = new FileOutputStream(file, true);


                        try {

                            f.write(entry.getBytes());
                            f.flush();
                            f.close();
                            //Toast.makeText(getBaseContext(), "Data saved", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                }
            }

        }
        if(sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){

            Log.d(TAG, "MagnetoSensorChanged: X:"+event.values[0]+" Y:"+event.values[1]+" Z:"+event.values[2]);
            mxval.setText(""+event.values[0]);
            myval.setText(""+event.values[1]);
            mzval.setText(""+event.values[2]);

            if(pressed_flag){

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                String format = simpleDateFormat.format(new Date());
                String entry = format+","+mxval.getText().toString() + "," + myval.getText().toString() + "," + mzval.getText().toString() + "\n";
                if(subId.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Enter Subject ID", Toast.LENGTH_SHORT).show();
                }
                else{

                    try {


                        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
                        String fileName = "Magneto_"+subId.getText().toString()+".csv";
                        String filePath = baseDir + File.separator + fileName;

                        File file = new File(filePath);
                        FileOutputStream f = new FileOutputStream(file, true);


                        try {

                            f.write(entry.getBytes());
                            f.flush();
                            f.close();
                            //Toast.makeText(getBaseContext(), "Data saved", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                }
            }

        }

        if(sensor.getType()==Sensor.TYPE_AMBIENT_TEMPERATURE){
            temp.setText(""+event.values[0]);
        }
        if(sensor.getType()==Sensor.TYPE_LIGHT){
            light.setText(""+event.values[0]);
        }
        if(sensor.getType()==Sensor.TYPE_RELATIVE_HUMIDITY){
            humi.setText(""+event.values[0]);
        }
        if(sensor.getType()==Sensor.TYPE_PRESSURE){
            pressure.setText(""+event.values[0]);

        }

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }


   /* @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }*/

    private void jsonParse() {
        String url = "https://worldtimeapi.org/api/timezone/America/New_York";
        request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override

                    public void onResponse(JSONObject response) {
                        try {
                             hora = response.getString("datetime");
                            Api_Time.append(hora +"\n");


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void setSomeVariable(String someVariable) {
        this.name = someVariable;
    }

    public static void write_csv(StringBuffer values, String nombre)  {

        StringBuffer entry = values;
        String Prueba = entry.toString();
        try{

            String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
                String fileName = "APIs_Time"+nombre+".csv";
                String filePath = baseDir + File.separator + fileName;


                File file = new File(filePath);
                FileOutputStream f = new FileOutputStream(file, true);

                f.write(entry.toString().getBytes());
                f.flush();
                f.close();
        }catch(Exception e){System.out.println(e);}






    }
}


