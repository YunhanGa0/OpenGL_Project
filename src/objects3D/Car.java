package objects3D;

import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;
import GraphicsObjects.Point4f;
import GraphicsObjects.Vector4f;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class Car {
    // Basic color definitions
    static float[] bodyColor = {1.0f, 0.0f, 0.0f, 1.0f};  // Bright red body
    static float[] wheelColor = {0.1f, 0.1f, 0.1f, 1.0f}; // Deep black tires
    static float[] wheelCapColor = {0.7f, 0.7f, 0.7f, 1.0f}; // Silver wheel hubs
    static float[] windowColor = {0.3f, 0.3f, 0.8f, 0.5f}; // Semi-transparent blue glass
    static float[] headlightColor = {1.0f, 1.0f, 0.8f, 1.0f}; // Yellow headlights
    static float[] taillightColor = {1.0f, 0.0f, 0.0f, 1.0f}; // Red taillights
    static float[] bumperColor = {0.2f, 0.2f, 0.2f, 1.0f};    // Dark grey bumper
    static float[] spoilerColor = {0.1f, 0.1f, 0.1f, 1.0f};   // Black spoiler

    private Cube carBody;
    private Cylinder wheel;
    private TexSphere wheelCap;
    private Cube headlight;
    private Cube taillight;
    private Cube bumper;
    private Cube spoiler;
    private float[] carColor;  // Add color property

    public Car(float[] color) {
        this.carColor = color;
        carBody = new Cube();
        wheel = new Cylinder();
        wheelCap = new TexSphere();
        headlight = new Cube();
        taillight = new Cube();
        bumper = new Cube();
        spoiler = new Cube();
    }

    public void drawCar(Texture bodyTexture, float scale) {
        glPushMatrix();
        {
            glScalef(scale, scale, scale);
            
            glEnable(GL_COLOR_MATERIAL);
            glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);
            
            // Draw main car body
            glPushMatrix();
            {
                glScalef(2.0f, 1.0f, 0.5f);
                setMaterial(carColor, 128.0f);
                carBody.drawCube();
            }
            glPopMatrix();

            // Draw car roof - lower overall position
            glPushMatrix();
            {
                // Lower z-axis position from 0.6f to 0.5f
                glTranslatef(0.0f, 0.0f, 0.5f);
                glScalef(1.2f, 0.72f, 0.32f);
                
                // Draw front and rear sloped frame
                glPushMatrix();
                {
                    // Draw front frame (trapezoid)
                    glBegin(GL_QUADS);
                    {
                        // Set material color
                        setMaterial(carColor, 128.0f);
                        
                        // Front face
                        glVertex3f(-1.4f, -1.4f, 0.0f);  // Bottom left
                        glVertex3f(-1.4f, 1.4f, 0.0f);   // Bottom right
                        glVertex3f(-0.8f, 0.8f, 1.4f);   // Top right
                        glVertex3f(-0.8f, -0.8f, 1.4f);  // Top left
                        
                        // Back face
                        glVertex3f(1.4f, -1.4f, 0.0f);   // Bottom left
                        glVertex3f(1.4f, 1.4f, 0.0f);    // Bottom right
                        glVertex3f(0.8f, 0.8f, 1.4f);    // Top right
                        glVertex3f(0.8f, -0.8f, 1.4f);   // Top left
                        
                        // Top face
                        glVertex3f(-0.8f, -0.8f, 1.4f);  // Front left
                        glVertex3f(-0.8f, 0.8f, 1.4f);   // Front right
                        glVertex3f(0.8f, 0.8f, 1.4f);    // Back right
                        glVertex3f(0.8f, -0.8f, 1.4f);   // Back left
                        
                        // Bottom face
                        glVertex3f(-1.4f, -1.4f, 0.0f);  // Front left
                        glVertex3f(-1.4f, 1.4f, 0.0f);   // Front right
                        glVertex3f(1.4f, 1.4f, 0.0f);    // Back right
                        glVertex3f(1.4f, -1.4f, 0.0f);   // Back left
                    }
                    glEnd();
                }
                glPopMatrix();
                
                // Draw main roof (transparent) - reduced size
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                glPushMatrix();
                {
                    glTranslatef(0.0f, 0.0f, 0.7f);
                    glScalef(0.75f, 0.8f, 0.65f);  // From 1.6f, 1.6f, 1.8f to 0.75f, 0.8f, 0.65f
                    setMaterial(windowColor, 128.0f);
                    carBody.drawCube();
                }
                glPopMatrix();
                
                glDisable(GL_BLEND);
            }
            glPopMatrix();

            // Add front bumper
            glPushMatrix();
            {
                glTranslatef(2.1f, 0.0f, -0.2f);
                glScalef(0.2f, 1.0f, 0.3f);
                setMaterial(bumperColor, 64.0f);
                bumper.drawCube();
            }
            glPopMatrix();

            // Add rear bumper
            glPushMatrix();
            {
                glTranslatef(-2.1f, 0.0f, -0.2f);
                glScalef(0.2f, 1.0f, 0.3f);
                setMaterial(bumperColor, 64.0f);
                bumper.drawCube();
            }
            glPopMatrix();

            // Add front headlights
            glPushMatrix();
            {
                // Front headlights
                glPushMatrix();
                {
                    glTranslatef(2.0f, 0.4f, 0.0f);
                    glScalef(0.1f, 0.2f, 0.2f);
                    setMaterial(headlightColor, 128.0f);
                    headlight.drawCube();
                }
                glPopMatrix();

                // Right front headlights
                glPushMatrix();
                {
                    glTranslatef(2.0f, -0.4f, 0.0f);
                    glScalef(0.1f, 0.2f, 0.2f);
                    setMaterial(headlightColor, 128.0f);
                    headlight.drawCube();
                }
                glPopMatrix();
            }
            glPopMatrix();

            // Add rear lights
            glPushMatrix();
            {
                // Left rear lights
                glPushMatrix();
                {
                    glTranslatef(-2.0f, 0.4f, 0.0f);
                    glScalef(0.1f, 0.2f, 0.2f);
                    setMaterial(taillightColor, 32.0f);
                    taillight.drawCube();
                }
                glPopMatrix();

                // Right rear lights
                glPushMatrix();
                {
                    glTranslatef(-2.0f, -0.4f, 0.0f);
                    glScalef(0.1f, 0.2f, 0.2f);
                    setMaterial(taillightColor, 32.0f);
                    taillight.drawCube();
                }
                glPopMatrix();
            }
            glPopMatrix();

            // Add spoiler
            glPushMatrix();
            {
                glTranslatef(-2.0f, 0.0f, 0.5f);
                glScalef(0.3f, 1.2f, 0.1f);
                setMaterial(spoilerColor, 16.0f);
                spoiler.drawCube();
                
                // Spoiler mount
                glPushMatrix();
                {
                    glTranslatef(0.0f, 0.0f, -1.5f);
                    glScalef(1.0f, 0.1f, 3.0f);
                    spoiler.drawCube();
                }
                glPopMatrix();
            }
            glPopMatrix();

            drawWheels();
            
            glDisable(GL_COLOR_MATERIAL);
        }
        glPopMatrix();
    }

    // Helper method: Set material properties
    private void setMaterial(float[] color, float shininess) {
        FloatBuffer ambient = BufferUtils.createFloatBuffer(4);
        ambient.put(new float[]{color[0]*0.3f, color[1]*0.3f, color[2]*0.3f, color[3]}).flip();
        
        FloatBuffer diffuse = BufferUtils.createFloatBuffer(4);
        diffuse.put(color).flip();
        
        FloatBuffer specular = BufferUtils.createFloatBuffer(4);
        specular.put(new float[]{1.0f, 1.0f, 1.0f, color[3]}).flip();
        
        FloatBuffer shininessBuffer = BufferUtils.createFloatBuffer(4);
        shininessBuffer.put(new float[]{shininess, 0.0f, 0.0f, 0.0f}).flip();
        
        glMaterial(GL_FRONT, GL_AMBIENT, ambient);
        glMaterial(GL_FRONT, GL_DIFFUSE, diffuse);
        glMaterial(GL_FRONT, GL_SPECULAR, specular);
        glMaterial(GL_FRONT, GL_SHININESS, shininessBuffer);
        
        glColor4f(color[0], color[1], color[2], color[3]);
    }

    private void drawWheels() {
        FloatBuffer wheelAmbient = BufferUtils.createFloatBuffer(4);
        wheelAmbient.put(new float[]{0.05f, 0.05f, 0.05f, 1.0f}).flip();
        
        FloatBuffer wheelDiffuse = BufferUtils.createFloatBuffer(4);
        wheelDiffuse.put(wheelColor).flip();
        
        FloatBuffer wheelSpecular = BufferUtils.createFloatBuffer(4);
        wheelSpecular.put(new float[]{0.5f, 0.5f, 0.5f, 1.0f}).flip();
        
        FloatBuffer wheelShininess = BufferUtils.createFloatBuffer(4);
        wheelShininess.put(new float[]{32.0f, 0.0f, 0.0f, 0.0f}).flip();
        
        glMaterial(GL_FRONT, GL_AMBIENT, wheelAmbient);
        glMaterial(GL_FRONT, GL_DIFFUSE, wheelDiffuse);
        glMaterial(GL_FRONT, GL_SPECULAR, wheelSpecular);
        glMaterial(GL_FRONT, GL_SHININESS, wheelShininess);
        
        float wheelRadius = 0.5f;
        float wheelWidth = 0.1f;
        
        drawWheel(-1.5f, 0.7f, -0.25f, wheelRadius, wheelWidth);
        drawWheel(-1.5f, -0.7f, -0.25f, wheelRadius, wheelWidth);
        drawWheel(1.5f, 0.7f, -0.25f, wheelRadius, wheelWidth);
        drawWheel(1.5f, -0.7f, -0.25f, wheelRadius, wheelWidth);
    }

    private void drawWheel(float x, float y, float z, float radius, float width) {
        glPushMatrix();
        {
            glTranslatef(x, y, z);
            glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
            
            glColor4f(wheelColor[0], wheelColor[1], wheelColor[2], wheelColor[3]);
            wheel.drawCylinder(radius, width, 32);
            
            glPushMatrix();
            {
                glTranslatef(0.0f, 0.0f, width/2);
                glScalef(0.7f, 0.7f, 0.1f);
                
                FloatBuffer capAmbient = BufferUtils.createFloatBuffer(4);
                capAmbient.put(new float[]{0.3f, 0.3f, 0.3f, 1.0f}).flip();
                
                FloatBuffer capDiffuse = BufferUtils.createFloatBuffer(4);
                capDiffuse.put(wheelCapColor).flip();
                
                FloatBuffer capSpecular = BufferUtils.createFloatBuffer(4);
                capSpecular.put(new float[]{1.0f, 1.0f, 1.0f, 1.0f}).flip();
                
                FloatBuffer capShininess = BufferUtils.createFloatBuffer(4);
                capShininess.put(new float[]{128.0f, 0.0f, 0.0f, 0.0f}).flip();
                
                glMaterial(GL_FRONT, GL_AMBIENT, capAmbient);
                glMaterial(GL_FRONT, GL_DIFFUSE, capDiffuse);
                glMaterial(GL_FRONT, GL_SPECULAR, capSpecular);
                glMaterial(GL_FRONT, GL_SHININESS, capShininess);
                
                glColor4f(wheelCapColor[0], wheelCapColor[1], wheelCapColor[2], wheelCapColor[3]);
                wheelCap.DrawTexSphere(radius, 16, 16, null);
            }
            glPopMatrix();
        }
        glPopMatrix();
    }

    /**
     * Calculate car position on track
     * @param trackRadius Track radius
     * @param angle Current car angle
     * @param bankingAngle Track banking angle
     * @return float[] {x, y, z, rotation} Car position and rotation angle
     */
    public float[] getPositionOnTrack(float trackRadius, float angle, float bankingAngle) {
        float[] position = new float[4];
        
        // Calculate basic car position on track (x and y coordinates)
        position[0] = (float) (trackRadius * Math.cos(angle));  // x coordinate
        position[1] = (float) (trackRadius * Math.sin(angle));  // y coordinate
        
        // Calculate z coordinate (considering track banking)
        float distanceFromCenter = trackRadius;
        position[2] = (float) (Math.sin(bankingAngle) * distanceFromCenter);
        
        // Calculate car rotation angle (to face driving direction)
        position[3] = (float) Math.toDegrees(angle) + 90;
        
        return position;
    }
}