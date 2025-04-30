package com.example.my_campus_core.service;

import java.util.List;

import com.example.my_campus_core.dto.ExamDto;
import com.example.my_campus_core.dto.request.ExamRequestDto;
import com.example.my_campus_core.dto.response.ResponseDto;

public interface ExamService {
    ResponseDto createNewExam(ExamRequestDto examRequestDto);

    boolean examForCourseExists(int courseId);

    ExamDto getExamCourseId(int courseId);

    List<ExamDto> getAllExamsForUserWithId(int professorId);

    ExamDto getExamById(int examId);

}
