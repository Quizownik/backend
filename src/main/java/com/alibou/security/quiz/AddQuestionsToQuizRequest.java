package com.alibou.security.quiz;

import java.util.List;

public record AddQuestionsToQuizRequest(Integer position, List <Integer> questionIds) {}