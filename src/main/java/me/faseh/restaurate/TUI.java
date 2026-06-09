package me.faseh.restaurate;

import me.faseh.restaurate.controller.*;
import me.faseh.restaurate.services.*;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Scanner;

public class TUI {

    private final Scanner scn = new Scanner(System.in);

    private final clienteService clienteService = new clienteService();
    private final pratosService pratosService = new pratosService();
    private final pedidoService pedidoService = new pedidoService();
    private final avaliacaoService avaliacaoService = new avaliacaoService();

    private clienteController clienteAtual = null;
    private pedidoController pedidoAtual  = null;

    public void menuPrincipal() {
        int opcao;
        do {
            System.out.println("\n" + Utils.equal);
            System.out.println("Trabalho A3 - Algoritmos e Programacao FASEH");
            System.out.println(Utils.equal);
            System.out.println("RestaurARTE - Menu Principal");
            System.out.println(Utils.dash);
            System.out.println("1 - Entrar no Restaurante");
            System.out.println("2 - Ver Avaliacoes");
            System.out.println("3 - Sobre Nos");
            System.out.println("0 - Sair");
            System.out.println(Utils.dash);
            System.out.print("Opcao: ");
            opcao = Utils.scnInt(scn);
            switch (opcao) {
                case 1 -> menuEntrar();
                case 2 -> menuAvaliacoes();
                case 3 -> msgSobreNos();
                case 0 -> System.out.println("\nEncerrando Programa...");
                default -> System.out.println("Opcao invalida.");
            }
        } while (opcao != 0);
    }

    private void menuEntrar() {
        System.out.println("\n" + Utils.equal);
        System.out.println("RestaurARTE - Menu Registro");
        System.out.println(Utils.dash);
        System.out.println("Diga o seu nome para registrar a entrada");
        System.out.println("Caso seja sua primeira vez entrando no restaurante, seu nome sera cadastrado.");
        System.out.println(Utils.dash);
        System.out.print("Digite seu nome: ");
        String nome = Utils.scnString(scn);

        Optional<clienteController> encontrado = clienteService.buscarPorNome(nome);

        if (encontrado.isPresent()) {
            clienteAtual = encontrado.get();
            System.out.println(Utils.dash);
            System.out.println("Bem-vindo(a) de volta, " + clienteAtual.getNomeCliente() + ".");
            menuClienteInfo();

            List<pedidoController> abertos = pedidoService.listarPorCliente(clienteAtual.getIdCliente()).stream().filter(p -> p.getDataSaida() == null).toList();
            if (!abertos.isEmpty()) {
                pedidoAtual = abertos.get(0);
                System.out.println("Seu Pedido (Pedido #" + pedidoAtual.getIdPedido() + " ainda esta em aberto.");
            } else {
                pedidoAtual = pedidoService.abrirPedido(clienteAtual.getIdCliente());
                System.out.println("Pedido #" + pedidoAtual.getIdPedido() + " aberto.");
            }
            System.out.print("\nPressione qualquer tecla para continuar...");
            scn.nextLine();
            menuRestaurante();

        } else {
            clienteAtual = clienteService.registrarCliente(nome);
            pedidoAtual  = pedidoService.abrirPedido(clienteAtual.getIdCliente());
            System.out.println(Utils.dash);
            System.out.println("Cadastro realizado. Seja bem vindo, " + clienteAtual.getNomeCliente() + ".");
            System.out.println("ID do cliente registrado: #" + clienteAtual.getIdCliente());
            System.out.println("Pedido #" + pedidoAtual.getIdPedido() + " aberto.");
            System.out.print("\nPressione qualquer tecla para continuar...");
            scn.nextLine();
            menuRestaurante();
        }
    }

    private void menuClienteInfo() {
        System.out.println("\n" + Utils.equal);
        System.out.println("Perfil do Cliente");
        System.out.println(Utils.dash);
        System.out.println("Nome: " + clienteAtual.getNomeCliente());
        System.out.println("ID: #" + clienteAtual.getIdCliente());
        System.out.println("Data de Entrada: " + clienteAtual.getFormData());

        List<pedidoController> todosPedidos = pedidoService.listarPorCliente(clienteAtual.getIdCliente());
        long encerrados = todosPedidos.stream().filter(p -> p.getDataSaida() != null).count();
        long abertos    = todosPedidos.stream().filter(p -> p.getDataSaida() == null).count();

        System.out.println(
                "Pedidos: " + todosPedidos.size()
                + " total  (" + encerrados + " encerrado(s), "
                + abertos + " em aberto)"
        );

        if (!todosPedidos.isEmpty()) {
            System.out.println("Historico:");
            todosPedidos.stream().limit(5).forEach(p -> {
                String status = p.getDataSaida() == null ? "Em aberto" : "Encerrado";
                System.out.println(
                        "Pedido #" + p.getIdPedido() + " — "
                        + p.getFormDataInicio()
                        + " — " + p.getIdsPratos().size()
                        + " prato(s) — " + status
                );
            });
            if (todosPedidos.size() > 5)
                System.out.println("... e mais " + (todosPedidos.size() - 5) + " pedido(s).");
        }
    }

