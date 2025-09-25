# 知识问答系统

## 项目简介
这是一个基于Spring Boot和RAG（检索增强生成）技术的知识问答系统。该系统允许用户上传文档，然后基于这些文档内容进行智能问答，支持流式输出回答结果。

## 对应的前端
git clone git@github.com:keyszuns/AI-Agent-Front-End.git

## 技术栈
- **后端框架**：Spring Boot 3.5.5
- **编程语言**：Java 17
- **文档存储**：Elasticsearch
- **AI集成**：Spring AI
- **大语言模型**：DeepSeek API
- **文档解析**：Apache PDFBox、Apache Tika、Apache POI
- **工具库**：Lombok
- **构建工具**：Maven

## 功能特点
- **文档管理**：支持上传、列出和删除文档
- **多格式支持**：支持PDF、Word、文本文件的解析
- **智能问答**：基于RAG技术，结合文档内容生成准确回答
- **流式输出**：支持问答结果的流式返回，提升用户体验
- **中文支持**：使用IK分词器，优化中文文本处理

## 快速开始

### 环境要求
- JDK 17或更高版本
- Maven 3.6.0或更高版本
- Elasticsearch 7.x或更高版本（运行在本地9200端口）
- DeepSeek API密钥

### 安装步骤
1. **克隆项目**
```bash
git clone git@github.com:keyszuns/AI-Agent.git
cd knowledge-base
```

2. **配置环境变量**
   设置DeepSeek API密钥作为环境变量：
```bash
# Windows
set KEYSZUNS_DEEPSEEK_API_KEY=your-api-key

# Linux/Mac
export KEYSZUNS_DEEPSEEK_API_KEY=your-api-key
```

3. **启动Elasticsearch**
   确保本地Elasticsearch服务已启动并运行在9200端口。

4. **构建项目**
```bash
mvn clean install
```

5. **运行项目**
```bash
mvn spring-boot:run
```
项目将在8081端口启动。

## 配置说明
主要配置位于`src/main/resources/application.yml`文件中：

```yaml
server:
  port: 8081

spring:
  application:
    name: knowledge-base-qa
  ai:
    openai:
      api-key: ${KEYSZUNS_DEEPSEEK_API_KEY} # DeepSeek API密钥
      base-url: https://api.deepseek.com # DeepSeek API地址
      chat:
        options:
          model: deepseek-chat # 使用的模型
  elasticsearch:
    uris: http://localhost:9200
  data:
    elasticsearch:
      repositories:
        enabled: true
```

## API接口说明

### 文档管理接口

#### 上传文档
- **URL**: `/documents/upload`
- **Method**: `POST`
- **Content-Type**: `multipart/form-data`
- **参数**: `file` (要上传的文件)
- **返回**: 上传结果消息

#### 列出文档
- **URL**: `/documents/list`
- **Method**: `GET`
- **返回**: 文档信息列表（包含文件名、上传日期和文件大小）

#### 删除文档
- **URL**: `/documents/delete`
- **Method**: `DELETE`
- **Content-Type**: `application/json`
- **请求体**: `{"name": "文件名"}`
- **返回**: 删除结果消息

### 问答接口

#### 流式问答
- **URL**: `/qa/ask/stream`
- **Method**: `POST`
- **Content-Type**: `application/json`
- **请求体**: 问题文本
- **返回**: 流式Server-Sent Events，包含回答内容

## 项目结构
```
├── src/main/java/com/keyszuns/knowledgebase/
│   ├── controller/          # 控制器层
│   │   ├── DocumentController.java  # 文档管理接口
│   │   └── QAController.java        # 问答接口
│   ├── service/             # 服务层
│   │   ├── FileParserService.java   # 文件解析服务
│   │   └── RagService.java          # RAG功能服务
│   ├── repository/          # 数据访问层
│   │   └── KnowledgeRepository.java # Elasticsearch存储接口
│   ├── entity/              # 实体类
│   │   └── document/        # 文档相关实体
│   ├── dto/                 # 数据传输对象
│   └── KnowledgeBaseApplication.java # 应用入口
├── src/main/resources/      # 资源文件
│   ├── application.yml      # 配置文件
│   ├── static/              # 静态资源
│   └── templates/           # 模板文件
├── pom.xml                  # Maven依赖配置
└── README.md                # 项目说明文档
```

## 核心功能说明

### RAG工作流程
1. **文档检索**：根据用户问题从Elasticsearch中检索相关文档
2. **上下文构建**：将检索到的文档内容构建为上下文
3. **提示词生成**：结合问题和上下文生成提示词
4. **调用模型**：将提示词发送给DeepSeek模型生成回答
5. **流式输出**：将回答结果以流式方式返回给用户

### 文件解析支持
- **文本文件**：直接读取文本内容
- **PDF文件**：使用Apache PDFBox解析
- **Word文件**：使用Apache Tika解析

## 注意事项
1. **API密钥安全**：请勿将DeepSeek API密钥直接硬编码到代码中，应通过环境变量或配置文件安全管理
2. **文件大小限制**：默认配置未限制上传文件大小，可在application.yml中配置`spring.servlet.multipart.max-file-size`和`spring.servlet.multipart.max-request-size`
3. **Elasticsearch配置**：确保Elasticsearch已正确安装并运行，索引会自动创建
4. **网络连接**：系统需要能够访问DeepSeek API，请确保网络连接正常

## License
[MIT](LICENSE)