package objects3D;

import static org.lwjgl.opengl.GL11.*;

import GraphicsObjects.Utils;
import GraphicsObjects.Vector4f;
import org.newdawn.slick.opengl.Texture;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class RaceTrack {
    
    private Sphere sphere = new Sphere();  // 添加成员变量
    
    public void drawTrack(float innerRadius, float outerRadius, float height, float bankingAngle, int segments, Texture trackTexture, Texture wallTexture, Texture baseTexture, Texture groundTexture) {
        // 计算赛道倾斜后的最大高度差
        float maxBankHeight = (float)(Math.sin(bankingAngle) * (outerRadius - innerRadius));
        
        // 调整基准高度，使内侧与地面齐平
        float baseHeight = height;
        float trackHeight = height + maxBankHeight;
        
        drawBase(innerRadius, outerRadius, baseHeight, bankingAngle, segments, baseTexture);
        drawTrackSurface(innerRadius, outerRadius, baseHeight + 0.1f, bankingAngle, segments, trackTexture);
        drawTrackWalls(innerRadius, outerRadius, baseHeight, bankingAngle, segments, null);
        drawGround(innerRadius, outerRadius, baseHeight, segments, groundTexture);
    }
    
    private void drawBase(float innerRadius, float outerRadius, float height, float bankingAngle, int segments, Texture texture) {
        float incTheta = (float) ((2.0f * Math.PI) / segments);
        float maxBankHeight = (float)(Math.sin(bankingAngle) * (outerRadius - innerRadius));
        
        // 设置基座材质
        FloatBuffer matEmission = BufferUtils.createFloatBuffer(4);
        matEmission.put(new float[] {0.3f, 0.3f, 0.3f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_EMISSION, matEmission);
        
        FloatBuffer matAmbient = BufferUtils.createFloatBuffer(4);
        matAmbient.put(new float[] {0.6f, 0.6f, 0.6f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_AMBIENT, matAmbient);
        
        FloatBuffer matDiffuse = BufferUtils.createFloatBuffer(4);
        matDiffuse.put(new float[] {1.0f, 1.0f, 1.0f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_DIFFUSE, matDiffuse);
        
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
        
        // 重置材质
        resetMaterial();
    }
    
    private void drawTrackSurface(float innerRadius, float outerRadius, float height, float bankingAngle, int segments, Texture texture) {
        float incTheta = (float) ((2.0f * Math.PI) / segments);
        
        // 设置赛道表面材质
        FloatBuffer matEmission = BufferUtils.createFloatBuffer(4);
        matEmission.put(new float[] {0.4f, 0.4f, 0.4f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_EMISSION, matEmission);
        
        FloatBuffer matAmbient = BufferUtils.createFloatBuffer(4);
        matAmbient.put(new float[] {0.6f, 0.6f, 0.6f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_AMBIENT, matAmbient);
        
        FloatBuffer matDiffuse = BufferUtils.createFloatBuffer(4);
        matDiffuse.put(new float[] {1.0f, 1.0f, 1.0f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_DIFFUSE, matDiffuse);
        
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
        
        // 重置材质
        resetMaterial();
    }
    
    private void drawTrackWalls(float innerRadius, float outerRadius, float height, float bankingAngle, int segments, Texture texture) {
        float incTheta = (float) ((2.0f * Math.PI) / segments);
        float wallHeight = 20.0f;
        float maxBankHeight = (float)(Math.sin(bankingAngle) * (outerRadius - innerRadius));
        
        // 禁用纹理和光照
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);
        
        // 启用深度测试
        glEnable(GL_DEPTH_TEST);
        
        // 启用混合
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        // 先绘制透明玻璃墙，但不写入深度缓冲
        glDepthMask(false);
        glColor4f(0.6f, 0.8f, 1.0f, 0.15f);
        glBegin(GL_QUADS);
        for (float theta = 0; theta < 2 * Math.PI; theta += incTheta) {
            float x1 = (float) (outerRadius * Math.cos(theta));
            float y1 = (float) (outerRadius * Math.sin(theta));
            float x2 = (float) (outerRadius * Math.cos(theta + incTheta));
            float y2 = (float) (outerRadius * Math.sin(theta + incTheta));
            
            glNormal3f(-(float)Math.cos(theta), -(float)Math.sin(theta), 0);
            
            glVertex3f(x1, y1, maxBankHeight);
            glVertex3f(x2, y2, maxBankHeight);
            glVertex3f(x2, y2, maxBankHeight + wallHeight);
            glVertex3f(x1, y1, maxBankHeight + wallHeight);
        }
        glEnd();
        
        // 恢复深度缓冲写入，为框架线条绘制
        glDepthMask(true);
        
        // 设置线条宽度
        glLineWidth(2.0f);
        
        // 绘制白色框架 - 使用完全不透明的白色
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        
        // 水平框架线（每隔一定高度）
        int horizontalDivisions = 4;  // 水平分段数
        float heightInterval = wallHeight / horizontalDivisions;
        
        for (int h = 0; h <= horizontalDivisions; h++) {
            float currentHeight = maxBankHeight + (h * heightInterval);
            glBegin(GL_LINE_LOOP);
            for (float theta = 0; theta < 2 * Math.PI; theta += incTheta) {
                float x = (float) (outerRadius * Math.cos(theta));
                float y = (float) (outerRadius * Math.sin(theta));
                glVertex3f(x, y, currentHeight);
            }
            glEnd();
        }
        
        // 垂直支柱（每隔几个段落）
        int verticalInterval = 4;  // 每隔4个段落绘制一个支柱
        for (float theta = 0; theta < 2 * Math.PI; theta += incTheta * verticalInterval) {
            float x = (float) (outerRadius * Math.cos(theta));
            float y = (float) (outerRadius * Math.sin(theta));
            
            glBegin(GL_LINES);
            glVertex3f(x, y, maxBankHeight);
            glVertex3f(x, y, maxBankHeight + wallHeight);
            glEnd();
        }
        
        // 恢复所有状态
        glLineWidth(1.0f);
        glEnable(GL_LIGHTING);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glDisable(GL_BLEND);
    }
    
    private void drawGround(float innerRadius, float outerRadius, float height, int segments, Texture texture) {
        float groundSize = outerRadius * 3.0f;  // 使地面足够大以覆盖整个场景
        
        // 设置地面材质
        FloatBuffer matEmission = BufferUtils.createFloatBuffer(4);
        matEmission.put(new float[] {0.3f, 0.3f, 0.3f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_EMISSION, matEmission);
        
        FloatBuffer matAmbient = BufferUtils.createFloatBuffer(4);
        matAmbient.put(new float[] {0.6f, 0.6f, 0.6f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_AMBIENT, matAmbient);
        
        FloatBuffer matDiffuse = BufferUtils.createFloatBuffer(4);
        matDiffuse.put(new float[] {1.0f, 1.0f, 1.0f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_DIFFUSE, matDiffuse);
        
        glEnable(GL_TEXTURE_2D);
        texture.bind();
        
        // 绘制单个大方形作为地面
        glBegin(GL_QUADS);
        glNormal3f(0, 0, 1);  // 地面法向量朝上
        
        // 使用更大的纹理重复次数来避免纹理拉伸
        float texRepeat = 8.0f;  // 纹理重复次数
        
        glTexCoord2f(0, 0);
        glVertex3f(-groundSize, -groundSize, -height);
        
        glTexCoord2f(texRepeat, 0);
        glVertex3f(groundSize, -groundSize, -height);
        
        glTexCoord2f(texRepeat, texRepeat);
        glVertex3f(groundSize, groundSize, -height);
        
        glTexCoord2f(0, texRepeat);
        glVertex3f(-groundSize, groundSize, -height);
        glEnd();
        
        glDisable(GL_TEXTURE_2D);
        
        // 重置材质
        resetMaterial();
    }
    
    public void drawLightPosts(float trackRadius, float baseHeight, float postHeight) {
        float postRadius = trackRadius * 1.2f;
        float lightSize = 20.0f;
        float adjustedBaseHeight = baseHeight + 60.0f;
        
        for (int i = 0; i < 4; i++) {
            float angle = (float)(i * Math.PI / 2);
            float x = (float)(postRadius * Math.cos(angle));
            float y = (float)(postRadius * Math.sin(angle));
            
            glPushMatrix();
            {
                glTranslatef(x, y, adjustedBaseHeight);
                
                // 1. 绘制底座
                glColor3f(0.3f, 0.3f, 0.3f);
                glPushMatrix();
                {
                    glScalef(20.0f, 20.0f, 5.0f);
                    drawCylinder(16);
                }
                glPopMatrix();
                
                // 2. 绘制主杆
                glColor3f(0.4f, 0.4f, 0.4f);
                glPushMatrix();
                {
                    glTranslatef(0, 0, 2.5f);
                    glScalef(10.0f, 10.0f, postHeight - 2.5f);
                    drawCylinder(16);
                }
                glPopMatrix();
                
                // 3. 绘制顶部连接件
                glColor3f(0.35f, 0.35f, 0.35f);
                glPushMatrix();
                {
                    glTranslatef(0, 0, postHeight - 10.0f);
                    glScalef(15.0f, 15.0f, 20.0f);
                    drawCylinder(16);
                }
                glPopMatrix();
                
                // 4. 绘制横臂
                glPushMatrix();
                {
                    glTranslatef(0, 0, postHeight - 5.0f);
                    float armAngle = (float)Math.toDegrees(angle);
                    glRotatef(armAngle + 180, 0, 0, 1);
                    
                    // 主横臂
                    glColor3f(0.4f, 0.4f, 0.4f);
                    glPushMatrix();
                    {
                        glRotatef(90, 0, 1, 0);
                        glScalef(6.0f, 6.0f, 60.0f);
                        drawCylinder(12);
                    }
                    glPopMatrix();
                }
                glPopMatrix();

                // 5. 绘制灯具组件
                glPushMatrix();
                {
                    // 移动到灯具位置
                    float lightX = -(float)(60.0f * Math.cos(angle));
                    float lightY = -(float)(60.0f * Math.sin(angle));
                    float lightZ = postHeight - 5.0f;
                    glTranslatef(lightX, lightY, lightZ);

                    // 旋转灯具朝向
                    glRotatef((float)Math.toDegrees(angle) + 90, 0, 0, 1);
                    glRotatef(60, 1, 0, 0);

                    // 主体外壳
                    glColor3f(0.2f, 0.2f, 0.2f);
                    glPushMatrix();
                    {
                        glScalef(30.0f, 25.0f, 15.0f);
                        drawBox();
                    }
                    glPopMatrix();

                    // 散热片（在外壳后部）
                    glColor3f(0.3f, 0.3f, 0.3f);
                    for(int fin = 0; fin < 8; fin++) {
                        glPushMatrix();
                        {
                            glTranslatef(0, 0, -8.0f + fin * 2.0f);  // 从外壳后部开始
                            glScalef(35.0f, 30.0f, 0.5f);  // 略大于外壳
                            drawBox();
                        }
                        glPopMatrix();
                    }

                    // 前框（装饰性边框）
                    glColor3f(0.25f, 0.25f, 0.25f);
                    glPushMatrix();
                    {
                        glTranslatef(0, 0, 7.5f);
                        glScalef(32.0f, 27.0f, 1.0f);
                        drawBox();
                    }
                    glPopMatrix();

                    // 发光部分
                    glPushMatrix();
                    {
                        // 设置发光材质
                        FloatBuffer matEmission = BufferUtils.createFloatBuffer(4);
                        matEmission.put(new float[] {1.0f, 1.0f, 0.8f, 1.0f}).flip();
                        glMaterial(GL_FRONT, GL_EMISSION, matEmission);

                        glColor3f(1.0f, 1.0f, 0.8f);
                        glTranslatef(0, 0, 7.0f);
                        glScalef(28.0f, 23.0f, 1.0f);
                        drawBox();

                        resetMaterial();
                    }
                    glPopMatrix();

                    // 光源位置标记（黄色小球）
                    glPushMatrix();
                    {
                        FloatBuffer matEmission = BufferUtils.createFloatBuffer(4);
                        matEmission.put(new float[] {1.0f, 1.0f, 0.0f, 1.0f}).flip();
                        glMaterial(GL_FRONT, GL_EMISSION, matEmission);

                        glColor3f(1.0f, 1.0f, 0.0f);
                        glTranslatef(0, 0, 7.0f);
                        sphere.drawSphere(3.0f, 16, 16);

                        resetMaterial();
                    }
                    glPopMatrix();
                    
                    // 6. 在光源位置绘制标记球体
                    glPushMatrix();
                    {
                        // 设置发光材质
                        FloatBuffer matEmission = BufferUtils.createFloatBuffer(4);
                        matEmission.put(new float[] {1.0f, 1.0f, 0.0f, 1.0f}).flip();
                        glMaterial(GL_FRONT, GL_EMISSION, matEmission);
                        
                        glColor3f(1.0f, 1.0f, 0.0f);  // 黄色
                        glTranslatef(0, 0, 7.0f);  // 移动到灯具前方
                        sphere.drawSphere(3.0f, 16, 16);  // 使用项目中的Sphere类
                        
                        resetMaterial();
                    }
                    glPopMatrix();
                }
                glPopMatrix();
            }
            glPopMatrix();
        }
    }
    
    private void drawCylinder(int segments) {
        float angleIncrement = 360.0f / segments;
        
        // 绘制圆柱体侧面
        glBegin(GL_QUAD_STRIP);
        for (int i = 0; i <= segments; i++) {
            float angle = (float)Math.toRadians(i * angleIncrement);
            float x = (float)Math.cos(angle);
            float y = (float)Math.sin(angle);
            
            glNormal3f(x, y, 0);
            glVertex3f(x, y, 1);
            glVertex3f(x, y, 0);
        }
        glEnd();
        
        // 绘制顶面和底面
        glBegin(GL_TRIANGLE_FAN);
        glNormal3f(0, 0, 1);
        glVertex3f(0, 0, 1);
        for (int i = 0; i <= segments; i++) {
            float angle = (float)Math.toRadians(i * angleIncrement);
            glVertex3f((float)Math.cos(angle), (float)Math.sin(angle), 1);
        }
        glEnd();
        
        glBegin(GL_TRIANGLE_FAN);
        glNormal3f(0, 0, -1);
        glVertex3f(0, 0, 0);
        for (int i = segments; i >= 0; i--) {
            float angle = (float)Math.toRadians(i * angleIncrement);
            glVertex3f((float)Math.cos(angle), (float)Math.sin(angle), 0);
        }
        glEnd();
    }
    
    private void drawBox() {
        glBegin(GL_QUADS);
        {
            // 前面
            glVertex3f(-0.5f, -0.5f, 0.5f);
            glVertex3f(0.5f, -0.5f, 0.5f);
            glVertex3f(0.5f, 0.5f, 0.5f);
            glVertex3f(-0.5f, 0.5f, 0.5f);
            
            // 后面
            glVertex3f(-0.5f, -0.5f, -0.5f);
            glVertex3f(-0.5f, 0.5f, -0.5f);
            glVertex3f(0.5f, 0.5f, -0.5f);
            glVertex3f(0.5f, -0.5f, -0.5f);
            
            // 顶面
            glVertex3f(-0.5f, 0.5f, -0.5f);
            glVertex3f(-0.5f, 0.5f, 0.5f);
            glVertex3f(0.5f, 0.5f, 0.5f);
            glVertex3f(0.5f, 0.5f, -0.5f);
            
            // 底面
            glVertex3f(-0.5f, -0.5f, -0.5f);
            glVertex3f(0.5f, -0.5f, -0.5f);
            glVertex3f(0.5f, -0.5f, 0.5f);
            glVertex3f(-0.5f, -0.5f, 0.5f);
            
            // 右面
            glVertex3f(0.5f, -0.5f, -0.5f);
            glVertex3f(0.5f, 0.5f, -0.5f);
            glVertex3f(0.5f, 0.5f, 0.5f);
            glVertex3f(0.5f, -0.5f, 0.5f);
            
            // 左面
            glVertex3f(-0.5f, -0.5f, -0.5f);
            glVertex3f(-0.5f, -0.5f, 0.5f);
            glVertex3f(-0.5f, 0.5f, 0.5f);
            glVertex3f(-0.5f, 0.5f, -0.5f);
        }
        glEnd();
    }
    
    // 添加一个辅助方法来重置材质
    private void resetMaterial() {
        FloatBuffer resetEmission = BufferUtils.createFloatBuffer(4);
        resetEmission.put(new float[] {0.0f, 0.0f, 0.0f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_EMISSION, resetEmission);
        
        FloatBuffer resetAmbient = BufferUtils.createFloatBuffer(4);
        resetAmbient.put(new float[] {0.2f, 0.2f, 0.2f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_AMBIENT, resetAmbient);
        
        FloatBuffer resetDiffuse = BufferUtils.createFloatBuffer(4);
        resetDiffuse.put(new float[] {0.8f, 0.8f, 0.8f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_DIFFUSE, resetDiffuse);
    }
}