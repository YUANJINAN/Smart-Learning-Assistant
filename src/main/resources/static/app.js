let currentUser = { userId: 1, username: "learner", displayName: "示例学习者", role: "LEARNER" };
let cachedAgentConfigs = [];

async function api(path, options = {}) {
    const response = await fetch(path, {
        headers: { "Content-Type": "application/json" },
        ...options
    });
    if (!response.ok) {
        const error = await response.json().catch(() => ({ message: "请求失败" }));
        throw new Error(error.message || "请求失败");
    }
    const text = await response.text();
    return text ? JSON.parse(text) : null;
}

function userId() {
    return currentUser.userId;
}

function splitInput(value) {
    return value.split(/[,，]/).map(item => item.trim()).filter(Boolean);
}

function joinText(values) {
    return (values || []).join("，");
}

function renderItem(title, body, meta = "") {
    return `<article class="item"><h3>${title}</h3><p>${body || ""}</p>${meta ? `<p class="meta">${meta}</p>` : ""}</article>`;
}

async function login() {
    const username = document.querySelector("#loginUser").value;
    currentUser = await api("/api/auth/login", {
        method: "POST",
        body: JSON.stringify({ username })
    });
    document.querySelector("#currentUserLabel").textContent = `${currentUser.displayName} / ${currentUser.role}`;
    await loadAll();
}

async function loadDashboard() {
    const dashboard = await api("/api/admin/dashboard");
    document.querySelector("#userCount").textContent = dashboard.userCount;
    document.querySelector("#courseCount").textContent = dashboard.courseCount;
    document.querySelector("#exerciseCount").textContent = dashboard.exerciseCount;
    document.querySelector("#planCount").textContent = dashboard.planCount;
}

async function loadUsers() {
    const users = await api("/api/users");
    document.querySelector("#userList").innerHTML = users.map(user => `
        <article class="item">
            <h3>${user.displayName}</h3>
            <p>${user.username} / ${user.role}</p>
            <p class="meta">状态：${user.status}，最近登录：${user.lastLoginAt?.replace("T", " ")}</p>
            <div class="option-row">
                <button data-user-status="${user.id}" data-status="ACTIVE">启用</button>
                <button data-user-status="${user.id}" data-status="DISABLED">停用</button>
            </div>
        </article>
    `).join("");
}

async function updateUserStatus(event) {
    const button = event.target.closest("button[data-user-status]");
    if (!button) return;
    await api(`/api/users/${button.dataset.userStatus}/status`, {
        method: "PUT",
        body: JSON.stringify({ status: button.dataset.status })
    });
    await Promise.all([loadUsers(), loadDashboard(), loadAuditLogs()]);
}

async function loadAgentConfigs() {
    cachedAgentConfigs = await api("/api/admin/agent-configs");
    document.querySelector("#agentConfigList").innerHTML = cachedAgentConfigs.length
        ? cachedAgentConfigs.map(config => `
            <article class="item">
                <h3>${config.name}</h3>
                <p>${config.responsibility}</p>
                <p class="meta">模型：${config.modelName} / 温度：${config.temperature} / 状态：${config.status}</p>
                <div class="option-row">
                    <button data-agent="${config.id}" data-agent-status="ACTIVE">启用</button>
                    <button data-agent="${config.id}" data-agent-status="DISABLED">停用</button>
                </div>
            </article>
        `).join("")
        : renderItem("智能体配置", "暂无配置记录。");
}

async function updateAgentStatus(event) {
    const button = event.target.closest("button[data-agent]");
    if (!button) return;
    const current = cachedAgentConfigs.find(config => String(config.id) === button.dataset.agent);
    if (!current) return;
    await api("/api/admin/agent-configs", {
        method: "POST",
        body: JSON.stringify({ ...current, status: button.dataset.agentStatus })
    });
    await Promise.all([loadAgentConfigs(), loadDashboard(), loadAuditLogs()]);
}

async function loadAuditLogs() {
    const logs = await api("/api/admin/audit-logs");
    document.querySelector("#auditLogList").innerHTML = logs.length
        ? logs.map(log => renderItem(log.action, log.detail, `${log.createdAt?.replace("T", " ")} / 用户 ${log.userId || "系统"}`)).join("")
        : renderItem("审计日志", "暂无日志记录。");
}

async function loadProfile() {
    const profile = await api(`/api/users/${userId()}/profile`);
    document.querySelector("#goalInput").value = profile.goal;
    document.querySelector("#levelInput").value = profile.level;
    document.querySelector("#interestsInput").value = joinText(profile.interests);
    document.querySelector("#weakPointsInput").value = joinText(profile.weakPoints);
    document.querySelector("#dailyTargetInput").value = profile.dailyTargetMinutes;
}

async function saveProfile() {
    await api(`/api/users/${userId()}/profile`, {
        method: "PUT",
        body: JSON.stringify({
            goal: document.querySelector("#goalInput").value,
            level: document.querySelector("#levelInput").value,
            interests: splitInput(document.querySelector("#interestsInput").value),
            weakPoints: splitInput(document.querySelector("#weakPointsInput").value),
            dailyTargetMinutes: Number(document.querySelector("#dailyTargetInput").value || 30)
        })
    });
    await Promise.all([loadProgress(), loadAuditLogs(), askAgents()]);
}

