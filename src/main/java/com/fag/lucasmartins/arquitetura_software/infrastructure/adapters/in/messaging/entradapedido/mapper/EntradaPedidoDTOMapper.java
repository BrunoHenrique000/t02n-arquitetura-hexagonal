package com.fag.lucasmartins.arquitetura_software.infrastructure.adapters.in.messaging.entradapedido.mapper;

import com.fag.lucasmartins.arquitetura_software.application.ports.out.persistence.PessoaRepositoryPort;
import com.fag.lucasmartins.arquitetura_software.application.ports.out.persistence.ProdutoRepositoryPort;
import com.fag.lucasmartins.arquitetura_software.core.domain.bo.*;
import com.fag.lucasmartins.arquitetura_software.infrastructure.adapters.in.messaging.entradapedido.dto.EntradaPedidoDTO;
import com.fag.lucasmartins.arquitetura_software.infrastructure.adapters.in.messaging.entradapedido.dto.OrderItemDTO;

import java.util.ArrayList;
import java.util.List;

public class EntradaPedidoDTOMapper {
    private final PessoaRepositoryPort pessoaRepo;
    private final ProdutoRepositoryPort produtoRepo;

    public EntradaPedidoDTOMapper(PessoaRepositoryPort pessoaRepo, ProdutoRepositoryPort produtoRepo) {
        this.pessoaRepo = pessoaRepo;
        this.produtoRepo = produtoRepo;
    }

    public PedidoBO mapToDomain(EntradaPedidoDTO dto) {
        PedidoBO bo = new PedidoBO();

        bo.setCep(dto.getZipCode());

        PessoaBO clienteBuscado = pessoaRepo.encontrarPorId(dto.getCustomerId());
        bo.setPessoa(clienteBuscado);

        List<PedidoProdutoBO> listaItens = new ArrayList<>();

        if (dto.getOrderItems() != null) {
            for (OrderItemDTO itemDto : dto.getOrderItems()) {
                PedidoProdutoBO produtoItem = new PedidoProdutoBO();

                produtoItem.setQuantidade(itemDto.getAmount());
                produtoItem.setProduto(produtoRepo.encontrarPorId(itemDto.getSku()));

                listaItens.add(produtoItem);
            }
        }

        bo.setItens(listaItens);
        return bo;
    }
}