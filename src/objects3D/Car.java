package objects3D;

import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;
import GraphicsObjects.Utils;
import GraphicsObjects.Point4f;
import GraphicsObjects.Vector4f;

public class Car {
    // 基础颜色定义
    static float[] bodyColor = {0.8f, 0.0f, 0.0f, 1.0f};  // 红色车身
    static float[] wheelColor = {0.2f, 0.2f, 0.2f, 1.0f}; // 黑色轮胎
    static float[] windowColor = {0.3f, 0.3f, 0.8f, 0.5f}; // 蓝色玻璃

    private TexCube carBody;
    private Cylinder wheel;
    private TexSphere wheelCap;

    public Car() {
        carBody = new TexCube();
        wheel = new Cylinder();
        wheelCap = new TexSphere();
    }

    public void drawCar(Texture bodyTexture, float scale) {
        glPushMatrix();
        {
            // 整体缩放
            glScalef(scale, scale, scale);
            
            // 绘制车身主体
            glPushMatrix();
            {
                glScalef(2.0f, 1.0f, 0.5f);  // 车身比例
                glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(bodyColor));
                carBody.drawTexCube(bodyTexture);
            }
            glPopMatrix();

            // 绘制车顶
            glPushMatrix();
            {
                glTranslatef(0.0f, 0.0f, 0.5f);
                glScalef(1.2f, 1.0f, 0.4f);
                glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(windowColor));
                carBody.drawTexCube(bodyTexture);
            }
            glPopMatrix();

            // 绘制四个轮子
            drawWheels();
        }
        glPopMatrix();
    }

    private void drawWheels() {
        float wheelRadius = 0.3f;
        float wheelWidth = 0.2f;
        
        glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(wheelColor));
        
        // 左前轮
        drawWheel(-1.5f, 0.8f, -0.3f, wheelRadius, wheelWidth);
        // 右前轮
        drawWheel(-1.5f, -0.8f, -0.3f, wheelRadius, wheelWidth);
        // 左后轮
        drawWheel(1.5f, 0.8f, -0.3f, wheelRadius, wheelWidth);
        // 右后轮
        drawWheel(1.5f, -0.8f, -0.3f, wheelRadius, wheelWidth);
    }

    private void drawWheel(float x, float y, float z, float radius, float width) {
        glPushMatrix();
        {
            glTranslatef(x, y, z);
            glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            
            // 轮胎
            wheel.drawCylinder(radius, width, 32);
            
            // 轮毂
            glPushMatrix();
            {
                glTranslatef(0.0f, 0.0f, width/2);
                glScalef(0.8f, 0.8f, 0.1f);
                wheelCap.DrawTexSphere(radius, 16, 16, null);
            }
            glPopMatrix();
        }
        glPopMatrix();
    }

    /**
     * 获取车辆在赛道上的位置和旋转角度
     * @param trackRadius 赛道半径
     * @param angle 当前角度（弧度）
     * @param bankingAngle 赛道倾斜角度（弧度）
     * @return float数组 [x, y, z, rotationAngleZ, rotationAngleX]
     */
    public float[] getPositionOnTrack(float trackRadius, float angle, float bankingAngle) {
        float x = (float)(trackRadius * Math.cos(angle));
        float y = (float)(trackRadius * Math.sin(angle));
        float z = 0.0f;  // 保持在水平面上
        
        // 计算车身的旋转角度
        float rotationZ = (float)Math.toDegrees(angle) + 90;  // 绕Z轴旋转使车身朝向赛道切线方向
        float rotationX = (float)Math.toDegrees(bankingAngle); // 绕X轴旋转以匹配赛道倾斜
        
        return new float[]{x, y, z, rotationZ, rotationX};
    }
}