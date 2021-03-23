package br.com.caelum.pm73.dao;

import static org.junit.Assert.assertEquals;

import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.caelum.pm73.dominio.Usuario;

public class UsuarioDaoTest {
	
	/** Para criar a estrutura das tabelas, primeiro execute a classe CriaTabelas do pacote
	 * curso. */
	
	@Test
	public void deveEncontrarPeloNomeEEmailMockado() {
		
		// estamos utilizando um banco de dados em memórida chamado HSQLDB
		Session session = new CriadorDeSessao().getSession();
		UsuarioDao usuarioDao = new UsuarioDao(session);
		
		String uNome = "João da Silva";
		String uEmail = "joao@email.com.br";
		Usuario novoUsuario = new Usuario(uNome, uEmail);
		
		usuarioDao.salvar(novoUsuario); // insert
		
		Usuario usuario = usuarioDao.porNomeEEmail(uNome, uEmail);
		
		assertEquals(uNome, usuario.getNome());
		assertEquals(uEmail, usuario.getEmail());
		
		session.close();
	}
}
