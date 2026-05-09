# Sistema AutoDrive

## Descrição

O AutoDrive é um sistema de gestão para concessionárias que combina o fluxo de vendas de veículos (novos e seminovos) com a operação de pós-venda em oficinas mecânicas. Ele organiza o estoque, gerencia agendamentos de test-drives, controla ordens de serviço e o inventário de peças de reposição.

O sistema foca no ciclo de vida do veículo dentro da loja: desde a entrada no estoque (com avaliação de seminovos), passando pela preparação técnica, até a venda final e manutenções periódicas. Ele garante que a comissão de vendedores e a produtividade de mecânicos sejam rastreadas com precisão.

## Requisitos Funcionais

### 1. Inventário de Veículos
- **REQ01**: Cadastrar veículos com Chassi, RENAVAM, Modelo e Ano.
- **REQ02**: Implementar herança para Veículos: Novos e Seminovos.
- **REQ03**: Controlar status do veículo (Estoque, Reservado, Vendido, Test-Drive).

### 2. Fluxo de Vendas
- **REQ04**: Registrar propostas de venda com cálculo de impostos e comissões.
- **REQ05**: Agendar Test-Drives validando disponibilidade e CNH do cliente.

### 3. Oficina e Manutenção
- **REQ06**: Abrir Ordem de Serviço (OS) vinculada a veículo e cliente.
- **REQ07**: Registrar itens da OS (composição): Mão de Obra e Peças.
- **REQ08**: Controlar estoque de peças de reposição da oficina.

### 4. Relatórios e Alertas
- **REQ09**: Gerar relatório de vendas por período e vendedor.
- **REQ10**: Notificar clientes sobre revisões preventivas por tempo/km.
- **REQ11**: Relatório de lucratividade da oficina (peças vs serviço).

### 5. Regras e Restrições
- **REQ12**: **Bloquear o faturamento** da venda de um veículo caso possua pendências de documentação (RENAVAM vazio).
- **REQ13**: **Não permitir a entrega** de veículo da oficina sem que a OS esteja com status "Paga".
- **REQ14**: **Impedir** que veículos com status "Em Manutenção" sejam alocados para Test-Drive.
- **REQ15**: **Bloquear a finalização** da venda caso o valor de entrada seja inferior ao mínimo configurado.
- **REQ16**: **Garantir** inclusão obrigatória de itens de revisão (ex: óleo) conforme modelo do carro.
- **REQ17**: **Não permitir cadastro** de seminovos sem registro de quilometragem inicial.
- **REQ18**: **Bloquear a exclusão** de OS que já possuam peças retiradas do estoque físico.
- **REQ19**: **Impedir a venda** de veículo que possua reserva ativa para outro cliente.
- **REQ20**: **Validar** disponibilidade do mecânico antes de confirmar agendamento de serviço.

## Possíveis APIs/Bibliotecas
- **Apache POI** – Inventário em Excel.
- **Java Time API** – Controle de revisões.
- **iText** – Geração de OS.
  
## Integrantes
- Samuel Cely de Araujo | samuel.cely@ufrpe.br
- Yuri Neves de Arruda Cabral | yuri.cabral@ufrpe.br
- Ótavio Santiago Wanderley de Mendonça | otavio.santiago@ufrpe.br
- Artur Cândido Dourado do Nascimento | artur.candido@ufrpe.br
