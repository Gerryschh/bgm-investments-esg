package it.bgm.investments.mapper;

import it.bgm.investments.api.model.UserSummaryModel;
import it.bgm.investments.config.CentralMapperConfig;
import it.bgm.investments.domain.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class)
public interface UserMapper {

    UserSummaryModel toSummary(User src);

    @InheritInverseConfiguration
    @Mapping(target = "passwordHash", ignore = true)
    User fromSummary(UserSummaryModel dto);
}