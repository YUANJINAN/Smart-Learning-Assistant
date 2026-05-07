package com.example.smartlearningassistant;

import com.example.smartlearningassistant.model.AgentConfig;
import com.example.smartlearningassistant.model.CourseUnit;
import com.example.smartlearningassistant.model.Exercise;
import com.example.smartlearningassistant.model.ExerciseResult;
import com.example.smartlearningassistant.model.ExerciseSubmission;
import com.example.smartlearningassistant.model.KnowledgeItem;
import com.example.smartlearningassistant.model.LearningProgress;
import com.example.smartlearningassistant.model.LoginRequest;
import com.example.smartlearningassistant.model.LoginResponse;
import com.example.smartlearningassistant.service.LearningAssistantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SmartLearningAssistantApplicationTests {
    @Autowired
    private LearningAssistantService service;

    @Test
    void contextLoads() {
    }

    @Test
    void providesDashboardAndExerciseSubmission() {
        assertThat(service.dashboard().getUserCount()).isEqualTo(2);

        ExerciseSubmission submission = new ExerciseSubmission();
        submission.setUserId(1L);
        submission.setExerciseId(1L);
        submission.setAnswer("B");

        ExerciseResult result = service.submitExercise(submission);

        assertThat(result.isCorrect()).isTrue();
        assertThat(result.getCorrectAnswer()).isEqualTo("B");

        LearningProgress progress = service.progress(1L);
        assertThat(progress.getSubmittedCount()).isGreaterThanOrEqualTo(1);
        assertThat(progress.getAccuracy()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void logsInAndUpdatesUserStatus() {
        LoginRequest request = new LoginRequest();
        request.setUsername("learner");

        LoginResponse response = service.login(request);

        assertThat(response.getUserId()).isEqualTo(1L);

        service.updateUserStatus(1L, "ACTIVE");
        assertThat(service.listUsers())
                .anySatisfy(user -> {
                    assertThat(user.getId()).isEqualTo(1L);
                    assertThat(user.getStatus()).isEqualTo("ACTIVE");
                });
    }

    @Test
    void managesTeachingResourcesAndAuditLogs() {
        CourseUnit course = new CourseUnit(null, "测试课程", "Java", "基础", "用于验证新增课程", List.of("新增课程", "验证练习"));
        CourseUnit savedCourse = service.saveCourse(course);

        Exercise exercise = new Exercise(null, savedCourse.getId(), "测试题目？", List.of("A. 正确", "B. 错误"), "A", "测试解析");
        Exercise savedExercise = service.saveExercise(exercise);

        KnowledgeItem item = new KnowledgeItem();
        item.setTitle("测试知识点");
        item.setCategory("Java");
        item.setContent("用于验证知识库新增。");
        KnowledgeItem savedKnowledge = service.saveKnowledge(item);

        AgentConfig config = new AgentConfig(null, "测试智能体", "验证配置持久化", "rule-based-agent", 1.4, "active", null);
        AgentConfig savedConfig = service.saveAgentConfig(config);

        assertThat(savedCourse.getId()).isNotNull();
        assertThat(savedExercise.getId()).isNotNull();
        assertThat(savedKnowledge.getId()).isNotNull();
        assertThat(savedConfig.getTemperature()).isEqualTo(1.0);
        assertThat(savedConfig.getStatus()).isEqualTo("ACTIVE");
        assertThat(service.auditLogs())
                .extracting(log -> log.getAction())
                .contains("SAVE_COURSE", "SAVE_EXERCISE", "SAVE_KNOWLEDGE", "SAVE_AGENT_CONFIG");
    }

}
