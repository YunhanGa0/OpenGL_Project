package objects3D;

import static org.lwjgl.opengl.GL11.*;
import GraphicsObjects.Point4f;
import GraphicsObjects.Vector4f;
import java.math.*;

/**
 * Class for rendering a basic cylinder using OpenGL
 * Creates a cylinder with specified dimensions using quad and triangle primitives
 */
public class Cylinder {

	/**
	 * Draws a cylinder with specified dimensions
	 * @param radius Radius of the cylinder base
	 * @param height Height of the cylinder
	 * @param nSegments Number of segments around the circumference
	 */
	public void drawCylinder(float radius, float height, int nSegments) {
	    float x, y, z;
	    float angle;
	    
	    // Draw the side surface using quads
	    glBegin(GL_QUADS);
	    for(float i = 0; i < nSegments; i++) {
	        angle = (float) (2.0 * Math.PI * i / nSegments);
	        float nextAngle = (float) (2.0 * Math.PI * (i+1) / nSegments);
	        
	        // First vertex
	        x = (float) (radius * Math.cos(angle));
	        y = (float) (radius * Math.sin(angle));
	        glNormal3f(x/radius, y/radius, 0);
	        glVertex3f(x, y, 0);
	        
	        // Second vertex
	        x = (float) (radius * Math.cos(nextAngle));
	        y = (float) (radius * Math.sin(nextAngle));
	        glNormal3f(x/radius, y/radius, 0);
	        glVertex3f(x, y, 0);
	        
	        // Third vertex
	        glNormal3f(x/radius, y/radius, 0);
	        glVertex3f(x, y, height);
	        
	        // Fourth vertex
	        x = (float) (radius * Math.cos(angle));
	        y = (float) (radius * Math.sin(angle));
	        glNormal3f(x/radius, y/radius, 0);
	        glVertex3f(x, y, height);
	    }
	    glEnd();
	    
	    // Draw top and bottom faces using triangle fans
	    for(int i = 0; i < 2; i++) {
	        glBegin(GL_TRIANGLE_FAN);
	        z = (i == 0) ? 0 : height;
	        glNormal3f(0, 0, (i == 0) ? -1 : 1);
	        glVertex3f(0, 0, z);
	        
	        for(float j = 0; j <= nSegments; j++) {
	            angle = (float) (2.0 * Math.PI * j / nSegments);
	            x = (float) (radius * Math.cos(angle));
	            y = (float) (radius * Math.sin(angle));
	            glVertex3f(x, y, z);
	        }
	        glEnd();
	    }
	}
}
