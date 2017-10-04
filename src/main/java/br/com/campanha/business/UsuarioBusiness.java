package br.com.campanha.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.campanha.comum.BaseValidator;
import br.com.campanha.domain.Usuario;
import br.com.campanha.domain.UsuarioMessage;
import br.com.campanha.exception.BasePersistException;
import br.com.campanha.repository.UsuarioBaseRepository;
import br.com.campanha.repository.UsuarioSearchRepository;

@Service
public class UsuarioBusiness {

	@Autowired
	private UsuarioSearchRepository searchDao;

	@Autowired
	private UsuarioBaseRepository baseDao;

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public UsuarioMessage putNew(UsuarioMessage message) throws Exception {
		message.setId(null);
		Usuario workUsuario = baseDao.save(new Usuario(message));
		return workUsuario.buildMessage();
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public UsuarioMessage putUpdate(UsuarioMessage message) throws Exception {
		Usuario workUsuario = message.getId() == null ? null : baseDao.findOne(message.getId());
		BaseValidator.notNull("Usuario nao localizado", workUsuario);
		this.validateExistente(message.getEmail(), message.getId());
		workUsuario = baseDao.save(workUsuario.updateValues(message));
		return workUsuario.buildMessage();
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void inativar(Long entityId) throws Exception {
		baseDao.findOne(entityId).getBaseModel().inativar();
	}

	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public Usuario findOneUsuarioByEmail(String email) {
		if (email == null) {
			return null;
		}
		return searchDao.findOneUsuarioByEmail(email);
	}

	private void validateExistente(String email, Long id) throws BasePersistException {
		if (id == null) {
			throw new BasePersistException("Id obrigatorio");
		}
		Usuario usuarioByEmail = searchDao.findOneUsuarioByEmail(email);
		if (usuarioByEmail != null && usuarioByEmail.getId().equals(id) == false) {
			throw new BasePersistException("Email ja esta em uso por outro usuario");
		}
	}
}
