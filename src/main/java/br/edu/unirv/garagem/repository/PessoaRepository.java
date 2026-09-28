package br.edu.unirv.garagem.repository;

import br.edu.unirv.garagem.model.Pessoa;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PessoaRepository implements IPessoaRepository {

    private final String filePath = "data/pessoas.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PessoaRepository() {
        garantirArquivoExiste();
    }

    private synchronized void garantirArquivoExiste() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                List<Pessoa> iniciais = new ArrayList<>();
                iniciais.add(new Pessoa(1, "Ana Souza", "111.222.333-44", "ana.souza@email.com", "(64) 99999-0001"));
                iniciais.add(new Pessoa(2, "Carlos Oliveira", "222.333.444-55", "carlos.oliveira@email.com", "(64) 98888-0002"));
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, iniciais);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized List<Pessoa> lerTodasDoArquivo() {
        File file = new File(filePath);
        if (!file.exists()) {
            garantirArquivoExiste();
        }
        try {
            return objectMapper.readValue(file, new TypeReference<List<Pessoa>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private synchronized void salvarTodasNoArquivo(List<Pessoa> pessoas) {
        try {
            File file = new File(filePath);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, pessoas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Pessoa> obterTodas() {
        return lerTodasDoArquivo();
    }

    @Override
    public Optional<Pessoa> obterPorId(int id) {
        return lerTodasDoArquivo().stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }

    @Override
    public void adicionar(Pessoa pessoa) {
        List<Pessoa> pessoas = lerTodasDoArquivo();
        int novoId = pessoas.stream()
                .mapToInt(Pessoa::getId)
                .max()
                .orElse(0) + 1;
        pessoa.setId(novoId);
        pessoas.add(pessoa);
        salvarTodasNoArquivo(pessoas);
    }

    @Override
    public void atualizar(Pessoa pessoa) {
        List<Pessoa> pessoas = lerTodasDoArquivo();
        for (int i = 0; i < pessoas.size(); i++) {
            if (pessoas.get(i).getId() == pessoa.getId()) {
                pessoas.set(i, pessoa);
                break;
            }
        }
        salvarTodasNoArquivo(pessoas);
    }

    @Override
    public void remover(int id) {
        List<Pessoa> pessoas = lerTodasDoArquivo();
        pessoas.removeIf(p -> p.getId() == id);
        salvarTodasNoArquivo(pessoas);
    }
}
