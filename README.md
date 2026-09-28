# Garagem de Veículos — Entrega 1 (Módulo Pessoas)

**Disciplina:** Arquitetura de Software • ESW430 • UniRV  
**Data limite de entrega:** 28/09/2026 até 20:50  

---

## 👥 Integrantes do Grupo
- Nycolas
- Wander
- Lucas
- Elias

---

## 🛠️ Linguagem e Framework
- **Linguagem:** Java 17+
- **Framework:** Spring Boot 3.2.4 com Spring MVC e Thymeleaf
- **Persistência:** Arquivo JSON (`data/pessoas.json`) com biblioteca Jackson (`ObjectMapper`)

---

## 🏗️ Arquitetura e Princípios SOLID
O projeto foi desenvolvido estritamente seguindo o padrão **MVC + Repository + Injeção de Dependência**:
- **View → Controller → Interface → Repositório → Arquivo JSON**
- `Pessoa`: Model com os campos obrigatórios (Id, Nome, CPF, E-mail, Telefone).
- `IPessoaRepository`: Contrato da interface do repositório.
- `PessoaRepository`: Implementação concreta `@Repository` com Jackson `ObjectMapper`.
- `PessoaController`: Controller que recebe `IPessoaRepository` via **Injeção de Dependência pelo Construtor**.
- Nenhuma classe além do `PessoaRepository` lê ou escreve o arquivo `pessoas.json`.

---

## 🚀 Como Executar o Projeto

1. Certifique-se de ter o **JDK 17 ou superior** instalado.
2. Na raiz da pasta `28-09-2026`, execute no terminal:

```bash
# Execução via Maven Wrapper (Windows)
.\mvnw.cmd spring-boot:run

# Execução via Maven Wrapper (Linux/Mac)
./mvnw spring-boot:run
```

3. Acesse a aplicação no navegador:  
   👉 `http://localhost:8080/pessoas`

---

## 🤖 Ferramentas de IA Utilizadas
- **Antigravity AI (Google DeepMind):** Auxílio no design do frontend com CSS responsivo, estruturação do projeto Spring Boot e validações do repositório JSON.

---

## 📸 Capturas de Tela (Docs)
As telas do sistema podem ser visualizadas na pasta `docs/`:
- `docs/listagem_pessoas.png` — Listagem com botões de Ação
- `docs/formulario_pessoa.png` — Formulário de Cadastro e Edição com validação
