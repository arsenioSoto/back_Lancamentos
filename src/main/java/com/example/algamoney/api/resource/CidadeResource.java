package com.example.algamoney.api.resource;

import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.model.Cidade;
import com.example.algamoney.api.repository.CidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/cidades")
public class CidadeResource {
	
	@Autowired
	private CidadeRepository cidadeRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public List<Cidade> pesquisar(@RequestParam Long estado) {
		return cidadeRepository.findByEstadoCodigo(estado);
	}
	
	@PostMapping
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_CATEGORIA') and hasAuthority('SCOPE_write')")
	public ResponseEntity<Cidade> criar(@Valid @RequestBody Cidade cidade, HttpServletResponse response) {
		Cidade cidadeSalva = cidadeRepository.save(cidade);
		
		publisher.publishEvent(new RecursoCriadoEvent(this, response, cidadeSalva.getCodigo()));

		return ResponseEntity.status(HttpStatus.CREATED).body(cidadeSalva);
	}

    @GetMapping("/{codigo}")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and hasAuthority('SCOPE_read')")
    public ResponseEntity<Cidade> buscarPeloCodigo(@PathVariable Long codigo) {
        Optional<Cidade> cidade = cidadeRepository.findById(codigo);

        return cidade.isPresent() ? ResponseEntity.ok(cidade.get()) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_REMOVER_LANCAMENTO') and hasAuthority('SCOPE_write')")
	public void remover(@PathVariable Long codigo) {
		cidadeRepository.deleteById(codigo);
	}

		
	//@PutMapping("/{codigo}")
	//@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO')")
	//public ResponseEntity<Cidade> atualizar(@PathVariable Long codigo, @Valid @RequestBody Cidade cidade) {
		//try {
		//	Cidade cidadeSalvo = cidadeService.atualizar(codigo, lancamento);
		//	return ResponseEntity.ok(cidadeSalvo);
		//} catch (IllegalArgumentException e) {
	//		return ResponseEntity.notFound().build();
		//}
	//}

}
