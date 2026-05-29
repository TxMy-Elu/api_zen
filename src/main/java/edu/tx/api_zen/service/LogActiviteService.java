package edu.tx.api_zen.service;

import edu.tx.api_zen.dao.LogActiviteDao;
import edu.tx.api_zen.dao.UserDao;
import edu.tx.api_zen.dto.LogActiviteCreateDto;
import edu.tx.api_zen.dto.LogActiviteDto;
import edu.tx.api_zen.mapper.LogActiviteMapper;
import edu.tx.api_zen.model.LogActivite;
import edu.tx.api_zen.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogActiviteService {

    private final LogActiviteDao logActiviteDao;
    private final UserDao userDao;

    // Liste les 50 dernières activités
    public List<LogActiviteDto> listRecent() {
        return logActiviteDao.findTop50ByOrderByDateActionDesc().stream()
                .map(LogActiviteMapper::toDto)
                .collect(Collectors.toList());
    }

    // Liste toutes les activités
    public List<LogActiviteDto> listAll() {
        return logActiviteDao.findAll().stream()
                .map(LogActiviteMapper::toDto)
                .collect(Collectors.toList());
    }

    // Par utilisateur
    public List<LogActiviteDto> getByUserId(Integer userId) {
        return logActiviteDao.findByUserIdOrderByDateActionDesc(userId).stream()
                .map(LogActiviteMapper::toDto)
                .collect(Collectors.toList());
    }

    // Par table
    public List<LogActiviteDto> getByTable(String tableConcernee) {
        return logActiviteDao.findByTableConcerneeOrderByDateActionDesc(tableConcernee).stream()
                .map(LogActiviteMapper::toDto)
                .collect(Collectors.toList());
    }

    // Par type d'action
    public List<LogActiviteDto> getByTypeAction(String typeAction) {
        return logActiviteDao.findByTypeActionOrderByDateActionDesc(typeAction).stream()
                .map(LogActiviteMapper::toDto)
                .collect(Collectors.toList());
    }

    // Historique d'un élément spécifique
    public List<LogActiviteDto> getByTableAndId(String tableConcernee, Integer idEnregistrement) {
        return logActiviteDao.findByTableConcerneeAndIdEnregistrementOrderByDateActionDesc(tableConcernee, idEnregistrement).stream()
                .map(LogActiviteMapper::toDto)
                .collect(Collectors.toList());
    }

    // Créer un log (utilisé par les autres services)
    public LogActiviteDto create(LogActiviteCreateDto dto) {
        LogActivite log = new LogActivite();
        log.setTypeAction(dto.getTypeAction());
        log.setTableConcernee(dto.getTableConcernee());
        log.setIdEnregistrement(dto.getIdEnregistrement());
        log.setDetails(dto.getDetails());
        log.setAdresseIp(dto.getAdresseIp());
        log.setDateAction(Instant.now());

        if (dto.getIdUtilisateur() != null) {
            User user = userDao.findById(dto.getIdUtilisateur()).orElse(null);
            log.setUser(user);
        }

        LogActivite saved = logActiviteDao.save(log);
        return LogActiviteMapper.toDto(saved);
    }

    // Méthode utilitaire pour loguer facilement une action
    public void log(String typeAction, String tableConcernee, Integer idEnregistrement, String details, String adresseIp, Integer userId) {
        LogActivite log = new LogActivite();
        log.setTypeAction(typeAction);
        log.setTableConcernee(tableConcernee);
        log.setIdEnregistrement(idEnregistrement);
        log.setDetails(details);
        log.setAdresseIp(adresseIp);
        log.setDateAction(Instant.now());

        if (userId != null) {
            User user = userDao.findById(userId).orElse(null);
            log.setUser(user);
        }

        logActiviteDao.save(log);
    }
}

