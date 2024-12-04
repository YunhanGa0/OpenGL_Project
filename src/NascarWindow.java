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
    private static final float TRACK_OUTER_RADIUS = 700.0f;  // 最外圈半径
    private static final float TRACK_INNER_RADIUS = 450.0f;  // 最内圈半径
    private static final float BANKING_ANGLE = (float)Math.PI/12;  // 赛道倾斜角度
    
    // 为每辆车设置固定的轨道半径和高度
    private static final float[] CAR_RADII = {
        320.0f + 150.0f,  // 第1辆车 - 最内圈
        370.0f + 150.0f,  // 第2辆车
        430.0f + 150.0f,  // 第3辆车
        490.0f + 150.0f   // 第4辆车 - 最外圈
    };
    
    private static final float[] CAR_HEIGHTS = {
        -102.0f,   // 第1辆车 - 最内圈（高度最高）
        -101.0f,  // 第2辆车
        -101.0f,  // 第3辆车
        -100.0f   // 第4辆车 - 最外圈（高度最低）
    };

    // 定义不同赛车的颜色
    private static final float[][] CAR_COLORS = {
        {1.0f, 0.0f, 0.0f, 1.0f},  // 第1辆车 - 红色
        {0.0f, 0.0f, 1.0f, 1.0f},  // 第2辆车 - 蓝色
        {0.0f, 1.0f, 0.0f, 1.0f},  // 第3辆车 - 绿色
        {1.0f, 1.0f, 0.0f, 1.0f}   // 第4辆车 - 黄色
    };

    // 在类的开头添加阴影高度数组，与CAR_HEIGHTS对应
    private static final float[] SHADOW_HEIGHTS = {
        50.0f,   // 第1辆车 - 最内圈
        32.0f,   // 第2辆车
        40.0f,   // 第3辆车
        50.0f    // 第4辆车 - 最外圈
    };

    private long lastFrameTime;

    // 添加光源数组
    private static final int LIGHT_COUNT = 4;
    private static final int[] LIGHTS = {
        GL_LIGHT1, GL_LIGHT2, GL_LIGHT3, GL_LIGHT4
    };

    private static final float[] SHADOW_COLOR = {0.1f, 0.1f, 0.1f, 0.3f};  // 更深的灰色，增加不透明度

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

        // 修改初始光照设置
        float lightIntensity = 2.0f;  // 增加默认光照强度
        FloatBuffer lightColor = BufferUtils.createFloatBuffer(4);
        lightColor.put(lightIntensity).put(lightIntensity).put(0.9f*lightIntensity).put(1.0f).flip();
        
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
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        glPushMatrix();
        {
            glTranslatef(600, 400, 0);
            glRotatef(-90, 1.0f, 0.0f, 0.0f);
            glScalef(zoomLevel, zoomLevel, zoomLevel);

            // 调整环境光，不要完全黑暗
            FloatBuffer ambientLight = BufferUtils.createFloatBuffer(4);
            ambientLight.put(new float[] {0.2f, 0.2f, 0.2f, 1.0f}).flip();  // 增加环境光
            glLightModel(GL_LIGHT_MODEL_AMBIENT, ambientLight);

            // 更新光源位置和属性
            float postRadius = TRACK_OUTER_RADIUS * 1.2f;
            float postHeight = 400.0f;
            
            for (int i = 0; i < LIGHT_COUNT; i++) {
                float angle = (float)(i * Math.PI / 2);
                float x = (float)(postRadius * Math.cos(angle));
                float y = (float)(postRadius * Math.sin(angle));
                
                // 设置光源位置
                FloatBuffer lightPos = BufferUtils.createFloatBuffer(4);
                lightPos.put(new float[] {x, y, postHeight, 1.0f}).flip();
                glLight(LIGHTS[i], GL_POSITION, lightPos);
                
                // 设置光源方向
                FloatBuffer spotDir = BufferUtils.createFloatBuffer(4);
                spotDir.put(new float[] {-x, -y, -postHeight, 0.0f}).flip();
                glLight(LIGHTS[i], GL_SPOT_DIRECTION, spotDir);
                
                // 增强光源颜色强度
                FloatBuffer lightDiffuse = BufferUtils.createFloatBuffer(4);
                lightDiffuse.put(new float[] {3.0f, 3.0f, 2.7f, 1.0f}).flip();  // 进一步增强漫反射
                glLight(LIGHTS[i], GL_DIFFUSE, lightDiffuse);
                
                FloatBuffer lightSpecular = BufferUtils.createFloatBuffer(4);
                lightSpecular.put(new float[] {3.0f, 3.0f, 2.7f, 1.0f}).flip();  // 进一步增强镜面反射
                glLight(LIGHTS[i], GL_SPECULAR, lightSpecular);
                
                // 调整聚光灯参数
                FloatBuffer spotCutoff = BufferUtils.createFloatBuffer(4);
                spotCutoff.put(new float[] {75.0f, 0.0f, 0.0f, 0.0f}).flip();  // 增大光照角度
                glLight(LIGHTS[i], GL_SPOT_CUTOFF, spotCutoff);
                
                // 调整衰减因子
                FloatBuffer constant = BufferUtils.createFloatBuffer(4);
                constant.put(new float[] {1.0f, 0.0f, 0.0f, 0.0f}).flip();
                glLight(LIGHTS[i], GL_CONSTANT_ATTENUATION, constant);
                
                FloatBuffer linear = BufferUtils.createFloatBuffer(4);
                linear.put(new float[] {0.0003f, 0.0f, 0.0f, 0.0f}).flip();  // 进一步减小线性衰减
                glLight(LIGHTS[i], GL_LINEAR_ATTENUATION, linear);
                
                FloatBuffer quadratic = BufferUtils.createFloatBuffer(4);
                quadratic.put(new float[] {0.000001f, 0.0f, 0.0f, 0.0f}).flip();  // 进一步减小二次衰减
                glLight(LIGHTS[i], GL_QUADRATIC_ATTENUATION, quadratic);
            }
            
            // 绘制赛道和大灯 - 不传递墙面纹理
            track.drawTrack(TRACK_INNER_RADIUS, TRACK_OUTER_RADIUS, 0.0f, BANKING_ANGLE, 60,
                          trackTexture, null, baseTexture, groundTexture);  // 将wallTexture替换为null
            track.drawLightPosts(TRACK_OUTER_RADIUS, -60.0f, postHeight);  // 添加postHeight参数

            // 绘制所有赛车
            for (int i = 0; i < CAR_COUNT; i++) {
                float[] carPos = cars[i].getPositionOnTrack(CAR_RADII[i], carAngles[i], BANKING_ANGLE);
                
                // 传入车辆索引i
                drawCarShadow(carPos, CAR_RADII[i], carAngles[i], 20.0f, i);
                
                // 然后绘制赛车
                glPushMatrix();
                {
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

    private void drawCarShadow(float[] carPos, float radius, float angle, float scale, int carIndex) {
        // 计算赛车在平面上的距离（相对于赛道中心）
        float distanceFromCenter = (float)Math.sqrt(carPos[0] * carPos[0] + carPos[1] * carPos[1]);
        
        // 只在赛道范围内绘制阴影
        if (distanceFromCenter < TRACK_OUTER_RADIUS && distanceFromCenter > TRACK_INNER_RADIUS) {
            glPushMatrix();
            {
                // 禁用深度写入，但保持深度测试
                glDepthMask(false);
                
                // 启用混合以实现半透明效果
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                
                // 禁用光照以确保阴影颜色正确
                glDisable(GL_LIGHTING);
                
                // 设置阴影颜色
                glColor4f(SHADOW_COLOR[0], SHADOW_COLOR[1], SHADOW_COLOR[2], SHADOW_COLOR[3]);
                
                // 使用对应车辆的阴影高度
                glTranslatef(carPos[0], carPos[1], SHADOW_HEIGHTS[carIndex]);
                
                // 根据赛道倾斜角度旋转阴影
                float tiltDirection = (float)Math.toDegrees(angle) + 90;
                glRotatef(tiltDirection, 0.0f, 0.0f, 1.0f);
                glRotatef((float) -Math.toDegrees(BANKING_ANGLE), 1.0f, 0.0f, 0.0f);
                glRotatef(-tiltDirection, 0.0f, 0.0f, 1.0f);
                
                // 压扁并放大阴影
                glScalef(scale * 1.2f, scale * 0.8f, 1.0f);
                
                // 绘制简单的椭圆形阴影
                glBegin(GL_TRIANGLE_FAN);
                {
                    glVertex3f(0.0f, 0.0f, 0.0f);  // 中心点
                    int segments = 32;
                    for (int i = 0; i <= segments; i++) {
                        float theta = (float)(i * 2.0f * Math.PI / segments);
                        float x = (float)Math.cos(theta) * 2.0f;  // 调整这个系数来改变阴影长度
                        float y = (float)Math.sin(theta);         // 调整这个系数来改变阴影宽度
                        glVertex3f(x, y, 0.0f);
                    }
                }
                glEnd();
                
                // 恢复状态
                glEnable(GL_LIGHTING);
                glDisable(GL_BLEND);
                glDepthMask(true);
            }
            glPopMatrix();
        }
    }

    public static void main(String[] argv) throws IOException {
        NascarWindow nascar = new NascarWindow();
        nascar.start();
    }
} 