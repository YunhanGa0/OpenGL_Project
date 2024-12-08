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
    private Texture baseTexture;
    private Texture groundTexture;
    private Texture centerTexture;  // The texture of the center area
    
    // 光照设置
    static float grey[] = { 0.5f, 0.5f, 0.5f, 1.0f };
    static float white[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    
    private Car[] cars;  // multi-car
    private Texture carTexture;
    private float[] carAngles;  // The Angle of each race car
    private float[] carSpeeds;  // The speed of each car
    private static final int CAR_COUNT = 4;  // The speed of each car
    private static final float TRACK_OUTER_RADIUS = 700.0f;  // The radius of the outermost ring
    private static final float TRACK_INNER_RADIUS = 450.0f;  // The innermost circle radius
    private static final float BANKING_ANGLE = (float)Math.PI/12;  // Track inclination angle
    
    // 为每辆车设置固定的轨道半径和高度
    private static final float[] CAR_RADII = {
        320.0f + 150.0f,  // Car 1 - the innermost circle
        370.0f + 150.0f,  // Car 2
        430.0f + 150.0f,  // Car 3
        490.0f + 150.0f   // 4th car - outermost circle
    };
    
    private static final float[] CAR_HEIGHTS = {
        -102.0f,   // Car 1 - the innermost circle (highest height)
        -101.0f,  // Car 2
        -101.0f,  // Car 3
        -100.0f   // Car 4 - the outermost circle (lowest height)
    };

    // Define different car colors
    private static final float[][] CAR_COLORS = {
        {1.0f, 0.0f, 0.0f, 1.0f},  // Car 1 - red
        {0.0f, 0.0f, 1.0f, 1.0f},  // Car 2 - blue
        {0.0f, 1.0f, 0.0f, 1.0f},  // Car 3 - green
        {1.0f, 1.0f, 0.0f, 1.0f}   // Car 4 - yellow
    };

    // Add shadow height array at the beginning of the class, corresponding to CAR_HEIGHTS
    private static final float[] SHADOW_HEIGHTS = {
        31.0f,   // Car 1 - the innermost circle
        32.0f,   // Car 2
        40.0f,   // Car 3
        50.0f    // Car 4 - the outermost circle
    };

    private long lastFrameTime;

    // Add light source array
    private static final int LIGHT_COUNT = 4;
    private static final int[] LIGHTS = {
        GL_LIGHT1, GL_LIGHT2, GL_LIGHT3, GL_LIGHT4
    };

    private static final float[] SHADOW_COLOR = {0.1f, 0.1f, 0.1f, 0.3f};  // Deeper grey, increase opacity

    private static final int CARS_PER_TRACK = 5;  // Number of cars per track

    private Car[][] trackCars;  // Cars on each track
    private float[][] trackCarAngles;  // Angles of cars on each track
    private float[][] trackCarSpeeds;  // Speeds of cars on each track

    // Add predefined color themes at the beginning of the NascarWindow class
    private static final RaceTrack.PitStopColors FERRARI_THEME = new RaceTrack.PitStopColors(
        new float[]{0.9f, 0.1f, 0.1f, 1.0f},  // Ferrari red main building
        new float[]{0.7f, 0.05f, 0.05f, 1.0f}, // Deep red roof
        new float[]{0.2f, 0.2f, 0.2f, 1.0f},   // Dark grey service area
        new float[]{0.1f, 0.1f, 0.1f, 1.0f},   // Black equipment
        new float[]{1.0f, 0.1f, 0.1f, 1.0f},   // Bright red sign
        new float[]{0.2f, 0.2f, 0.2f, 0.6f}    // Semi-transparent grey window
    );

    private static final RaceTrack.PitStopColors MERCEDES_THEME = new RaceTrack.PitStopColors(
        new float[]{0.2f, 0.2f, 0.2f, 1.0f},  // Mercedes silver grey main building
        new float[]{0.15f, 0.15f, 0.15f, 1.0f}, // Dark grey roof
        new float[]{0.3f, 0.3f, 0.3f, 1.0f},   // Light grey service area
        new float[]{0.1f, 0.1f, 0.1f, 1.0f},   // Black equipment
        new float[]{0.0f, 0.8f, 0.0f, 1.0f},   // Mercedes green sign
        new float[]{0.2f, 0.2f, 0.2f, 0.6f}    // Semi-transparent grey window
    );

    private static final RaceTrack.PitStopColors REDBULL_THEME = new RaceTrack.PitStopColors(
        new float[]{0.0f, 0.0f, 0.4f, 1.0f},  // Deep blue main building
        new float[]{0.0f, 0.0f, 0.3f, 1.0f},  // Deeper blue roof
        new float[]{0.2f, 0.2f, 0.2f, 1.0f},  // Dark grey service area
        new float[]{0.1f, 0.1f, 0.1f, 1.0f},  // Black equipment
        new float[]{0.8f, 0.0f, 0.0f, 1.0f},  // Red Bull red sign
        new float[]{0.2f, 0.2f, 0.8f, 0.6f}   // Semi-transparent blue window
    );

    // Add a variable to track the current theme in the class
    private RaceTrack.PitStopColors currentPitStopTheme = FERRARI_THEME;

    // Add camera related variables at the beginning of the class
    private boolean isFollowCamera = false;  // Whether to enable follow view
    private boolean isOrbitCamera = false;    // Orbit view (V key)
    private boolean waitForKeyreleaseC = true; // C key anti-repeat
    private boolean waitForKeyreleaseV = true; // V key anti-repeat
    private static final float CAMERA_HEIGHT = 15.0f;       // Camera height (relative to car top)
    private static final float CAMERA_FORWARD = 0.0f;       // Located at the center of the car head
    private static final float CAMERA_LEFT = 0.0f;          // No need for left-right offset
    private static final float LOOK_AHEAD = 150.0f;         // Forward observation distance
    private static final float LOOK_UP = 0.0f;              // Keep horizontal line of sight
    private static final float SIDE_VIEW_ANGLE = (float) (2.0f * Math.PI/3);  // Increase to 120-degree view

    // Modify orbit view parameters
    private static final float ORBIT_RADIUS = 1000.0f;     // Increase orbit radius
    private static final float ORBIT_HEIGHT = -500.0f;    // Keep negative height
    private static final float ORBIT_SPEED = -0.05f;        // Keep rotation speed
    private static final float TRACK_CENTER_X = 0.0f;     // Track center X coordinate
    private static final float TRACK_CENTER_Y = 0.0f;     // Track center Y coordinate
    private static final float TRACK_CENTER_Z = 0.0f;  // Keep observation point Z coordinate

    private float orbitAngle = 0.0f;                      // Current rotation angle

    // Add auto-play related variables at the beginning of the class
    private boolean isAutoPlay = true;    // Default enable auto-play
    private float autoPlayTimer = 0.0f;   // Auto-play timer
    private static final float ORBIT_VIEW_DURATION = 5.0f;   // Orbit view duration
    private static final float FOLLOW_VIEW_DURATION = 3.0f;  // Follow view duration
    private static final float TOTAL_CYCLE_TIME = ORBIT_VIEW_DURATION + FOLLOW_VIEW_DURATION;  // Total cycle time

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
        // Initialize OpenGL settings
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        changeOrth();
        MyArcball.startBall(0, 0, 1200, 800);
        glMatrixMode(GL_MODELVIEW);

        // Comment out ambient light settings
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

        // Load textures
        trackTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/track.png"));
        baseTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/base2.png"));
        groundTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/ground.png"));
        centerTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/img_1.png"));

        // Initialize the track
        track = new RaceTrack();

        // Modify initial lighting settings
        float lightIntensity = 2.0f;  // Increase default light intensity
        FloatBuffer lightColor = BufferUtils.createFloatBuffer(4);
        lightColor.put(lightIntensity).put(lightIntensity).put(0.9f*lightIntensity).put(1.0f).flip();

        for (int i = 0; i < LIGHT_COUNT; i++) {
            glLight(LIGHTS[i], GL_DIFFUSE, lightColor);
            glLight(LIGHTS[i], GL_SPECULAR, lightColor);
            glEnable(LIGHTS[i]);
        }
    }

    private void initCars() throws IOException {
        // Initialize the array of cars
        cars = new Car[CAR_COUNT];
        carAngles = new float[CAR_COUNT];
        carSpeeds = new float[CAR_COUNT];

        // Initialize each car on the track
        trackCars = new Car[CAR_COUNT - 1][CARS_PER_TRACK];
        trackCarAngles = new float[CAR_COUNT - 1][CARS_PER_TRACK];
        trackCarSpeeds = new float[CAR_COUNT - 1][CARS_PER_TRACK];

        // Load car texture
        carTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/car_texture.png"));

        // Create cars and set initial positions and speeds
        for (int i = 0; i < CAR_COUNT; i++) {
            cars[i] = new Car(CAR_COLORS[i]);  // Pass in the corresponding color
            carAngles[i] = (float)(i * (2.0f * Math.PI / CAR_COUNT));
            carSpeeds[i] = 1.0f + (float)(Math.random() * 0.5f);

            // Add extra cars to each track
            if (i > 0) {  // Skip the red car's track
                for (int j = 0; j < CARS_PER_TRACK; j++) {
                    trackCars[i - 1][j] = new Car(new float[]{(float)Math.random(), (float)Math.random(), (float)Math.random(), 1.0f});
                    trackCarAngles[i - 1][j] = (float)(j * (2.0f * Math.PI / CARS_PER_TRACK));
                    trackCarSpeeds[i - 1][j] = carSpeeds[i];  // Uniform speed
                }
            }
        }
    }

    private void update(float delta) {
        // Update the position of each car
        for (int i = 0; i < CAR_COUNT; i++) {
            carAngles[i] += carSpeeds[i] * delta;
            if (carAngles[i] > 2 * Math.PI) {
                carAngles[i] -= 2 * Math.PI;
            }

            // Update the extra cars on each track
            if (i > 0) {
                for (int j = 0; j < CARS_PER_TRACK; j++) {
                    trackCarAngles[i - 1][j] += trackCarSpeeds[i - 1][j] * delta;
                    if (trackCarAngles[i - 1][j] > 2 * Math.PI) {
                        trackCarAngles[i - 1][j] -= 2 * Math.PI;
                    }
                }
            }
        }

        // Handle mouse input to update the view
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

        // Handle mouse wheel zoom
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            OrthoNumber += 20; // Zoom out
        } else if (dWheel > 0) {
            OrthoNumber -= 20; // Zoom in
        }

        // Handle keyboard input
        if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
            MyArcball.reset();
        }

        // Add keyboard control to switch pit stop themes
        if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
            currentPitStopTheme = FERRARI_THEME;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
            currentPitStopTheme = MERCEDES_THEME;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_3)) {
            currentPitStopTheme = REDBULL_THEME;
        }

        // C key to switch follow camera
        if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
            if (waitForKeyreleaseC) {
                isFollowCamera = !isFollowCamera;
                if (isFollowCamera) {
                    isOrbitCamera = false;
                    isAutoPlay = false;  // Disable auto-play
                } else {
                    isAutoPlay = true;   // Restore auto-play when not following camera
                    autoPlayTimer = 0.0f; // Reset timer
                }
                waitForKeyreleaseC = false;
            }
        } else {
            waitForKeyreleaseC = true;
        }
        
        // V key to switch orbit camera
        if (Keyboard.isKeyDown(Keyboard.KEY_V)) {
            if (waitForKeyreleaseV) {
                isOrbitCamera = !isOrbitCamera;
                if (isOrbitCamera) {
                    isFollowCamera = false;
                    isAutoPlay = false;  // Disable auto-play
                } else {
                    isAutoPlay = true;   // Restore auto-play when not orbiting
                    autoPlayTimer = 0.0f; // Reset timer
                }
                waitForKeyreleaseV = false;
            }
        } else {
            waitForKeyreleaseV = true;
        }

        // Auto-play logic
        if (isAutoPlay) {
            autoPlayTimer += delta;
            if (autoPlayTimer >= TOTAL_CYCLE_TIME) {
                autoPlayTimer -= TOTAL_CYCLE_TIME;  // Reset timer
            }
            
            // Switch view based on timer
            if (autoPlayTimer < ORBIT_VIEW_DURATION) {
                // Orbit view duration
                isOrbitCamera = true;
                isFollowCamera = false;
            } else {
                // Follow view duration
                isOrbitCamera = false;
                isFollowCamera = true;
            }
        }
        
        // Update track camera angle
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
            // Use perspective projection for more realistic view
            gluPerspective(60.0f, 1200.0f/800.0f, 1.0f, 3000.0f);
        } else {
            // Original orthographic projection
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
                // Get red car's position and angle
                float[] carPos = cars[0].getPositionOnTrack(CAR_RADII[0], carAngles[0], BANKING_ANGLE);
                
                // Calculate camera position (in front of car)
                float camX = carPos[0] + (float)(CAMERA_FORWARD * Math.cos(carAngles[0]) +
                                                LOOK_AHEAD * Math.cos(carAngles[0] + SIDE_VIEW_ANGLE));
                float camY = carPos[1] + (float)(CAMERA_FORWARD * Math.sin(carAngles[0]) +
                                                LOOK_AHEAD * Math.sin(carAngles[0] + SIDE_VIEW_ANGLE));
                float camZ = carPos[2] + CAR_HEIGHTS[0] + CAMERA_HEIGHT;
                
                // Calculate look-at point (car position)
                float lookX = carPos[0];
                float lookY = carPos[1];
                float lookZ = carPos[2] + CAR_HEIGHTS[0] + LOOK_UP;
                
                // Set camera view
                glLoadIdentity();
                gluLookAt(camX, camY, camZ, lookX, lookY, lookZ, 0.0f, 0.0f, 1.0f);
            } else if (isOrbitCamera) {
                // Calculate camera position (orbiting above track)
                float camX = (float)(ORBIT_RADIUS * Math.cos(orbitAngle + Math.PI/2));
                float camY = (float)(ORBIT_RADIUS * Math.sin(orbitAngle + Math.PI/2));
                float camZ = TRACK_CENTER_Z + ORBIT_HEIGHT;
                
                // Set camera view
                glLoadIdentity();
                
                // First translate entire scene
                glTranslatef(600, 400, 0);  // Move entire scene to correct position
                
                // Use gluLookAt to directly set view, looking at track center
                gluLookAt(camX, camY, camZ,                    // Camera position
                          0.0f, 0.0f, TRACK_CENTER_Z,          // Look at center point
                          0.0f, 0.0f, 1.0f);                   // Up direction
            } else {
                // Original free camera code
                glTranslatef(600, 400, 0);
                glRotatef(-90, 1.0f, 0.0f, 0.0f);
                glScalef(zoomLevel, zoomLevel, zoomLevel);
                
                FloatBuffer CurrentMatrix = BufferUtils.createFloatBuffer(16);
                glGetFloat(GL_MODELVIEW_MATRIX, CurrentMatrix);
                MyArcball.getMatrix(CurrentMatrix);
                glLoadMatrix(CurrentMatrix);
            }

            // Adjust ambient light to avoid complete darkness
            FloatBuffer ambientLight = BufferUtils.createFloatBuffer(4);
            ambientLight.put(new float[] {0.2f, 0.2f, 0.2f, 1.0f}).flip();  // Increase ambient light
            glLightModel(GL_LIGHT_MODEL_AMBIENT, ambientLight);

            // Update light positions and properties
            float postRadius = TRACK_OUTER_RADIUS * 1.2f;
            float postHeight = 400.0f;
            
            for (int i = 0; i < LIGHT_COUNT; i++) {
                float angle = (float)(i * Math.PI / 2);
                float x = (float)(postRadius * Math.cos(angle));
                float y = (float)(postRadius * Math.sin(angle));
                
                // Set light position
                FloatBuffer lightPos = BufferUtils.createFloatBuffer(4);
                lightPos.put(new float[] {x, y, postHeight, 1.0f}).flip();
                glLight(LIGHTS[i], GL_POSITION, lightPos);
                
                // Set light direction
                FloatBuffer spotDir = BufferUtils.createFloatBuffer(4);
                spotDir.put(new float[] {-x, -y, -postHeight, 0.0f}).flip();
                glLight(LIGHTS[i], GL_SPOT_DIRECTION, spotDir);
                
                // Enhance light color intensity
                FloatBuffer lightDiffuse = BufferUtils.createFloatBuffer(4);
                lightDiffuse.put(new float[] {3.0f, 3.0f, 2.7f, 1.0f}).flip();  // Further enhance diffuse
                glLight(LIGHTS[i], GL_DIFFUSE, lightDiffuse);
                
                FloatBuffer lightSpecular = BufferUtils.createFloatBuffer(4);
                lightSpecular.put(new float[] {3.0f, 3.0f, 2.7f, 1.0f}).flip();  // Further enhance specular
                glLight(LIGHTS[i], GL_SPECULAR, lightSpecular);
                
                // Adjust spotlight parameters
                FloatBuffer spotCutoff = BufferUtils.createFloatBuffer(4);
                spotCutoff.put(new float[] {75.0f, 0.0f, 0.0f, 0.0f}).flip();  // Increase light angle
                glLight(LIGHTS[i], GL_SPOT_CUTOFF, spotCutoff);
                
                // Adjust attenuation factors
                FloatBuffer constant = BufferUtils.createFloatBuffer(4);
                constant.put(new float[] {1.0f, 0.0f, 0.0f, 0.0f}).flip();
                glLight(LIGHTS[i], GL_CONSTANT_ATTENUATION, constant);
                
                FloatBuffer linear = BufferUtils.createFloatBuffer(4);
                linear.put(new float[] {0.0003f, 0.0f, 0.0f, 0.0f}).flip();  // Further reduce linear attenuation
                glLight(LIGHTS[i], GL_LINEAR_ATTENUATION, linear);
                
                FloatBuffer quadratic = BufferUtils.createFloatBuffer(4);
                quadratic.put(new float[] {0.000001f, 0.0f, 0.0f, 0.0f}).flip();  // Further reduce quadratic attenuation
                glLight(LIGHTS[i], GL_QUADRATIC_ATTENUATION, quadratic);
            }
            
            // Draw track and lights - no wall texture passed
            track.drawTrack(TRACK_INNER_RADIUS, TRACK_OUTER_RADIUS, 0.0f, BANKING_ANGLE, 60,
                          trackTexture, null, baseTexture, groundTexture, currentPitStopTheme);  // Replace wallTexture with null
            track.drawLightPosts(TRACK_OUTER_RADIUS, -60.0f, postHeight);  // Add postHeight parameter

            // Add center area decorations
            glPushMatrix();
            {
                // Enable blending
                glEnable(GL_BLEND);
                glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
                
                // Enable texture
                glEnable(GL_TEXTURE_2D);
                centerTexture.bind();
                
                // Draw a plane slightly smaller than inner circle
                float size = TRACK_INNER_RADIUS * 0.3f;  // Adjust this coefficient to change size
                float height = 1.0f;  // Same as shadow height to ensure above ground
                
                glBegin(GL_QUADS);
                {
                    glTexCoord2f(0.0f, 0.0f); glVertex3f(-size-200.0f, -size, height);
                    glTexCoord2f(1.0f, 0.0f); glVertex3f(size-200.0f, -size, height);
                    glTexCoord2f(1.0f, 1.0f); glVertex3f(size-200.0f, size, height);
                    glTexCoord2f(0.0f, 1.0f); glVertex3f(-size-200.0f, size, height);
                }
                glEnd();
                
                // Restore states
                glDisable(GL_TEXTURE_2D);
                glEnable(GL_LIGHTING);
                glDisable(GL_BLEND);
            }
            glPopMatrix();
            
            // Draw all cars
            for (int i = 0; i < CAR_COUNT; i++) {
                float[] carPos = cars[i].getPositionOnTrack(CAR_RADII[i], carAngles[i], BANKING_ANGLE);
                
                // Pass car index i
                drawCarShadow(carPos, CAR_RADII[i], carAngles[i], 20.0f, i);
                
                // Then draw car
                glPushMatrix();
                {
                    glTranslatef(carPos[0], carPos[1], carPos[2] + CAR_HEIGHTS[i]);

                    // 2. Tilt towards track inside
                    float tiltDirection = (float)Math.toDegrees(carAngles[i]) + 90;
                    glRotatef(tiltDirection, 0.0f, 0.0f, 1.0f);
                    glRotatef((float) -Math.toDegrees(BANKING_ANGLE), 1.0f, 0.0f, 0.0f);
                    glRotatef(-tiltDirection, 0.0f, 0.0f, 1.0f);

                    // 3. Finally set car orientation
                    glRotatef(carPos[3], 0.0f, 0.0f, 1.0f);

                    // Draw car
                    cars[i].drawCar(carTexture, 20.0f);
                }
                glPopMatrix();

                // Draw extra cars on each track
                if (i > 0) {
                    for (int j = 0; j < CARS_PER_TRACK; j++) {
                        float[] extraCarPos = trackCars[i - 1][j].getPositionOnTrack(CAR_RADII[i], trackCarAngles[i - 1][j], BANKING_ANGLE);

                        // Draw extra car shadow
                        drawCarShadow(extraCarPos, CAR_RADII[i], trackCarAngles[i - 1][j], 20.0f, i);

                        // Then draw extra car
                        glPushMatrix();
                        {
                            glTranslatef(extraCarPos[0], extraCarPos[1], extraCarPos[2] + CAR_HEIGHTS[i]);

                            // 2. Tilt towards track inside
                            float extraTiltDirection = (float)Math.toDegrees(trackCarAngles[i - 1][j]) + 90;
                            glRotatef(extraTiltDirection, 0.0f, 0.0f, 1.0f);
                            glRotatef((float) -Math.toDegrees(BANKING_ANGLE), 1.0f, 0.0f, 0.0f);
                            glRotatef(-extraTiltDirection, 0.0f, 0.0f, 1.0f);

                            // 3. Finally set car orientation
                            glRotatef(extraCarPos[3], 0.0f, 0.0f, 1.0f);

                            // Draw extra car
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
        // Calculate car distance on plane (relative to track center)
        float distanceFromCenter = (float)Math.sqrt(carPos[0] * carPos[0] + carPos[1] * carPos[1]);
        
        // Only draw shadows within track bounds
        if (distanceFromCenter < TRACK_OUTER_RADIUS && distanceFromCenter > TRACK_INNER_RADIUS) {
            glPushMatrix();
            {
                // Disable depth write but keep depth testing
                glDepthMask(false);
                
                // Enable blending for transparency
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                
                // Disable lighting for correct shadow color
                glDisable(GL_LIGHTING);
                
                // Set shadow color
                glColor4f(SHADOW_COLOR[0], SHADOW_COLOR[1], SHADOW_COLOR[2], SHADOW_COLOR[3]);
                
                // Use corresponding car's shadow height
                glTranslatef(carPos[0], carPos[1], SHADOW_HEIGHTS[carIndex]);
                
                // Rotate shadow based on track banking angle
                float tiltDirection = (float)Math.toDegrees(angle) + 90;
                glRotatef(tiltDirection, 0.0f, 0.0f, 1.0f);
                glRotatef((float) -Math.toDegrees(BANKING_ANGLE), 1.0f, 0.0f, 0.0f);
                glRotatef(-tiltDirection, 0.0f, 0.0f, 1.0f);
                
                // Flatten and enlarge shadow
                glScalef(scale * 1.2f, scale * 0.8f, 1.0f);
                
                // Draw simple elliptical shadow
                glBegin(GL_TRIANGLE_FAN);
                {
                    glVertex3f(0.0f, 0.0f, 0.0f);  // Center point
                    int segments = 32;
                    for (int i = 0; i <= segments; i++) {
                        float theta = (float)(i * 2.0f * Math.PI / segments);
                        float x = (float)Math.cos(theta) * 2.0f;  // Adjust this coefficient to change shadow length
                        float y = (float)Math.sin(theta);         // Adjust this coefficient to change shadow width
                        glVertex3f(x, y, 0.0f);
                    }
                }
                glEnd();
                
                // Restore states
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