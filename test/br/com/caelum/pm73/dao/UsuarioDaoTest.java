package br.com.caelum.pm73.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
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
		
		this.session.beginTransaction();
	}
	
	@After
	public void closeSession() {
		this.session.getTransaction().rollback(); // boa prática para não deixar o dado salvo no banco, para não atrapalhar os testes
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
	
	@Test
	public void deveDeletarUmUsuario() {
		
		Usuario usuario = new Usuario("Joseph", "jose@mail.com.br");
		
		usuarioDao.salvar(usuario);
		usuarioDao.deletar(usuario);
		
		// fazer esses comando só devem ser usados pq estamos utilizando o Hibernate, ele coloca
		// muita coisa em cache, por isso chamamos os métodos abaixo.
		session.flush(); // garante que o comando vai para o banco
		session.clear(); // apaga o cache
		/* Geralmente em testes que fazemos SELECTs logo após uma deleção ou alteração em batch, 
		 * o uso do flush é obrigatório.*/
		
		Usuario deletado = usuarioDao.porNomeEEmail("Joseph", "jose@mail.com.br");
		
		Assert.assertNull(deletado);
	}
	
	@Test
	public void deveAlterarUmUsuario() {
		
		Usuario usuario = new Usuario("Joseph", "jose@mail.com.br");
		
		usuarioDao.salvar(usuario);
		
		usuario.setNome("Jose");
		usuarioDao.atualizar(usuario);
		
		session.flush();
		
		Usuario usuarioNulo = usuarioDao.porNomeEEmail("Joseph", "jose@mail.com.br");
		Usuario usuarioAtualizado = usuarioDao.porNomeEEmail("Jose", "jose@mail.com.br");
		
		assertNull(usuarioNulo);
		assertEquals(usuario.getNome(), usuarioAtualizado.getNome());
	}
}
