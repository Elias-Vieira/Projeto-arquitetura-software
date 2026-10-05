package br.edu.unirv.garagem.repository;

import br.edu.unirv.garagem.model.Veiculo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VeiculoRepository implements IVeiculoRepository {

    private final String filePath = "data/veiculos.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public VeiculoRepository() {
        garantirArquivoExiste();
    }

    private synchronized void garantirArquivoExiste() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                List<Veiculo> iniciais = new ArrayList<>();
                iniciais.add(new Veiculo(1, "ABC-1D23", "Toyota", "Corolla", 2022, "Prata"));
                iniciais.add(new Veiculo(2, "XYZ-9876", "Honda", "Civic", 2021, "Preto"));
                iniciais.add(new Veiculo(3, "KKL-4567", "Fiat", "Toro", 2023, "Branco"));
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, iniciais);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized List<Veiculo> lerTodasDoArquivo() {
        File file = new File(filePath);
        if (!file.exists()) {
            garantirArquivoExiste();
        }
        try {
            return objectMapper.readValue(file, new TypeReference<List<Veiculo>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private synchronized void salvarTodasNoArquivo(List<Veiculo> veiculos) {
        try {
            File file = new File(filePath);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, veiculos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Veiculo> obterTodas() {
        return lerTodasDoArquivo();
    }

    @Override
    public Optional<Veiculo> obterPorId(int id) {
        return lerTodasDoArquivo().stream()
                .filter(v -> v.getId() == id)
                .findFirst();
    }

    @Override
    public void adicionar(Veiculo veiculo) {
        List<Veiculo> veiculos = lerTodasDoArquivo();
        int novoId = veiculos.stream()
                .mapToInt(Veiculo::getId)
                .max()
                .orElse(0) + 1;
        veiculo.setId(novoId);
        veiculos.add(veiculo);
        salvarTodasNoArquivo(veiculos);
    }

    @Override
    public void atualizar(Veiculo veiculo) {
        List<Veiculo> veiculos = lerTodasDoArquivo();
        for (int i = 0; i < veiculos.size(); i++) {
            if (veiculos.get(i).getId() == veiculo.getId()) {
                veiculos.set(i, veiculo);
                break;
            }
        }
        salvarTodasNoArquivo(veiculos);
    }

    @Override
    public void remover(int id) {
        List<Veiculo> veiculos = lerTodasDoArquivo();
        veiculos.removeIf(v -> v.getId() == id);
        salvarTodasNoArquivo(veiculos);
    }
}
