using AstroTrack.DTOs.Requests;
using AstroTrack.Enums;
using AstroTrack.Models;
using AstroTrack.Repositories.Interfaces;
using AstroTrack.Services.Implementations;
using Moq;

namespace AstroTrack.Tests;

public class VeiculoServiceTests
{
    private readonly Mock<IVeiculoRepository> _veiculoRepository = new();
    private readonly VeiculoService _service;

    public VeiculoServiceTests()
    {
        _service = new VeiculoService(_veiculoRepository.Object);
    }

    [Fact]
    public async Task InserirAsync_ComPlacaNova_DeveSalvarERetornarVeiculo()
    {
        var request = new VeiculoRequest("ABC1D23", "Actros", "Mercedes-Benz", 2022, StatusVeiculo.DISPONIVEL);

        _veiculoRepository.Setup(r => r.GetByPlacaAsync(request.Placa)).ReturnsAsync((Veiculo?)null);
        _veiculoRepository
            .Setup(r => r.AddAsync(It.IsAny<Veiculo>()))
            .ReturnsAsync((Veiculo v) =>
            {
                v.IdVeiculo = 5;
                return v;
            });

        var resultado = await _service.InserirAsync(request);

        Assert.Equal(5, resultado.IdVeiculo);
        Assert.Equal("ABC1D23", resultado.Placa);
        Assert.Equal("Actros", resultado.Modelo);
        Assert.Equal("Mercedes-Benz", resultado.Marca);
        Assert.Equal(2022, resultado.Ano);
        Assert.Equal(StatusVeiculo.DISPONIVEL, resultado.Status);
        _veiculoRepository.Verify(r => r.AddAsync(It.Is<Veiculo>(v => v.Placa == request.Placa)), Times.Once);
    }

    [Fact]
    public async Task DeletarAsync_ComIdExistente_DeveRemoverVeiculo()
    {
        var veiculo = new Veiculo
        {
            IdVeiculo = 7,
            Placa = "XYZ9876",
            Modelo = "FH 540",
            Marca = "Volvo",
            Ano = 2020,
            Status = StatusVeiculo.DISPONIVEL
        };
        _veiculoRepository.Setup(r => r.GetByIdAsync(7)).ReturnsAsync(veiculo);

        await _service.DeletarAsync(7);

        _veiculoRepository.Verify(r => r.DeleteAsync(veiculo), Times.Once);
    }
}
