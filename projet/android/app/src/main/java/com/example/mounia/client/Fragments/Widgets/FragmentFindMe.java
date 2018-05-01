package com.example.mounia.client.Fragments.Widgets;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.mounia.client.CommunicationClientServer.CANEnums;
import com.example.mounia.client.CommunicationClientServer.CANMessage;
import com.example.mounia.client.CommunicationClientServer.Communication;
import com.example.mounia.client.Observateurs.Observateur;
import com.example.mounia.client.Utilitaires.Configurations;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by anthonyabboud on 18-03-22.
 */

// Source: https://gist.github.com/ybakos/4151696
public class FragmentFindMe extends FragmentWidget implements Observateur {

    @Override
    public void onStart() {

        super.onStart();

        for (FragmentWidget fragmentDyn : obtenirFragmentsEnfants()) {
            preparerConteneurFragmentEnfant();
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        for (int i = 0; i < obtenirFragmentsEnfants().size(); i++) {
            //transaction.add(FactoryFragmentWidget.getFragmentContainerId(this), enfant);
            transaction.add(fragmentContainerIDs.get(i), obtenirFragmentsEnfants().get(i));
        }
        transaction.commit();
    }

    // Preparer un conteneur pour chaque fragment dyn. de facon aussi dyn.
    public void preparerConteneurFragmentEnfant() {
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            linearLayout.setId(View.generateViewId());
            fragmentContainerIDs.add(linearLayout.getId());
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0);
        layoutParams.weight = 1;
        linearLayout.setLayoutParams(layoutParams);
        ViewGroup viewGroup = (ViewGroup) getView();
        viewGroup.addView(linearLayout);
    }

    @Override
    public void mettreAJour() {
        for (int i=0; i< Communication.data.size(); i++) {
            CANMessage msg = Communication.data.get(i);

            // Longitude
            if(msg.msgID == CANEnums.CANSid.GPS1_LONGITUDE){
                myRenderer.rocketLongitude = -(double) msg.data1;
            }
            // Latitude
            else if(msg.msgID == CANEnums.CANSid.GPS1_LATITUDE){
                myRenderer.rocketLatitude = (double) msg.data1;
            }
            // Altitude
            else if(msg.msgID == CANEnums.CANSid.GPS1_ALT_MSL){
                myRenderer.rocketAltitude = (double) msg.data1;
            }
        }
    }

    class MyRenderer implements GLSurfaceView.Renderer {

        private FragmentFindMe.Arrow arrow = new FragmentFindMe.Arrow();
        private float rotation;

        // Fixed client location (Spaceport America)
        //private Semaphore sem = new Semaphore(1);
        private final double clientLongitude = -106.9193209;
        private final double clientLatitude = 32.9401475;
        private final double clientAltitude = 152.0;

        // Just for testing while waiting for real data
        public double rocketLongitude = -106.91425323486328;
        public double rocketLatitude = 32.9432487487793;
        public double rocketAltitude = 1392.5999755859375;

        private final double radiusEarth = 6371.0;
        private double subY, subX;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            if(Configurations.themeOriginal) {
                gl.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
            }
            else {
                gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            }
            // Depth buffer setup.
            gl.glClearDepthf(1.0f);
            // Enables depth testing.
            gl.glEnable(GL10.GL_DEPTH_TEST);
            // The type of depth testing to do.
            gl.glDepthFunc(GL10.GL_LEQUAL);
            // Really nice perspective calculations.
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            // Sets the current view port to the new size.
            gl.glViewport(0, 0, width, height);
            // Select the projection matrix
            gl.glMatrixMode(GL10.GL_PROJECTION);
            // Reset the projection matrix
            gl.glLoadIdentity();
            // Calculate the aspect ratio of the window
            GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
            // Select the modelview matrix
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            // Reset the modelview matrix
            gl.glLoadIdentity();
        }

        @Override
        public void onDrawFrame(GL10 gl) {

            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            gl.glLoadIdentity();
            gl.glTranslatef(0.0f, 0.0f, -10.0f);
            float rot = findRocketDirection();
            float heightAngle = lookUp();

            gl.glPushMatrix();
            gl.glRotatef(rot, 0.0f, 0.0f, 1.0f);
            gl.glRotatef(heightAngle, 1.0f, 0.0f, 0.0f);
            arrow.draw(gl);
            gl.glPopMatrix();

            gl.glLoadIdentity();
        }

        public float lookUp(){
            float heightAngle = (float) (180 *
                    Math.tan(((rocketAltitude-clientAltitude)/1000)/
                            (Math.sqrt(Math.pow(subX, 2) + Math.pow(subY, 2)))) / Math.PI);
            return heightAngle;
        }

