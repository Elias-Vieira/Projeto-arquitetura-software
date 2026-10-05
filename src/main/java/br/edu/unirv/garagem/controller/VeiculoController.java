package br.edu.unirv.garagem.controller;

import br.edu.unirv.garagem.model.Veiculo;
import br.edu.unirv.garagem.repository.IVeiculoRepository;
import br.edu.unirv.garagem.service.ReservaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Year;
import java.util.Optional;

@Controller
@RequestMapping("/veiculos")
public class VeiculoController {

    private final IVeiculoRepository veiculoRepository;
    private final ReservaService reservaService;

    public VeiculoController(IVeiculoRepository veiculoRepository, ReservaService reservaService) {
        this.veiculoRepository = veiculoRepository;
        this.reservaService = reservaService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("veiculos", veiculoRepository.obterTodas());
        return "veiculo/index";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("veiculo", new Veiculo());
        model.addAttribute("titulo", "Cadastrar Novo Veículo");
        return "veiculo/VeiculoForm";
    }

    @PostMapping("/novo")
    public String salvarNovo(@ModelAttribute Veiculo veiculo, Model model) {
        String erro = validarVeiculo(veiculo, 0);
        if (erro != null) {
            model.addAttribute("erro", erro);
            model.addAttribute("veiculo", veiculo);
            model.addAttribute("titulo", "Cadastrar Novo Veículo");
            return "veiculo/VeiculoForm";
        }

        veiculo.setPlaca(veiculo.getPlaca().trim().toUpperCase());
        veiculoRepository.adicionar(veiculo);
        return "redirect:/veiculos";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable int id, Model model) {
        Optional<Veiculo> veiculoOpt = veiculoRepository.obterPorId(id);
        if (veiculoOpt.isEmpty()) {
            return "redirect:/veiculos";
        }
        model.addAttribute("veiculo", veiculoOpt.get());
        model.addAttribute("titulo", "Editar Veículo");
        return "veiculo/VeiculoForm";
    }

    @PostMapping("/{id}/editar")
    public String salvarEdicao(@PathVariable int id, @ModelAttribute Veiculo veiculo, Model model) {
        veiculo.setId(id);
        String erro = validarVeiculo(veiculo, id);
        if (erro != null) {
            model.addAttribute("erro", erro);
            model.addAttribute("veiculo", veiculo);
            model.addAttribute("titulo", "Editar Veículo");
            return "veiculo/VeiculoForm";
        }

        veiculo.setPlaca(veiculo.getPlaca().trim().toUpperCase());
        veiculoRepository.atualizar(veiculo);
        return "redirect:/veiculos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable int id, RedirectAttributes redirectAttributes) {
        if (reservaService.veiculoTemReservasAtivasOuFuturas(id)) {
            redirectAttributes.addFlashAttribute("erro", "Não é possível excluir este veículo pois ele possui reservas ativas ou futuras.");
            return "redirect:/veiculos";
        }
        veiculoRepository.remover(id);
        return "redirect:/veiculos";
    }

    private String validarVeiculo(Veiculo veiculo, int idAtual) {
        if (veiculo.getPlaca() == null || veiculo.getPlaca().trim().isEmpty()) {
            return "O campo Placa é obrigatório.";
        }
        if (veiculo.getMarca() == null || veiculo.getMarca().trim().isEmpty()) {
            return "O campo Marca é obrigatório.";
        }
        if (veiculo.getModelo() == null || veiculo.getModelo().trim().isEmpty()) {
            return "O campo Modelo é obrigatório.";
        }
        if (veiculo.getAno() < 1900 || veiculo.getAno() > Year.now().getValue() + 1) {
            return "Informe um Ano válido (entre 1900 e " + (Year.now().getValue() + 1) + ").";
        }

        String placaLimpa = veiculo.getPlaca().trim().toUpperCase();

        boolean placaDuplicada = veiculoRepository.obterTodas().stream()
                .filter(v -> v.getId() != idAtual)
                .anyMatch(v -> v.getPlaca().trim().equalsIgnoreCase(placaLimpa));

        if (placaDuplicada) {
            return "Já existe um veículo cadastrado com a placa (" + placaLimpa + ").";
        }

        return null;
    }
}
