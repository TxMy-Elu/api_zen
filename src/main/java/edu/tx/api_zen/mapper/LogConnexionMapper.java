package edu.tx.api_zen.mapper;

import edu.tx.api_zen.dto.LogConnexionDto;
import edu.tx.api_zen.model.LogConnexion;

public class LogConnexionMapper {

    public static LogConnexionDto toDto(LogConnexion log) {
        if (log == null) return null;
        LogConnexionDto dto = new LogConnexionDto();
        dto.setIdLogConnexion(log.getIdLogConnexion());
        dto.setDateConnexion(log.getDateConnexion());
        dto.setAdresseIp(log.getAdresseIp());
        dto.setReussite(log.getReussite());
        dto.setMotifEchec(log.getMotifEchec());
        dto.setEmailTente(log.getEmailTente());
        return dto;
    }
}

