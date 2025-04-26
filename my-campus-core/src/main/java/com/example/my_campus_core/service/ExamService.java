package com.example.my_campus_core.service;

import com.example.my_campus_core.dto.request.ExamRequestDto;
import com.example.my_campus_core.dto.response.ResponseDto;

public interface ExamService {
    ResponseDto createNewExam(ExamRequestDto examRequestDto);

}
