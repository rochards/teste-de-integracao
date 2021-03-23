package br.com.caelum.pm73.dao;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Calendar.Builder;
import java.util.List;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.pm73.builder.LeilaoBuilder;
import br.com.caelum.pm73.dominio.Leilao;
import br.com.caelum.pm73.dominio.Usuario;

public class LeilaoDaoTest {
	
	/** Para criar a estrutura das tabelas, primeiro execute a classe CriaTabelas do pacote
	 * curso. */
	
	private Session session;
	private UsuarioDao usuarioDao;
	private LeilaoDao leilaoDao;
	
	@Before
	public void setUp() {
		// estamos utilizando um banco de dados em memórida chamado HSQLDB
		this.session = new CriadorDeSessao().getSession();
		this.usuarioDao = new UsuarioDao(session);
		this.leilaoDao = new LeilaoDao(session);
		
		this.session.beginTransaction();
	}
	
	@After
	public void closeSession() {
		this.session.getTransaction().rollback(); // boa prática para não deixar o dado salvo no banco, para não atrapalhar os testes
		this.session.close();
	}
	
	@Test
	public void deveContarLeiloesNaoEncerrados() {
		
		String uNome = "maurício";
		String uEmail = "mauricio@email.com.br";
		Usuario usuario = new Usuario(uNome, uEmail);
		
		Leilao ativo = new LeilaoBuilder()
				.comNome("Geladeira")
				.comDono(usuario)
				.comValor(700.0)
				.constroi();
		Leilao encerrado = new LeilaoBuilder()
				.comNome("SP5")
				.comDono(usuario)
				.comValor(3500.0)
				.usado()
				.encerrado()
				.constroi();
		
		usuarioDao.salvar(usuario);
		leilaoDao.salvar(ativo);
		leilaoDao.salvar(encerrado);
		
		long total = leilaoDao.total();
		
		assertEquals(1L, total);
	}
	
	@Test
	public void deveContarLeiloesEncerradosIgualAZero() {
		
		String uNome = "maurício";
		String uEmail = "mauricio@email.com.br";
		Usuario usuario = new Usuario(uNome, uEmail);
		
		Leilao encerrado1 = new Leilao("Geladeria", 700.0, usuario, false);
		encerrado1.encerra();
		Leilao encerrado2 = new Leilao("PS5", 3500.0, usuario, false);
		encerrado2.encerra();
		
		usuarioDao.salvar(usuario);
		leilaoDao.salvar(encerrado1);
		leilaoDao.salvar(encerrado2);
		
		long total = leilaoDao.total();
		
		assertEquals(0, total);
	}
	
	@Test
	public void deveContarLeiloesNovos() {
		
		String uNome = "maurício";
		String uEmail = "mauricio@email.com.br";
		Usuario usuario = new Usuario(uNome, uEmail);
		
		Leilao novo = new Leilao("Geladeira", 700.0, usuario, false);
		Leilao usado = new Leilao("PS5", 3500.0, usuario, true);
		
		usuarioDao.salvar(usuario);
		leilaoDao.salvar(novo);
		leilaoDao.salvar(usado);
		
		List<Leilao> listaDeNovos = leilaoDao.novos();
		
		assertEquals(1, listaDeNovos.size());
		assertEquals("Geladeira", listaDeNovos.get(0).getNome());
	}
	
	@Test
	public void deveContarLeiloesAntigos() {
		
		String uNome = "maurício";
		String uEmail = "mauricio@email.com.br";
		Usuario usuario = new Usuario(uNome, uEmail);
		
		Calendar dataRecente = Calendar.getInstance();
        Calendar dataAntiga = Calendar.getInstance();
        dataAntiga.add(Calendar.DAY_OF_MONTH, -10);
        
		Leilao novo = new Leilao("Geladeira", 700.0, usuario, false);
		novo.setDataAbertura(dataRecente);
		Leilao antigo = new Leilao("PS5", 3500.0, usuario, true);
		antigo.setDataAbertura(dataAntiga);
		
		
		usuarioDao.salvar(usuario);
		leilaoDao.salvar(novo);
		leilaoDao.salvar(antigo);
		
		List<Leilao> listaDeAntigos = leilaoDao.antigos();
		assertEquals(1, listaDeAntigos.size());
	}
	
	@Test
    public void deveTrazerSomenteLeiloesAntigosHaMaisDe7Dias() {
        
		Usuario mauricio = new Usuario("Mauricio Aniche",
                "mauricio@aniche.com.br");

        Leilao noLimite = 
                new Leilao("XBox", 700.0, mauricio, false);

        Calendar dataAntiga = Calendar.getInstance();
        dataAntiga.add(Calendar.DAY_OF_MONTH, -7);

        noLimite.setDataAbertura(dataAntiga);

        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(noLimite);

        List<Leilao> antigos = leilaoDao.antigos();

        assertEquals(1, antigos.size());
    }
	
	@Test
	public void deveTrazerLeiloesNaoEncerradosNoPeriodo() {
		
		Calendar comecoDoIntervalo = Calendar.getInstance();
		comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
		Calendar fimDoIntervalo = Calendar.getInstance();
		
		Usuario usuario = new Usuario("Gio", "gio@email.com.br");
		
		Leilao l1 = new Leilao("PS5", 3940.0, usuario, false);
		Calendar datal1 = Calendar.getInstance();
		datal1.add(Calendar.DAY_OF_MONTH, -2);
		l1.setDataAbertura(datal1);
		
		Leilao l2 = new Leilao("TV", 2500.0, usuario, false);
		Calendar datal2 = Calendar.getInstance();
		datal2.add(Calendar.DAY_OF_MONTH, -20);
		l2.setDataAbertura(datal2);
		
		usuarioDao.salvar(usuario);
		leilaoDao.salvar(l1);
		leilaoDao.salvar(l2);
		
		List<Leilao> leiloes = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);
		
		assertEquals(1, leiloes.size());
		assertEquals("PS5", leiloes.get(0).getNome());
	}
	
	@Test
	public void naoDeveTrazerLeiloesEncerradosNoPeriodo() {
		
		Calendar comecoDoIntervalo = Calendar.getInstance();
		comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
		Calendar fimDoIntervalo = Calendar.getInstance();
		
		Usuario usuario = new Usuario("Gio", "gio@email.com.br");
		
		Leilao l1 = new Leilao("PS5", 3940.0, usuario, false);
		Calendar datal1 = Calendar.getInstance();
		datal1.add(Calendar.DAY_OF_MONTH, -2);
		l1.setDataAbertura(datal1);
		l1.encerra();
		
		usuarioDao.salvar(usuario);
		leilaoDao.salvar(l1);
		
		List<Leilao> leiloes = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);
		
		assertEquals(0, leiloes.size());
	}
}
