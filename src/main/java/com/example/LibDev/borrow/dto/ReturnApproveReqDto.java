package com.example.LibDev.borrow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReturnApproveReqDto {
    private List<Long> borrowIds;
}
