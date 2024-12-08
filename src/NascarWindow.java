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
    private Texture centerTexture;  // 中心区域的纹理
    
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
        31.0f,   // 第1辆车 - 最内圈
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

    private static final int CARS_PER_TRACK = 5;  // 每个赛道上的赛车数量

    private Car[][] trackCars;  // 每个赛道上的赛车
    private float[][] trackCarAngles;  // 每个赛道上赛车的角度
    private float[][] trackCarSpeeds;  // 每个赛道上赛车的速度

    // 在NascarWindow类的开头添加一些预定义的颜色主题
    private static final RaceTrack.PitStopColors FERRARI_THEME = new RaceTrack.PitStopColors(
        new float[]{0.9f, 0.1f, 0.1f, 1.0f},  // 法拉利红主建筑
        new float[]{0.7f, 0.05f, 0.05f, 1.0f}, // 深红色屋顶
        new float[]{0.2f, 0.2f, 0.2f, 1.0f},   // 深灰色维修区
        new float[]{0.1f, 0.1f, 0.1f, 1.0f},   // 黑色设备
        new float[]{1.0f, 0.1f, 0.1f, 1.0f},   // 亮红色标志牌
        new float[]{0.2f, 0.2f, 0.2f, 0.6f}    // 半透明灰色窗户
    );

    private static final RaceTrack.PitStopColors MERCEDES_THEME = new RaceTrack.PitStopColors(
        new float[]{0.2f, 0.2f, 0.2f, 1.0f},  // 奔驰银灰色主建筑
        new float[]{0.15f, 0.15f, 0.15f, 1.0f}, // 深灰色屋顶
        new float[]{0.3f, 0.3f, 0.3f, 1.0f},   // 浅灰色维修区
        new float[]{0.1f, 0.1f, 0.1f, 1.0f},   // 黑色设备
        new float[]{0.0f, 0.8f, 0.0f, 1.0f},   // 梅赛德斯绿色标志牌
        new float[]{0.2f, 0.2f, 0.2f, 0.6f}    // 半透明灰色窗户
    );

    private static final RaceTrack.PitStopColors REDBULL_THEME = new RaceTrack.PitStopColors(
        new float[]{0.0f, 0.0f, 0.4f, 1.0f},  // 深蓝色主建筑
        new float[]{0.0f, 0.0f, 0.3f, 1.0f},  // 更深的蓝色屋顶
        new float[]{0.2f, 0.2f, 0.2f, 1.0f},  // 深灰色维修区
        new float[]{0.1f, 0.1f, 0.1f, 1.0f},  // 黑色设备
        new float[]{0.8f, 0.0f, 0.0f, 1.0f},  // 红牛红色标志牌
        new float[]{0.2f, 0.2f, 0.8f, 0.6f}   // 半透明蓝色窗户
    );

    // 在类中添加一个变量来跟踪当前主题
    private RaceTrack.PitStopColors currentPitStopTheme = FERRARI_THEME;

    // 在类开头添加摄像头相关变量
    private boolean isFollowCamera = false;  // 是否启用跟随视角
    private boolean isOrbitCamera = false;    // 环绕视角（V键）
    private boolean waitForKeyreleaseC = true; // C键防重复
    private boolean waitForKeyreleaseV = true; // V键防重复
    private static final float CAMERA_HEIGHT = 15.0f;       // 摄像头高度（相对于车顶）
    private static final float CAMERA_FORWARD = 0.0f;       // 位于车头正中
    private static final float CAMERA_LEFT = 0.0f;          // 不需要左右偏移
    private static final float LOOK_AHEAD = 150.0f;         // 前方观察距离
    private static final float LOOK_UP = 0.0f;              // 保持水平视线
    private static final float SIDE_VIEW_ANGLE = (float) (2.0f * Math.PI/3);  // 增加到120度的视角

    // 修改环绕视角的参数
    private static final float ORBIT_RADIUS = 1000.0f;     // 增大轨迹半径
    private static final float ORBIT_HEIGHT = -500.0f;    // 保持负高度
    private static final float ORBIT_SPEED = -0.05f;        // 保持旋转速度
    private static final float TRACK_CENTER_X = 0.0f;     // 赛道中心X坐标
    private static final float TRACK_CENTER_Y = 0.0f;     // 赛道中心Y坐标
    private static final float TRACK_CENTER_Z = 0.0f;  // 保持观察点Z坐标

    private float orbitAngle = 0.0f;                      // 当前旋转角度

    // 在类的开头添加自动播放相关变量
    private boolean isAutoPlay = true;    // 默认开启自动播放
    private float autoPlayTimer = 0.0f;   // 自动播放计时器
    private static final float ORBIT_VIEW_DURATION = 5.0f;   // 环绕视角持续时间
    private static final float FOLLOW_VIEW_DURATION = 3.0f;  // 跟随视角持续时间
    private static final float TOTAL_CYCLE_TIME = ORBIT_VIEW_DURATION + FOLLOW_VIEW_DURATION;  // 总循环时间

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
        centerTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/img_1.png"));

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

        // 初始化每个赛道上的赛车
        trackCars = new Car[CAR_COUNT - 1][CARS_PER_TRACK];
        trackCarAngles = new float[CAR_COUNT - 1][CARS_PER_TRACK];
        trackCarSpeeds = new float[CAR_COUNT - 1][CARS_PER_TRACK];

        // 加载赛车纹理
        carTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/car_texture.png"));

        // 创建赛车并设置初始位置和速度
        for (int i = 0; i < CAR_COUNT; i++) {
            cars[i] = new Car(CAR_COLORS[i]);  // 传入对应的颜色
            carAngles[i] = (float)(i * (2.0f * Math.PI / CAR_COUNT));
            carSpeeds[i] = 1.0f + (float)(Math.random() * 0.5f);

            // 为每个赛道添加额外的赛车
            if (i > 0) {  // 跳过红色赛车的道
                for (int j = 0; j < CARS_PER_TRACK; j++) {
                    trackCars[i - 1][j] = new Car(new float[]{(float)Math.random(), (float)Math.random(), (float)Math.random(), 1.0f});
                    trackCarAngles[i - 1][j] = (float)(j * (2.0f * Math.PI / CARS_PER_TRACK));
                    trackCarSpeeds[i - 1][j] = carSpeeds[i];  // 统一速度
                }
            }
        }
    }

    private void update(float delta) {
        // 更新每辆赛车的位置
        for (int i = 0; i < CAR_COUNT; i++) {
            carAngles[i] += carSpeeds[i] * delta;
            if (carAngles[i] > 2 * Math.PI) {
                carAngles[i] -= 2 * Math.PI;
            }

            // 更新每个赛道上的额外赛车
            if (i > 0) {
                for (int j = 0; j < CARS_PER_TRACK; j++) {
                    trackCarAngles[i - 1][j] += trackCarSpeeds[i - 1][j] * delta;
                    if (trackCarAngles[i - 1][j] > 2 * Math.PI) {
                        trackCarAngles[i - 1][j] -= 2 * Math.PI;
                    }
                }
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
            OrthoNumber += 20; // 缩视角
        } else if (dWheel > 0) {
            OrthoNumber -= 20; // 放大视角
        }

        // 处理键盘输入
        if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
            MyArcball.reset();
        }

        // 添加键盘控制来切换维修站主题
        if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
            currentPitStopTheme = FERRARI_THEME;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
            currentPitStopTheme = MERCEDES_THEME;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_3)) {
            currentPitStopTheme = REDBULL_THEME;
        }

        // C键切换跟随视角
        if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
            if (waitForKeyreleaseC) {
                isFollowCamera = !isFollowCamera;
                if (isFollowCamera) {
                    isOrbitCamera = false;
                    isAutoPlay = false;  // 关闭自动播放
                } else {
                    isAutoPlay = true;   // 取消跟随视角时恢复自动播放
                    autoPlayTimer = 0.0f; // 重置计时器
                }
                waitForKeyreleaseC = false;
            }
        } else {
            waitForKeyreleaseC = true;
        }
        
        // V键切换环绕视角
        if (Keyboard.isKeyDown(Keyboard.KEY_V)) {
            if (waitForKeyreleaseV) {
                isOrbitCamera = !isOrbitCamera;
                if (isOrbitCamera) {
                    isFollowCamera = false;
                    isAutoPlay = false;  // 关闭自动播放
                } else {
                    isAutoPlay = true;   // 取消环绕视角时恢复自动播放
                    autoPlayTimer = 0.0f; // 重置计时器
                }
                waitForKeyreleaseV = false;
            }
        } else {
            waitForKeyreleaseV = true;
        }

        // 自动播放逻辑
        if (isAutoPlay) {
            autoPlayTimer += delta;
            if (autoPlayTimer >= TOTAL_CYCLE_TIME) {
                autoPlayTimer -= TOTAL_CYCLE_TIME;  // 重置计时器
            }
            
            // 根据计时器切换视角
            if (autoPlayTimer < ORBIT_VIEW_DURATION) {
                // 环绕视角时间段
                isOrbitCamera = true;
                isFollowCamera = false;
            } else {
                // 跟随视角时间段
                isOrbitCamera = false;
                isFollowCamera = true;
            }
        }
        
        // 更新轨道摄像头角度
        if (isOrbitCamera) {
            orbitAngle += ORBIT_SPEED * delta;
            if (orbitAngle >= 2 * Math.PI) {
                orbitAngle -= 2 * Math.PI;
            }
        }
    }

    private void changeOrth() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        if (isFollowCamera) {
            // 使用透视投影提供更真实的视角效果
            gluPerspective(60.0f, 1200.0f/800.0f, 1.0f, 3000.0f);
        } else {
            // 原有的正交投影
            glOrtho(1200 - OrthoNumber, OrthoNumber, (800 - (OrthoNumber * 0.66f)),
                    (OrthoNumber * 0.66f), 100000, -100000);
        }

        glMatrixMode(GL_MODELVIEW);
    }

    private void renderGL() {
        changeOrth();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        glPushMatrix();
        {
            if (isFollowCamera) {
                // 获取红色赛车的位置和角度
                float[] carPos = cars[0].getPositionOnTrack(CAR_RADII[0], carAngles[0], BANKING_ANGLE);
                
                // 计算摄像位置（车头正前方）
                float camX = carPos[0] + (float)(CAMERA_FORWARD * Math.cos(carAngles[0]) +
                                                LOOK_AHEAD * Math.cos(carAngles[0] + SIDE_VIEW_ANGLE));
                float camY = carPos[1] + (float)(CAMERA_FORWARD * Math.sin(carAngles[0]) +
                                                LOOK_AHEAD * Math.sin(carAngles[0] + SIDE_VIEW_ANGLE));
                float camZ = carPos[2] + CAR_HEIGHTS[0] + CAMERA_HEIGHT;
                
                // 计算观察点（车的位置）
                float lookX = carPos[0];
                float lookY = carPos[1];
                float lookZ = carPos[2] + CAR_HEIGHTS[0] + LOOK_UP;
                
                // 设置视角
                glLoadIdentity();
                gluLookAt(camX, camY, camZ, lookX, lookY, lookZ, 0.0f, 0.0f, 1.0f);
            } else if (isOrbitCamera) {
                // 计算摄像头位置（在赛道外围上空环绕）
                float camX = (float)(ORBIT_RADIUS * Math.cos(orbitAngle + Math.PI/2));
                float camY = (float)(ORBIT_RADIUS * Math.sin(orbitAngle + Math.PI/2));
                float camZ = TRACK_CENTER_Z + ORBIT_HEIGHT;
                
                // 设置视角
                glLoadIdentity();
                
                // 先进行整体场景的平移
                glTranslatef(600, 400, 0);  // 将整个场景移动到正确的位置
                
                // 使用gluLookAt直接设置视角，看向赛道中心
                gluLookAt(camX, camY, camZ,                    // 摄像头位置
                          0.0f, 0.0f, TRACK_CENTER_Z,          // 看向原点（赛道中心）
                          0.0f, 0.0f, 1.0f);                   // 上方向
            } else {
                // 原有的自由视角代码
                glTranslatef(600, 400, 0);
                glRotatef(-90, 1.0f, 0.0f, 0.0f);
                glScalef(zoomLevel, zoomLevel, zoomLevel);
                
                FloatBuffer CurrentMatrix = BufferUtils.createFloatBuffer(16);
                glGetFloat(GL_MODELVIEW_MATRIX, CurrentMatrix);
                MyArcball.getMatrix(CurrentMatrix);
                glLoadMatrix(CurrentMatrix);
            }

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
                          trackTexture, null, baseTexture, groundTexture, currentPitStopTheme);  // 将wallTexture替换为null
            track.drawLightPosts(TRACK_OUTER_RADIUS, -60.0f, postHeight);  // 添加postHeight参数

            // 添加中心区域的装饰
            glPushMatrix();
            {
                // 启用混合
                glEnable(GL_BLEND);
                glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
                
                // 禁用光照以确保纹理颜色正确显示
                //glDisable(GL_LIGHTING);
                
                // 启用纹理
                glEnable(GL_TEXTURE_2D);
                centerTexture.bind();
                
                // 绘制一个平面，尺寸略小于内圈
                float size = TRACK_INNER_RADIUS * 0.3f;  // 可以调整这个系数来改变大小
                float height = 1.0f;  // 与阴影高度相同，确保在地面上方一点
                
                glBegin(GL_QUADS);
                {
                    glTexCoord2f(0.0f, 0.0f); glVertex3f(-size-200.0f, -size, height);
                    glTexCoord2f(1.0f, 0.0f); glVertex3f(size-200.0f, -size, height);
                    glTexCoord2f(1.0f, 1.0f); glVertex3f(size-200.0f, size, height);
                    glTexCoord2f(0.0f, 1.0f); glVertex3f(-size-200.0f, size, height);
                }
                glEnd();
                
                // 恢复状态
                glDisable(GL_TEXTURE_2D);
                glEnable(GL_LIGHTING);
                glDisable(GL_BLEND);
            }
            glPopMatrix();
            
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

                // 绘制每个赛道上的额外赛车
                if (i > 0) {
                    for (int j = 0; j < CARS_PER_TRACK; j++) {
                        float[] extraCarPos = trackCars[i - 1][j].getPositionOnTrack(CAR_RADII[i], trackCarAngles[i - 1][j], BANKING_ANGLE);

                        // 绘制额外赛车的阴影
                        drawCarShadow(extraCarPos, CAR_RADII[i], trackCarAngles[i - 1][j], 20.0f, i);

                        // 然后绘制额外赛车
                        glPushMatrix();
                        {
                            glTranslatef(extraCarPos[0], extraCarPos[1], extraCarPos[2] + CAR_HEIGHTS[i]);

                            // 2. 向赛道内侧倾斜
                            float extraTiltDirection = (float)Math.toDegrees(trackCarAngles[i - 1][j]) + 90;
                            glRotatef(extraTiltDirection, 0.0f, 0.0f, 1.0f);
                            glRotatef((float) -Math.toDegrees(BANKING_ANGLE), 1.0f, 0.0f, 0.0f);
                            glRotatef(-extraTiltDirection, 0.0f, 0.0f, 1.0f);

                            // 3. 最后设置车身朝向
                            glRotatef(extraCarPos[3], 0.0f, 0.0f, 1.0f);

                            // 绘制额外赛车
                            trackCars[i - 1][j].drawCar(carTexture, 20.0f);
                        }
                        glPopMatrix();
                    }
                }
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
                
                // 禁用光照以确保影颜色正确
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
                
                // 扁并放大影
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