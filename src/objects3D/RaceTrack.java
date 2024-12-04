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
        
        // 添加换胎站
        drawPitStop(innerRadius);
        
        // 在绘制完赛道后，添加内部道路
        drawInnerRoad(innerRadius);
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
        matEmission.put(new float[] {0.5f, 0.5f, 0.5f, 1.0f}).flip();  // 增加发光
        glMaterial(GL_FRONT, GL_EMISSION, matEmission);
        
        FloatBuffer matAmbient = BufferUtils.createFloatBuffer(4);
        matAmbient.put(new float[] {0.9f, 0.9f, 0.9f, 1.0f}).flip();  // 增加环境光反射
        glMaterial(GL_FRONT, GL_AMBIENT, matAmbient);
        
        FloatBuffer matDiffuse = BufferUtils.createFloatBuffer(4);
        matDiffuse.put(new float[] {1.0f, 1.0f, 1.0f, 1.0f}).flip();  // 保持漫反射
        glMaterial(GL_FRONT, GL_DIFFUSE, matDiffuse);
        
        // 添加镜面反射
        FloatBuffer matSpecular = BufferUtils.createFloatBuffer(4);
        matSpecular.put(new float[] {1.0f, 1.0f, 1.0f, 1.0f}).flip();
        glMaterial(GL_FRONT, GL_SPECULAR, matSpecular);
        
        // 设置镜面反射的亮度（光泽度）
        glMaterialf(GL_FRONT, GL_SHININESS, 128.0f);
        
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
    
    public void drawPitStop(float innerRadius) {
        float pitStopSize = 80.0f;
        float pitStopX = 150.0f;
        float pitStopY = 0.0f;
        float pitStopZ = 10.0f;

        glPushMatrix();
        {
            glTranslatef(pitStopX, pitStopY, pitStopZ);
            
            // 1. 主体建筑
            drawPitStopMainBuilding(pitStopSize);
            
            // 2. 维修区域
            drawServiceArea(pitStopSize);
            
            // 3. 装饰元素
            drawPitStopDecorations(pitStopSize);
        }
        glPopMatrix();
    }

    private void drawPitStopMainBuilding(float size) {
        // 主建筑材质（亮红色金属质感）
        setMaterial(new float[]{0.9f, 0.1f, 0.1f, 1.0f}, 128.0f, 0.4f);  // 鲜艳的红色
        
        // 主体建筑
        glPushMatrix();
        {
            glScalef(size, size * 0.8f, size * 0.4f);
            drawBox();
        }
        glPopMatrix();
        
        // 屋顶（深红色）
        setMaterial(new float[]{0.7f, 0.05f, 0.05f, 1.0f}, 96.0f, 0.3f);  // 较暗的红色
        glPushMatrix();
        {
            glTranslatef(0.0f, 0.0f, (size * 0.4f)-3.5f);
            glScalef(size * 1.2f, size * 0.9f, size * 0.15f);
            drawBox();
        }
        glPopMatrix();
    }

    private void drawServiceArea(float size) {
        // 维修区地板（深灰色）
        setMaterial(new float[]{0.2f, 0.2f, 0.2f, 1.0f}, 32.0f, 0.1f);
        glPushMatrix();
        {
            glTranslatef(0.0f, size * 0.9f, -8.0f);
            glScalef(size * 0.8f, size * 0.3f, size * 0.05f);
            drawBox();
        }
        glPopMatrix();

        // 维修设备（黑色）
        setMaterial(new float[]{0.1f, 0.1f, 0.1f, 1.0f}, 64.0f, 0.1f);
        for(int i = 0; i < 3; i++) {
            glPushMatrix();
            {
                float xOffset = (i - 1) * (size * 0.3f);
                glTranslatef(xOffset, size * 0.9f, (size * 0.1f)-8.0f);
                glScalef(size * 0.1f, size * 0.2f, size * 0.2f);
                drawBox();
            }
            glPopMatrix();
        }

        // 轮胎架
        drawTireRack(size);
    }

    private void drawTireRack(float size) {
        // 轮胎架（暗红色）
        setMaterial(new float[]{0.6f, 0.05f, 0.05f, 1.0f}, 96.0f, 0.2f);
        glPushMatrix();
        {
            glTranslatef(-size * 0.8f, size * 0.9f, (size * 0.15f)-10.0f);
            glScalef(size * 0.1f, size * 0.2f, size * 0.3f);
            drawBox();
        }
        glPopMatrix();

        // 轮胎（纯黑色）
        setMaterial(new float[]{0.05f, 0.05f, 0.05f, 1.0f}, 16.0f, 0.0f);
        for(int i = 0; i < 3; i++) {
            glPushMatrix();
            {
                glTranslatef(-size * 0.8f, size * 0.9f, ((i * 0.1f + 0.1f) * size)-10.0f);
                glRotatef(90, 1, 0, 0);
                Cylinder tire = new Cylinder();
                tire.drawCylinder(size * 0.08f, size * 0.05f, 32);
            }
            glPopMatrix();
        }
    }

    private void drawPitStopDecorations(float size) {
        // 标志牌（亮红色带发光效果）
        setMaterial(new float[]{1.0f, 0.1f, 0.1f, 1.0f}, 128.0f, 0.5f);
        glPushMatrix();
        {
            glTranslatef(0.0f, 0.0f, (size * 0.6f)-10.0f);
            glScalef(size * 0.8f, size * 0.2f, size * 0.1f);
            drawBox();
        }
        glPopMatrix();
        
        // 环绕窗户（深灰色半透明）
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        setMaterial(new float[]{0.2f, 0.2f, 0.2f, 0.6f}, 128.0f, 0.1f);
        
        float windowHeight = size * 0.2f;  // 窗户带的高度
        float windowZ = size * 0.25f;      // 窗户的z轴位置
        float wallThickness = size * 0.02f; // 窗户厚度
        
        // 前面的窗户
        glPushMatrix();
        {
            glTranslatef(0.0f, size * 0.4f, windowZ);
            glScalef(size, wallThickness, windowHeight);
            drawBox();
        }
        glPopMatrix();
        
        // 后面的窗户
        glPushMatrix();
        {
            glTranslatef(0.0f, -size * 0.4f, windowZ);
            glScalef(size, wallThickness, windowHeight);
            drawBox();
        }
        glPopMatrix();
        
        // 左侧窗户
        glPushMatrix();
        {
            glTranslatef(-size * 0.5f, 0.0f, windowZ);
            glScalef(wallThickness, size * 0.8f, windowHeight);
            drawBox();
        }
        glPopMatrix();
        
        // 右侧窗户
        glPushMatrix();
        {
            glTranslatef(size * 0.5f, 0.0f, windowZ);
            glScalef(wallThickness, size * 0.8f, windowHeight);
            drawBox();
        }
        glPopMatrix();
        
        // 四个角的连接处（圆角效果）
        float cornerSize = size * 0.1f;
        int segments = 16;
        for(int corner = 0; corner < 4; corner++) {
            glPushMatrix();
            {
                // 计算每个角的位置
                float xSign = (corner % 2 == 0) ? -1 : 1;
                float ySign = (corner < 2) ? 1 : -1;
                glTranslatef(xSign * (size * 0.5f - cornerSize), 
                            ySign * (size * 0.4f - cornerSize), 
                            windowZ);
                
                // 绘制圆角连接
                glBegin(GL_QUAD_STRIP);
                for(int i = 0; i <= segments; i++) {
                    float angle = (float)(i * Math.PI / 2) / segments;
                    if(corner == 0) angle += Math.PI;
                    if(corner == 1) angle += Math.PI * 1.5f;
                    if(corner == 2) angle += 0;
                    if(corner == 3) angle += Math.PI * 0.5f;
                    
                    float x = (float)Math.cos(angle) * cornerSize;
                    float y = (float)Math.sin(angle) * cornerSize;
                    
                    glVertex3f(x, y, windowHeight/2);
                    glVertex3f(x, y, -windowHeight/2);
                }
                glEnd();
            }
            glPopMatrix();
        }
        
        glDisable(GL_BLEND);
    }

    private void setMaterial(float[] color, float shininess, float emissionIntensity) {
        FloatBuffer matEmission = BufferUtils.createFloatBuffer(4);
        matEmission.put(new float[]{
            color[0] * emissionIntensity, 
            color[1] * emissionIntensity, 
            color[2] * emissionIntensity, 
            color[3]
        }).flip();
        glMaterial(GL_FRONT, GL_EMISSION, matEmission);
        
        FloatBuffer matAmbient = BufferUtils.createFloatBuffer(4);
        matAmbient.put(new float[]{
            color[0] * 0.6f, 
            color[1] * 0.6f, 
            color[2] * 0.6f, 
            color[3]
        }).flip();
        glMaterial(GL_FRONT, GL_AMBIENT, matAmbient);
        
        FloatBuffer matDiffuse = BufferUtils.createFloatBuffer(4);
        matDiffuse.put(color).flip();
        glMaterial(GL_FRONT, GL_DIFFUSE, matDiffuse);
        
        FloatBuffer matSpecular = BufferUtils.createFloatBuffer(4);
        matSpecular.put(new float[]{0.8f, 0.8f, 0.8f, color[3]}).flip();
        glMaterial(GL_FRONT, GL_SPECULAR, matSpecular);
        
        glMaterialf(GL_FRONT, GL_SHININESS, shininess);
    }

    public void drawInnerRoad(float innerRadius) {
        // 道路材质（深灰色）
        setMaterial(new float[]{0.1f, 0.1f, 0.1f, 1.0f}, 32.0f, 0.1f);
        
        float roadWidth = 80.0f;  // 道路宽度
        float roadLength = innerRadius * 2.0f;  // 道路长度，确保两端与内边缘相接
        
        glPushMatrix();
        {
            // 调整道路的高度
            glTranslatef(0.0f, 0.0f, 0.0f);  // 将道路抬高一点，避免与地面重叠
            // 调整道路的走向
            glRotatef(0.0f, 0.0f, 0.0f, 1.0f);  // 将道路旋转90度以垂直于当前赛道
            
            // 绘制主要道路
            glPushMatrix();
            {
                // 调整道路的位置，使其两端与内边缘相接
                glTranslatef(0.0f, 0, 0.0f);
                // 调整道路的长度和宽度
                glScalef(roadWidth, roadLength, 2.0f);
                drawBox();
            }
            glPopMatrix();
            
            // 绘制道路边缘的白线
            setMaterial(new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 32.0f, 0.1f);
            
            // 上边的白线
            glPushMatrix();
            {
                glTranslatef(roadWidth / 2 + 1.0f, 0, 0.5f);
                glScalef(2.0f, roadLength, 1.0f);
                drawBox();
            }
            glPopMatrix();
            
            // 下边的白线
            glPushMatrix();
            {
                glTranslatef(-roadWidth / 2 - 1.0f, 0, 0.5f);
                glScalef(2.0f, roadLength, 1.0f);
                drawBox();
            }
            glPopMatrix();
            
            // 中间的虚线
            float dashLength = 20.0f;  // 虚线长度
            float gapLength = 10.0f;   // 虚线间隔
            int numDashes = (int)(roadLength / (dashLength + gapLength));
            
            for(int i = 0; i < numDashes; i++) {
                glPushMatrix();
                {
                    float yPos = i * (dashLength + gapLength);
                    glTranslatef(0.0f, yPos-roadLength/2, 1.0f);
                    glScalef(2.0f, dashLength, 1.0f);
                    drawBox();
                }
                glPopMatrix();
            }
        }
        glPopMatrix();
    }
}