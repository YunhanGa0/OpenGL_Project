package objects3D;

import static org.lwjgl.opengl.GL11.*;
import GraphicsObjects.Point4f;
import GraphicsObjects.Utils;
import GraphicsObjects.Vector4f;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;

/**
 * Class representing a humanoid figure that can be animated
 * Includes walking animations, limb movements and textured body parts
 */
public class Human {

	// Color definitions for different parts of the human model
	// Basic colors used for material properties
	static float black[] = { 0.0f, 0.0f, 0.0f, 1.0f };
	static float white[] = { 1.0f, 1.0f, 1.0f, 1.0f };

	static float grey[] = { 0.5f, 0.5f, 0.5f, 1.0f };
	static float spot[] = { 0.1f, 0.1f, 0.1f, 0.5f };

	// primary colours
	static float red[] = { 1.0f, 0.0f, 0.0f, 1.0f };
	static float green[] = { 0.0f, 1.0f, 0.0f, 1.0f };
	static float blue[] = { 0.0f, 0.0f, 1.0f, 1.0f };

	// secondary colours
	static float yellow[] = { 1.0f, 1.0f, 0.0f, 1.0f };
	static float magenta[] = { 1.0f, 0.0f, 1.0f, 1.0f };
	static float cyan[] = { 0.0f, 1.0f, 1.0f, 1.0f };

	// other colours
	static float orange[] = { 1.0f, 0.5f, 0.0f, 1.0f, 1.0f };
	static float brown[] = { 0.5f, 0.25f, 0.0f, 1.0f, 1.0f };
	static float dkgreen[] = { 0.0f, 0.5f, 0.0f, 1.0f, 1.0f };
	static float pink[] = { 1.0f, 0.6f, 0.6f, 1.0f, 1.0f };

	public Human() {

	}

	/**
	 * Main method to draw the human figure with animations
	 * @param delta Time parameter for continuous animation
	 * @param GoodAnimation Flag to switch between simple and complex animation
	 * @param faceTexture Texture for the face
	 * @param texture Texture for the body parts
	 */
	public void drawHuman(float delta, boolean GoodAnimation, Texture faceTexture, Texture texture) throws IOException {
		// Calculate the animation phase
		float theta = (float) (delta * 2 * Math.PI);
		float LimbRotation;

		// Initialize leg angles
		float leftUpperLegAngle = 90;  // Default angle
		float leftLowerLegAngle = 0;
		float rightUpperLegAngle = 90;
		float rightLowerLegAngle = 0;

		if (GoodAnimation) {
			LimbRotation = (float) Math.cos(theta) * 45;
			float TorsoRotation = (float) Math.cos(theta) * 10;
			glRotatef(TorsoRotation, 0.0f, 0.0f, 1.0f);
			float HeadRotation = (float) Math.cos(theta) * 5;
			glRotatef(HeadRotation, 0.0f, 1.0f, 0.0f);

			// 计算腿部角度
			leftUpperLegAngle = calculateLegAngle(theta, true);
			leftLowerLegAngle = calculateLegAngle(theta, false);
			rightUpperLegAngle = calculateLegAngle(theta + (float)Math.PI, true);
			rightLowerLegAngle = calculateLegAngle(theta + (float)Math.PI, false);
		} else {
			LimbRotation = 0;
		}

		Sphere sphere = new Sphere();
		Cylinder cylinder = new Cylinder();

		glPushMatrix();

		{
			glTranslatef(0.0f, 0.5f, 0.0f);
			// 第一个躯干球体
			glColor3f(white[0], white[1], white[2]);
			glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(white));
			glEnable(GL_TEXTURE_2D);
			texture.bind();
			TexSphere bodySphere1 = new TexSphere();
			bodySphere1.DrawTexSphere(0.5f, 32, 32, texture);
			glDisable(GL_TEXTURE_2D);

			// chest
			glPushMatrix();
			{
				glTranslatef(0.0f, 0.5f, 0.0f);
				// 第二个躯干球体
				glColor3f(white[0], white[1], white[2]);
				glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(white));
				glEnable(GL_TEXTURE_2D);
				texture.bind();
				TexSphere bodySphere2 = new TexSphere();
				bodySphere2.DrawTexSphere(0.55f, 32, 32, texture);
				glDisable(GL_TEXTURE_2D);

				// neck
				glColor3f(orange[0], orange[1], orange[2]);
				glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(orange));
				glPushMatrix();
				{
					glTranslatef(0.0f, 0.0f, 0.0f);
					glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
					// glRotatef(45.0f,0.0f,1.0f,0.0f);
					cylinder.drawCylinder(0.15f, 0.7f, 32);

					// head
					glColor3f(white[0], white[1], white[2]);
					glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(white));
					glPushMatrix();
					{
						glTranslatef(0.0f, 0.0f, 1.0f);
						glEnable(GL_TEXTURE_2D);
						faceTexture.bind();
						TexSphere headSphere = new TexSphere();
						headSphere.DrawTexSphere(0.5f, 32, 32, faceTexture);
						glDisable(GL_TEXTURE_2D);
						glPopMatrix();
					}
					glPopMatrix();


