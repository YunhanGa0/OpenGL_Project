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
    
    // 光照设置
    static float grey[] = { 0.5f, 0.5f, 0.5f, 1.0f };
    static float white[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    
    private Car[] cars;  // 多辆赛车
    private Texture carTexture;
    private float[] carAngles;  // 每辆赛车的角度
    private float[] carSpeeds;  // 每辆赛车的速度
    private static final int CAR_COUNT = 4;  // 赛道上的赛车数量
    private static final float TRACK_RADIUS = 250.0f;  // 赛道半径
    private long lastFrameTime;
    private static final float BANKING_ANGLE = (float)Math.PI/24;  // 赛道倾斜角度
    private static final float CAR_HEIGHT = -10.0f;  // 调整这个值来改变赛车高度

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

        // 设置光照
        FloatBuffer lightPos = BufferUtils.createFloatBuffer(4);
        lightPos.put(10000f).put(1000f).put(1000).put(0).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPos);
        glLight(GL_LIGHT0, GL_DIFFUSE, Utils.ConvertForGL(white));
        glEnable(GL_LIGHT0);

        glEnable(GL_LIGHTING);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_NORMALIZE);
        glEnable(GL_COLOR_MATERIAL);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // 加载纹理
        trackTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/track.png"));
        wallTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/wall.png"));
        baseTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/base.png"));

        // 初始化赛道
        track = new RaceTrack();
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
            cars[i] = new Car();
            carAngles[i] = (float)(i * (2.0f * Math.PI / CAR_COUNT));  // 均匀分布在赛道上
            carSpeeds[i] = 1.0f + (float)(Math.random() * 0.5f);  // 随机速度变化
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

            // 绘制赛道
            track.drawTrack(200.0f, 300.0f, 0.0f, BANKING_ANGLE, 60,
                          trackTexture, wallTexture, baseTexture);

            // 绘制所有赛车
            for (int i = 0; i < CAR_COUNT; i++) {
                glPushMatrix();
                {
                    float[] carPos = cars[i].getPositionOnTrack(TRACK_RADIUS, carAngles[i], BANKING_ANGLE);

                    // 1. 先移动到赛道上的位置
                    glTranslatef(carPos[0], carPos[1], carPos[2] + CAR_HEIGHT);

                    // 2. 向赛道内侧倾斜
                    // 根据车辆在赛道上的位置计算倾斜方向
                    float tiltDirection = (float)Math.toDegrees(carAngles[i]) + 90;
                    glRotatef(tiltDirection, 0.0f, 0.0f, 1.0f);  // 先旋转到正确的方向
                    glRotatef((float) -Math.toDegrees(BANKING_ANGLE), 1.0f, 0.0f, 0.0f);  // 使用赛道倾斜角度，负号使其向内倾斜
                    glRotatef(-tiltDirection, 0.0f, 0.0f, 1.0f); // 恢复原来的方向

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