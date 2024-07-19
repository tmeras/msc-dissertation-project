package com.theodoremeras.dissertation.ec_application;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class EcApplicationMapper {

    private ModelMapper modelMapper;

    public EcApplicationMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public EcApplicationDto mapToDto(EcApplicationEntity ecApplicationEntity){
        return modelMapper.map(ecApplicationEntity, EcApplicationDto.class);
    }

    public EcApplicationEntity mapFromDto(EcApplicationDto ecApplicationDto){
        return modelMapper.map(ecApplicationDto, EcApplicationEntity.class);
    }

}
