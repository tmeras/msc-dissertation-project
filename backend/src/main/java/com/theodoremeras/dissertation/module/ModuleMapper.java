package com.theodoremeras.dissertation.module;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ModuleMapper {

    private ModelMapper modelMapper;

    public ModuleMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ModuleDto mapToDto(ModuleEntity moduleEntity) {
        return modelMapper.map(moduleEntity, ModuleDto.class);
    }

    public ModuleEntity mapFromDto(ModuleDto moduleDto) {
        return modelMapper.map(moduleDto, ModuleEntity.class);
    }

}
