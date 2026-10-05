package br.edu.unirv.garagem.controller;

import br.edu.unirv.garagem.model.Pessoa;
import br.edu.unirv.garagem.repository.IPessoaRepository;
import br.edu.unirv.garagem.service.ReservaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/pessoas")
public class PessoaController {

    private final IPessoaRepository pessoaRepository;
    private final ReservaService reservaService;

    public PessoaController(IPessoaRepository pessoaRepository, ReservaService reservaService) {
        this.pessoaRepository = pessoaRepository;
        this.reservaService = reservaService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("pessoas", pessoaRepository.obterTodas());
        return "pessoa/index";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("pessoa", new Pessoa());
        model.addAttribute("titulo", "Cadastrar Nova Pessoa");
        return "pessoa/PessoaForm";
    }

    @PostMapping("/novo")
    public String salvarNovo(@ModelAttribute Pessoa pessoa, Model model) {
        String erro = validarPessoa(pessoa, 0);
        if (erro != null) {
            model.addAttribute("erro", erro);
            model.addAttribute("pessoa", pessoa);
            model.addAttribute("titulo", "Cadastrar Nova Pessoa");
            return "pessoa/PessoaForm";
        }

        pessoa.setCpf(formatarCpf(pessoa.getCpf()));
        pessoaRepository.adicionar(pessoa);
        return "redirect:/pessoas";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable int id, Model model) {
        Optional<Pessoa> pessoaOpt = pessoaRepository.obterPorId(id);
        if (pessoaOpt.isEmpty()) {
            return "redirect:/pessoas";
        }
        model.addAttribute("pessoa", pessoaOpt.get());
        model.addAttribute("titulo", "Editar Pessoa");
        return "pessoa/PessoaForm";
    }

    @PostMapping("/{id}/editar")
    public String salvarEdicao(@PathVariable int id, @ModelAttribute Pessoa pessoa, Model model) {
        pessoa.setId(id);
        String erro = validarPessoa(pessoa, id);
        if (erro != null) {
            model.addAttribute("erro", erro);
            model.addAttribute("pessoa", pessoa);
            model.addAttribute("titulo", "Editar Pessoa");
            return "pessoa/PessoaForm";
        }

        pessoa.setCpf(formatarCpf(pessoa.getCpf()));
        pessoaRepository.atualizar(pessoa);
        return "redirect:/pessoas";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable int id, RedirectAttributes redirectAttributes) {
        if (reservaService.pessoaTemReservasAtivasOuFuturas(id)) {
            redirectAttributes.addFlashAttribute("erro", "Não é possível excluir esta pessoa pois ela possui reservas ativas ou futuras.");
            return "redirect:/pessoas";
        }
        pessoaRepository.remover(id);
        return "redirect:/pessoas";
    }

    private String validarPessoa(Pessoa pessoa, int idAtual) {
        if (pessoa.getNome() == null || pessoa.getNome().trim().isEmpty()) {
            return "O campo Nome é obrigatório.";
        }
        if (pessoa.getCpf() == null || pessoa.getCpf().trim().isEmpty()) {
            return "O campo CPF é obrigatório.";
        }

        String cpfLimpo = pessoa.getCpf().replaceAll("\\D", "");
        if (cpfLimpo.length() != 11) {
            return "O CPF deve conter exatamente 11 dígitos numéricos.";
        }

        if (!isCpfValido(cpfLimpo)) {
            return "O CPF informado (" + pessoa.getCpf() + ") é inválido. Verifique os números digitados.";
        }

        if (pessoa.getEmail() == null || pessoa.getEmail().trim().isEmpty()) {
            return "O campo E-mail é obrigatório.";
        }
        if (!pessoa.getEmail().contains("@") || !pessoa.getEmail().contains(".")) {
            return "Informe um E-mail em formato válido.";
        }

        boolean cpfDuplicado = pessoaRepository.obterTodas().stream()
                .filter(p -> p.getId() != idAtual)
                .anyMatch(p -> p.getCpf().replaceAll("\\D", "").equals(cpfLimpo));

        if (cpfDuplicado) {
            return "Já existe uma pessoa cadastrada com este CPF (" + formatarCpf(cpfLimpo) + ").";
        }

        return null;
    }

    private boolean isCpfValido(String cpfLimpo) {
        if (cpfLimpo == null || cpfLimpo.length() != 11) return false;
        if (cpfLimpo.matches("(\\d)\\1{10}")) return false;

        try {
            int soma1 = 0;
            for (int i = 0; i < 9; i++) {
                soma1 += (cpfLimpo.charAt(i) - '0') * (10 - i);
            }
            int resto1 = (soma1 * 10) % 11;
            if (resto1 == 10) resto1 = 0;
            if (resto1 != (cpfLimpo.charAt(9) - '0')) return false;

            int soma2 = 0;
            for (int i = 0; i < 10; i++) {
                soma2 += (cpfLimpo.charAt(i) - '0') * (11 - i);
            }
            int resto2 = (soma2 * 10) % 11;
            if (resto2 == 10) resto2 = 0;
            return resto2 == (cpfLimpo.charAt(10) - '0');
        } catch (Exception e) {
            return false;
        }
    }

    private String formatarCpf(String cpf) {
        if (cpf == null) return "";
        String limpo = cpf.replaceAll("\\D", "");
        if (limpo.length() != 11) return cpf;
        return String.format("%s.%s.%s-%s",
                limpo.substring(0, 3),
                limpo.substring(3, 6),
                limpo.substring(6, 9),
                limpo.substring(9, 11));
    }
}
