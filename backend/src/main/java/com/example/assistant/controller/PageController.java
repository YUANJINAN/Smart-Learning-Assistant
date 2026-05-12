package com.example.assistant.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PageController {
    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> favicon() {
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = {"/", "/learner", "/admin"}, produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        return """
                <!doctype html>
                <html lang="zh-CN">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>智能学习助手</title>
                    <style>
                        body { margin: 0; font-family: "Microsoft YaHei", "Segoe UI", Arial, sans-serif; color: #1d2733; background: #f4f7fb; }
                        main { max-width: 1080px; margin: 0 auto; padding: 40px 24px; }
                        header { display: flex; align-items: center; justify-content: space-between; gap: 20px; margin-bottom: 24px; }
                        h1 { margin: 0; font-size: 28px; }
                        .grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 16px; }
                        .panel { border: 1px solid #d9e2ec; border-radius: 8px; padding: 20px; background: #fff; }
                        a, button { display: inline-block; margin: 6px 8px 6px 0; border: 0; border-radius: 6px; padding: 10px 14px; color: #fff; background: #0f7a68; text-decoration: none; font: inherit; cursor: pointer; }
                        pre { overflow: auto; min-height: 120px; padding: 14px; border-radius: 6px; background: #14202e; color: #d7e8ff; }
                        @media (max-width: 760px) { header, .grid { grid-template-columns: 1fr; display: grid; } }
                    </style>
                </head>
                <body>
                <main>
                    <header>
                        <div>
                            <h1>智能学习助手</h1>
                            <p>Spring Boot 后端已启动。Vue 前端可在安装 Node.js 后通过 frontend 目录运行。</p>
                        </div>
                        <nav>
                            <a href="/api/admin/dashboard">后台概览接口</a>
                            <a href="/api/practice/questions">练习题接口</a>
                        </nav>
                    </header>
                    <section class="grid">
                        <article class="panel">
                            <h2>学习者端</h2>
                            <p>包含学习档案、智能问答、练习批改、错题本、学习计划和学习报告。</p>
                            <button onclick="callApi('/api/profile?userId=1')">读取学习档案</button>
                            <button onclick="askAgent()">调用多智能体</button>
                        </article>
                        <article class="panel">
                            <h2>管理员端</h2>
                            <p>包含用户管理、知识库、题库、权限、日志和模型配置。</p>
                            <button onclick="callApi('/api/admin/users')">读取用户列表</button>
                            <button onclick="callApi('/api/admin/logs')">读取操作日志</button>
                        </article>
                    </section>
                    <h2>接口返回</h2>
                    <pre id="output">点击上方按钮测试接口。</pre>
                </main>
                <script>
                    async function callApi(path, options) {
                        const response = await fetch(path, options);
                        const text = await response.text();
                        document.getElementById('output').textContent = text ? JSON.stringify(JSON.parse(text), null, 2) : '';
                    }
                    async function askAgent() {
                        await callApi('/api/chat/ask', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({ userId: 1, topic: 'Java Web', question: '如何提升 REST API 设计能力？' })
                        });
                    }
                </script>
                </body>
                </html>
                """;
    }
}
