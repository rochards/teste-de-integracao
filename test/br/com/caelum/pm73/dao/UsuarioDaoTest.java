package br.com.caelum.pm73.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.caelum.pm73.dominio.Usuario;

public class UsuarioDaoTest {
	
	/** Para criar a estrutura das tabelas, primeiro execute a classe CriaTabelas do pacote
	 * curso. */
	
	private Session session;
	private UsuarioDao usuarioDao;
	
	@Before
	public void setUp() {
		// estamos utilizando um banco de dados em memórida chamado HSQLDB
		this.session = new CriadorDeSessao().getSession();
		this.usuarioDao = new UsuarioDao(session);
	}
	
	@After
	public void closeSession() {
		this.session.close();
	}
	
	@Test
	public void deveEncontrarPeloNomeEEmail() {
		
		String uNome = "João da Silva";
		String uEmail = "joao@email.com.br";
		Usuario novoUsuario = new Usuario(uNome, uEmail);
		
		usuarioDao.salvar(novoUsuario); // insert
		
		Usuario usuario = usuarioDao.porNomeEEmail(uNome, uEmail);
		
		assertEquals(uNome, usuario.getNome());
		assertEquals(uEmail, usuario.getEmail());
	}
	
	@Test
	public void deveRetornarUsuarioNulo() {
		
		String uNome = "João Siqueira";
		String uEmail = "siqueira@email.com.br";
		
		Usuario usuario = usuarioDao.porNomeEEmail(uNome, uEmail);
		
		assertNull(usuario);
	}
}
