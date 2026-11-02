# ═════════════════════════════════════════════════════════════════════════════
# AstroTrack - container dos testes BDD e de API (Cucumber + RestAssured)
#
# Uso pelo docker-compose da raiz (sobe Oracle e API antes, se preciso):
#   docker compose --profile testes run --rm --build testes
#
# A URL da API vem de API_BASE_URL (no compose: http://api:8080). O entrypoint
# espera a API responder antes de rodar os testes e copia o relatório para
# /testes/relatorios (volume montado na máquina host).
# ═════════════════════════════════════════════════════════════════════════════
FROM maven:3.9-eclipse-temurin-17
WORKDIR /testes

# Estratégia de cache: copiamos PRIMEIRO só o pom.xml e baixamos as
# dependências. Alterar features, steps ou schemas não invalida essa camada,
# então os pacotes do Maven não são baixados de novo a cada build.
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY executar-testes.sh /usr/local/bin/executar-testes.sh
RUN chmod +x /usr/local/bin/executar-testes.sh

COPY src ./src

ENV API_BASE_URL=http://localhost:8080 \
    API_WAIT_TIMEOUT=300 \
    RELATORIOS_DIR=/testes/relatorios

ENTRYPOINT ["executar-testes.sh"]
CMD ["mvn", "-B", "clean", "test"]
