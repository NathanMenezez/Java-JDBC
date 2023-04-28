package br.com.alura.bytebank.domain.conta;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.management.RuntimeErrorException;

import br.com.alura.bytebank.domain.cliente.Cliente;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;

public class ContaDAO {

    private Connection conn;
    ContaDAO(Connection connection){
        this.conn = connection;
    }

    public void save(DadosAberturaConta dadosDaConta){
        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), cliente, BigDecimal.ZERO, true);
        String sql = "INSERT INTO CONTA (numero, saldo, cliente_nome, cliente_cpf, cliente_email, esta_ativa)" + //setar o código sql para insersão de dados na tabela
                    "VALUES (?,?,?,?,?,?)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql); //instanciar um objeto do tipo preparedStatment no qual fica responsavel por setar os valores no comando sql e executa o comando
            preparedStatement.setInt(1, conta.getNumero()); // seta o tipo do valor a ser inserido e qual a posição dele no string do código sql
            preparedStatement.setBigDecimal(2, BigDecimal.ZERO);
            preparedStatement.setString(3, dadosDaConta.dadosCliente().nome());
            preparedStatement.setString(4, dadosDaConta.dadosCliente().cpf());
            preparedStatement.setString(5, dadosDaConta.dadosCliente().email());
            preparedStatement.setBoolean(6, conta.getEstaAtiva());
            preparedStatement.execute();
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Conta> listAll(){
        Set<Conta> contas = new HashSet<>();
        String sql = "SELECT * FROM CONTA WHERE esta_ativa = true";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet result = preparedStatement.executeQuery();
            
            while(result.next()){
                Integer numero = result.getInt(1);
                BigDecimal saldo = result.getBigDecimal(2);
                String nome = result.getString(3);
                String cpf = result.getString(4);
                String email = result.getString(5);
                Boolean estaAtiva = result.getBoolean(6);
                DadosCadastroCliente cadastroCliente = new DadosCadastroCliente(nome, cpf, email);
                contas.add(new Conta(numero, new Cliente(cadastroCliente), saldo, estaAtiva));
            }
            preparedStatement.close();
            result.close();
            conn.close();
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return contas;
    }

    public Conta listByNumero(Integer numero){
        Conta conta = null;
        String sql = "SELECT * FROM conta WHERE numero = ? AND esta_ativa = true";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, numero);
            ResultSet result = preparedStatement.executeQuery();
            while(result.next()){
                Integer numeroConta = result.getInt(1);
                BigDecimal saldo = result.getBigDecimal(2);
                String nome = result.getString(3);
                String cpf = result.getString(4);
                String email = result.getString(5);
                Boolean estaAtiva = result.getBoolean(6);
                DadosCadastroCliente cadastroCliente = new DadosCadastroCliente(nome, cpf, email);
                conta = new Conta(numeroConta, new Cliente(cadastroCliente), saldo, estaAtiva);
            }
            result.close();
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conta;
    }

    public void alterarSaldo(Integer numeroConta, BigDecimal valor){
        PreparedStatement preparedStatement;
        String sql = "UPDATE conta SET saldo = ? WHERE numero = ?;";

        try {
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setBigDecimal(1, valor);
            preparedStatement.setInt(2, numeroConta);
            preparedStatement.execute();
            preparedStatement.close();
            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Integer numeroConta){
        String sql = "DELETE FROM conta WHERE numero = ?;";

        PreparedStatement preparedStatement;

        try{
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, numeroConta);
            preparedStatement.execute();
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void alterarLogico(Integer numeroDaConta) {
        PreparedStatement preparedStatement;
        String sql = "UPDATE conta SET esta_ativa = false WHERE numero = ?;";

        try {
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, numeroDaConta);
            preparedStatement.execute();
            preparedStatement.close();
            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
