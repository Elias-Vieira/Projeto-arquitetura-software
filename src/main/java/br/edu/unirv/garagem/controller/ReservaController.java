package br.edu.unirv.garagem.controller;

import br.edu.unirv.garagem.model.Pessoa;
import br.edu.unirv.garagem.model.Reserva;
import br.edu.unirv.garagem.model.Veiculo;
import br.edu.unirv.garagem.repository.IPessoaRepository;
import br.edu.unirv.garagem.repository.IReservaRepository;
import br.edu.unirv.garagem.repository.IVeiculoRepository;
import br.edu.unirv.garagem.service.ReservaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ReservaController {

    private final IReservaRepository reservaRepository;
    private final IVeiculoRepository veiculoRepository;
    private final IPessoaRepository pessoaRepository;
    private final ReservaService reservaService;

    public ReservaController(IReservaRepository reservaRepository,
                             IVeiculoRepository veiculoRepository,
                             IPessoaRepository pessoaRepository,
                             ReservaService reservaService) {
        this.reservaRepository = reservaRepository;
        this.veiculoRepository = veiculoRepository;
        this.pessoaRepository = pessoaRepository;
        this.reservaService = reservaService;
    }

    public static class VeiculoStatusItem {
        private Veiculo veiculo;
        private String status; // "Disponível" ou "Reservado"

        public VeiculoStatusItem(Veiculo veiculo, String status) {
            this.veiculo = veiculo;
            this.status = status;
        }

        public Veiculo getVeiculo() { return veiculo; }
        public String getStatus() { return status; }
    }

    public static class ReservaViewItem {
        private Reserva reserva;
        private String veiculoInfo;
        private String pessoaNome;

        public ReservaViewItem(Reserva reserva, String veiculoInfo, String pessoaNome) {
            this.reserva = reserva;
            this.veiculoInfo = veiculoInfo;
            this.pessoaNome = pessoaNome;
        }

        public Reserva getReserva() { return reserva; }
        public String getVeiculoInfo() { return veiculoInfo; }
        public String getPessoaNome() { return pessoaNome; }
    }

    @GetMapping({"/", "/reservas"})
    public String index(Model model) {
        List<Veiculo> veiculos = veiculoRepository.obterTodas();
        List<Pessoa> pessoas = pessoaRepository.obterTodas();
        List<Reserva> reservas = reservaRepository.obterTodas();

        Map<Integer, Veiculo> veiculoMap = veiculos.stream()
                .collect(Collectors.toMap(Veiculo::getId, v -> v, (v1, v2) -> v1));
        Map<Integer, Pessoa> pessoaMap = pessoas.stream()
                .collect(Collectors.toMap(Pessoa::getId, p -> p, (p1, p2) -> p1));

        List<VeiculoStatusItem> veiculosComStatus = new ArrayList<>();
        for (Veiculo v : veiculos) {
            boolean reservado = reservaService.isVeiculoReservadoHoje(v.getId());
            veiculosComStatus.add(new VeiculoStatusItem(v, reservado ? "Reservado" : "Disponível"));
        }

        List<ReservaViewItem> reservasView = new ArrayList<>();
        for (Reserva r : reservas) {
            Veiculo v = veiculoMap.get(r.getVeiculoId());
            Pessoa p = pessoaMap.get(r.getPessoaId());
            String veiculoInfo = (v != null) ? (v.getPlaca() + " - " + v.getMarca() + " " + v.getModelo()) : "Veículo Desconhecido";
            String pessoaNome = (p != null) ? p.getNome() : "Pessoa Desconhecida";
            reservasView.add(new ReservaViewItem(r, veiculoInfo, pessoaNome));
        }

        model.addAttribute("veiculosStatus", veiculosComStatus);
        model.addAttribute("reservas", reservasView);
        return "reserva/index";
    }

    @GetMapping("/reservas/novo")
    public String novoForm(Model model) {
        model.addAttribute("reserva", new Reserva());
        model.addAttribute("veiculos", veiculoRepository.obterTodas());
        model.addAttribute("pessoas", pessoaRepository.obterTodas());
        model.addAttribute("titulo", "Nova Reserva");
        return "reserva/ReservaForm";
    }

    @PostMapping("/reservas/novo")
    public String salvarNovo(@ModelAttribute Reserva reserva, Model model) {
        String erro = reservaService.validarReserva(reserva, 0);
        if (erro != null) {
            model.addAttribute("erro", erro);
            model.addAttribute("reserva", reserva);
            model.addAttribute("veiculos", veiculoRepository.obterTodas());
            model.addAttribute("pessoas", pessoaRepository.obterTodas());
            model.addAttribute("titulo", "Nova Reserva");
            return "reserva/ReservaForm";
        }

        reservaRepository.adicionar(reserva);
        return "redirect:/reservas";
    }

    @GetMapping("/reservas/{id}/editar")
    public String editarForm(@PathVariable int id, Model model) {
        Optional<Reserva> reservaOpt = reservaRepository.obterPorId(id);
        if (reservaOpt.isEmpty()) {
            return "redirect:/reservas";
        }
        model.addAttribute("reserva", reservaOpt.get());
        model.addAttribute("veiculos", veiculoRepository.obterTodas());
        model.addAttribute("pessoas", pessoaRepository.obterTodas());
        model.addAttribute("titulo", "Editar Período da Reserva");
        return "reserva/ReservaForm";
    }

    @PostMapping("/reservas/{id}/editar")
    public String salvarEdicao(@PathVariable int id, @ModelAttribute Reserva reserva, Model model) {
        reserva.setId(id);
        String erro = reservaService.validarReserva(reserva, id);
        if (erro != null) {
            model.addAttribute("erro", erro);
            model.addAttribute("reserva", reserva);
            model.addAttribute("veiculos", veiculoRepository.obterTodas());
            model.addAttribute("pessoas", pessoaRepository.obterTodas());
            model.addAttribute("titulo", "Editar Período da Reserva");
            return "reserva/ReservaForm";
        }

        reservaRepository.atualizar(reserva);
        return "redirect:/reservas";
    }

    @PostMapping("/reservas/{id}/excluir")
    public String excluir(@PathVariable int id) {
        reservaRepository.remover(id);
        return "redirect:/reservas";
    }
}
