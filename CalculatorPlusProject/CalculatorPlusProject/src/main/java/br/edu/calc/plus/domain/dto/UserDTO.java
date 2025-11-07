package br.edu.calc.plus.domain.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import br.edu.calc.plus.domain.Usuario;
import groovy.transform.builder.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

	@NotNull
	@NotEmpty
	private String nome;

	@NotNull
	@NotEmpty
	private String login;

	@NotBlank(message = "A senha é obrigatória")
	@Size(min = 8, max = 16, message = "A senha deve ter entre 8 e 16 caracteres")
	private String senha;

	@NotNull
	@NotEmpty
	private String email;
	private String cidade;

	@Past
	@NotNull
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
	private LocalDate nascimento;

	public Usuario ConvertUsuario() {
		return new Usuario(null, nome, login, email, senha, cidade, nascimento);
	}

}