					// left shoulder
					glColor3f(blue[0], blue[1], blue[2]);
					glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(blue));
					glPushMatrix();
					{
						glTranslatef(0.6f, 0.4f, 0.0f);
						sphere.drawSphere(0.25f, 32, 32);

						// left arm
						glColor3f(orange[0], orange[1], orange[2]);
						glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(orange));
						glPushMatrix();
						{
							glTranslatef(0.0f, 0.0f, 0.0f);
							glRotatef(90.0f, 1.0f, 0.0f, 0.0f);

							glRotatef(LimbRotation, 1.0f, 0.0f, 0.0f);
							// glRotatef(27.5f,0.0f,1.0f,0.0f);
							cylinder.drawCylinder(0.15f, 0.7f, 32);

							// left elbow
							glColor3f(blue[0], blue[1], blue[2]);
							glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(blue));
							glPushMatrix();
							{
								glTranslatef(0.0f, 0.0f, 0.75f);
								sphere.drawSphere(0.2f, 32, 32);

								// left forearm
								glColor3f(orange[0], orange[1], orange[2]);
								glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(orange));
								glPushMatrix();
								{
									glTranslatef(0.0f, 0.0f, 0.0f);
									glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
									// glRotatef(90.0f,0.0f,1.0f,0.0f);
									cylinder.drawCylinder(0.1f, 0.7f, 32);

									// left hand
									glColor3f(blue[0], blue[1], blue[2]);
									glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(blue));
									glPushMatrix();
									{
										glTranslatef(0.0f, 0.0f, 0.75f);
										sphere.drawSphere(0.2f, 32, 32);

									}
									glPopMatrix();
								}
								glPopMatrix();
							}
							glPopMatrix();
						}
						glPopMatrix();
					}
					glPopMatrix();
					// to chest
					// right shoulder
					glColor3f(blue[0], blue[1], blue[2]);
					glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(blue));
					glPushMatrix();
					{
						glTranslatef(-0.6f, 0.4f, 0.0f);
						sphere.drawSphere(0.25f, 32, 32);

						// right arm
						glColor3f(orange[0], orange[1], orange[2]);
						glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(orange));
						glPushMatrix();
						{
							glTranslatef(0.0f, 0.0f, 0.0f);
							glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
							glRotatef(-LimbRotation, 1.0f, 0.0f, 0.0f);
							cylinder.drawCylinder(0.15f, 0.7f, 32);

							// right elbow
							glColor3f(blue[0], blue[1], blue[2]);
							glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(blue));
							glPushMatrix();
							{
								glTranslatef(0.0f, 0.0f, 0.75f);
								sphere.drawSphere(0.2f, 32, 32);

								// right forearm
								glColor3f(orange[0], orange[1], orange[2]);
								glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(orange));
								glPushMatrix();
								{
									glTranslatef(0.0f, 0.0f, 0.0f);
									glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
									cylinder.drawCylinder(0.1f, 0.7f, 32);

									// right hand
									glColor3f(blue[0], blue[1], blue[2]);
									glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(blue));
									glPushMatrix();
									{
										glTranslatef(0.0f, 0.0f, 0.75f);
										sphere.drawSphere(0.2f, 32, 32);
									}
									glPopMatrix();
								}
								glPopMatrix();
							}
							glPopMatrix();
						}
						glPopMatrix();
					}
					glPopMatrix();
					// right shoulder

					// right arm

					// right elbow

					// right forearm
					// right hand

					// chest

				}
				glPopMatrix();

				// pelvis

				// left hip
				glColor3f(blue[0], blue[1], blue[2]);
				glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(blue));
				glPushMatrix();
				{
					glTranslatef(-0.4f, -0.2f, 0.0f);

					sphere.drawSphere(0.25f, 32, 32);

					// left high leg
					glColor3f(orange[0], orange[1], orange[2]);
					glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(orange));
					glPushMatrix();
					{
						glTranslatef(0.0f, 0.0f, 0.0f);
						glRotatef(0.0f, 0.0f, 0.0f, 0.0f);

						glRotatef((LimbRotation / 2) + 90, 1.0f, 0.0f, 0.0f);
						// glRotatef(90.0f,1.0f,0.0f,0.0f);
						cylinder.drawCylinder(0.15f, 1.0f, 32);

						// left knee
						glColor3f(blue[0], blue[1], blue[2]);
						glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(blue));
						glPushMatrix();
						{
							glTranslatef(0.0f, 0.0f, 1.05f);
							glRotatef(0.0f, 0.0f, 0.0f, 0.0f);
							sphere.drawSphere(0.2f, 32, 32);

							// left low leg
							glColor3f(orange[0], orange[1], orange[2]);
							glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(orange));
							glPushMatrix();
							{
								glTranslatef(0.0f, 0.0f, 0.0f);
								glRotatef(leftLowerLegAngle, 1.0f, 0.0f, 0.0f);
								cylinder.drawCylinder(0.12f, 1.0f, 32);

								// left foot
								glColor3f(blue[0], blue[1], blue[2]);
								glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(blue));
								glPushMatrix();
								{
									glTranslatef(0.0f, 0.0f, 1.05f);
									sphere.drawSphere(0.2f, 32, 32);

								}
								glPopMatrix();
							}
							glPopMatrix();
						}
						glPopMatrix();
					}
					glPopMatrix();
				}
				glPopMatrix();

				// pelvis

				// right hip
				// right hip
				glColor3f(blue[0], blue[1], blue[2]);
				glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(blue));
				glPushMatrix();
				{
					glTranslatef(0.4f, -0.2f, 0.0f);
					sphere.drawSphere(0.25f, 32, 32);

					// right high leg
					glColor3f(orange[0], orange[1], orange[2]);
					glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(orange));
					glPushMatrix();
					{
						glTranslatef(0.0f, 0.0f, 0.0f);
						glRotatef(rightUpperLegAngle, 1.0f, 0.0f, 0.0f);
						cylinder.drawCylinder(0.15f, 1.0f, 32);

						// right knee
						glColor3f(blue[0], blue[1], blue[2]);
						glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(blue));
						glPushMatrix();
						{
							glTranslatef(0.0f, 0.0f, 1.05f);
							sphere.drawSphere(0.2f, 32, 32);

							// right low leg
							glColor3f(orange[0], orange[1], orange[2]);
							glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(orange));
							glPushMatrix();
							{
								glTranslatef(0.0f, 0.0f, 0.0f);
								glRotatef(rightLowerLegAngle, 1.0f, 0.0f, 0.0f);
								cylinder.drawCylinder(0.12f, 1.0f, 32);

								// right foot
								glColor3f(blue[0], blue[1], blue[2]);
								glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(blue));
								glPushMatrix();
								{
									glTranslatef(0.0f, 0.0f, 1.05f);
									sphere.drawSphere(0.2f, 32, 32);
								}
								glPopMatrix();
							}
							glPopMatrix();
						}
						glPopMatrix();
					}
					glPopMatrix();
				}
				glPopMatrix();

			}
			glPopMatrix();

		}

	}

	/**
	 * Calculate the rotation angles for leg animation
	 * @param theta Current animation phase
	 * @param isUpperLeg True for thigh, false for calf
	 * @return Calculated angle in degrees
	 */
	private float calculateLegAngle(float theta, boolean isUpperLeg) {
		if (isUpperLeg) {
			// Thigh swing range: 45° ~ 135°
			return (float)(Math.cos(theta) * 45f + 90);
		} else {
			// Modified calf rotation logic
			float kneeAngle = (float)Math.cos(theta);
			if (kneeAngle > 0) { // When leg is in front
				return -kneeAngle * 80; // Maintain bend during forward swing
			} else { // When leg is behind
				return 0; // Fully extended during backward swing
			}
		}
	}

	/**
	 * Calculate the rotation angles for arm animation
	 * @param theta Current animation phase
	 * @param isUpperArm True for upper arm, false for forearm
	 * @return Calculated angle in degrees
	 */
	private float calculateArmAngle(float theta, boolean isUpperArm) {
		if (isUpperArm) {
			// Upper arm swing range: -45° ~ 45°
			return (float)(Math.cos(theta) * 45);
		} else {
			// Enhanced forearm bending when upper arm is raised
			float elbowAngle = (float)Math.cos(theta);
			if (elbowAngle > 0) { // When arm is in front
				return elbowAngle * 60; // Maximum elbow bend 60 degrees
			} else { // When arm is behind
				return elbowAngle * 20; // Mostly straight with slight bend
			}
		}
	}

}

/*
 *
 *
 * }
 *
 */
