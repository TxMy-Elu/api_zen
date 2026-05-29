param([string]$cmd = "dev")

switch ($cmd) {
    "dev"         { docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d }
    "dev-build"   { docker compose -f docker-compose.yml -f docker-compose.dev.yml up --build -d }
    "down"        { docker compose -f docker-compose.yml -f docker-compose.dev.yml down }
    "down-clean"  { docker compose -f docker-compose.yml -f docker-compose.dev.yml down -v }
    "prod"        { docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d }
    "prod-build"  { docker compose -f docker-compose.yml -f docker-compose.prod.yml up --build -d }
    "logs"        { docker compose -f docker-compose.yml -f docker-compose.dev.yml logs -f }
    "restart"     { docker compose -f docker-compose.yml -f docker-compose.dev.yml restart api }
    default       { Write-Host "Commandes: dev | dev-build | down | down-clean | prod | prod-build | logs | restart" }
}