    private void menuRestaurante() {
        int opcao;
        do {
            System.out.println("\n" + Utils.equal);
            System.out.printf("Seja bem vindo, %s.  |  Pedido #%d%n", clienteAtual.getNomeCliente(), pedidoAtual.getIdPedido());
            System.out.println(Utils.dash);
            System.out.println("1 - Acessar o Cardapio");
            System.out.println("2 - Ver meu Pedido Atual");
            System.out.println("3 - Encerrar e Pagar");
            System.out.println("0 - Sair do Restaurante");
            System.out.println(Utils.dash);
            System.out.print("Opcao: ");
            opcao = Utils.scnInt(scn);
            switch (opcao) {
                case 1 -> menuCardapio();
                case 2 -> verPedidoAtual();
                case 3 -> endPedido();
                case 0 -> {
                    clienteAtual = null;
                    pedidoAtual  = null;
                    requestAvaliacao();
                }
                default -> System.out.println("Opcao invalida.");
            }
        } while (opcao != 0 && clienteAtual != null);
    }

    private void requestAvaliacao() {
        int opcao;
        do {
            System.out.println("Gostaria de avaliar sua experiencia?");
            System.out.println("1 - Fazer Avaliacao");
            System.out.println("0 - Sair do Restaurante");
            System.out.println(Utils.dash);
            System.out.print("Opcao: ");
            opcao = Utils.scnInt(scn);
            switch (opcao) {
                case 1 -> addAvaliacao();
                case 0 -> {
                    System.out.println("Agradecemos sua escolha.");
                }
                default -> System.out.println("Opcao invalida.");
            }
        } while (opcao != 0);
    }

    private void menuCardapio() {
        int opcao;
        do {
            System.out.println("\n" + Utils.equal);
            System.out.println("RestaurARTE - Cardapio");
            System.out.println(Utils.dash);
            System.out.printf("%-4s  %-30s  %8s  %s%n", "ID", "Prato", "Preco", "Tempo");
            System.out.println(Utils.dash);
            pratosService.listarTodos().forEach(p -> System.out.println("  " + p));
            System.out.println(Utils.dash);
            System.out.print("ID do prato para adicionar (0 = voltar): ");
            opcao = Utils.scnInt(scn);
            if (opcao != 0) {
                Optional<pratosController> prato = pratosService.buscarPorId(opcao);
                if (prato.isEmpty()) {
                    System.out.println("Prato nao encontrado.");
                } else {
                    pedidoService.adicionarPrato(pedidoAtual.getIdPedido(), opcao);
                    pedidoAtual = pedidoService.buscarPorId(pedidoAtual.getIdPedido()).orElse(pedidoAtual);
                    System.out.println("  \"" + prato.get().getNomePrato() + "\" adicionado ao seu pedido!");
                }
            }
        } while (opcao != 0);
    }

    private void verPedidoAtual() {
        pedidoAtual = pedidoService.buscarPorId(pedidoAtual.getIdPedido()).orElse(pedidoAtual);
        System.out.println("\n" + Utils.equal);
        System.out.println("Meu Pedido  #" + pedidoAtual.getIdPedido());
        System.out.println(Utils.dash);
        List<Integer> ids = pedidoAtual.getIdsPratos();
        if (ids.isEmpty()) {
            System.out.println("Nenhum prato adicionado ainda.");
        } else {
            double total = 0;
            for (int idPrato : ids) {
                Optional<pratosController> p = pratosService.buscarPorId(idPrato);
                if (p.isPresent()) {
                    System.out.println("    " + p.get());
                    total += p.get().getPrecoVal();
                }
            }
            System.out.println(Utils.dash);
            System.out.printf("TOTAL: %s%n", Utils.formPreco(total));
        }
        System.out.println(Utils.dash);
        System.out.print("\nPressione qualquer tecla para continuar...");
        scn.nextLine();
    }

