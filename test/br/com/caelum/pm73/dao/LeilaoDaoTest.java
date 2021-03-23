package br.com.caelum.pm73.dao;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Calendar.Builder;
import java.util.List;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
		
		Leilao ativo = new Leilao("Geladeria", 700.0, usuario, false);
		Leilao encerrado = new Leilao("PS5", 3500.0, usuario, false);
		encerrado.encerra();
		
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
}
