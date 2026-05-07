package com.example.smartlearningassistant;

import com.example.smartlearningassistant.model.ExerciseResult;
import com.example.smartlearningassistant.model.ExerciseSubmission;
import com.example.smartlearningassistant.model.LearningProgress;
import com.example.smartlearningassistant.model.LoginRequest;
import com.example.smartlearningassistant.model.LoginResponse;
import com.example.smartlearningassistant.service.LearningAssistantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

}
