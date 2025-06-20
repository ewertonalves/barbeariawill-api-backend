package com.whatsapp.barbeariaWill.application.useCase;

import com.whatsapp.barbeariaWill.domain.port.out.AppointmentInterfacePort;
import com.whatsapp.barbeariaWill.domain.port.out.WhatsAppClientIntefacePort;

import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class EscolherHorarioUseCase {

    private static final DateTimeFormatter FORMATO_DATA  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA  = DateTimeFormatter.ofPattern("HH:mm");

    private final AppointmentInterfacePort repo;
    private final WhatsAppClientIntefacePort client;
    private final WorkScheduleUseCase workScheduleService;
    private final ListTimeBlocksUseCase listTimeBlocksUseCase;

    public EscolherHorarioUseCase(AppointmentInterfacePort repo,
                                 WhatsAppClientIntefacePort client,
                                 WorkScheduleUseCase workScheduleService,
                                 ListTimeBlocksUseCase listTimeBlocksUseCase) {
        this.repo = repo;
        this.client = client;
        this.workScheduleService = workScheduleService;
        this.listTimeBlocksUseCase = listTimeBlocksUseCase;
    }

    public void execute(String telefone, String hora) {
        var agendamento = repo.buscarPorTelefone(telefone)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado para " + telefone));
        
        LocalTime horaFormat = LocalTime.parse(hora, FORMATO_HORA);
        
        // Verifica se o horário está bloqueado
        var blocos = listTimeBlocksUseCase.execute();
        boolean bloqueado = blocos.stream().anyMatch(block ->
            block.date().equals(agendamento.getData()) &&
            ( !horaFormat.isBefore(block.startTime()) && !horaFormat.isAfter(block.endTime()) )
        );
        if (bloqueado) {
            client.enviarTexto(telefone, "Desculpe, este horário está indisponível. Por favor, escolha outro horário.");
            client.enviarListaHorarios(telefone, agendamento.getData().format(FORMATO_DATA));
            return;
        }

        if (!workScheduleService.isDisponivelParaAgendamento(agendamento.getData(), horaFormat)) {
            client.enviarTexto(telefone, "Desculpe, este horário não está disponível. Por favor, escolha outro horário.");
            client.enviarListaHorarios(telefone, agendamento.getData().format(FORMATO_DATA));
            return;
        }

        agendamento.definirHora(horaFormat);
        repo.salvar(agendamento);

        String resumo = String.format(
                "Você escolheu: %s em %s às %s. Confirma? (Sim/Não)",
                agendamento.getServicoId(),
                agendamento.getData().format(FORMATO_DATA),
                agendamento.getHora().format(FORMATO_HORA)
        );
        client.enviarConfirmacao(telefone, resumo);
    }
}
