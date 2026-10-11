using AstroTrack.DTOs.Requests;
using AstroTrack.Enums;
using AstroTrack.Exceptions;
using AstroTrack.Models;
using AstroTrack.Repositories.Interfaces;
using AstroTrack.Services.Implementations;
using Moq;

namespace AstroTrack.Tests;

public class CheckpointServiceTests
{
    private readonly Mock<ICheckpointRepository> _checkpointRepository = new();
    private readonly Mock<IViagemRepository> _viagemRepository = new();
    private readonly CheckpointService _service;

    public CheckpointServiceTests()
    {
        _service = new CheckpointService(_checkpointRepository.Object, _viagemRepository.Object);
    }

    private static CheckpointRequest CriarRequest(long idViagem) => new(
        idViagem, -23.5505200m, -46.6333080m, new DateTime(2026, 10, 1, 10, 30, 0), 0, 1);

    [Fact]
    public async Task InserirAsync_EmViagemEmAndamento_DeveSalvarCheckpoint()
    {
        var viagem = new Viagem { IdViagem = 20, Status = StatusViagem.EM_ANDAMENTO };
        var request = CriarRequest(20);

        _viagemRepository.Setup(r => r.GetByIdAsync(20)).ReturnsAsync(viagem);
        _checkpointRepository
            .Setup(r => r.AddAsync(It.IsAny<Checkpoint>()))
            .ReturnsAsync((Checkpoint c) =>
            {
                c.IdCheckpoint = 300;
                return c;
            });

        var resultado = await _service.InserirAsync(request);

        Assert.Equal(300, resultado.IdCheckpoint);
        Assert.Equal(20, resultado.IdViagem);
        Assert.Equal(request.Latitude, resultado.Latitude);
        Assert.Equal(request.Longitude, resultado.Longitude);
        Assert.Equal(0, resultado.BotaoPanico);
        Assert.Equal(1, resultado.PortaAberta);
        _checkpointRepository.Verify(r => r.AddAsync(It.IsAny<Checkpoint>()), Times.Once);
    }

    [Fact]
    public async Task InserirAsync_EmViagemFinalizada_DeveLancarBadRequestException()
    {
        var viagem = new Viagem { IdViagem = 21, Status = StatusViagem.FINALIZADA };
        _viagemRepository.Setup(r => r.GetByIdAsync(21)).ReturnsAsync(viagem);

        await Assert.ThrowsAsync<BadRequestException>(() => _service.InserirAsync(CriarRequest(21)));

        _checkpointRepository.Verify(r => r.AddAsync(It.IsAny<Checkpoint>()), Times.Never);
    }
}
