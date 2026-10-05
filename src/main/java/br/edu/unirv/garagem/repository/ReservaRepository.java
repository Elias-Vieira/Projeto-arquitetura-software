package br.edu.unirv.garagem.repository;

import br.edu.unirv.garagem.model.Reserva;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ReservaRepository implements IReservaRepository {

    private final String filePath = "data/reservas.json";
    private final ObjectMapper objectMapper;

    public ReservaRepository() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        garantirArquivoExiste();
    }

    private synchronized void garantirArquivoExiste() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                List<Reserva> iniciais = new ArrayList<>();
                // Exemplo inicial de reserva
                iniciais.add(new Reserva(1, 1, 1, LocalDate.now().minusDays(1), LocalDate.now().plusDays(4)));
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, iniciais);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized List<Reserva> lerTodasDoArquivo() {
        File file = new File(filePath);
        if (!file.exists()) {
            garantirArquivoExiste();
        }
        try {
            return objectMapper.readValue(file, new TypeReference<List<Reserva>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private synchronized void salvarTodasNoArquivo(List<Reserva> reservas) {
        try {
            File file = new File(filePath);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, reservas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Reserva> obterTodas() {
        return lerTodasDoArquivo();
    }

    @Override
    public Optional<Reserva> obterPorId(int id) {
        return lerTodasDoArquivo().stream()
                .filter(r -> r.getId() == id)
                .findFirst();
    }

    @Override
    public void adicionar(Reserva reserva) {
        List<Reserva> reservas = lerTodasDoArquivo();
        int novoId = reservas.stream()
                .mapToInt(Reserva::getId)
                .max()
                .orElse(0) + 1;
        reserva.setId(novoId);
        reservas.add(reserva);
        salvarTodasNoArquivo(reservas);
    }

    @Override
    public void atualizar(Reserva reserva) {
        List<Reserva> reservas = lerTodasDoArquivo();
        for (int i = 0; i < reservas.size(); i++) {
            if (reservas.get(i).getId() == reserva.getId()) {
                reservas.set(i, reserva);
                break;
            }
        }
        salvarTodasNoArquivo(reservas);
    }

    @Override
    public void remover(int id) {
        List<Reserva> reservas = lerTodasDoArquivo();
        reservas.removeIf(r -> r.getId() == id);
        salvarTodasNoArquivo(reservas);
    }
}
