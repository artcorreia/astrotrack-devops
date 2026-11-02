#!/usr/bin/env bash
# ═════════════════════════════════════════════════════════════════════════════
# Entrypoint do container de testes BDD.
#
# 1. Espera a API responder HTTP 200 em /swagger/v1/swagger.json. O documento
#    OpenAPI é gerado pela própria API, então só responde quando ela subiu de
#    verdade (o Oracle demora alguns minutos na primeira inicialização).
# 2. Roda o comando recebido (por padrão "mvn -B clean test", ver CMD).
# 3. Copia o relatório do Cucumber e a saída do Maven para RELATORIOS_DIR, que
#    o docker-compose monta como volume na máquina host.
#
# Variáveis:
#   API_BASE_URL      URL da API (padrão http://localhost:8080)
#   API_WAIT_TIMEOUT  segundos máximos de espera pela API (padrão 300)
#   RELATORIOS_DIR    pasta de saída dos relatórios (padrão /testes/relatorios)
# ═════════════════════════════════════════════════════════════════════════════
set -uo pipefail

API_BASE_URL="${API_BASE_URL:-http://localhost:8080}"
API_WAIT_TIMEOUT="${API_WAIT_TIMEOUT:-300}"
RELATORIOS_DIR="${RELATORIOS_DIR:-/testes/relatorios}"
url="${API_BASE_URL%/}/swagger/v1/swagger.json"

echo "Aguardando a API em $url (limite de ${API_WAIT_TIMEOUT}s)..."
inicio=$(date +%s)
until [ "$(curl -s -o /dev/null -w '%{http_code}' --max-time 5 "$url")" = "200" ]; do
  if [ $(( $(date +%s) - inicio )) -ge "$API_WAIT_TIMEOUT" ]; then
    echo "ERRO: a API não respondeu HTTP 200 em ${API_WAIT_TIMEOUT}s" >&2
    exit 1
  fi
  sleep 5
done
echo "API respondendo após $(( $(date +%s) - inicio ))s."

mkdir -p "$RELATORIOS_DIR"
"$@" 2>&1 | tee "$RELATORIOS_DIR/saida-testes.txt"
status=$?

if [ -f target/cucumber-reports.html ]; then
  cp target/cucumber-reports.html "$RELATORIOS_DIR/"
  echo "Relatório copiado para $RELATORIOS_DIR/cucumber-reports.html"
fi

exit "$status"
