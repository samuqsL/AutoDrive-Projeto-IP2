package br.ufrpe.autodrive.negocio.beans;

public enum StatusOS {
    // FUNÇÃO LOCALIZADA: Estados da OS controlados pela fila automática da oficina
    ABERTA,               // Significa que está na fila aguardando mecânico disponível
    PROCESSO_MANUTENCAO,  // Significa que a OS já possui um mecânico trabalhando nela
    PAGA,                 // Mantido do fluxo original
    FINALIZADA            // Significa que o serviço acabou e incrementará a produtividade
}
