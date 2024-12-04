import java.io.IOException;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import GraphicsObjects.Arcball;
import GraphicsObjects.Utils;
import objects3D.*;

public class NascarWindow {
    private boolean MouseOnepressed = true;
    private boolean dragMode = false;
    private int OrthoNumber = 1200;
    private float zoomLevel = 1.0f;
    
    private Arcball MyArcball = new Arcball();
    private RaceTrack track;
    private Texture trackTexture;
    private Texture wallTexture;
    private Texture baseTexture;
    private Texture groundTexture;
    
    // 光照设置
    static float grey[] = { 0.5f, 0.5f, 0.5f, 1.0f };
    static float white[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    
    private Car[] cars;  // 多辆赛车
    private Texture carTexture;
    private float[] carAngles;  // 每辆赛车的角度
    private float[] carSpeeds;  // 每辆赛车的速度
    private static final int CAR_COUNT = 4;  // 赛道上的赛车数量
    private static final float TRACK_OUTER_RADIUS = 550.0f;  // 最外圈半径
    private static final float TRACK_INNER_RADIUS = 300.0f;  // 最内圈半径
    private static final float BANKING_ANGLE = (float)Math.PI/12;  // 赛道倾斜角度
    
    // 为每辆车设置固定的轨道半径和高度
    private static final float[] CAR_RADII = {
        320.0f,  // 第1辆车 - 最内圈
        370.0f,  // 第2辆车
        430.0f,  // 第3辆车
        490.0f   // 第4辆车 - 最外圈
    };
    
    private static final float[] CAR_HEIGHTS = {
        -62.0f,   // 第1辆车 - 最内圈（高度最高）
        -61.0f,  // 第2辆车
        -61.0f,  // 第3辆车
        -60.0f   // 第4辆车 - 最外圈（高度最低）
    };

    // 定义不同赛车的颜色
    private static final float[][] CAR_COLORS = {
        {1.0f, 0.0f, 0.0f, 1.0f},  // 第1辆车 - 红色
        {0.0f, 0.0f, 1.0f, 1.0f},  // 第2辆车 - 蓝色
        {0.0f, 1.0f, 0.0f, 1.0f},  // 第3辆车 - 绿色
        {1.0f, 1.0f, 0.0f, 1.0f}   // 第4辆车 - 黄色
    };

    private long lastFrameTime;

    // 添加光源数组
    private static final int LIGHT_COUNT = 4;
    private static final int[] LIGHTS = {
        GL_LIGHT1, GL_LIGHT2, GL_LIGHT3, GL_LIGHT4
    };

    public void start() throws IOException {
        try {
            Display.setDisplayMode(new DisplayMode(1200, 800));
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        initGL();

        lastFrameTime = System.currentTimeMillis();
        initCars();

        while (!Display.isCloseRequested()) {
            long currentTime = System.currentTimeMillis();
            float delta = (currentTime - lastFrameTime) / 1000.0f;
            lastFrameTime = currentTime;

            update(delta);
            renderGL();
            Display.update();
            Display.sync(60);
        }

        Display.destroy();
    }

    private void initGL() throws IOException {
        // 初始化OpenGL设置
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        changeOrth();
        MyArcball.startBall(0, 0, 1200, 800);
        glMatrixMode(GL_MODELVIEW);

        // 注释掉环境光设置
        /*
        FloatBuffer lightPos = BufferUtils.createFloatBuffer(4);
        lightPos.put(10000f).put(1000f).put(1000).put(0).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPos);
        glLight(GL_LIGHT0, GL_DIFFUSE, Utils.ConvertForGL(white));
        glEnable(GL_LIGHT0);
        */

        glEnable(GL_LIGHTING);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_NORMALIZE);
        glEnable(GL_COLOR_MATERIAL);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // 加载纹理
        trackTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/track.png"));
        baseTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/base2.png"));
        groundTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/ground.png"));

        // 初始化赛道
        track = new RaceTrack();

        // 设置聚光灯的颜色 - 增加亮度以补偿环境光的缺失
        float lightIntensity = 1.0f;  // 增加光照强度
        FloatBuffer lightColor = BufferUtils.createFloatBuffer(4);
        lightColor.put(lightIntensity).put(lightIntensity).put(lightIntensity).put(1.0f).flip();
        
        for (int i = 0; i < LIGHT_COUNT; i++) {
            glLight(LIGHTS[i], GL_DIFFUSE, lightColor);
            glLight(LIGHTS[i], GL_SPECULAR, lightColor);
            glEnable(LIGHTS[i]);
        }
    }

    private void initCars() throws IOException {
        // 初始化赛车数组
        cars = new Car[CAR_COUNT];
        carAngles = new float[CAR_COUNT];
        carSpeeds = new float[CAR_COUNT];

        // 加载赛车纹理
        carTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/car_texture.png"));

        // 创建赛车并设置初始位置和速度
        for (int i = 0; i < CAR_COUNT; i++) {
            cars[i] = new Car(CAR_COLORS[i]);  // 传入对应的颜色
            carAngles[i] = (float)(i * (2.0f * Math.PI / CAR_COUNT));
            carSpeeds[i] = 1.0f + (float)(Math.random() * 0.5f);
        }
    }

    private void update(float delta) {
        // 更新每辆赛车的位置
        for (int i = 0; i < CAR_COUNT; i++) {
            carAngles[i] += carSpeeds[i] * delta;
            if (carAngles[i] > 2 * Math.PI) {
                carAngles[i] -= 2 * Math.PI;
            }
        }

        // 处理鼠标输入以更新视角
        int MouseX = Mouse.getX();
        int MouseY = Mouse.getY();
        boolean MouseButtonPressed = Mouse.isButtonDown(0);

        if (MouseButtonPressed && !MouseOnepressed) {
            MouseOnepressed = true;
            MyArcball.startBall(1200 - MouseX, 800 - MouseY, 1200, 800);
            dragMode = true;
        } else if (!MouseButtonPressed) {
            MouseOnepressed = false;
            dragMode = false;
        }

        if (dragMode) {
            MyArcball.updateBall(1200 - MouseX, 800 - MouseY, 1200, 800);
        }

        // 处理鼠标滚轮缩放
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            OrthoNumber += 20; // 缩小视角
        } else if (dWheel > 0) {
            OrthoNumber -= 20; // 放大视角
        }

        // 处理键盘输入
        if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
            MyArcball.reset();
        }
    }

    private void changeOrth() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(1200 - OrthoNumber, OrthoNumber, (800 - (OrthoNumber * 0.66f)),
                (OrthoNumber * 0.66f), 100000, -100000);
        glMatrixMode(GL_MODELVIEW);

        FloatBuffer CurrentMatrix = BufferUtils.createFloatBuffer(16);
        glGetFloat(GL_MODELVIEW_MATRIX, CurrentMatrix);
        MyArcball.getMatrix(CurrentMatrix);
        glLoadMatrix(CurrentMatrix);
    }

    private void renderGL() {
        changeOrth();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(0.5f, 0.7f, 1.0f, 1.0f);

        glPushMatrix();
        {
            // 调整场景位置和视角
            glTranslatef(600, 400, 0);
            glRotatef(-90, 1.0f, 0.0f, 0.0f);
            glScalef(zoomLevel, zoomLevel, zoomLevel);

            // 更新光源位置
            float postRadius = TRACK_OUTER_RADIUS * 1.2f;
            float postHeight = 400.0f;
            
            for (int i = 0; i < LIGHT_COUNT; i++) {
                float angle = (float)(i * Math.PI / 2);
                float x = (float)(postRadius * Math.cos(angle));
                float y = (float)(postRadius * Math.sin(angle));
                
                // 设置光源位置和方向
                FloatBuffer lightPos = BufferUtils.createFloatBuffer(4);
                lightPos.put(x).put(y).put(postHeight).put(1.0f).flip();
                
                FloatBuffer spotDir = BufferUtils.createFloatBuffer(4);
                spotDir.put(-x).put(-y).put(-postHeight).put(0.0f).flip();
                
                glLight(LIGHTS[i], GL_POSITION, lightPos);
                glLight(LIGHTS[i], GL_SPOT_DIRECTION, spotDir);
                
                // 使用 FloatBuffer 设置聚光灯角度和衰减
                FloatBuffer spotCutoff = BufferUtils.createFloatBuffer(4);
                spotCutoff.put(45.0f).put(0.0f).put(0.0f).put(0.0f).flip();
                glLight(LIGHTS[i], GL_SPOT_CUTOFF, spotCutoff);
                
                FloatBuffer spotExponent = BufferUtils.createFloatBuffer(4);
                spotExponent.put(2.0f).put(0.0f).put(0.0f).put(0.0f).flip();  // 降低衰减指数
                glLight(LIGHTS[i], GL_SPOT_EXPONENT, spotExponent);
            }
            
            // 绘制赛道和大灯 - 不传递墙面纹理
            track.drawTrack(TRACK_INNER_RADIUS, TRACK_OUTER_RADIUS, 0.0f, BANKING_ANGLE, 60,
                          trackTexture, null, baseTexture, groundTexture);  // 将wallTexture替换为null
            track.drawLightPosts(TRACK_OUTER_RADIUS, -60.0f, postHeight);  // 添加postHeight参数

            // 绘制所有赛车
            for (int i = 0; i < CAR_COUNT; i++) {
                glPushMatrix();
                {
                    // 使用预设的轨道半径和高度
                    float[] carPos = cars[i].getPositionOnTrack(CAR_RADII[i], carAngles[i], BANKING_ANGLE);

                    // 1. 先移动到赛道上的位置，使用预设高度
                    glTranslatef(carPos[0], carPos[1], carPos[2] + CAR_HEIGHTS[i]);

                    // 2. 向赛道内侧倾斜
                    float tiltDirection = (float)Math.toDegrees(carAngles[i]) + 90;
                    glRotatef(tiltDirection, 0.0f, 0.0f, 1.0f);
                    glRotatef((float) -Math.toDegrees(BANKING_ANGLE), 1.0f, 0.0f, 0.0f);
                    glRotatef(-tiltDirection, 0.0f, 0.0f, 1.0f);

                    // 3. 最后设置车身朝向
                    glRotatef(carPos[3], 0.0f, 0.0f, 1.0f);

                    // 绘制赛车
                    cars[i].drawCar(carTexture, 20.0f);
                }
                glPopMatrix();
            }
        }
        glPopMatrix();
    }

    public static void main(String[] argv) throws IOException {
        NascarWindow nascar = new NascarWindow();
        nascar.start();
    }
} 