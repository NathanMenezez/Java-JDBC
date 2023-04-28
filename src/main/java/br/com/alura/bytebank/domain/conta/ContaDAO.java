package br.com.alura.bytebank.domain.conta;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import br.com.alura.bytebank.domain.cliente.Cliente;

public class ContaDAO {

    private Connection conn;
    ContaDAO(Connection connection){
        this.conn = connection;
    }

    public void save(DadosAberturaConta dadosDaConta){
        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), cliente);
        String sql = "INSERT INTO CONTA (numero, saldo, cliente_nome, cliente_cpf, cliente_email)" + //setar o código sql para insersão de dados na tabela
                    "VALUES (?,?,?,?,?)";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql); //instanciar um objeto do tipo preparedStatment no qual fica responsavel por setar os valores no comando sql e executa o comando
            preparedStatement.setInt(1, conta.getNumero()); // seta o tipo do valor a ser inserido e qual a posição dele no string do código sql
            preparedStatement.setBigDecimal(2, BigDecimal.ZERO);
            preparedStatement.setString(3, dadosDaConta.dadosCliente().nome());
            preparedStatement.setString(4, dadosDaConta.dadosCliente().cpf());
            preparedStatement.setString(5, dadosDaConta.dadosCliente().email());
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