    private void endPedido() {
        verPedidoAtual();
        if (pedidoAtual.getIdsPratos().isEmpty()) {
            clienteAtual = null;
            pedidoAtual  = null;
            addAvaliacao();
        }
        System.out.println("Confirmar pagamento?");
        System.out.println("1 - Sim, pagar e sair");
        System.out.println("0 - Cancelar");
        System.out.print("Opcao: ");
        if (Utils.scnInt(scn) == 1) {
            pedidoService.fecharPedido(pedidoAtual.getIdPedido());
            pedidoService.marcarEntregue(pedidoAtual.getIdPedido());
            System.out.println("\nPedido encerrado. Agradecemos sua escolha, " + clienteAtual.getNomeCliente() + "!");
            clienteAtual = null;
            pedidoAtual  = null;
            addAvaliacao();
        }
    }

    private void addAvaliacao() {
        System.out.println("\n" + Utils.equal);
        System.out.println("Avalie sua experiencia abaixo:");
        System.out.println(Utils.dash);
        System.out.println("0 - Pessimo  |  1 - Ruim   |  2 - Regular");
        System.out.println("3 - Bom      |  4 - Otimo  |  5 - Excelente");
        System.out.println(Utils.dash);
        System.out.print("Sua nota: ");
        int nota = scnNota();

        System.out.print("Comentario ( Pressione Enter para deixar comentario em branco ): ");
        String comentario = scn.nextLine().trim();

        avaliacaoService.registrar(clienteAtual.getIdCliente(), clienteAtual.getNomeCliente(), nota, comentario);

        System.out.println(Utils.dash);
        System.out.println("Avaliacao registrada: " + "★".repeat(nota) + "☆".repeat(5 - nota) + "  (" + nota + "/5)");
        if (!comentario.isEmpty()) System.out.println("Comentario: \"" + comentario + "\"");
        System.out.println("Obrigado pelo seu feedback!");
    }

    private void menuAvaliacoes() {
        System.out.println("\n" + Utils.equal);
        System.out.println("RestaurARTE - Menu de Avaliacoes");
        System.out.println(Utils.dash);
        OptionalDouble media = avaliacaoService.calcularMedia();

        if (media.isPresent()) {
            double m = media.getAsDouble();
            int estrelas = (int) Math.round(m);
            System.out.printf("Media geral: %s  (%.1f / 5)%n", "★".repeat(estrelas) + "☆".repeat(5 - estrelas), m);
        } else {
            System.out.println("Media geral: sem avaliacoes ainda.");
        }

        System.out.println("\n" + Utils.equal);
        System.out.println("Todas as Avaliacoes:");
        System.out.println(Utils.dash);
        List<avaliacaoController> lista = avaliacaoService.listarTodas();

        if (lista.isEmpty()) {
            System.out.println("Nenhuma avaliacao encontrada.");
        } else {
            System.out.printf("  %-20s  %-7s  %-5s  %s%n", "Cliente", "Nota", "Data", "Comentario");
            System.out.println(Utils.dash);
            lista.forEach(a -> System.out.println("  " + a));
        }

        System.out.println(Utils.dash);
        System.out.print("\nPressione qualquer tecla para continuar...");
        scn.nextLine();
    }

    private void msgSobreNos() {
        System.out.println("\n" + Utils.equal);
        System.out.println("Sobre Nos");
        System.out.println(Utils.dash);
        System.out.println("RestaurARTE - Restaurante em Terminal");
        System.out.println("Programa desenvolvido em Java 21 + integracao com SQLite");
        System.out.println("Trabalho A3 - Algoritmos e Programacao FASEH");
        System.out.println(Utils.dash);
        System.out.println("Desenvolvido por:" +
                "Ian Fernandes Guimaraes" +
                "ADD NOME 2" +
                "ADD NOME 3" +
                "ADD NOME 4" +
                "ADD NOME 5" +
                "ADD NOME 6"
        );
        System.out.println(Utils.dash);
        System.out.print("\nPressione qualquer tecla para continuar...");
        scn.nextLine();
    }

    private int scnNota() {
        while (true) {
            int nota = Utils.scnInt(scn);
            if (nota >= 0 && nota <= 5) return nota;
            System.out.print("Nota invalida. Digite um valor de 0 a 5: ");
        }
    }
}
