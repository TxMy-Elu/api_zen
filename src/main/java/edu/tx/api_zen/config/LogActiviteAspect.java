package edu.tx.api_zen.config;

import edu.tx.api_zen.dao.UserDao;
import edu.tx.api_zen.service.LogActiviteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class LogActiviteAspect {

    private final LogActiviteService logActiviteService;
    private final UserDao userDao;

    // Log automatique après création (méthodes "create" dans les services)
    @AfterReturning(pointcut = "execution(* edu.tx.api_zen.service.*.create(..))", returning = "result")
    public void logCreate(JoinPoint joinPoint, Object result) {
        logAction("CREATE", joinPoint, result);
    }

    // Log automatique après mise à jour (méthodes "update" dans les services)
    @AfterReturning(pointcut = "execution(* edu.tx.api_zen.service.*.update(..))", returning = "result")
    public void logUpdate(JoinPoint joinPoint, Object result) {
        logAction("UPDATE", joinPoint, result);
    }

    // Log automatique après suppression (méthodes "delete" dans les services)
    @AfterReturning(pointcut = "execution(* edu.tx.api_zen.service.*.delete(..))")
    public void logDelete(JoinPoint joinPoint) {
        logAction("DELETE", joinPoint, null);
    }

    private void logAction(String typeAction, JoinPoint joinPoint, Object result) {
        try {
            // Éviter de loguer le LogActiviteService lui-même (boucle infinie)
            String className = joinPoint.getTarget().getClass().getSimpleName();
            if (className.equals("LogActiviteService")) {
                return;
            }

            // Extraire le nom de la table depuis le nom du service
            String tableConcernee = className.replace("Service", "").toLowerCase();

            // Extraire l'ID si possible
            Integer idEnregistrement = extractId(joinPoint, result);

            // Récupérer l'IP
            String adresseIp = getClientIp();

            // Construire les détails
            String details = typeAction + " sur " + tableConcernee;
            if (idEnregistrement != null) {
                details += " (ID: " + idEnregistrement + ")";
            }

            // Récupérer l'ID de l'utilisateur connecté
            Integer userId = getCurrentUserId();

            // Loguer l'action
            logActiviteService.log(typeAction, tableConcernee, idEnregistrement, details, adresseIp, userId);

        } catch (Exception e) {
            // Ne pas faire échouer l'action principale si le log échoue
            System.err.println("Erreur lors du log d'activité: " + e.getMessage());
        }
    }

    private Integer extractId(JoinPoint joinPoint, Object result) {
        // Essayer d'extraire l'ID du résultat
        if (result != null) {
            try {
                var method = result.getClass().getMethod("getIdArticle");
                return (Integer) method.invoke(result);
            } catch (Exception ignored) {}
            try {
                var method = result.getClass().getMethod("getId");
                return (Integer) method.invoke(result);
            } catch (Exception ignored) {}
            try {
                var method = result.getClass().getMethod("getIdCategorie");
                return (Integer) method.invoke(result);
            } catch (Exception ignored) {}
            try {
                var method = result.getClass().getMethod("getIdExercice");
                return (Integer) method.invoke(result);
            } catch (Exception ignored) {}
        }

        // Essayer d'extraire l'ID des arguments (pour delete)
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Integer) {
            return (Integer) args[0];
        }

        return null;
    }

    private Integer getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return null;
            }
            String email = auth.getName();
            return userDao.findByEmail(email).map(u -> u.getId()).orElse(null);
        } catch (Exception ignored) {}
        return null;
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty()) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception ignored) {}
        return "unknown";
    }
}