async function loadProgress() {
    const progress = await api(`/api/users/${userId()}/progress`);
    document.querySelector("#progressBox").innerHTML = `
        <div><strong>${progress.submittedCount}</strong><span>提交次数</span></div>
        <div><strong>${progress.accuracy}%</strong><span>正确率</span></div>
        <div><strong>${progress.averagePlanProgress}%</strong><span>计划进度</span></div>
        <p>${progress.suggestion}</p>
    `;
    const mistakes = await api(`/api/users/${userId()}/mistakes`);
    document.querySelector("#mistakeList").innerHTML = mistakes.length
        ? mistakes.map(item => renderItem(item.question, `你的答案：${item.submittedAnswer}；正确答案：${item.correctAnswer}`, item.explanation)).join("")
        : renderItem("错题本", "暂时没有错题记录。");
}

async function loadCourses() {
    const courses = await api("/api/courses");
    document.querySelector("#courseFilter").innerHTML = `<option value="">全部课程</option>` + courses.map(course => `<option value="${course.id}">${course.title}</option>`).join("");
    document.querySelector("#courseList").innerHTML = courses.map(course => renderItem(course.title, course.summary, `${course.subject} / ${course.difficulty}`)).join("");
    await loadExercises();
}

async function loadExercises() {
    const courseId = document.querySelector("#courseFilter").value;
    const exercises = await api(`/api/exercises${courseId ? `?courseId=${courseId}` : ""}`);
    document.querySelector("#exerciseList").innerHTML = exercises.map(exercise => `
        <article class="item">
            <h3>${exercise.question}</h3>
            <div class="option-row">
                ${exercise.options.map(option => `<button data-exercise="${exercise.id}" data-answer="${option.slice(0, 1)}">${option}</button>`).join("")}
            </div>
            <p class="meta" id="result-${exercise.id}"></p>
        </article>
    `).join("");
}

async function submitExercise(event) {
    const button = event.target.closest("button[data-exercise]");
    if (!button) return;
    const result = await api("/api/exercises/submit", {
        method: "POST",
        body: JSON.stringify({
            userId: userId(),
            exerciseId: Number(button.dataset.exercise),
            answer: button.dataset.answer
        })
    });
    document.querySelector(`#result-${result.exerciseId}`).textContent = `${result.correct ? "回答正确" : "回答错误"}。正确答案：${result.correctAnswer}。${result.explanation}`;
    await Promise.all([loadProgress(), loadAuditLogs()]);
}

async function loadPlans() {
    const plans = await api(`/api/plans?userId=${userId()}`);
    document.querySelector("#planList").innerHTML = plans.map(plan => renderItem(plan.title, joinText(plan.tasks), `${plan.startDate} 至 ${plan.endDate} / 进度 ${plan.progress}%`)).join("");
}

async function createPlan() {
    const profile = await api(`/api/users/${userId()}/profile`);
    const today = new Date();
    const end = new Date();
    end.setDate(today.getDate() + 10);
    await api("/api/plans", {
        method: "POST",
        body: JSON.stringify({
            userId: userId(),
            title: `${profile.goal} 学习计划`,
            startDate: today.toISOString().slice(0, 10),
            endDate: end.toISOString().slice(0, 10),
            tasks: ["完成课程学习", "提交练习并复盘错题", "更新知识库与复习安排"],
            progress: 0
        })
    });
    await Promise.all([loadPlans(), loadDashboard(), loadProgress(), loadAuditLogs()]);
}

async function searchKnowledge() {
    const keyword = encodeURIComponent(document.querySelector("#knowledgeKeyword").value);
    const items = await api(`/api/knowledge?keyword=${keyword}`);
    document.querySelector("#knowledgeList").innerHTML = items.map(item => renderItem(item.title, item.content, item.category)).join("");
}

async function askAgents() {
    const reply = await api("/api/agents/ask", {
        method: "POST",
        body: JSON.stringify({
            userId: userId(),
            topic: document.querySelector("#knowledgeKeyword").value || "Java",
            question: document.querySelector("#agentQuestion").value
        })
    });
    document.querySelector("#agentReply").innerHTML = `
        <div>${reply.tutorAdvice}</div>
        <div>${reply.plannerAdvice}</div>
        <div>${reply.reviewAdvice}</div>
        <div>推荐资源：${joinText(reply.recommendedResources)}</div>
    `;
}

async function loadAll() {
    await Promise.all([
        loadDashboard(),
        loadUsers(),
        loadAgentConfigs(),
        loadAuditLogs(),
        loadProfile(),
        loadProgress(),
        loadCourses(),
        loadPlans(),
        searchKnowledge(),
        askAgents()
    ]);
}

document.querySelector("#loginBtn").addEventListener("click", login);
document.querySelector("#saveProfileBtn").addEventListener("click", saveProfile);
document.querySelector("#refreshProgressBtn").addEventListener("click", loadProgress);
document.querySelector("#courseFilter").addEventListener("change", loadExercises);
document.querySelector("#exerciseList").addEventListener("click", submitExercise);
document.querySelector("#createPlanBtn").addEventListener("click", createPlan);
document.querySelector("#searchKnowledgeBtn").addEventListener("click", searchKnowledge);
document.querySelector("#askAgentBtn").addEventListener("click", askAgents);
document.querySelector("#refreshUsersBtn").addEventListener("click", loadUsers);
document.querySelector("#userList").addEventListener("click", updateUserStatus);
document.querySelector("#refreshAgentsBtn").addEventListener("click", loadAgentConfigs);
document.querySelector("#agentConfigList").addEventListener("click", updateAgentStatus);
document.querySelector("#refreshAuditBtn").addEventListener("click", loadAuditLogs);

login().catch(error => {
    document.body.insertAdjacentHTML("afterbegin", `<div class="error">${error.message}</div>`);
});
