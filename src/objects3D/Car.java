package objects3D;

import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;
import GraphicsObjects.Point4f;
import GraphicsObjects.Vector4f;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class Car {
    // 基础颜色定义
    static float[] bodyColor = {1.0f, 0.0f, 0.0f, 1.0f};  // 鲜红色车身
    static float[] wheelColor = {0.1f, 0.1f, 0.1f, 1.0f}; // 深黑色轮胎
    static float[] wheelCapColor = {0.7f, 0.7f, 0.7f, 1.0f}; // 银色轮毂
    static float[] windowColor = {0.3f, 0.3f, 0.8f, 0.5f}; // 半透明蓝色玻璃
    static float[] headlightColor = {1.0f, 1.0f, 0.8f, 1.0f}; // 黄色前灯
    static float[] taillightColor = {1.0f, 0.0f, 0.0f, 1.0f}; // 红色尾灯
    static float[] bumperColor = {0.2f, 0.2f, 0.2f, 1.0f};    // 深灰色保险杠
    static float[] spoilerColor = {0.1f, 0.1f, 0.1f, 1.0f};   // 黑色尾翼

    private Cube carBody;
    private Cylinder wheel;
    private TexSphere wheelCap;
    private Cube headlight;
    private Cube taillight;
    private Cube bumper;
    private Cube spoiler;
    private float[] carColor;  // 添加颜色属性

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
            
            // 绘制车身主体
            glPushMatrix();
            {
                glScalef(2.0f, 1.0f, 0.5f);
                setMaterial(carColor, 128.0f);
                carBody.drawCube();
            }
            glPopMatrix();

            // 绘制车顶 - 降低整体位置
            glPushMatrix();
            {
                // 将z轴位置从0.6f降低到0.5f
                glTranslatef(0.0f, 0.0f, 0.5f);
                glScalef(1.2f, 0.72f, 0.32f);
                
                // 绘制前后倾斜的梯形框架
                glPushMatrix();
                {
                    // 绘制前框架（梯形）
                    glBegin(GL_QUADS);
                    {
                        // 设置材质颜色
                        setMaterial(carColor, 128.0f);
                        
                        // 前面
                        glVertex3f(-1.4f, -1.4f, 0.0f);  // 左下
                        glVertex3f(-1.4f, 1.4f, 0.0f);   // 右下
                        glVertex3f(-0.8f, 0.8f, 1.4f);   // 右上
                        glVertex3f(-0.8f, -0.8f, 1.4f);  // 左上
                        
                        // 后面
                        glVertex3f(1.4f, -1.4f, 0.0f);   // 左下
                        glVertex3f(1.4f, 1.4f, 0.0f);    // 右下
                        glVertex3f(0.8f, 0.8f, 1.4f);    // 右上
                        glVertex3f(0.8f, -0.8f, 1.4f);   // 左上
                        
                        // 顶面
                        glVertex3f(-0.8f, -0.8f, 1.4f);  // 左前
                        glVertex3f(-0.8f, 0.8f, 1.4f);   // 右前
                        glVertex3f(0.8f, 0.8f, 1.4f);    // 右后
                        glVertex3f(0.8f, -0.8f, 1.4f);   // 左后
                        
                        // 底面
                        glVertex3f(-1.4f, -1.4f, 0.0f);  // 左前
                        glVertex3f(-1.4f, 1.4f, 0.0f);   // 右前
                        glVertex3f(1.4f, 1.4f, 0.0f);    // 右后
                        glVertex3f(1.4f, -1.4f, 0.0f);   // 左后
                    }
                    glEnd();
                }
                glPopMatrix();
                
                // 绘制主车顶（透明）- 缩小尺寸
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                glPushMatrix();
                {
                    glTranslatef(0.0f, 0.0f, 0.7f);
                    glScalef(0.75f, 0.8f, 0.8f);  // 从1.6f, 1.6f, 1.8f缩小
                    setMaterial(windowColor, 128.0f);
                    carBody.drawCube();
                }
                glPopMatrix();
                
                glDisable(GL_BLEND);
            }
            glPopMatrix();

            // 添加前保险杠
            glPushMatrix();
            {
                glTranslatef(2.1f, 0.0f, -0.2f);
                glScalef(0.2f, 1.0f, 0.3f);
                setMaterial(bumperColor, 64.0f);
                bumper.drawCube();
            }
            glPopMatrix();

            // 添加后保险杠
            glPushMatrix();
            {
                glTranslatef(-2.1f, 0.0f, -0.2f);
                glScalef(0.2f, 1.0f, 0.3f);
                setMaterial(bumperColor, 64.0f);
                bumper.drawCube();
            }
            glPopMatrix();

            // 添加前车灯
            glPushMatrix();
            {
                // 前灯
                glPushMatrix();
                {
                    glTranslatef(2.0f, 0.4f, 0.0f);
                    glScalef(0.1f, 0.2f, 0.2f);
                    setMaterial(headlightColor, 128.0f);
                    headlight.drawCube();
                }
                glPopMatrix();

                // 右前灯
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

            // 添加尾灯
            glPushMatrix();
            {
                // 左尾灯
                glPushMatrix();
                {
                    glTranslatef(-2.0f, 0.4f, 0.0f);
                    glScalef(0.1f, 0.2f, 0.2f);
                    setMaterial(taillightColor, 32.0f);
                    taillight.drawCube();
                }
                glPopMatrix();

                // 右尾灯
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

            // 添加尾翼
            glPushMatrix();
            {
                glTranslatef(-2.0f, 0.0f, 0.5f);
                glScalef(0.3f, 1.2f, 0.1f);
                setMaterial(spoilerColor, 16.0f);
                spoiler.drawCube();
                
                // 尾翼支架
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

    // 辅助方法：设置材质
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
     * 计算赛车在赛道上的位置
     * @param trackRadius 赛道半径
     * @param angle 赛车当前角度
     * @param bankingAngle 赛道倾斜角度
     * @return float[] {x, y, z, rotation} 赛车的位置和旋转角度
     */
    public float[] getPositionOnTrack(float trackRadius, float angle, float bankingAngle) {
        float[] position = new float[4];
        
        // 计算赛车在赛道上的基本位置（x和y坐标）
        position[0] = (float) (trackRadius * Math.cos(angle));  // x坐标
        position[1] = (float) (trackRadius * Math.sin(angle));  // y坐标
        
        // 计算z坐标（考虑赛道倾斜）
        float distanceFromCenter = trackRadius;
        position[2] = (float) (Math.sin(bankingAngle) * distanceFromCenter);
        
        // 计算赛车的旋转角度（使其朝向行驶方向）
        position[3] = (float) Math.toDegrees(angle) + 90;
        
        return position;
    }
}