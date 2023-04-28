package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.ConnectionFactory;
import br.com.alura.bytebank.domain.RegraDeNegocioException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Set;

public class ContaService {

    private ConnectionFactory con; //declarar a variavel da nossa connectionfactory

    public ContaService(){
        this.con = new ConnectionFactory(); //no construtor da ContaService inicializar a variavel declarada acima assim abrindo a conexão somente quando um objeto do tipo conta service for instanciado
    }

    public Set<Conta> listarContasAbertas() {
        Connection connection = con.recoveryConnection();
        return new ContaDAO(connection).listAll();
    }

    public Conta listById(Integer numero){
        Connection connection = con.recoveryConnection();
        return new ContaDAO(connection).listByNumero(numero);
    }

    public BigDecimal consultarSaldo(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        return conta.getSaldo();
    }

    public void abrir(DadosAberturaConta dadosDaConta) {
        Connection conn = con.recoveryConnection(); //instancia um objeto do tipo conn para fazer o uso dos metodos para salvamento dos dados
        ContaDAO contaDAO = new ContaDAO(conn);
        contaDAO.save(dadosDaConta);
    }

    public void realizarSaque(Integer numeroDaConta, BigDecimal valor) {
        Conta conta = new ContaDAO(con.recoveryConnection()).listByNumero(numeroDaConta);
        if(conta == null){
            throw new RegraDeNegocioException("Conta não encontrada!");
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do saque deve ser superior a zero!");
        }

        if (valor.compareTo(conta.getSaldo()) > 0) {
            throw new RegraDeNegocioException("Saldo insuficiente!");
        }

        BigDecimal novoValor = conta.getSaldo().subtract(valor);
        new ContaDAO(con.recoveryConnection()).alterarSaldo(numeroDaConta, novoValor);
    }

    public void realizarDeposito(Integer numeroDaConta, BigDecimal valor) {
        Conta conta = new ContaDAO(con.recoveryConnection()).listByNumero(numeroDaConta);

        if(conta == null){
            throw new RegraDeNegocioException("Conta não encontrada!");
        }

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do deposito deve ser superior a zero!");
        }

        BigDecimal novoValor = conta.getSaldo().add(valor);
        new ContaDAO(con.recoveryConnection()).alterarSaldo(numeroDaConta, novoValor);
        
    }

    public void encerrar(Integer numeroDaConta) {
        var conta = new ContaDAO(con.recoveryConnection()).listByNumero(numeroDaConta);
        if(conta == null){
            throw new RegraDeNegocioException("Conta não encontrada!");
        }
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }

        new ContaDAO(con.recoveryConnection()).delete(numeroDaConta);
    }

    public void encerrarLogico(Integer numeroDaConta){
        var conta = new ContaDAO(con.recoveryConnection()).listByNumero(numeroDaConta);
        if(conta == null){
            throw new RegraDeNegocioException("Conta não encontrada!");
        }
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }

        new ContaDAO(con.recoveryConnection()).alterarLogico(numeroDaConta);
    }

    public void realizarTransferencia(Integer numeroDaContaOrigem, Integer numeroDaContaDestino, BigDecimal valor) {
        this.realizarSaque(numeroDaContaOrigem, valor);
        this.realizarDeposito(numeroDaContaDestino, valor);
    }

    private Conta buscarContaPorNumero(Integer numero) {
        var conta = new ContaDAO(con.recoveryConnection()).listByNumero(numero);
        if(conta == null){
            throw new RegraDeNegocioException("Conta não encontrada!");
        }

        return conta;
    }
}
