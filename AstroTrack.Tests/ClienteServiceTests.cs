using AstroTrack.DTOs.Requests;
using AstroTrack.Enums;
using AstroTrack.Exceptions;
using AstroTrack.Models;
using AstroTrack.Repositories.Interfaces;
using AstroTrack.Services.Implementations;
using Moq;

namespace AstroTrack.Tests;

public class ClienteServiceTests
{
    private readonly Mock<IClienteRepository> _clienteRepository = new();
    private readonly ClienteService _service;

    public ClienteServiceTests()
    {
        _service = new ClienteService(_clienteRepository.Object);
    }

    private static Cliente CriarCliente(long id, string nome = "Transportes Orion") => new()
    {
        IdCliente = id,
        Nome = nome,
        Cnpj = $"12.345.678/0001-{id:00}",
        Email = $"cliente{id}@astrotrack.com",
        Telefone = "11999990000",
        Status = StatusCadastro.ATIVO
    };

    [Fact]
    public async Task ListarTodosAsync_DeveRetornarTodosOsClientesMapeados()
    {
        _clienteRepository
            .Setup(r => r.GetAllAsync())
            .ReturnsAsync([CriarCliente(1, "Cliente A"), CriarCliente(2, "Cliente B")]);

        var resultado = await _service.ListarTodosAsync();

        Assert.Equal(2, resultado.Count);
        Assert.Equal(1, resultado[0].IdCliente);
        Assert.Equal("Cliente A", resultado[0].Nome);
        Assert.Equal(2, resultado[1].IdCliente);
        Assert.Equal("Cliente B", resultado[1].Nome);
        _clienteRepository.Verify(r => r.GetAllAsync(), Times.Once);
    }

    [Fact]
    public async Task BuscarPorIdAsync_ComIdExistente_DeveRetornarCliente()
    {
        var cliente = CriarCliente(10);
        _clienteRepository.Setup(r => r.GetByIdAsync(10)).ReturnsAsync(cliente);

        var resultado = await _service.BuscarPorIdAsync(10);

        Assert.Equal(10, resultado.IdCliente);
        Assert.Equal(cliente.Nome, resultado.Nome);
        Assert.Equal(cliente.Cnpj, resultado.Cnpj);
        Assert.Equal(cliente.Email, resultado.Email);
        Assert.Equal(StatusCadastro.ATIVO, resultado.Status);
    }

    [Fact]
    public async Task BuscarPorIdAsync_ComIdInexistente_DeveLancarNotFoundException()
    {
        _clienteRepository.Setup(r => r.GetByIdAsync(99)).ReturnsAsync((Cliente?)null);

        var ex = await Assert.ThrowsAsync<NotFoundException>(() => _service.BuscarPorIdAsync(99));

        Assert.Contains("99", ex.Message);
    }

    [Fact]
    public async Task InserirAsync_ComDadosValidos_DeveSalvarERetornarCliente()
    {
        var request = new ClienteRequest(
            "Nebula Logística", "98.765.432/0001-10", "contato@nebula.com", "11988887777", StatusCadastro.ATIVO);

        _clienteRepository.Setup(r => r.GetByCnpjAsync(request.Cnpj)).ReturnsAsync((Cliente?)null);
        _clienteRepository.Setup(r => r.GetByEmailAsync(request.Email)).ReturnsAsync((Cliente?)null);
        _clienteRepository
            .Setup(r => r.AddAsync(It.IsAny<Cliente>()))
            .ReturnsAsync((Cliente c) =>
            {
                c.IdCliente = 1;
                return c;
            });

        var resultado = await _service.InserirAsync(request);

        Assert.Equal(1, resultado.IdCliente);
        Assert.Equal(request.Nome, resultado.Nome);
        Assert.Equal(request.Cnpj, resultado.Cnpj);
        Assert.Equal(request.Email, resultado.Email);
        _clienteRepository.Verify(r => r.AddAsync(It.Is<Cliente>(c =>
            c.Nome == request.Nome &&
            c.Cnpj == request.Cnpj &&
            c.Email == request.Email &&
            c.Telefone == request.Telefone &&
            c.Status == request.Status)), Times.Once);
    }
}
