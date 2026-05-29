package edu.tx.api_zen.service;

import edu.tx.api_zen.dao.LogConnexionDao;
import edu.tx.api_zen.dto.LogConnexionDto;
import edu.tx.api_zen.mapper.LogConnexionMapper;
import edu.tx.api_zen.model.LogConnexion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogConnexionService {

    private final LogConnexionDao logConnexionDao;

    public List<LogConnexionDto> listRecent() {
        return logConnexionDao.findTop50ByOrderByDateConnexionDesc().stream()
                .map(LogConnexionMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<LogConnexionDto> listAll() {
        return logConnexionDao.findAll().stream()
                .map(LogConnexionMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<LogConnexionDto> getByEmail(String email) {
        return logConnexionDao.findByEmailTenteOrderByDateConnexionDesc(email).stream()
                .map(LogConnexionMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<LogConnexionDto> getByReussite(Boolean reussite) {
        return logConnexionDao.findByReussiteOrderByDateConnexionDesc(reussite).stream()
                .map(LogConnexionMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<LogConnexionDto> getByIp(String ip) {
        return logConnexionDao.findByAdresseIpOrderByDateConnexionDesc(ip).stream()
                .map(LogConnexionMapper::toDto)
                .collect(Collectors.toList());
    }

    // Méthode utilisée par AuthService pour loguer les connexions
    public void logConnexion(String email, String ipAddress, boolean reussite, String motifEchec) {
        LogConnexion log = new LogConnexion();
        log.setEmailTente(email);
        log.setAdresseIp(ipAddress);
        log.setReussite(reussite);
        log.setMotifEchec(motifEchec);
        log.setDateConnexion(Instant.now());
        logConnexionDao.save(log);
    }
}

