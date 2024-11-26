package objects3D;

import static org.lwjgl.opengl.GL11.*;

/**
 * Class for rendering a basic sphere using OpenGL
 * Creates a sphere using spherical coordinates and quad primitives
 */
public class Sphere {

	public Sphere() {

	}

	// Implement using notes and examine Tetrahedron to aid in the coding look at
	// lecture 7 , 7b and 8
	// 7b should be your primary source, we will cover more about circles in later
	// lectures to understand why the code works

	/**
	 * Draws a sphere with specified dimensions
	 * Uses spherical coordinates to generate vertices
	 * @param radius Radius of the sphere
	 * @param nSlices Number of horizontal slices
	 * @param nSegments Number of vertical segments
	 */
	public void drawSphere(float radius, float nSlices, float nSegments) {
		float x, y, z;
		float inctheta = (float) ((2.0f * Math.PI) / nSlices);
		float incphi = (float) (Math.PI / nSegments);
		
		glBegin(GL_QUADS);
		for(float theta = (float) -Math.PI; theta < Math.PI; theta += inctheta) {
			for(float phi = (float) -(Math.PI/2.0f); phi < (Math.PI/2.0f); phi += incphi) {
				x = (float) (Math.cos(phi) * Math.cos(theta) * radius);
				y = (float) (Math.cos(phi) * Math.sin(theta) * radius);
				z = (float) (Math.sin(phi) * radius);
				
				glNormal3f(x/radius, y/radius, z/radius);
				glVertex3f(x, y, z);
				
				x = (float) (Math.cos(phi) * Math.cos(theta + inctheta) * radius);
				y = (float) (Math.cos(phi) * Math.sin(theta + inctheta) * radius);
				z = (float) (Math.sin(phi) * radius);
				glNormal3f(x/radius, y/radius, z/radius);
				glVertex3f(x, y, z);
				
				x = (float) (Math.cos(phi + incphi) * Math.cos(theta + inctheta) * radius);
				y = (float) (Math.cos(phi + incphi) * Math.sin(theta + inctheta) * radius);
				z = (float) (Math.sin(phi + incphi) * radius);
				glNormal3f(x/radius, y/radius, z/radius);
				glVertex3f(x, y, z);
				
				x = (float) (Math.cos(phi + incphi) * Math.cos(theta) * radius);
				y = (float) (Math.cos(phi + incphi) * Math.sin(theta) * radius);
				z = (float) (Math.sin(phi + incphi) * radius);
				glNormal3f(x/radius, y/radius, z/radius);
				glVertex3f(x, y, z);
			}
		}
		glEnd();
	}
}
