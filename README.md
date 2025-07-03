# EventUSP


Esse site tem o objetivo de reunir eventos da Universidade de São Paulo (USP) em uma plataforma única, facilitando o acesso aos alunos e ao público em geral para descobrir e participar dos eventos acadêmicos, culturais e sociais.

## Sobre

O EventUSP consistirá de um site para ampliar o acesso e divulgação dos eventos universitários situados em todos os campus da Universidade de São Paulo.

O site terá como features:
  - Um organizador consegue realizar a postagem de eventos.
  - Eventos contam com data e horário, localização, e outroas informações relevantes (cardápio, produtos, colaboradores, etc.).
  - Participantes conseguem fazer a confirmação de participação em um evento, também podendo ver quantas pessoas vão.
  - Participantes também conseguem fazer avaliações com comentários e uma nota de até 5 estrelas sobre edições anteriores desse mesmo evento, que ficam disponíveis para todos verem.

## Tecnologias Usadas

### Backend
- **Kotlin + Ktor framework** 
- **JWT** 
- **MySQL** 
- **Gradlew** 

### Frontend
- **HTML/CSS**
- **React**
- **TypeScript**

## Como Rodar o Projeto

No diretório backend execute:

```./gradlew build```

Para inicializar o projeto, compilando todos os arquivos e baixando as dependências necessárias, além de rodar os testes automatizados.
Para rodar apenas os tests:

```./gradlew test```

Depois do build, para que o back fique de fato funcionando execute:

```./gradlew run```

Com o back corretamente em execução. Podemos ativar o front:

```npm run dev```

Que então irá indicar em qual endereço o site está disponível para ser visualizado (ex: http://localhost:5173/)

## Licença

Este projeto está licenciado sob a **GNU General Public License 3.0** - consulte o arquivo [LICENSE](LICENSE) para mais detalhes.
