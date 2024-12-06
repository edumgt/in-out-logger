package com.example.demo.commute.mapper;

import com.example.demo.common.tools.CommonMapper;
import com.example.demo.commute.dto.CommuteDto;
import com.example.demo.commute.entity.Commute;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-06T07:51:33+0000",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.2.jar, environment: Java 17.0.9 (Amazon.com Inc.)"
)
@Component
public class CommuteMapperImpl implements CommuteMapper {

    @Autowired
    private CommonMapper commonMapper;

    @Override
    public CommuteDto toDto(Commute commute) {
        if ( commute == null ) {
            return null;
        }

        CommuteDto commuteDto = new CommuteDto();

        commuteDto.setCreatedBy( commonMapper.createdByToId( commute.getCreatedBy() ) );
        commuteDto.setUpdatedBy( commonMapper.updatedByToId( commute.getUpdatedBy() ) );
        commuteDto.setCreatedAt( commute.getCreatedAt() );
        commuteDto.setUpdatedAt( commute.getUpdatedAt() );
        commuteDto.setId( commute.getId() );
        commuteDto.setDate( commute.getDate() );
        commuteDto.setCheckInTime( commute.getCheckInTime() );
        commuteDto.setCheckOutTime( commute.getCheckOutTime() );

        return commuteDto;
    }

    @Override
    public Commute toEntity(CommuteDto commuteDto) {
        if ( commuteDto == null ) {
            return null;
        }

        Commute.CommuteBuilder<?, ?> commute = Commute.builder();

        commute.createdAt( commuteDto.getCreatedAt() );
        commute.updatedAt( commuteDto.getUpdatedAt() );
        commute.id( commuteDto.getId() );
        commute.date( commuteDto.getDate() );
        commute.checkInTime( commuteDto.getCheckInTime() );
        commute.checkOutTime( commuteDto.getCheckOutTime() );

        return commute.build();
    }
}
