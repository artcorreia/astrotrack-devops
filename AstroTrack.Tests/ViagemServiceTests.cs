using AstroTrack.DTOs.Requests;
using AstroTrack.Enums;
using AstroTrack.Exceptions;
using AstroTrack.Models;
using AstroTrack.Repositories.Interfaces;
using AstroTrack.Services.Implementations;
using Moq;

namespace AstroTrack.Tests;

public class ViagemServiceTests
{
    private readonly Mock<IViagemRepository> _viagemRepository = new();
    private readonly Mock<IClienteRepository> _clienteRepository = new();
    private readonly Mock<IMotoristaRepository> _motoristaRepository = new();
    private readonly Mock<IVeiculoRepository> _veiculoRepository = new();
    private readonly ViagemService _service;

    public ViagemServiceTests()
    {
        _service = new ViagemService(
            _viagemRepository.Object,
            _clienteRepository.Object,
            _motoristaRepository.Object,
            _veiculoRepository.Object);
    }

    [Fact]
    public async Task InserirAsync_EmAndamento_DeveSalvarViagemEMarcarVeiculoEmViagem()
    {
        var cliente = new Cliente
        {
            IdCliente = 1, Nome = "Transportes Orion", Cnpj = "12.345.678/0001-90",
            Email = "orion@astrotrack.com", Telefone = "11999990000", Status = StatusCadastro.ATIVO
        };
        var motorista = new Motorista
        {
            IdMotorista = 2, Nome = "Carlos Silva", Cpf = "123.456.789-00",
            Cnh = "12345678900", Telefone = "11988887777", Status = StatusCadastro.ATIVO
        };
        var veiculo = new Veiculo
        {
            IdVeiculo = 3, Placa = "ABC1D23", Modelo = "Actros", Marca = "Mercedes-Benz",
            Ano = 2022, Status = StatusVeiculo.DISPONIVEL
        };
        var request = new ViagemRequest(
            1, 2, 3, "São Paulo", "Curitiba",
            new DateTime(2026, 10, 1, 8, 0, 0), null, StatusViagem.EM_ANDAMENTO, 408.5m);

        _clienteRepository.Setup(r => r.GetByIdAsync(1)).ReturnsAsync(cliente);
        _motoristaRepository.Setup(r => r.GetByIdAsync(2)).ReturnsAsync(motorista);
        _veiculoRepository.Setup(r => r.GetByIdAsync(3)).ReturnsAsync(veiculo);
        _veiculoRepository.Setup(r => r.UpdateAsync(veiculo)).ReturnsAsync(veiculo);
        _viagemRepository.Setup(r => r.GetViagensEmAndamentoAsync()).ReturnsAsync([]);
        _viagemRepository
            .Setup(r => r.AddAsync(It.IsAny<Viagem>()))
            .ReturnsAsync((Viagem v) =>
            {
                v.IdViagem = 100;
                return v;
            });

        var resultado = await _service.InserirAsync(request);

        Assert.Equal(100, resultado.IdViagem);
        Assert.Equal("Transportes Orion", resultado.NomeCliente);
        Assert.Equal("Carlos Silva", resultado.NomeMotorista);
        Assert.Equal("ABC1D23", resultado.PlacaVeiculo);
        Assert.Equal(StatusViagem.EM_ANDAMENTO, resultado.Status);
        Assert.Equal(StatusVeiculo.EM_VIAGEM, veiculo.Status);
        _veiculoRepository.Verify(r => r.UpdateAsync(veiculo), Times.Once);
        _viagemRepository.Verify(r => r.AddAsync(It.IsAny<Viagem>()), Times.Once);
    }

    [Fact]
    public async Task DeletarAsync_ComViagemEmAndamento_DeveLancarBadRequestException()
    {
        var viagem = new Viagem { IdViagem = 50, Status = StatusViagem.EM_ANDAMENTO };
        _viagemRepository.Setup(r => r.GetByIdAsync(50)).ReturnsAsync(viagem);

        await Assert.ThrowsAsync<BadRequestException>(() => _service.DeletarAsync(50));

        _viagemRepository.Verify(r => r.DeleteAsync(It.IsAny<Viagem>()), Times.Never);
    }
}
