package edu.tx.api_zen.mapper;

import edu.tx.api_zen.dto.LogActiviteDto;
import edu.tx.api_zen.model.LogActivite;

public class LogActiviteMapper {

    public static LogActiviteDto toDto(LogActivite log) {
        if (log == null) return null;
        LogActiviteDto dto = new LogActiviteDto();
        dto.setIdLogActivite(log.getIdLogActivite());
        dto.setDateAction(log.getDateAction());
        dto.setTypeAction(log.getTypeAction());
        dto.setTableConcernee(log.getTableConcernee());
        dto.setIdEnregistrement(log.getIdEnregistrement());
        dto.setDetails(log.getDetails());
        dto.setAdresseIp(log.getAdresseIp());
        if (log.getUser() != null) {
            dto.setIdUtilisateur(log.getUser().getId());
            dto.setNomUtilisateur(log.getUser().getPrenom() + " " + log.getUser().getNom());
        }
        return dto;
    }
}

