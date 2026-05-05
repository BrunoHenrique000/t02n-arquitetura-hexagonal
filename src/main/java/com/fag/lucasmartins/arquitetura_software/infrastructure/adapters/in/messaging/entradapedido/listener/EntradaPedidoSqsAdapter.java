package com.fag.lucasmartins.arquitetura_software.infrastructure.adapters.in.messaging.entradapedido.listener;

import com.fag.lucasmartins.arquitetura_software.application.ports.in.service.PedidoServicePort;
import com.fag.lucasmartins.arquitetura_software.application.ports.out.persistence.PessoaRepositoryPort;
import com.fag.lucasmartins.arquitetura_software.application.ports.out.persistence.ProdutoRepositoryPort;
import com.fag.lucasmartins.arquitetura_software.core.domain.bo.PedidoBO;
import com.fag.lucasmartins.arquitetura_software.infrastructure.adapters.in.messaging.entradapedido.dto.EntradaPedidoDTO;
import com.fag.lucasmartins.arquitetura_software.infrastructure.adapters.in.messaging.entradapedido.mapper.EntradaPedidoDTOMapper;
import com.fag.lucasmartins.arquitetura_software.infrastructure.adapters.in.messaging.exceptions.ConsumerSQSException;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EntradaPedidoSqsAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntradaPedidoSqsAdapter.class);

    private final PedidoServicePort servicePort;
    private final EntradaPedidoDTOMapper dtoListMapper;

    public EntradaPedidoSqsAdapter(PedidoServicePort servicePort,
            PessoaRepositoryPort pessoaRepo,
            ProdutoRepositoryPort produtoRepo) {
        this.servicePort = servicePort;
        this.dtoListMapper = new EntradaPedidoDTOMapper(pessoaRepo, produtoRepo);
    }

    @SqsListener(value = "${aws.sqs.queue.entrada-pedido}")
    public void handleMessage(EntradaPedidoDTO payload) {
        try {
            LOGGER.info("Iniciando processamento da mensagem do cliente ID: {}", payload.getCustomerId());

            PedidoBO pedidoValidado = dtoListMapper.mapToDomain(payload);
            PedidoBO resultado = servicePort.criarPedido(pedidoValidado);

            LOGGER.info("Sucesso: Pedido {} gerado para o cliente {}", resultado.getId(), payload.getCustomerId());
        } catch (Exception ex) {
            LOGGER.error("Falha crítica ao processar SQS para o cliente {}", payload.getCustomerId());
            throw new ConsumerSQSException("Erro no processamento do evento de entrada", ex);
        }
    }
}