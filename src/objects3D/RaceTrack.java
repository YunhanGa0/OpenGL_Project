package objects3D;

import static org.lwjgl.opengl.GL11.*;

import GraphicsObjects.Utils;
import GraphicsObjects.Vector4f;
import org.newdawn.slick.opengl.Texture;

public class RaceTrack {
    
    public void drawTrack(float innerRadius, float outerRadius, float height, float bankingAngle, int segments, Texture trackTexture, Texture wallTexture, Texture baseTexture) {
        // 计算赛道倾斜后的最大高度差
        float maxBankHeight = (float)(Math.sin(bankingAngle) * (outerRadius - innerRadius));
        
        // 调整基准高度，使内侧与地面齐平
        float baseHeight = height;
        float trackHeight = height + maxBankHeight;
        
        drawBase(innerRadius, outerRadius, baseHeight, bankingAngle, segments, baseTexture);
        drawTrackSurface(innerRadius, outerRadius, baseHeight, bankingAngle, segments, trackTexture);
        drawTrackWalls(innerRadius, outerRadius, baseHeight, bankingAngle, segments, wallTexture);
    }
    
    private void drawBase(float innerRadius, float outerRadius, float height, float bankingAngle, int segments, Texture texture) {
        float incTheta = (float) ((2.0f * Math.PI) / segments);
        float maxBankHeight = (float)(Math.sin(bankingAngle) * (outerRadius - innerRadius));
        
        glEnable(GL_TEXTURE_2D);
        texture.bind();
        
        // 1. 绘制顶面（与赛道接触的面）
        glBegin(GL_QUADS);
        for (float theta = 0; theta < 2 * Math.PI; theta += incTheta) {
            // 内圈顶点
            float x1 = (float) (innerRadius * Math.cos(theta));
            float y1 = (float) (innerRadius * Math.sin(theta));
            float z1 = 0;  // 基准高度为0
            
            float x2 = (float) (innerRadius * Math.cos(theta + incTheta));
            float y2 = (float) (innerRadius * Math.sin(theta + incTheta));
            float z2 = 0;
            
            // 外圈顶点
            float x3 = (float) (outerRadius * Math.cos(theta));
            float y3 = (float) (outerRadius * Math.sin(theta));
            float z3 = maxBankHeight;  // 外圈升高
            
            float x4 = (float) (outerRadius * Math.cos(theta + incTheta));
            float y4 = (float) (outerRadius * Math.sin(theta + incTheta));
            float z4 = maxBankHeight;
            
            // 计算法向量
            Vector4f v1 = new Vector4f(x3 - x1, y3 - y1, z3 - z1, 0);
            Vector4f v2 = new Vector4f(x2 - x1, y2 - y1, z2 - z1, 0);
            Vector4f normal = v1.cross(v2).Normal();
            glNormal3f(normal.x, normal.y, normal.z);
            
            // 设置纹理坐标和顶点
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
        
        // 2. 绘制外侧垂直面
        glBegin(GL_QUADS);
        for (float theta = 0; theta < 2 * Math.PI; theta += incTheta) {
            float x1 = (float) (outerRadius * Math.cos(theta));
            float y1 = (float) (outerRadius * Math.sin(theta));
            float x2 = (float) (outerRadius * Math.cos(theta + incTheta));
            float y2 = (float) (outerRadius * Math.sin(theta + incTheta));
            
            // 底部顶点
            glTexCoord2f(theta/(2*(float)Math.PI), 0);
            glVertex3f(x1, y1, -height);
            glTexCoord2f((theta+incTheta)/(2*(float)Math.PI), 0);
            glVertex3f(x2, y2, -height);
            // 顶部顶点
            glTexCoord2f((theta+incTheta)/(2*(float)Math.PI), 1);
            glVertex3f(x2, y2, maxBankHeight);
            glTexCoord2f(theta/(2*(float)Math.PI), 1);
            glVertex3f(x1, y1, maxBankHeight);
        }
        glEnd();
        
        // 3. 绘制内侧垂直面
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
        
        // 4. 绘制底面
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
            
            glNormal3f(0, 0, -1);  // 底面法向量朝下
            
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
    }
    
    private void drawTrackSurface(float innerRadius, float outerRadius, float height, float bankingAngle, int segments, Texture texture) {
        float incTheta = (float) ((2.0f * Math.PI) / segments);
        
        glEnable(GL_TEXTURE_2D);
        texture.bind();
        
        glBegin(GL_QUADS);
        for (float theta = 0; theta < 2 * Math.PI; theta += incTheta) {
            // 计算四个顶点
            // 内圈顶点1
            float x1 = (float) (innerRadius * Math.cos(theta));
            float y1 = (float) (innerRadius * Math.sin(theta));
            float z1 = height;  // 内圈高度保持为基准高度
            
            // 内圈顶点2
            float x2 = (float) (innerRadius * Math.cos(theta + incTheta));
            float y2 = (float) (innerRadius * Math.sin(theta + incTheta));
            float z2 = height;  // 内圈高度保持为基准高度
            
            // 外圈顶点1
            float x3 = (float) (outerRadius * Math.cos(theta));
            float y3 = (float) (outerRadius * Math.sin(theta));
            float z3 = height + (float)(Math.sin(bankingAngle) * (outerRadius - innerRadius)); // 外圈升高
            
            // 外圈顶点2
            float x4 = (float) (outerRadius * Math.cos(theta + incTheta));
            float y4 = (float) (outerRadius * Math.sin(theta + incTheta));
            float z4 = height + (float)(Math.sin(bankingAngle) * (outerRadius - innerRadius)); // 外圈升高
            
            // 计算法向量
            Vector4f v1 = new Vector4f(x3 - x1, y3 - y1, z3 - z1, 0);
            Vector4f v2 = new Vector4f(x2 - x1, y2 - y1, z2 - z1, 0);
            Vector4f normal = v1.cross(v2).Normal();
            
            glNormal3f(normal.x, normal.y, normal.z);
            
            // 设置纹理坐标和顶点
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
    }
    
    private void drawTrackWalls(float innerRadius, float outerRadius, float height, float bankingAngle, int segments, Texture texture) {
        float incTheta = (float) ((2.0f * Math.PI) / segments);
        float wallHeight = 10.0f;  // 墙的高度
        float maxBankHeight = (float)(Math.sin(bankingAngle) * (outerRadius - innerRadius));
        
        glEnable(GL_TEXTURE_2D);
        texture.bind();
        
        // 绘制外侧墙
        glBegin(GL_QUADS);
        for (float theta = 0; theta < 2 * Math.PI; theta += incTheta) {
            float x1 = (float) (outerRadius * Math.cos(theta));
            float y1 = (float) (outerRadius * Math.sin(theta));
            float x2 = (float) (outerRadius * Math.cos(theta + incTheta));
            float y2 = (float) (outerRadius * Math.sin(theta + incTheta));
            
            // 墙面法向量
            float nx = -(float)Math.cos(theta);
            float ny = -(float)Math.sin(theta);
            glNormal3f(nx, ny, 0);
            
            // 底部顶点
            glTexCoord2f(theta/(2*(float)Math.PI), 0);
            glVertex3f(x1, y1, maxBankHeight);
            glTexCoord2f((theta+incTheta)/(2*(float)Math.PI), 0);
            glVertex3f(x2, y2, maxBankHeight);
            
            // 顶部顶点
            glTexCoord2f((theta+incTheta)/(2*(float)Math.PI), 1);
            glVertex3f(x2, y2, maxBankHeight + wallHeight);
            glTexCoord2f(theta/(2*(float)Math.PI), 1);
            glVertex3f(x1, y1, maxBankHeight + wallHeight);
        }
        glEnd();
        
        glDisable(GL_TEXTURE_2D);
    }
}