package objects3D;

import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;

/**
 * Class for rendering a textured cylinder using OpenGL
 * Handles the creation of a cylinder with texture mapping
 */
public class TexCylinder {
    public TexCylinder() {
    }

    /**
     * Draws a textured cylinder with specified dimensions
     * @param radius Radius of the cylinder
     * @param height Height of the cylinder
     * @param nSegments Number of segments around the circumference
     * @param myTexture Texture to be applied to the cylinder surface
     */
    public void drawTexCylinder(float radius, float height, int nSegments, Texture myTexture) {
        float x, y, z;
        float s, t; // Texture coordinates
        
        float inctheta = (float) ((2.0f * Math.PI) / nSegments);
        
        // Draw the side surface
        glBegin(GL_QUADS);
        for (float theta = (float) -Math.PI; theta < Math.PI; theta += inctheta) {
            // Calculate texture coordinates
            s = (float)(theta / (2.0f * Math.PI)) + 0.5f;
            
            // Bottom vertex
            x = (float) (Math.cos(theta) * radius);
            y = (float) (Math.sin(theta) * radius);
            z = 0.0f;
            glTexCoord2f(s, 0.0f);
            glNormal3f(x/radius, y/radius, 0.0f);
            glVertex3f(x, y, z);
            
            x = (float) (Math.cos(theta + inctheta) * radius);
            y = (float) (Math.sin(theta + inctheta) * radius);
            z = 0.0f;
            glTexCoord2f(s + 1.0f/nSegments, 0.0f);
            glNormal3f(x/radius, y/radius, 0.0f);
            glVertex3f(x, y, z);
            
            // Top vertex
            x = (float) (Math.cos(theta + inctheta) * radius);
            y = (float) (Math.sin(theta + inctheta) * radius);
            z = height;
            glTexCoord2f(s + 1.0f/nSegments, 1.0f);
            glNormal3f(x/radius, y/radius, 0.0f);
            glVertex3f(x, y, z);
            
            x = (float) (Math.cos(theta) * radius);
            y = (float) (Math.sin(theta) * radius);
            z = height;
            glTexCoord2f(s, 1.0f);
            glNormal3f(x/radius, y/radius, 0.0f);
            glVertex3f(x, y, z);
        }
        glEnd();
    }
} 