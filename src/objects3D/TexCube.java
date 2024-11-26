package objects3D;

import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;

import GraphicsObjects.Point4f;
import GraphicsObjects.Vector4f;

/**
 * Class for rendering a textured cube using OpenGL
 * Implements cube texture mapping with proper face normals
 */
public class TexCube {

	/**
	 * Draws a textured cube with unit dimensions
	 * Each face of the cube is mapped with the provided texture
	 * @param myTexture Texture to be applied to all faces of the cube
	 */
	public void drawTexCube(Texture myTexture) {
		Point4f vertices[] = {
			new Point4f(-1.0f, -1.0f, -1.0f, 0.0f),
			new Point4f(-1.0f, -1.0f, 1.0f, 0.0f),
			new Point4f(-1.0f, 1.0f, -1.0f, 0.0f),
			new Point4f(-1.0f, 1.0f, 1.0f, 0.0f),
			new Point4f(1.0f, -1.0f, -1.0f, 0.0f),
			new Point4f(1.0f, -1.0f, 1.0f, 0.0f),
			new Point4f(1.0f, 1.0f, -1.0f, 0.0f),
			new Point4f(1.0f, 1.0f, 1.0f, 0.0f)
		};

		int faces[][] = {
			{0, 4, 5, 1}, // Front
			{0, 2, 6, 4}, // Left
			{0, 1, 3, 2}, // Bottom
			{4, 6, 7, 5}, // Right
			{1, 5, 7, 3}, // Back
			{2, 3, 7, 6}  // Top
		};

		float texCoords[][] = {
			{0.0f, 0.0f},  // Bottom-left
			{1.0f, 0.0f},  // Bottom-right
			{1.0f, 1.0f},  // Top-right
			{0.0f, 1.0f}   // Top-left
		};

		glEnable(GL_TEXTURE_2D);
		myTexture.bind();

		glBegin(GL_QUADS);
		for (int face = 0; face < 6; face++) {
			Vector4f v = vertices[faces[face][1]].MinusPoint(vertices[faces[face][0]]);
			Vector4f w = vertices[faces[face][3]].MinusPoint(vertices[faces[face][0]]);
			Vector4f normal = v.cross(w).Normal();
			glNormal3f(normal.x, normal.y, normal.z);

			for (int i = 0; i < 4; i++) {
				glTexCoord2f(texCoords[i][0], texCoords[i][1]);
				glVertex3f(vertices[faces[face][i]].x, 
						  vertices[faces[face][i]].y, 
						  vertices[faces[face][i]].z);
			}
		}
		glEnd();
		
		glDisable(GL_TEXTURE_2D);
	}

}

/*
 * 
 * 
 * }
 * 
 */
