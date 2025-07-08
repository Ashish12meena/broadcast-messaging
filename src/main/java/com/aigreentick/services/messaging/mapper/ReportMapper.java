package com.aigreentick.services.messaging.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.aigreentick.services.messaging.dto.ReportResponseDto;
import com.aigreentick.services.messaging.model.Report;


@Component
public class ReportMapper {
     public ReportResponseDto toReportDto(Report report) {
        ReportResponseDto reportResponseDto = new ReportResponseDto();
        reportResponseDto.setBroadCastId(report.getBroadcastId());
        reportResponseDto.setCampaignId(report.getCampaignId());
        reportResponseDto.setMessageId(report.getMessageId());
        reportResponseDto.setMessageStatus(report.getMessageStatus());
        reportResponseDto.setMobile(report.getMobile());
        reportResponseDto.setResponse(report.getResponse());
        reportResponseDto.setStatus(report.getStatus());
        reportResponseDto.setWaId(report.getWaId());
        return reportResponseDto;
    }
    public List<ReportResponseDto> toReportDtoList(List<Report> reports) {
        return reports.stream().map(this::toReportDto).toList();
    }
}

