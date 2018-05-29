package android.firstopneglproject.com.particlesline;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.firstopneglproject.com.particlesline.objects.ParticleFireworksExplosion;
import android.graphics.Color;
import android.opengl.GLSurfaceView.Renderer;

import android.firstopneglproject.com.particlesline.objects.ParticleShooter;
import android.firstopneglproject.com.particlesline.objects.ParticleSystem;
import android.firstopneglproject.com.particlesline.programs.ParticleShaderProgram;
import android.firstopneglproject.com.particlesline.util.Geometry.Point;
import android.firstopneglproject.com.particlesline.util.Geometry.Vector;
import android.firstopneglproject.com.particlesline.util.MatrixHelper;
import android.firstopneglproject.com.particlesline.util.TextureHelper;

import java.util.Random;

public class ParticlesRenderer implements Renderer {
    private final Context context;

    private final float[] projectionMatrix = new float[16];    
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    
    // Maximum saturation and value.
    private final float[] hsv = {0f, 1f, 1f};
    
    private ParticleShaderProgram particleProgram;      
    private ParticleSystem particleSystem;
    private ParticleFireworksExplosion particleFireworksExplosion;
    private Random random;
    private long globalStartTime;
    private int texture;

    public ParticlesRenderer(Context context) {
        this.context = context;
    }
    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        // Enable additive blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
        
        particleProgram = new ParticleShaderProgram(context);        
        particleSystem = new ParticleSystem(10000);        
        globalStartTime = System.nanoTime();
        
        particleFireworksExplosion = new ParticleFireworksExplosion();
        
        random = new Random();
        
        texture = TextureHelper.loadTexture(context, R.drawable.particle_texture);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {                
        glViewport(0, 0, width, height);        

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width
            / (float) height, 1f, 10f);
        
        setIdentityM(viewMatrix, 0);
        translateM(viewMatrix, 0, 0f, -1.5f, -5f);   
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0,
            viewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {        
        glClear(GL_COLOR_BUFFER_BIT);
        
        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;
        
        if (random.nextFloat() < 1f) {
            hsv[0] = random.nextInt(360);
        
            particleFireworksExplosion.addPartical(
                    particleSystem,
                    new Point(
                            -1f + random.nextFloat() * 3f,
                            0f + random.nextFloat() * 4f,
                            -1f + random.nextFloat() * 3f),
                    Color.HSVToColor(hsv),
                    globalStartTime);
        }
        
        
        particleProgram.useProgram();
        
        particleProgram.setUniforms(viewProjectionMatrix, currentTime, texture);
        particleSystem.bindData(particleProgram);
        particleSystem.draw(); 
    }
}