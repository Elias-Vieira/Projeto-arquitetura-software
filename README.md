# Garagem de Veículos — Entrega 2 (Veículos + Reservas)

**Disciplina:** Arquitetura de Software • ESW430 • UniRV  
**Data limite de entrega:** 05/10/2026 até 19:00  

---

## 👥 Integrantes do Grupo
- Nycolas
- Wander
- Lucas
- Elias
- Matheus

---

## 🛠️ Linguagem e Framework
- **Linguagem:** Java 17+
- **Framework:** Spring Boot 3.2.4 com Spring MVC e Thymeleaf
- **Persistência:** Arquivos JSON com biblioteca Jackson (`ObjectMapper` + `JavaTimeModule`):
  - `data/pessoas.json`
  - `data/veiculos.json`
  - `data/reservas.json`

---

## 🏗️ Arquitetura e Princípios SOLID
O sistema segue estritamente o padrão **MVC + Repository + Injeção de Dependência**:
- **View → Controller → Interface / Service → Repositório → Arquivo JSON**
- Cada entidade possui seu próprio conjunto de arquivos isolados:
  - `Pessoa`: `Pessoa`, `IPessoaRepository`, `PessoaRepository`, `PessoaController`, `PessoaForm.html`, `index.html`
  - `Veiculo`: `Veiculo`, `IVeiculoRepository`, `VeiculoRepository`, `VeiculoController`, `VeiculoForm.html`, `index.html`
  - `Reserva`: `Reserva`, `IReservaRepository`, `ReservaRepository`, `ReservaController`, `ReservaService`, `ReservaForm.html`, `index.html`
- **Inversão de Dependência (D):** Todos os Controllers recebem interfaces via construtor (Injeção de Dependência).
- **Responsabilidade Única (S) & Regras de Negócio:** A verificação de conflitos de período entre reservas é realizada pela classe `ReservaService` (`existeConflito`), isolando a regra de negócio fora dos Controllers.
- **Validação de Conflito de Reservas:** Impede sobreposição de reservas para o mesmo veículo (`novaInicio <= existenteFim && novaFim >= existenteInicio`).
- **Desafios Extras Implementados:** 
  - Impede exclusão de pessoa ou veículo que possuam reservas ativas ou futuras.
  - Exibe o status da frota de veículos em tempo real (Disponível vs Reservado) na data de hoje.

---

## 🚀 Como Executar o Projeto

1. Certifique-se de ter o **JDK 17 ou superior** instalado.
2. Na raiz do projeto, execute no terminal:

```bash
# Execução via Maven Wrapper (Windows)
.\mvnw.cmd spring-boot:run

# Execução via Maven Wrapper (Linux/Mac)
./mvnw spring-boot:run
```

3. Acesse a aplicação no navegador:  
   👉 `http://localhost:8080/` (Redireciona automaticamente para a página inicial de **Reservas**)

---

## 🤖 Ferramentas de IA Utilizadas
- **Antigravity AI (Google DeepMind):** Leitura de especificações do projeto, auxílio no design responsivo em CSS/HTML Thymeleaf, implementação dos repositórios JSON com Jackson e lógica de checagem de conflitos de reservas.

---

## 📸 Capturas de Tela (Docs)
As telas do sistema estão disponíveis na pasta `docs/`:
- `docs/listagem_pessoas.png` — Módulo de Pessoas
- `docs/formulario_pessoa.png` — Formulário de Pessoas
- `docs/requisitos_docx.txt` — Especificações extraídas da atividade prática
