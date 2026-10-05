package br.edu.unirv.garagem.service;

import br.edu.unirv.garagem.model.Pessoa;
import br.edu.unirv.garagem.model.Reserva;
import br.edu.unirv.garagem.model.Veiculo;
import br.edu.unirv.garagem.repository.IPessoaRepository;
import br.edu.unirv.garagem.repository.IReservaRepository;
import br.edu.unirv.garagem.repository.IVeiculoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    private final IReservaRepository reservaRepository;
    private final IVeiculoRepository veiculoRepository;
    private final IPessoaRepository pessoaRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ReservaService(IReservaRepository reservaRepository,
                          IVeiculoRepository veiculoRepository,
                          IPessoaRepository pessoaRepository) {
        this.reservaRepository = reservaRepository;
        this.veiculoRepository = veiculoRepository;
        this.pessoaRepository = pessoaRepository;
    }

    public boolean existeConflito(int veiculoId, LocalDate inicio, LocalDate fim, int idIgnorado) {
        if (inicio == null || fim == null) {
            return false;
        }

        List<Reserva> reservas = reservaRepository.obterTodas();
        for (Reserva r : reservas) {
            if (r.getVeiculoId() == veiculoId && r.getId() != idIgnorado) {
                // novaInicio <= existenteFim && novaFim >= existenteInicio
                if (!inicio.isAfter(r.getDataFim()) && !fim.isBefore(r.getDataInicio())) {
                    return true;
                }
            }
        }
        return false;
    }

    public String validarReserva(Reserva reserva, int idIgnorado) {
        if (reserva.getVeiculoId() == null || reserva.getVeiculoId() <= 0) {
            return "Selecione um veículo válido.";
        }
        if (reserva.getPessoaId() == null || reserva.getPessoaId() <= 0) {
            return "Selecione uma pessoa válida.";
        }
        if (reserva.getDataInicio() == null) {
            return "A data de início é obrigatória.";
        }
        if (reserva.getDataFim() == null) {
            return "A data de fim é obrigatória.";
        }
        if (reserva.getDataFim().isBefore(reserva.getDataInicio())) {
            return "A data de fim (" + reserva.getDataFim().format(dateFormatter) + 
                   ") não pode ser anterior à data de início (" + reserva.getDataInicio().format(dateFormatter) + ").";
        }

        Optional<Veiculo> veiculoOpt = veiculoRepository.obterPorId(reserva.getVeiculoId());
        if (veiculoOpt.isEmpty()) {
            return "O veículo selecionado não foi encontrado no sistema.";
        }

        Optional<Pessoa> pessoaOpt = pessoaRepository.obterPorId(reserva.getPessoaId());
        if (pessoaOpt.isEmpty()) {
            return "A pessoa selecionada não foi encontrada no sistema.";
        }

        if (existeConflito(reserva.getVeiculoId(), reserva.getDataInicio(), reserva.getDataFim(), idIgnorado)) {
            Veiculo v = veiculoOpt.get();
            return "Bloqueado: O veículo (" + v.getPlaca() + " - " + v.getModelo() + ") já possui uma reserva que se sobrepõe ao período de "
                    + reserva.getDataInicio().format(dateFormatter) + " a " + reserva.getDataFim().format(dateFormatter) + ".";
        }

        return null;
    }

    public boolean isVeiculoReservadoHoje(int veiculoId) {
        LocalDate hoje = LocalDate.now();
        return reservaRepository.obterTodas().stream()
                .filter(r -> r.getVeiculoId() == veiculoId)
                .anyMatch(r -> (r.getDataInicio().isBefore(hoje) || r.getDataInicio().isEqual(hoje)) &&
                               (r.getDataFim().isAfter(hoje) || r.getDataFim().isEqual(hoje)));
    }

    public boolean pessoaTemReservasAtivasOuFuturas(int pessoaId) {
        LocalDate hoje = LocalDate.now();
        return reservaRepository.obterTodas().stream()
                .filter(r -> r.getPessoaId() == pessoaId)
                .anyMatch(r -> r.getDataFim().isAfter(hoje) || r.getDataFim().isEqual(hoje));
    }

    public boolean veiculoTemReservasAtivasOuFuturas(int veiculoId) {
        LocalDate hoje = LocalDate.now();
        return reservaRepository.obterTodas().stream()
                .filter(r -> r.getVeiculoId() == veiculoId)
                .anyMatch(r -> r.getDataFim().isAfter(hoje) || r.getDataFim().isEqual(hoje));
    }
}
