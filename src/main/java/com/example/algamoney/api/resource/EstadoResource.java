package com.example.algamoney.api.resource;

import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.model.Estado;
import com.example.algamoney.api.repository.EstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/estados")
public class EstadoResource {
	
	@Autowired
	private EstadoRepository estadoRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public List<Estado> listar() {
		return estadoRepository.findAll();
	}
	
	@PostMapping
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_CATEGORIA') and hasAuthority('SCOPE_write')")
	public ResponseEntity<Estado> criar(@Valid @RequestBody Estado estado, HttpServletResponse response) {
		Estado estadoSalva = estadoRepository.save(estado);
		
		publisher.publishEvent(new RecursoCriadoEvent(this, response, estadoSalva.getCodigo()));

		return ResponseEntity.status(HttpStatus.CREATED).body(estadoSalva);
	}

    @GetMapping("/{codigo}")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and hasAuthority('SCOPE_read')")
    public ResponseEntity<Estado> buscarPeloCodigo(@PathVariable Long codigo) {
        Optional<Estado> estado = estadoRepository.findById(codigo);

        return estado.isPresent() ? ResponseEntity.ok(estado.get()) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_REMOVER_LANCAMENTO') and hasAuthority('SCOPE_write')")
	public void remover(@PathVariable Long codigo) {
		estadoRepository.deleteById(codigo);
	}

		
	//@PutMapping("/{codigo}")
	//@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO')")
	//public ResponseEntity<Estado> atualizar(@PathVariable Long codigo, @Valid @RequestBody Estado estado) {
		//try {
		//	Estado estadoSalvo = estadoService.atualizar(codigo, lancamento);
		//	return ResponseEntity.ok(estadoSalvo);
		//} catch (IllegalArgumentException e) {
	//		return ResponseEntity.notFound().build();
		//}
	//}

}

