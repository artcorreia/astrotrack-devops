# ═════════════════════════════════════════════════════════════════════════════
# AstroTrack API - Dockerfile multi-stage
#
# A imagem é construída em dois estágios: um com o SDK completo do .NET, usado
# só para compilar, e outro apenas com o runtime do ASP.NET, que é o que vai
# para produção. O SDK (~800 MB), o código-fonte e os artefatos intermediários
# ficam no primeiro estágio e não chegam à imagem final, que fica menor, sobe
# mais rápido e tem menos superfície de ataque.
# ═════════════════════════════════════════════════════════════════════════════


# ─── Estágio 1: build ────────────────────────────────────────────────────────
# Imagem com o SDK do .NET 9 (compilador, NuGet, MSBuild).
FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build
WORKDIR /src

# Estratégia de cache: copiamos PRIMEIRO só o .csproj e rodamos o restore.
# O Docker cacheia cada camada; como o .csproj muda raramente, a camada com os
# pacotes NuGet baixados é reaproveitada nos builds seguintes. Alterar apenas
# arquivos .cs não invalida esse cache, então o restore não roda de novo.
COPY AstroTrack/AstroTrack.csproj AstroTrack/
RUN dotnet restore AstroTrack/AstroTrack.csproj

# Agora sim copiamos o restante do código e publicamos em Release.
# --no-restore reaproveita os pacotes da camada anterior.
# UseAppHost=false: não gera o executável nativo; a app roda com "dotnet AstroTrack.dll".
COPY AstroTrack/ AstroTrack/
RUN dotnet publish AstroTrack/AstroTrack.csproj \
    --configuration Release \
    --output /app/publish \
    --no-restore \
    /p:UseAppHost=false


# ─── Estágio 2: runtime (imagem final) ───────────────────────────────────────
# Imagem só com o runtime do ASP.NET Core 9, sem SDK nem código-fonte.
FROM mcr.microsoft.com/dotnet/aspnet:9.0 AS final
WORKDIR /app

# Apenas o resultado do publish é copiado do estágio de build.
# Os arquivos ficam com dono root: o usuário da aplicação consegue ler e
# executar, mas não alterar os binários.
COPY --from=build /app/publish .

# Porta HTTP da API. O Program.cs lê a variável PORT e escuta nela.
# ASPNETCORE_HTTP_PORTS (que a imagem base define como 8080) é limpa para
# que PORT seja a única fonte da porta.
ENV PORT=8080 \
    ASPNETCORE_HTTP_PORTS=
EXPOSE 8080

# Segurança: roda como o usuário não-root "app", que já existe na imagem base.
# Se a aplicação for comprometida, o atacante não tem privilégios de root no container.
USER app

ENTRYPOINT ["dotnet", "AstroTrack.dll"]
