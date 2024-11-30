package objects3D;

import static org.lwjgl.opengl.GL11.*;

import GraphicsObjects.Utils;
import org.newdawn.slick.opengl.Texture;

public class RaceTrack {
    
    public void drawTrack(float innerRadius, float outerRadius, float height, float bankingAngle, int segments, Texture trackTexture, Texture wallTexture, Texture baseTexture) {
        drawBase(innerRadius, outerRadius, height, bankingAngle, segments, baseTexture);
        drawTrackSurface(innerRadius, outerRadius, height, bankingAngle, segments, trackTexture);
        drawTrackWalls(innerRadius, outerRadius, height, bankingAngle, segments, wallTexture);
    }
    
    private void drawBase(float innerRadius, float outerRadius, float height, float bankingAngle, int segments, Texture texture) {
        float baseHeight = 0.0f;
        float maxBankZ = (float) (Math.sin(bankingAngle) * outerRadius);
        float incTheta = (float) ((2.0f * Math.PI) / segments);
        
        float[] baseColor = {1.0f, 1.0f, 1.0f, 1.0f};
        glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(baseColor));
        
        glEnable(GL_TEXTURE_2D);
        texture.bind();
        
        // 外侧底座墙
        glBegin(GL_QUADS);
        for (float theta = 0; theta < 2 * Math.PI; theta += incTheta) {
            float texX1 = theta / (2 * (float)Math.PI);
            float texX2 = (theta + incTheta) / (2 * (float)Math.PI);
            
            float x1 = (float) (outerRadius * Math.cos(theta));
            float y1 = (float) (outerRadius * Math.sin(theta));
            float bankZ1 = (float) (Math.sin(bankingAngle) * outerRadius);
            
            float x2 = (float) (outerRadius * Math.cos(theta + incTheta));
            float y2 = (float) (outerRadius * Math.sin(theta + incTheta));
            
            glTexCoord2f(texX1, 0);
            glVertex3f(x1, y1, baseHeight);
            glTexCoord2f(texX2, 0);
            glVertex3f(x2, y2, baseHeight);
            glTexCoord2f(texX2, 1);
            glVertex3f(x2, y2, height + bankZ1);
            glTexCoord2f(texX1, 1);
            glVertex3f(x1, y1, height + bankZ1);
        }
        glEnd();
        
        // 内侧底座墙和底面的代码类似...
        
        glDisable(GL_TEXTURE_2D);
    }
    
    private void drawTrackSurface(float innerRadius, float outerRadius, float height, float bankingAngle, int segments, Texture texture) {
        float incTheta = (float) ((2.0f * Math.PI) / segments);
        
        glEnable(GL_TEXTURE_2D);
        texture.bind();
        
        glBegin(GL_QUADS);
        for (float theta = 0; theta < 2 * Math.PI; theta += incTheta) {
            // 计算纹理坐标
            float texX1 = theta / (2 * (float)Math.PI);
            float texX2 = (theta + incTheta) / (2 * (float)Math.PI);
            
            // 外圈顶点
            float x1 = (float) (outerRadius * Math.cos(theta));
            float y1 = (float) (outerRadius * Math.sin(theta));
            float bankZ1 = (float) (Math.sin(bankingAngle) * outerRadius);
            float normalZ1 = (float) Math.sin(bankingAngle);
            
            glNormal3f((float) -Math.cos(theta), (float) -Math.sin(theta), normalZ1);
            glTexCoord2f(texX1, 1.0f);
            glVertex3f(x1, y1, height + bankZ1);
            
            // 外圈下一个顶点
            float x2 = (float) (outerRadius * Math.cos(theta + incTheta));
            float y2 = (float) (outerRadius * Math.sin(theta + incTheta));
            glTexCoord2f(texX2, 1.0f);
            glVertex3f(x2, y2, height + bankZ1);
            
            // 内圈下一个顶点
            float x3 = (float) (innerRadius * Math.cos(theta + incTheta));
            float y3 = (float) (innerRadius * Math.sin(theta + incTheta));
            float bankZ2 = (float) (Math.sin(bankingAngle) * innerRadius);
            glTexCoord2f(texX2, 0.0f);
            glVertex3f(x3, y3, height + bankZ2);
            
            // 内圈顶点
            float x4 = (float) (innerRadius * Math.cos(theta));
            float y4 = (float) (innerRadius * Math.sin(theta));
            glTexCoord2f(texX1, 0.0f);
            glVertex3f(x4, y4, height + bankZ2);
        }
        glEnd();
        glDisable(GL_TEXTURE_2D);
    }
    
    private void drawTrackWalls(float innerRadius, float outerRadius, float height, float bankingAngle, int segments, Texture texture) {
        float wallHeight = 50.0f;
        float incTheta = (float) ((2.0f * Math.PI) / segments);
        
        // 设置墙体材质
        float[] wallColor = {0.8f, 0.8f, 0.8f, 1.0f};  // 亮灰色
        glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(wallColor));
        
        glEnable(GL_TEXTURE_2D);
        texture.bind();
        
        // 外墙
        glBegin(GL_QUADS);
        for (float theta = 0; theta < 2 * Math.PI; theta += incTheta) {
            float texX1 = theta / (2 * (float)Math.PI);
            float texX2 = (theta + incTheta) / (2 * (float)Math.PI);
            
            float x1 = (float) (outerRadius * Math.cos(theta));
            float y1 = (float) (outerRadius * Math.sin(theta));
            float bankZ = (float) (Math.sin(bankingAngle) * outerRadius);
            
            float x2 = (float) (outerRadius * Math.cos(theta + incTheta));
            float y2 = (float) (outerRadius * Math.sin(theta + incTheta));
            
            // 墙面法向量
            float nx = (float)Math.cos(theta);
            float ny = (float)Math.sin(theta);
            glNormal3f(nx, ny, 0);
            
            glTexCoord2f(texX1, 0);
            glVertex3f(x1, y1, height + bankZ);
            glTexCoord2f(texX2, 0);
            glVertex3f(x2, y2, height + bankZ);
            glTexCoord2f(texX2, 1);
            glVertex3f(x2, y2, height + bankZ + wallHeight);
            glTexCoord2f(texX1, 1);
            glVertex3f(x1, y1, height + bankZ + wallHeight);
        }
        glEnd();
        
        // 内墙
        /* glBegin(GL_QUADS);
        for (float theta = 0; theta < 2 * Math.PI; theta += incTheta) {
            float texX1 = theta / (2 * (float)Math.PI);
            float texX2 = (theta + incTheta) / (2 * (float)Math.PI);
            
            float x1 = (float) (innerRadius * Math.cos(theta));
            float y1 = (float) (innerRadius * Math.sin(theta));
            float bankZ = (float) (Math.sin(bankingAngle) * innerRadius);
            
            float x2 = (float) (innerRadius * Math.cos(theta + incTheta));
            float y2 = (float) (innerRadius * Math.sin(theta + incTheta));
            
            // 墙面法向量
            float nx = -(float)Math.cos(theta);
            float ny = -(float)Math.sin(theta);
            glNormal3f(nx, ny, 0);
            
            glTexCoord2f(texX1, 0);
            glVertex3f(x1, y1, height + bankZ);
            glTexCoord2f(texX2, 0);
            glVertex3f(x2, y2, height + bankZ);
            glTexCoord2f(texX2, 1);
            glVertex3f(x2, y2, height + bankZ + wallHeight);
            glTexCoord2f(texX1, 1);
            glVertex3f(x1, y1, height + bankZ + wallHeight);
        }
        glEnd();

         */
        
        glDisable(GL_TEXTURE_2D);
    }
}