package objects3D;

import static org.lwjgl.opengl.GL11.*;

import GraphicsObjects.Utils;
import GraphicsObjects.Vector4f;
import org.newdawn.slick.opengl.Texture;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class RaceTrack {
    
    private Sphere sphere = new Sphere();  // Add member variable
    
    public void drawTrack(float innerRadius, float outerRadius, float height, float bankingAngle, int segments, Texture trackTexture, Texture wallTexture, Texture baseTexture, Texture groundTexture, PitStopColors pitStopColors) {
        // Calculate the maximum height difference after the track is tilted
        float maxBankHeight = (float)(Math.sin(bankingAngle) * (outerRadius - innerRadius));
        
        // Adjust the base height to make the inner side level with the ground
        float baseHeight = height;
        float trackHeight = height + maxBankHeight;
        
        drawBase(innerRadius, outerRadius, baseHeight, bankingAngle, segments, baseTexture);
        drawTrackSurface(innerRadius, outerRadius, baseHeight + 0.1f, bankingAngle, segments, trackTexture);
        drawTrackWalls(innerRadius, outerRadius, baseHeight, bankingAngle, segments, null);
        drawGround(innerRadius, outerRadius, baseHeight, segments, groundTexture);
        
        // Use the color theme passed in
        drawPitStop(innerRadius, pitStopColors);
        
        // Add internal road
        drawInnerRoad(innerRadius);
    }
    
    private void drawBase(float innerRadius, float outerRadius, float height, float bankingAngle, int segments, Texture texture) {
        float incTheta = (float) ((2.0f * Math.PI) / segments);
        float maxBankHeight = (float)(Math.sin(bankingAngle) * (outerRadius - innerRadius));
        
        // Set the base material
        FloatBuffer matEmission = BufferUtils.createFloatBuffer(4);
        matEmission.put(new float[] {0.3f, 0.3f, 0.3f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_EMISSION, matEmission);
        
        FloatBuffer matAmbient = BufferUtils.createFloatBuffer(4);
        matAmbient.put(new float[] {0.6f, 0.6f, 0.6f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_AMBIENT, matAmbient);
        
        FloatBuffer matDiffuse = BufferUtils.createFloatBuffer(4);
        matDiffuse.put(new float[] {1.0f, 1.0f, 1.0f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_DIFFUSE, matDiffuse);
        
        glEnable(GL_TEXTURE_2D);
        texture.bind();
        
        // 1. Draw the top surface (the surface in contact with the track)
        glBegin(GL_QUADS);
        for (float theta = 0; theta < 2 * Math.PI; theta += incTheta) {
            // Inner circle vertex
            float x1 = (float) (innerRadius * Math.cos(theta));
            float y1 = (float) (innerRadius * Math.sin(theta));
            float z1 = 0;  // Base height is 0
            
            float x2 = (float) (innerRadius * Math.cos(theta + incTheta));
            float y2 = (float) (innerRadius * Math.sin(theta + incTheta));
            float z2 = 0;
            
            // Outer circle vertex
            float x3 = (float) (outerRadius * Math.cos(theta));
            float y3 = (float) (outerRadius * Math.sin(theta));
            float z3 = maxBankHeight;  // Outer circle is raised
            
            float x4 = (float) (outerRadius * Math.cos(theta + incTheta));
            float y4 = (float) (outerRadius * Math.sin(theta + incTheta));
            float z4 = maxBankHeight;
            
            // Calculate the normal vector
            Vector4f v1 = new Vector4f(x3 - x1, y3 - y1, z3 - z1, 0);
            Vector4f v2 = new Vector4f(x2 - x1, y2 - y1, z2 - z1, 0);
            Vector4f normal = v1.cross(v2).Normal();
            glNormal3f(normal.x, normal.y, normal.z);
            
            // Set the texture coordinates and vertices
            glTexCoord2f(theta/(2*(float)Math.PI), 0);
            glVertex3f(x1, y1, z1);
            glTexCoord2f((theta+incTheta)/(2*(float)Math.PI), 0);
            glVertex3f(x2, y2, z2);
            glTexCoord2f((theta+incTheta)/(2*(float)Math.PI), 1);
            glVertex3f(x4, y4, z4);
            glTexCoord2f(theta/(2*(float)Math.PI), 1);
            glVertex3f(x3, y3, z3);
        }
        glEnd();
        
        // 2. Draw the outer vertical surface
        glBegin(GL_QUADS);
        for (float theta = 0; theta < 2 * Math.PI; theta += incTheta) {
            float x1 = (float) (outerRadius * Math.cos(theta));
            float y1 = (float) (outerRadius * Math.sin(theta));
            float x2 = (float) (outerRadius * Math.cos(theta + incTheta));
            float y2 = (float) (outerRadius * Math.sin(theta + incTheta));
            
            // Bottom vertex
            glTexCoord2f(theta/(2*(float)Math.PI), 0);
            glVertex3f(x1, y1, -height);
            glTexCoord2f((theta+incTheta)/(2*(float)Math.PI), 0);
            glVertex3f(x2, y2, -height);
            // Top vertex
            glTexCoord2f((theta+incTheta)/(2*(float)Math.PI), 1);
            glVertex3f(x2, y2, maxBankHeight);
            glTexCoord2f(theta/(2*(float)Math.PI), 1);
            glVertex3f(x1, y1, maxBankHeight);
        }
        glEnd();
        
        // 3. Draw the inner vertical surface
        glBegin(GL_QUADS);
        for (float theta = 0; theta < 2 * Math.PI; theta += incTheta) {
            float x1 = (float) (innerRadius * Math.cos(theta));
            float y1 = (float) (innerRadius * Math.sin(theta));
            float x2 = (float) (innerRadius * Math.cos(theta + incTheta));
            float y2 = (float) (innerRadius * Math.sin(theta + incTheta));
            
            glTexCoord2f(theta/(2*(float)Math.PI), 0);
            glVertex3f(x1, y1, -height);
            glTexCoord2f((theta+incTheta)/(2*(float)Math.PI), 0);
            glVertex3f(x2, y2, -height);
            glTexCoord2f((theta+incTheta)/(2*(float)Math.PI), 1);
            glVertex3f(x2, y2, 0);
            glTexCoord2f(theta/(2*(float)Math.PI), 1);
            glVertex3f(x1, y1, 0);
        }
        glEnd();
        
        // 4. Draw the bottom surface
        glBegin(GL_QUADS);
        for (float theta = 0; theta < 2 * Math.PI; theta += incTheta) {
            float x1 = (float) (innerRadius * Math.cos(theta));
            float y1 = (float) (innerRadius * Math.sin(theta));
            float x2 = (float) (innerRadius * Math.cos(theta + incTheta));
            float y2 = (float) (innerRadius * Math.sin(theta + incTheta));
            float x3 = (float) (outerRadius * Math.cos(theta + incTheta));
            float y3 = (float) (outerRadius * Math.sin(theta + incTheta));
            float x4 = (float) (outerRadius * Math.cos(theta));
            float y4 = (float) (outerRadius * Math.sin(theta));
            
            glNormal3f(0, 0, -1);  // Bottom surface normal vector points down
            
            glTexCoord2f(theta/(2*(float)Math.PI), 0);
            glVertex3f(x1, y1, -height);
            glTexCoord2f((theta+incTheta)/(2*(float)Math.PI), 0);
            glVertex3f(x2, y2, -height);
            glTexCoord2f((theta+incTheta)/(2*(float)Math.PI), 1);
            glVertex3f(x3, y3, -height);
            glTexCoord2f(theta/(2*(float)Math.PI), 1);
            glVertex3f(x4, y4, -height);
        }
        glEnd();
        
        glDisable(GL_TEXTURE_2D);
        
        // Reset the material
        resetMaterial();
    }
    
    private void drawTrackSurface(float innerRadius, float outerRadius, float height, float bankingAngle, int segments, Texture texture) {
        float incTheta = (float) ((2.0f * Math.PI) / segments);
        
        // Set the track surface material
        FloatBuffer matEmission = BufferUtils.createFloatBuffer(4);
        matEmission.put(new float[] {0.4f, 0.4f, 0.4f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_EMISSION, matEmission);
        
        FloatBuffer matAmbient = BufferUtils.createFloatBuffer(4);
        matAmbient.put(new float[] {0.6f, 0.6f, 0.6f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_AMBIENT, matAmbient);
        
        FloatBuffer matDiffuse = BufferUtils.createFloatBuffer(4);
        matDiffuse.put(new float[] {1.0f, 1.0f, 1.0f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_DIFFUSE, matDiffuse);
        
        glEnable(GL_TEXTURE_2D);
        texture.bind();
        
        glBegin(GL_QUADS);
        for (float theta = 0; theta < 2 * Math.PI; theta += incTheta) {
            // Calculate four vertices
            // Inner circle vertex 1
            float x1 = (float) (innerRadius * Math.cos(theta));
            float y1 = (float) (innerRadius * Math.sin(theta));
            float z1 = height;  // Inner circle height remains at base height
            
            // Inner circle vertex 2
            float x2 = (float) (innerRadius * Math.cos(theta + incTheta));
            float y2 = (float) (innerRadius * Math.sin(theta + incTheta));
            float z2 = height;  // Inner circle height remains at base height
            
            // Outer circle vertex 1
            float x3 = (float) (outerRadius * Math.cos(theta));
            float y3 = (float) (outerRadius * Math.sin(theta));
            float z3 = height + (float)(Math.sin(bankingAngle) * (outerRadius - innerRadius)); // Outer circle is raised
            
            // Outer circle vertex 2
            float x4 = (float) (outerRadius * Math.cos(theta + incTheta));
            float y4 = (float) (outerRadius * Math.sin(theta + incTheta));
            float z4 = height + (float)(Math.sin(bankingAngle) * (outerRadius - innerRadius)); // Outer circle is raised
            
            // Calculate the normal vector
            Vector4f v1 = new Vector4f(x3 - x1, y3 - y1, z3 - z1, 0);
            Vector4f v2 = new Vector4f(x2 - x1, y2 - y1, z2 - z1, 0);
            Vector4f normal = v1.cross(v2).Normal();
            
            glNormal3f(normal.x, normal.y, normal.z);
            
            // Set the texture coordinates and vertices
            float texX1 = theta / (2 * (float)Math.PI);
            float texX2 = (theta + incTheta) / (2 * (float)Math.PI);
            
            glTexCoord2f(texX1, 0);
            glVertex3f(x1, y1, z1);
            glTexCoord2f(texX2, 0);
            glVertex3f(x2, y2, z2);
            glTexCoord2f(texX2, 1);
            glVertex3f(x4, y4, z4);
            glTexCoord2f(texX1, 1);
            glVertex3f(x3, y3, z3);
        }
        glEnd();
        
        glDisable(GL_TEXTURE_2D);
        
        // Reset the material
        resetMaterial();
    }
    private void drawTrackWalls(float innerRadius, float outerRadius, float height, float bankingAngle, int segments, Texture texture) {
        float incTheta = (float) ((2.0f * Math.PI) / segments);
        float wallHeight = 20.0f;
        float maxBankHeight = (float)(Math.sin(bankingAngle) * (outerRadius - innerRadius));
        
        // Disable texture and lighting
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);
        
        // Enable depth testing
        glEnable(GL_DEPTH_TEST);
        
        // Enable blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        // Draw transparent glass walls first, but do not write to depth buffer
        glDepthMask(false);
        glColor4f(0.6f, 0.8f, 1.0f, 0.15f);
        glBegin(GL_QUADS);
        for (float theta = 0; theta < 2 * Math.PI; theta += incTheta) {
            float x1 = (float) (outerRadius * Math.cos(theta));
            float y1 = (float) (outerRadius * Math.sin(theta));
            float x2 = (float) (outerRadius * Math.cos(theta + incTheta));
            float y2 = (float) (outerRadius * Math.sin(theta + incTheta));
            
            glNormal3f(-(float)Math.cos(theta), -(float)Math.sin(theta), 0);
            
            glVertex3f(x1, y1, maxBankHeight);
            glVertex3f(x2, y2, maxBankHeight);
            glVertex3f(x2, y2, maxBankHeight + wallHeight);
            glVertex3f(x1, y1, maxBankHeight + wallHeight);
        }
        glEnd();
        
        // Restore depth buffer writing for frame line drawing
        glDepthMask(true);
        
        // Set line width
        glLineWidth(2.0f);
        
        // Draw white frame - use completely opaque white
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        
        // Horizontal frame lines (spaced at regular intervals)
        int horizontalDivisions = 4;  // Number of horizontal divisions
        float heightInterval = wallHeight / horizontalDivisions;
        
        for (int h = 0; h <= horizontalDivisions; h++) {
            float currentHeight = maxBankHeight + (h * heightInterval);
            glBegin(GL_LINE_LOOP);
            for (float theta = 0; theta < 2 * Math.PI; theta += incTheta) {
                float x = (float) (outerRadius * Math.cos(theta));
                float y = (float) (outerRadius * Math.sin(theta));
                glVertex3f(x, y, currentHeight);
            }
            glEnd();
        }
        
        // Vertical supports (spaced at regular intervals)
        int verticalInterval = 4;  // Draw a support every 4 segments
        for (float theta = 0; theta < 2 * Math.PI; theta += incTheta * verticalInterval) {
            float x = (float) (outerRadius * Math.cos(theta));
            float y = (float) (outerRadius * Math.sin(theta));
            
            glBegin(GL_LINES);
            glVertex3f(x, y, maxBankHeight);
            glVertex3f(x, y, maxBankHeight + wallHeight);
            glEnd();
        }
        
        // Restore all states
        glLineWidth(1.0f);
        glEnable(GL_LIGHTING);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glDisable(GL_BLEND);
    }
    
    private void drawGround(float innerRadius, float outerRadius, float height, int segments, Texture texture) {
        float groundSize = outerRadius * 3.0f;  // Make the ground large enough to cover the entire scene
        
        // Set ground material
        FloatBuffer matEmission = BufferUtils.createFloatBuffer(4);
        matEmission.put(new float[] {0.5f, 0.5f, 0.5f, 1.0f}).flip();  // Add light
        glMaterial(GL_FRONT, GL_EMISSION, matEmission);
        
        FloatBuffer matAmbient = BufferUtils.createFloatBuffer(4);
        matAmbient.put(new float[] {0.9f, 0.9f, 0.9f, 1.0f}).flip();  // Add ambient light reflection
        glMaterial(GL_FRONT, GL_AMBIENT, matAmbient);
        
        FloatBuffer matDiffuse = BufferUtils.createFloatBuffer(4);
        matDiffuse.put(new float[] {1.0f, 1.0f, 1.0f, 1.0f}).flip();  // Keep diffuse reflection
        glMaterial(GL_FRONT, GL_DIFFUSE, matDiffuse);
        
        // Add specular reflection
        FloatBuffer matSpecular = BufferUtils.createFloatBuffer(4);
        matSpecular.put(new float[] {1.0f, 1.0f, 1.0f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_SPECULAR, matSpecular);
        
        // Set the brightness (glossiness) of the specular reflection
        glMaterialf(GL_FRONT, GL_SHININESS, 128.0f);
        
        glEnable(GL_TEXTURE_2D);
        texture.bind();
        
        // Draw a single large square as the ground
        glBegin(GL_QUADS);
        glNormal3f(0, 0, 1);  // Ground normal vector points up
        
        // Use larger texture repeat count to avoid texture stretching
        float texRepeat = 8.0f;  // Texture repeat count
        
        glTexCoord2f(0, 0);
        glVertex3f(-groundSize, -groundSize, -height);
        
        glTexCoord2f(texRepeat, 0);
        glVertex3f(groundSize, -groundSize, -height);
        
        glTexCoord2f(texRepeat, texRepeat);
        glVertex3f(groundSize, groundSize, -height);
        
        glTexCoord2f(0, texRepeat);
        glVertex3f(-groundSize, groundSize, -height);
        glEnd();
        
        glDisable(GL_TEXTURE_2D);
        
        // Reset the material
        resetMaterial();
    }
    
    public void drawLightPosts(float trackRadius, float baseHeight, float postHeight) {
        float postRadius = trackRadius * 1.2f;
        float lightSize = 20.0f;
        float adjustedBaseHeight = baseHeight + 60.0f;
        
        for (int i = 0; i < 4; i++) {
            float angle = (float)(i * Math.PI / 2);
            float x = (float)(postRadius * Math.cos(angle));
            float y = (float)(postRadius * Math.sin(angle));
            
            glPushMatrix();
            {
                glTranslatef(x, y, adjustedBaseHeight);
                
                // 1. Draw the base
                glColor3f(0.3f, 0.3f, 0.3f);
                glPushMatrix();
                {
                    glScalef(20.0f, 20.0f, 5.0f);
                    drawCylinder(16);
                }
                glPopMatrix();
                
                // 2. Draw the main pole
                glColor3f(0.4f, 0.4f, 0.4f);
                glPushMatrix();
                {
                    glTranslatef(0, 0, 2.5f);
                    glScalef(10.0f, 10.0f, postHeight - 2.5f);
                    drawCylinder(16);
                }
                glPopMatrix();
                
                // 3. Draw the top connector
                glColor3f(0.35f, 0.35f, 0.35f);
                glPushMatrix();
                {
                    glTranslatef(0, 0, postHeight - 10.0f);
                    glScalef(15.0f, 15.0f, 20.0f);
                    drawCylinder(16);
                }
                glPopMatrix();
                
                // 4. Draw the horizontal arm
                glPushMatrix();
                {
                    glTranslatef(0, 0, postHeight - 5.0f);
                    float armAngle = (float)Math.toDegrees(angle);
                    glRotatef(armAngle + 180, 0, 0, 1);
                    
                    // Main horizontal arm
                    glColor3f(0.4f, 0.4f, 0.4f);
                    glPushMatrix();
                    {
                        glRotatef(90, 0, 1, 0);
                        glScalef(6.0f, 6.0f, 60.0f);
                        drawCylinder(12);
                    }
                    glPopMatrix();
                }
                glPopMatrix();

                // 5. Draw the lamp component
                glPushMatrix();
                {
                    // Move to the lamp position
                    float lightX = -(float)(60.0f * Math.cos(angle));
                    float lightY = -(float)(60.0f * Math.sin(angle));
                    float lightZ = postHeight - 5.0f;
                    glTranslatef(lightX, lightY, lightZ);

                    // Rotate the lamp direction
                    glRotatef((float)Math.toDegrees(angle) + 90, 0, 0, 1);
                    glRotatef(60, 1, 0, 0);

                    // Main body shell
                    glColor3f(0.2f, 0.2f, 0.2f);
                    glPushMatrix();
                    {
                        glScalef(30.0f, 25.0f, 15.0f);
                        drawBox();
                    }
                    glPopMatrix();

                    // Heat sink (at the back of the shell)
                    glColor3f(0.3f, 0.3f, 0.3f);
                    for(int fin = 0; fin < 8; fin++) {
                        glPushMatrix();
                        {
                            glTranslatef(0, 0, -8.0f + fin * 2.0f);  // Start from the back of the shell
                            glScalef(35.0f, 30.0f, 0.5f);  // Slightly larger than the shell
                            drawBox();
                        }
                        glPopMatrix();
                    }

                    // Front frame (decorative border)
                    glColor3f(0.25f, 0.25f, 0.25f);
                    glPushMatrix();
                    {
                        glTranslatef(0, 0, 7.5f);
                        glScalef(32.0f, 27.0f, 1.0f);
                        drawBox();
                    }
                    glPopMatrix();

                    // Luminous part
                    glPushMatrix();
                    {
                        // Set the luminous material
                        FloatBuffer matEmission = BufferUtils.createFloatBuffer(4);
                        matEmission.put(new float[] {1.0f, 1.0f, 0.8f, 1.0f}).flip();
                        glMaterial(GL_FRONT, GL_EMISSION, matEmission);

                        glColor3f(1.0f, 1.0f, 0.8f);
                        glTranslatef(0, 0, 7.0f);
                        glScalef(28.0f, 23.0f, 1.0f);
                        drawBox();

                        resetMaterial();
                    }
                    glPopMatrix();

                    // Light source position marker (yellow ball)
                    glPushMatrix();
                    {
                        FloatBuffer matEmission = BufferUtils.createFloatBuffer(4);
                        matEmission.put(new float[] {1.0f, 1.0f, 0.0f, 1.0f}).flip();
                        glMaterial(GL_FRONT, GL_EMISSION, matEmission);

                        glColor3f(1.0f, 1.0f, 0.0f);
                        glTranslatef(0, 0, 7.0f);
                        sphere.drawSphere(3.0f, 16, 16);

                        resetMaterial();
                    }
                    glPopMatrix();
                    
                    // 6. Draw a marker sphere at the light source position
                    glPushMatrix();
                    {
                        // Set the luminous material
                        FloatBuffer matEmission = BufferUtils.createFloatBuffer(4);
                        matEmission.put(new float[] {1.0f, 1.0f, 0.0f, 1.0f}).flip();
                        glMaterial(GL_FRONT, GL_EMISSION, matEmission);
                        
                        glColor3f(1.0f, 1.0f, 0.0f);  // Yellow
                        glTranslatef(0, 0, 7.0f);  // Move to the front of the lamp
                        sphere.drawSphere(3.0f, 16, 16);  // Use the Sphere class in the project
                        
                        resetMaterial();
                    }
                    glPopMatrix();
                }
                glPopMatrix();
            }
            glPopMatrix();
        }
    }
    
    private void drawCylinder(int segments) {
        float angleIncrement = 360.0f / segments;
        
        // Draw the cylinder side
        glBegin(GL_QUAD_STRIP);
        for (int i = 0; i <= segments; i++) {
            float angle = (float)Math.toRadians(i * angleIncrement);
            float x = (float)Math.cos(angle);
            float y = (float)Math.sin(angle);
            
            glNormal3f(x, y, 0);
            glVertex3f(x, y, 1);
            glVertex3f(x, y, 0);
        }
        glEnd();
        
        // Draw the top and bottom
        glBegin(GL_TRIANGLE_FAN);
        glNormal3f(0, 0, 1);
        glVertex3f(0, 0, 1);
        for (int i = 0; i <= segments; i++) {
            float angle = (float)Math.toRadians(i * angleIncrement);
            glVertex3f((float)Math.cos(angle), (float)Math.sin(angle), 1);
        }
        glEnd();
        
        glBegin(GL_TRIANGLE_FAN);
        glNormal3f(0, 0, -1);
        glVertex3f(0, 0, 0);
        for (int i = segments; i >= 0; i--) {
            float angle = (float)Math.toRadians(i * angleIncrement);
            glVertex3f((float)Math.cos(angle), (float)Math.sin(angle), 0);
        }
        glEnd();
    }
    private void drawBox() {
        glBegin(GL_QUADS);
        {
            // Front
            glVertex3f(-0.5f, -0.5f, 0.5f);
            glVertex3f(0.5f, -0.5f, 0.5f);
            glVertex3f(0.5f, 0.5f, 0.5f);
            glVertex3f(-0.5f, 0.5f, 0.5f);
            
            // Back
            glVertex3f(-0.5f, -0.5f, -0.5f);
            glVertex3f(-0.5f, 0.5f, -0.5f);
            glVertex3f(0.5f, 0.5f, -0.5f);
            glVertex3f(0.5f, -0.5f, -0.5f);
            
            // Top
            glVertex3f(-0.5f, 0.5f, -0.5f);
            glVertex3f(-0.5f, 0.5f, 0.5f);
            glVertex3f(0.5f, 0.5f, 0.5f);
            glVertex3f(0.5f, 0.5f, -0.5f);
            
            // Bottom
            glVertex3f(-0.5f, -0.5f, -0.5f);
            glVertex3f(0.5f, -0.5f, -0.5f);
            glVertex3f(0.5f, -0.5f, 0.5f);
            glVertex3f(-0.5f, -0.5f, 0.5f);
            
            // Right
            glVertex3f(0.5f, -0.5f, -0.5f);
            glVertex3f(0.5f, 0.5f, -0.5f);
            glVertex3f(0.5f, 0.5f, 0.5f);
            glVertex3f(0.5f, -0.5f, 0.5f);
            
            // Left
            glVertex3f(-0.5f, -0.5f, -0.5f);
            glVertex3f(-0.5f, -0.5f, 0.5f);
            glVertex3f(-0.5f, 0.5f, 0.5f);
            glVertex3f(-0.5f, 0.5f, -0.5f);
        }
        glEnd();
    }
    
    // Add a helper method to reset the material
    private void resetMaterial() {
        FloatBuffer resetEmission = BufferUtils.createFloatBuffer(4);
        resetEmission.put(new float[] {0.0f, 0.0f, 0.0f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_EMISSION, resetEmission);
        
        FloatBuffer resetAmbient = BufferUtils.createFloatBuffer(4);
        resetAmbient.put(new float[] {0.2f, 0.2f, 0.2f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_AMBIENT, resetAmbient);
        
        FloatBuffer resetDiffuse = BufferUtils.createFloatBuffer(4);
        resetDiffuse.put(new float[] {0.8f, 0.8f, 0.8f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_DIFFUSE, resetDiffuse);
    }
    
    // Add a new color theme structure in the RaceTrack class
    public static class PitStopColors {
        public float[] mainColor;      // Main building color
        public float[] roofColor;      // Roof color
        public float[] serviceColor;   // Service area color
        public float[] equipmentColor; // Equipment color
        public float[] signColor;      // Sign color
        public float[] windowColor;    // Window color
        
        public PitStopColors(
            float[] mainColor,
            float[] roofColor, 
            float[] serviceColor,
            float[] equipmentColor,
            float[] signColor,
            float[] windowColor
        ) {
            this.mainColor = mainColor;
            this.roofColor = roofColor;
            this.serviceColor = serviceColor;
            this.equipmentColor = equipmentColor;
            this.signColor = signColor;
            this.windowColor = windowColor;
        }
        
        // Default red theme
        public static PitStopColors getDefaultTheme() {
            return new PitStopColors(
                new float[]{0.9f, 0.1f, 0.1f, 1.0f},  // Bright red main building
                new float[]{0.7f, 0.05f, 0.05f, 1.0f}, // Dark red roof
                new float[]{0.2f, 0.2f, 0.2f, 1.0f},   // Dark gray service area
                new float[]{0.1f, 0.1f, 0.1f, 1.0f},   // Black equipment
                new float[]{1.0f, 0.1f, 0.1f, 1.0f},   // Bright red sign
                new float[]{0.2f, 0.2f, 0.2f, 0.6f}    // Semi-transparent gray window
            );
        }
    }
    
    // Modify the drawPitStop method
    public void drawPitStop(float innerRadius, PitStopColors colors) {
        float pitStopSize = 80.0f;
        float pitStopX = 150.0f;
        float pitStopZ = 10.0f;
        float spacing = 200.0f;
        float trackRadius = 700.0f;
        
        // Define different color themes
        PitStopColors[] themes = {
            new PitStopColors(              // Red theme - Ferrari
                new float[]{0.9f, 0.1f, 0.1f, 1.0f},  // Bright red main building
                new float[]{0.7f, 0.05f, 0.05f, 1.0f}, // Dark red roof
                new float[]{0.2f, 0.2f, 0.2f, 1.0f},   // Dark gray service area
                new float[]{0.1f, 0.1f, 0.1f, 1.0f},   // Black equipment
                new float[]{1.0f, 0.1f, 0.1f, 1.0f},   // Bright red sign
                new float[]{0.2f, 0.2f, 0.2f, 0.6f}    // Semi-transparent gray window
            ),
            new PitStopColors(              // Silver theme - Mercedes
                new float[]{0.1f, 0.1f, 0.1f, 1.0f},  // Silver main building
                new float[]{0.0f, 0.0f, 0.0f, 1.0f},  // Dark silver roof
                new float[]{0.3f, 0.3f, 0.3f, 1.0f},  // Gray service area
                new float[]{0.1f, 0.1f, 0.1f, 1.0f},  // Black equipment
                new float[]{0.0f, 0.6f, 0.2f, 1.0f},  // Green sign (Mercedes logo color)
                new float[]{0.2f, 0.2f, 0.2f, 0.6f}   // Semi-transparent gray window
            ),
            new PitStopColors(              // Dark blue theme - Red Bull
                new float[]{0.3f, 0.0f, 0.0f, 1.0f},  // Dark blue main building
                new float[]{0.0f, 0.0f, 0.1f, 1.0f},  // Darker blue roof
                new float[]{0.2f, 0.2f, 0.2f, 1.0f},  // Dark gray service area
                new float[]{0.1f, 0.1f, 0.1f, 1.0f},  // Black equipment
                new float[]{0.3f, 0.0f, 0.0f, 1.0f},  // Red sign (Red Bull logo color)
                new float[]{0.2f, 0.2f, 0.2f, 0.6f}   // Semi-transparent gray window
            ),
            new PitStopColors(              // Orange theme - McLaren
                new float[]{0.8f, 0.1f, 0.0f, 1.0f},  // Orange main building
                new float[]{0.0f, 0.3f, 0.5f, 1.0f},  // Dark orange roof
                new float[]{0.2f, 0.2f, 0.2f, 1.0f},  // Dark gray service area
                new float[]{0.1f, 0.1f, 0.1f, 1.0f},  // Black equipment
                new float[]{0.8f, 0.1f, 0.0f, 1.0f},  // Blue sign (McLaren logo color)
                new float[]{0.2f, 0.2f, 0.2f, 0.6f}   // Semi-transparent gray window
            )
        };
        
        // Draw the shadow and body of each pit stop
        for (int i = 0; i < themes.length; i++) {
            float yOffset = (i - (themes.length - 1) / 2.0f) * spacing;
            
            // Calculate the nearest light position
            float lightAngle = (float)(i * Math.PI / 2);
            float lightX = (float)(trackRadius * 1.2f * Math.cos(lightAngle));
            float lightY = (float)(trackRadius * 1.2f * Math.sin(lightAngle));
            float lightZ = 400.0f;
            
            // Draw the shadow
            glPushMatrix();
            {
                glDepthMask(false);
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                glDisable(GL_LIGHTING);
                
                // Set the shadow color
                glColor4f(0.1f, 0.1f, 0.1f, 0.3f);
                
                // Calculate the shadow projection matrix
                FloatBuffer shadowMatrix = calculateShadowMatrix(
                    new float[]{lightX, lightY, lightZ, 1.0f},  // Light position
                    new float[]{0, 0, 1, 0}                     // Ground normal vector
                );
                
                // Apply the projection matrix
                glPushMatrix();
                {
                    glTranslatef(pitStopX, yOffset, 1.0f);  // Move to the pit stop position, slightly above the ground
                    glMultMatrix(shadowMatrix);
                    
                    // Draw the shadow of the pit stop
                    glRotatef(90, 0.0f, 0.0f, 1.0f);
                    drawPitStopMainBuilding(pitStopSize, themes[i]);
                    drawServiceArea(pitStopSize, themes[i]);
                    // Do not draw the shadow of the decoration to simplify
                }
                glPopMatrix();
                
                // Restore the state
                glEnable(GL_LIGHTING);
                glDisable(GL_BLEND);
                glDepthMask(true);
            }
            glPopMatrix();
            
            // Draw the body of the pit stop
            glPushMatrix();
            {
                glTranslatef(pitStopX, yOffset, pitStopZ);
                glRotatef(90, 0.0f, 0.0f, 1.0f);
                drawPitStopMainBuilding(pitStopSize, themes[i]);  // Use the corresponding theme
                drawServiceArea(pitStopSize, themes[i]);
                drawPitStopDecorations(pitStopSize, themes[i]);
            }
            glPopMatrix();
        }
    }

    // Helper method to calculate the shadow projection matrix
    private FloatBuffer calculateShadowMatrix(float[] lightPos, float[] planeNormal) {
        float dot = lightPos[0] * planeNormal[0] +
                    lightPos[1] * planeNormal[1] +
                    lightPos[2] * planeNormal[2] +
                    lightPos[3] * planeNormal[3];
        
        float[] shadowMat = new float[16];
        
        shadowMat[0] = dot - lightPos[0] * planeNormal[0];
        shadowMat[4] = -lightPos[0] * planeNormal[1];
        shadowMat[8] = -lightPos[0] * planeNormal[2];
        shadowMat[12] = -lightPos[0] * planeNormal[3];
        
        shadowMat[1] = -lightPos[1] * planeNormal[0];
        shadowMat[5] = dot - lightPos[1] * planeNormal[1];
        shadowMat[9] = -lightPos[1] * planeNormal[2];
        shadowMat[13] = -lightPos[1] * planeNormal[3];
        
        shadowMat[2] = -lightPos[2] * planeNormal[0];
        shadowMat[6] = -lightPos[2] * planeNormal[1];
        shadowMat[10] = dot - lightPos[2] * planeNormal[2];
        shadowMat[14] = -lightPos[2] * planeNormal[3];
        
        shadowMat[3] = -lightPos[3] * planeNormal[0];
        shadowMat[7] = -lightPos[3] * planeNormal[1];
        shadowMat[11] = -lightPos[3] * planeNormal[2];
        shadowMat[15] = dot - lightPos[3] * planeNormal[3];
        
        // Create a direct buffer
        FloatBuffer shadowBuffer = BufferUtils.createFloatBuffer(16);
        shadowBuffer.put(shadowMat).flip();
        return shadowBuffer;
    }

    private void drawPitStopMainBuilding(float size, PitStopColors colors) {
        // Main building
        setMaterial(colors.mainColor, 128.0f, 0.4f);
        glPushMatrix();
        {
            glScalef(size, size * 0.8f, size * 0.4f);
            drawBox();
        }
        glPopMatrix();
        
        // Roof
        setMaterial(colors.roofColor, 96.0f, 0.3f);
        glPushMatrix();
        {
            glTranslatef(0.0f, 0.0f, (size * 0.4f)-3.5f);
            glScalef(size * 1.2f, size * 0.9f, size * 0.15f);
            drawBox();
        }
        glPopMatrix();
    }

    private void drawServiceArea(float size, PitStopColors colors) {
        // Service area floor
        setMaterial(colors.serviceColor, 32.0f, 0.1f);
        glPushMatrix();
        {
            glTranslatef(0.0f, size * 0.9f, -8.0f);
            glScalef(size * 0.8f, size * 0.3f, size * 0.05f);
            drawBox();
        }
        glPopMatrix();

        // Service equipment
        setMaterial(colors.equipmentColor, 64.0f, 0.1f);
        for(int i = 0; i < 3; i++) {
            glPushMatrix();
            {
                float xOffset = (i - 1) * (size * 0.3f);
                glTranslatef(xOffset, size * 0.9f, (size * 0.1f)-8.0f);
                glScalef(size * 0.1f, size * 0.2f, size * 0.2f);
                drawBox();
            }
            glPopMatrix();
        }

        drawTireRack(size, colors);
    }

    private void drawPitStopDecorations(float size, PitStopColors colors) {
        // Sign (bright red with glowing effect)
        setMaterial(colors.signColor, 128.0f, 0.5f);
        glPushMatrix();
        {
            glTranslatef(0.0f, 0.0f, (size * 0.6f)-10.0f);
            glScalef(size * 0.8f, size * 0.2f, size * 0.1f);
            drawBox();
        }
        glPopMatrix();
        
        // Surrounding windows (semi-transparent dark gray)
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        setMaterial(colors.windowColor, 128.0f, 0.1f);
        
        float windowHeight = size * 0.2f;  // Window height
        float windowZ = size * 0.25f;      // Window z-axis position
        float wallThickness = size * 0.02f; // Window thickness
        
        // Front window
        glPushMatrix();
        {
            glTranslatef(0.0f, size * 0.4f, windowZ);
            glScalef(size, wallThickness, windowHeight);
            drawBox();
        }
        glPopMatrix();
        
        // Back window
        glPushMatrix();
        {
            glTranslatef(0.0f, -size * 0.4f, windowZ);
            glScalef(size, wallThickness, windowHeight);
            drawBox();
        }
        glPopMatrix();
        
        // Left window
        glPushMatrix();
        {
            glTranslatef(-size * 0.5f, 0.0f, windowZ);
            glScalef(wallThickness, size * 0.8f, windowHeight);
            drawBox();
        }
        glPopMatrix();
        
        // 右侧窗户
        glPushMatrix();
        {
            glTranslatef(size * 0.5f, 0.0f, windowZ);
            glScalef(wallThickness, size * 0.8f, windowHeight);
            drawBox();
        }
        glPopMatrix();
        
        // 四个角的连接处（圆角效果）
        float cornerSize = size * 0.1f;
        int segments = 16;
        for(int corner = 0; corner < 4; corner++) {
            glPushMatrix();
            {
                // 计算每个角的位置
                float xSign = (corner % 2 == 0) ? -1 : 1;
                float ySign = (corner < 2) ? 1 : -1;
                glTranslatef(xSign * (size * 0.5f - cornerSize), 
                            ySign * (size * 0.4f - cornerSize), 
                            windowZ);
                
                // 绘制圆角连接
                glBegin(GL_QUAD_STRIP);
                for(int i = 0; i <= segments; i++) {
                    float angle = (float)(i * Math.PI / 2) / segments;
                    if(corner == 0) angle += Math.PI;
                    if(corner == 1) angle += Math.PI * 1.5f;
                    if(corner == 2) angle += 0;
                    if(corner == 3) angle += Math.PI * 0.5f;
                    
                    float x = (float)Math.cos(angle) * cornerSize;
                    float y = (float)Math.sin(angle) * cornerSize;
                    
                    glVertex3f(x, y, windowHeight/2);
                    glVertex3f(x, y, -windowHeight/2);
                }
                glEnd();
            }
            glPopMatrix();
        }
        
        glDisable(GL_BLEND);
    }

    private void setMaterial(float[] color, float shininess, float emissionIntensity) {
        FloatBuffer matEmission = BufferUtils.createFloatBuffer(4);
        matEmission.put(new float[]{
            color[0] * emissionIntensity, 
            color[1] * emissionIntensity, 
            color[2] * emissionIntensity, 
            color[3]
        }).flip();
        glMaterial(GL_FRONT, GL_EMISSION, matEmission);
        
        FloatBuffer matAmbient = BufferUtils.createFloatBuffer(4);
        matAmbient.put(new float[]{
            color[0] * 0.6f, 
            color[1] * 0.6f, 
            color[2] * 0.6f, 
            color[3]
        }).flip();
        glMaterial(GL_FRONT, GL_AMBIENT, matAmbient);
        
        FloatBuffer matDiffuse = BufferUtils.createFloatBuffer(4);
        matDiffuse.put(color).flip();
        glMaterial(GL_FRONT, GL_DIFFUSE, matDiffuse);
        
        FloatBuffer matSpecular = BufferUtils.createFloatBuffer(4);
        matSpecular.put(new float[]{0.8f, 0.8f, 0.8f, color[3]}).flip();
        glMaterial(GL_FRONT, GL_SPECULAR, matSpecular);
        
        glMaterialf(GL_FRONT, GL_SHININESS, shininess);
    }

    public void drawInnerRoad(float innerRadius) {
        // 道路材质（深灰色）
        setMaterial(new float[]{0.1f, 0.1f, 0.1f, 1.0f}, 32.0f, 0.1f);
        
        float roadWidth = 80.0f;  // 道路宽度
        float roadLength = innerRadius * 2.0f;  // 道路长度，确保两端与内边缘相接
        
        glPushMatrix();
        {
            // 调整道路的高度
            glTranslatef(0.0f, 0.0f, 0.0f);  // 将道路抬高一点，避免与地面重叠
            // 调整道路的走向
            glRotatef(0.0f, 0.0f, 0.0f, 1.0f);  // 将道路旋转90度以垂直于当前赛道
            
            // 绘制主要道路
            glPushMatrix();
            {
                // 调整道路的位置，使其两端与内边缘相接
                glTranslatef(0.0f, 0, 0.0f);
                // 调整道路的长度和宽度
                glScalef(roadWidth, roadLength, 2.0f);
                drawBox();
            }
            glPopMatrix();
            
            // 绘制道路边缘的白线
            setMaterial(new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 32.0f, 0.1f);
            
            // 上边的白线
            glPushMatrix();
            {
                glTranslatef(roadWidth / 2 + 1.0f, 0, 0.5f);
                glScalef(2.0f, roadLength, 1.0f);
                drawBox();
            }
            glPopMatrix();
            
            // 下边的白线
            glPushMatrix();
            {
                glTranslatef(-roadWidth / 2 - 1.0f, 0, 0.5f);
                glScalef(2.0f, roadLength, 1.0f);
                drawBox();
            }
            glPopMatrix();
            
            // 中间的虚线
            float dashLength = 20.0f;  // 虚线长度
            float gapLength = 10.0f;   // 虚线间隔
            int numDashes = (int)(roadLength / (dashLength + gapLength));
            
            for(int i = 0; i < numDashes; i++) {
                glPushMatrix();
                {
                    float yPos = i * (dashLength + gapLength);
                    glTranslatef(0.0f, yPos-roadLength/2, 1.0f);
                    glScalef(2.0f, dashLength, 1.0f);
                    drawBox();
                }
                glPopMatrix();
            }
        }
        glPopMatrix();
    }

    private void drawTireRack(float size, PitStopColors colors) {
        // 轮胎架（使用与主建筑相配的暗色）
        float[] rackColor = new float[] {
            colors.mainColor[0] * 0.7f,
            colors.mainColor[1] * 0.7f,
            colors.mainColor[2] * 0.7f,
            colors.mainColor[3]
        };
        
        setMaterial(rackColor, 96.0f, 0.2f);
        glPushMatrix();
        {
            glTranslatef(-size * 0.8f, size * 0.9f, (size * 0.15f)-10.0f);
            glScalef(size * 0.1f, size * 0.2f, size * 0.3f);
            drawBox();
        }
        glPopMatrix();

        // 轮胎（纯黑色）
        float[] tireColor = new float[]{0.05f, 0.05f, 0.05f, 1.0f};
        setMaterial(tireColor, 16.0f, 0.0f);
        for(int i = 0; i < 3; i++) {
            glPushMatrix();
            {
                glTranslatef(-size * 0.8f, size * 0.9f, ((i * 0.1f + 0.1f) * size)-10.0f);
                glRotatef(90, 1, 0, 0);
                Cylinder tire = new Cylinder();
                tire.drawCylinder(size * 0.08f, size * 0.05f, 32);
            }
            glPopMatrix();
        }
        
        // 轮胎中心（使用与主建筑相同的颜色）
        setMaterial(colors.mainColor, 128.0f, 0.4f);
        for(int i = 0; i < 3; i++) {
            glPushMatrix();
            {
                glTranslatef(-size * 0.8f, size * 0.9f + size * 0.025f, ((i * 0.1f + 0.1f) * size)-10.0f);
                glRotatef(90, 1, 0, 0);
                Cylinder hubcap = new Cylinder();
                hubcap.drawCylinder(size * 0.02f, size * 0.05f, 16);
            }
            glPopMatrix();
        }
    }
}