package com.example.demo.calendar.mapper;

import com.example.demo.calendar.dto.VacationDto;
import com.example.demo.calendar.dto.response.PendingVacationDto;
import com.example.demo.calendar.entity.Vacation;
import com.example.demo.common.tools.CommonMapper;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-06T07:51:33+0000",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.2.jar, environment: Java 17.0.9 (Amazon.com Inc.)"
)
@Component
public class VacationMapperImpl implements VacationMapper {

    @Autowired
    private CommonMapper commonMapper;

    @Override
    public VacationDto toDto(Vacation vacation) {
        if ( vacation == null ) {
            return null;
        }

        VacationDto vacationDto = new VacationDto();

        vacationDto.setCreatedBy( commonMapper.createdByToId( vacation.getCreatedBy() ) );
        vacationDto.setUpdatedBy( commonMapper.updatedByToId( vacation.getUpdatedBy() ) );
        vacationDto.setApprovedBy( approvedByToString( vacation.getApprovedBy() ) );
        vacationDto.setCreatedAt( vacation.getCreatedAt() );
        vacationDto.setUpdatedAt( vacation.getUpdatedAt() );
        vacationDto.setVacationStatus( vacation.getVacationStatus() );
        vacationDto.setVacationType( vacation.getVacationType() );
        vacationDto.setStart( vacation.getStart() );
        vacationDto.setEnd( vacation.getEnd() );
        vacationDto.setReason( vacation.getReason() );
        vacationDto.setApprovedAt( vacation.getApprovedAt() );

        return vacationDto;
    }

    @Override
    public PendingVacationDto toPendingVacationDto(Vacation vacation) {
        if ( vacation == null ) {
            return null;
        }

        PendingVacationDto pendingVacationDto = new PendingVacationDto();

        pendingVacationDto.setCreatedBy( commonMapper.createdByToId( vacation.getCreatedBy() ) );
        pendingVacationDto.setUpdatedBy( commonMapper.updatedByToId( vacation.getUpdatedBy() ) );
        pendingVacationDto.setEmployeeName( createdByToName( vacation.getCreatedBy() ) );
        pendingVacationDto.setVacationId( vacation.getId() );
        pendingVacationDto.setCreatedAt( vacation.getCreatedAt() );
        pendingVacationDto.setUpdatedAt( vacation.getUpdatedAt() );
        pendingVacationDto.setStart( vacation.getStart() );
        pendingVacationDto.setEnd( vacation.getEnd() );
        pendingVacationDto.setReason( vacation.getReason() );
        pendingVacationDto.setVacationType( vacation.getVacationType() );

        return pendingVacationDto;
    }

    @Override
    public Vacation toEntity(VacationDto vacationDto) {
        if ( vacationDto == null ) {
            return null;
        }

        Vacation vacation = new Vacation();

        vacation.setVacationStatus( vacationDto.getVacationStatus() );
        vacation.setVacationType( vacationDto.getVacationType() );
        vacation.setReason( vacationDto.getReason() );
        vacation.setStart( vacationDto.getStart() );
        vacation.setEnd( vacationDto.getEnd() );
        vacation.setApprovedAt( vacationDto.getApprovedAt() );

        return vacation;
    }
}