        public float findRocketDirection(){
            // Latitude et longitude en radians
            double radClientLatitude = clientLatitude * Math.PI / 180;
            double radRocketLatitude = rocketLatitude * Math.PI / 180;

            // Coordonnees cartesiennes du client
            double xClient = radiusEarth * Math.cos(radClientLatitude);
            double yClient = radiusEarth * radClientLatitude;

            // Coordonnees cartesiennes de la fusee
            double xRocket = radiusEarth * Math.cos(radRocketLatitude);
            double yRocket = radiusEarth * radRocketLatitude;

            // Difference
            subY = yRocket - yClient;
            subX = xRocket - xClient;

            // Calcul de l'angle en 2D
            float angle = (float)(180 * Math.atan(Math.abs((subY)/(subX))) / Math.PI);

            // Ajustement de l'angle dependamment de la direction desiree de la fleche
            if(rocketLatitude > clientLatitude && rocketLongitude > clientLongitude){
                angle -= 90.0f;
            }
            else if(rocketLatitude < clientLatitude && rocketLongitude < clientLongitude){
                angle += 90.0f;
            }
            else if(rocketLatitude < clientLatitude && rocketLongitude > clientLongitude){
                angle += 180.0f;
            }

            return angle;
        }

    }

    class Arrow {

        private FloatBuffer mVertexBuffer;
        private FloatBuffer mColorBuffer;
        private ByteBuffer  mIndexBuffer;

        private float vertices[] = {
                -1.0f, 0.0f, 1.0f, // Left Front
                1.0f, 0.0f, 1.0f, // Right Front
                -1.0f, 0.0f, -1.0f, // Left Back
                1.0f, 0.0f, -1.0f, // Right Back
                0.0f, 2.0f, 0.0f, // Top

                -0.25f, 0.0f, 0.25f, // Top Branch Left Front
                0.25f, 0.0f, 0.25f, // Top Branch Right Front
                -0.25f, 0.0f, -0.25f, // Top Branch Left Back
                0.25f, 0.0f, -0.25f, // Top Branch Right Back

                -0.25f, -2.0f, 0.25f, // Bottom Branch Left Front
                0.25f, -2.0f, 0.25f, // Bottom Branch Right Front
                -0.25f, -2.0f, -0.25f, //Bottom Branch Left Back
                0.25f, -2.0f, -0.25f // Bottom Branch Right Back
        };

        private float colors[] = {
                // Green on pyramid body
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,

                // Red at the tip
                1.0f, 0.0f, 0.0f, 1.0f,

                // Green on top branch
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,

                // Magenta on bottom branch
                1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f,
        };

        private byte indices[] = {
                // pyramid
                4, 1, 0,
                4, 2, 0,
                4, 2, 3,
                4, 1, 3,
                0, 2, 1,
                1, 2, 3,

                // Left branch face
                5, 9, 7,
                7, 9, 11,

                // Bottom branch face
                11, 9, 10,
                11, 12, 10,

                // Front branch face
                5, 9, 6,
                6, 10, 9,

                // Right branch face
                6, 10, 12,
                6, 8, 12,

                // Back branch face
                7, 11, 12,
                7, 8, 12

        };

        public Arrow() {
            ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
            byteBuf.order(ByteOrder.nativeOrder());
            mVertexBuffer = byteBuf.asFloatBuffer();
            mVertexBuffer.put(vertices);
            mVertexBuffer.position(0);

            byteBuf = ByteBuffer.allocateDirect(colors.length * 4);
            byteBuf.order(ByteOrder.nativeOrder());
            mColorBuffer = byteBuf.asFloatBuffer();
            mColorBuffer.put(colors);
            mColorBuffer.position(0);

            mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
            mIndexBuffer.put(indices);
            mIndexBuffer.position(0);
        }

        public void draw(GL10 gl) {
            gl.glFrontFace(GL10.GL_CW);

            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
            gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);

            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

            gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_BYTE,
                    mIndexBuffer);

            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        }
    }

    private GLSurfaceView mGLView;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public FragmentFindMe() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    MyRenderer myRenderer = new MyRenderer();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mGLView = new GLSurfaceView(getActivity());

        mGLView.setRenderer(myRenderer);
        return mGLView;
    }

    @Override
    public void onPause(){
        super.onPause();
        mGLView.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        mGLView.onResume();
    }

    public static  FragmentFindMe newInstance(int sectionNumber) {
        FragmentFindMe fragment = new FragmentFindMe();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
}






/*package com.example.mounia.client.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mounia.client.R;

import android.location.LocationManager;
import android.location.LocationListener;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.content.Context.LOCATION_SERVICE;

*//**
 * Created by mounianordine on 18-03-22.
 *//*

class Triangle {
    float color[] = {1.0f, 0.0f, 0.0f, 1.0f};

    private static final int BYTE_PER_FLOAT = 4;
    private FloatBuffer vertexBuffer;
    static final int COORDS_PER_VERTEX = 3;

    float triangleCoords[] = {
            0.0f, 0.5f, 0.0f,
            -0,5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };

    private String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    " gl_Position = vPosition;" +
                    "}";

    private String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    " gl_FragColor = vColor;" +
                    "}";

    private int mProgram;

    public Triangle(){
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * BYTE_PER_FLOAT);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);

        MyGLRenderer renderer = new MyGLRenderer();
        int vertexShader = renderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = renderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    private int mPositionHandle;
    private int mColorHandle;
    private int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private int vertexStride = COORDS_PER_VERTEX * 4;

    public void draw(){

        GLES20.glUseProgram(mProgram);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}


class MyGLSurfaceView extends GLSurfaceView {
    private final MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}

class MyGLRenderer implements GLSurfaceView.Renderer {
    private Triangle mTriangle;

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        mTriangle = new Triangle();
    }

    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        mTriangle.draw();
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    public int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}

public class FragmentFindMe extends Fragment {

    private GLSurfaceView mGLView;

    private static final String ARG_SECTION_NUMBER = "section_number";

    //private LocationManager locationManager;
    //private LocationListener locationListener;
    //private double clientLatitude;
    //private double clientLongitude;


    public FragmentFindMe() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        *//*locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                clientLatitude = location.getLatitude();
                clientLongitude = location.getLongitude();
                Log.i("FragmentFindMe", "\n " + clientLatitude + " " + clientLongitude);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            }, 10);
            return;
        } else {
            requestLocationUpdates();
        }*//*

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View layout = inflater.inflate(R.layout.layout_find_me, container, false);
        //Log.i("FragmentFindMe","Hello");
        mGLView = new MyGLSurfaceView(this.getActivity());
        return mGLView;
    }

    public static  FragmentFindMe newInstance(int sectionNumber) {
        FragmentFindMe fragment = new FragmentFindMe();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
*//*
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 10:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    requestLocationUpdates();
                }
        }
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
    }

}*/
