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

    private Cube carBody;
    private Cylinder wheel;
    private TexSphere wheelCap;

    public Car() {
        carBody = new Cube();
        wheel = new Cylinder();
        wheelCap = new TexSphere();
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
                
                // 创建并设置材质 - 修正 shininess 缓冲区
                FloatBuffer ambient = BufferUtils.createFloatBuffer(4);
                ambient.put(new float[]{0.3f, 0.0f, 0.0f, 1.0f}).flip();
                
                FloatBuffer diffuse = BufferUtils.createFloatBuffer(4);
                diffuse.put(bodyColor).flip();
                
                FloatBuffer specular = BufferUtils.createFloatBuffer(4);
                specular.put(new float[]{1.0f, 1.0f, 1.0f, 1.0f}).flip();
                
                // 修正：shininess 需要4个元素的缓冲区
                FloatBuffer shininess = BufferUtils.createFloatBuffer(4);
                shininess.put(new float[]{128.0f, 0.0f, 0.0f, 0.0f}).flip();
                
                glMaterial(GL_FRONT, GL_AMBIENT, ambient);
                glMaterial(GL_FRONT, GL_DIFFUSE, diffuse);
                glMaterial(GL_FRONT, GL_SPECULAR, specular);
                glMaterial(GL_FRONT, GL_SHININESS, shininess);
                
                glColor4f(bodyColor[0], bodyColor[1], bodyColor[2], bodyColor[3]);
                carBody.drawCube();
            }
            glPopMatrix();

            // 绘制车顶 - 同样修正 shininess 缓冲区
            glPushMatrix();
            {
                glTranslatef(0.0f, 0.0f, 0.5f);
                glScalef(1.2f, 1.0f, 0.4f);
                
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                
                FloatBuffer windowAmbient = BufferUtils.createFloatBuffer(4);
                windowAmbient.put(new float[]{0.1f, 0.1f, 0.3f, windowColor[3]}).flip();
                
                FloatBuffer windowDiffuse = BufferUtils.createFloatBuffer(4);
                windowDiffuse.put(windowColor).flip();
                
                FloatBuffer windowSpecular = BufferUtils.createFloatBuffer(4);
                windowSpecular.put(new float[]{1.0f, 1.0f, 1.0f, windowColor[3]}).flip();
                
                FloatBuffer windowShininess = BufferUtils.createFloatBuffer(4);
                windowShininess.put(new float[]{128.0f, 0.0f, 0.0f, 0.0f}).flip();
                
                glMaterial(GL_FRONT, GL_AMBIENT, windowAmbient);
                glMaterial(GL_FRONT, GL_DIFFUSE, windowDiffuse);
                glMaterial(GL_FRONT, GL_SPECULAR, windowSpecular);
                glMaterial(GL_FRONT, GL_SHININESS, windowShininess);
                
                glColor4f(windowColor[0], windowColor[1], windowColor[2], windowColor[3]);
                carBody.drawCube();
                glDisable(GL_BLEND);
            }
            glPopMatrix();

            drawWheels();
            
            glDisable(GL_COLOR_MATERIAL);
        }
        glPopMatrix();
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
        
        float wheelRadius = 0.3f;
        float wheelWidth = 0.2f;
        
        drawWheel(-1.5f, 0.8f, -0.3f, wheelRadius, wheelWidth);
        drawWheel(-1.5f, -0.8f, -0.3f, wheelRadius, wheelWidth);
        drawWheel(1.5f, 0.8f, -0.3f, wheelRadius, wheelWidth);
        drawWheel(1.5f, -0.8f, -0.3f, wheelRadius, wheelWidth);
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
                glScalef(0.8f, 0.8f, 0.1f);
                
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