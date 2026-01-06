import java.util.*;

public class RoteadorEntregas {

    // Representa uma rua ou conexão entre dois pontos
    static class Conexao implements Comparable<Conexao> {
        String destino;
        double custo; // Pode ser tempo em minutos ou distância em km

        public Conexao(String destino, double custo) {
            this.destino = destino;
            this.custo = custo;
        }

        @Override
        public int compareTo(Conexao outra) {
            return Double.compare(this.custo, outra.custo);
        }
    }

    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);
        Map<String, List<Conexao>> mapaUrbano = new HashMap<>();

        System.out.println("--- CONFIGURAÇÃO DO SISTEMA DE LOGÍSTICA ---");
        System.out.print("Quantas ruas/conexões deseja cadastrar no sistema? ");
        int totalConexoes = leitor.nextInt();
        leitor.nextLine(); // Limpa o buffer

        for (int i = 0; i < totalConexoes; i++) {
            System.out.println("\nCadastro da Conexão #" + (i + 1));
            System.out.print("Ponto de Origem: ");
            String origem = leitor.nextLine().trim();
            System.out.print("Ponto de Destino: ");
            String destino = leitor.nextLine().trim();
            System.out.print("Custo (Tempo ou Distância): ");
            double custo = leitor.nextDouble();
            leitor.nextLine(); // Limpa o buffer

            // Adiciona ao Grafo (Lista de Adjacência)
            mapaUrbano.computeIfAbsent(origem, k -> new ArrayList<>()).add(new Conexao(destino, custo));
            // Caso as ruas sejam de mão dupla, descomente a linha abaixo:
            // mapaUrbano.computeIfAbsent(destino, k -> new ArrayList<>()).add(new Conexao(origem, custo));
        }

        char continuar = 's';
        while (continuar == 's' || continuar == 'S') {
            System.out.println("\n--- SOLICITAÇÃO DE ROTA MAIS PRÓXIMA ---");
            System.out.print("Onde o entregador está agora? ");
            String atual = leitor.nextLine().trim();
            System.out.print("Qual o destino da entrega? ");
            String alvo = leitor.nextLine().trim();

            buscarMelhorRota(mapaUrbano, atual, alvo);

            System.out.print("\nDeseja consultar outra rota? (s/n): ");
            continuar = leitor.next().charAt(0);
            leitor.nextLine(); // Limpa buffer
        }

        System.out.println("\nSistema encerrado. Boa entrega!");
        leitor.close();
    }

    public static void buscarMelhorRota(Map<String, List<Conexao>> grafo, String inicio, String fim) {
        if (!grafo.containsKey(inicio)) {
            System.out.println("Erro: O local de origem '" + inicio + "' não consta no mapa cadastrado.");
            return;
        }

        // Estruturas do Algoritmo de Dijkstra
        Map<String, Double> menoresCustos = new HashMap<>();
        Map<String, String> rotaAnterior = new HashMap<>();
        PriorityQueue<Conexao> filaPrioridade = new PriorityQueue<>();

        // Inicializa distâncias com "infinito"
        for (String local : grafo.keySet()) {
            menoresCustos.put(local, Double.MAX_VALUE);
            // Garante que destinos que não têm saídas também estejam no mapa de custos
            for (Conexao c : grafo.get(local)) {
                menoresCustos.putIfAbsent(c.destino, Double.MAX_VALUE);
            }
        }

        menoresCustos.put(inicio, 0.0);
        filaPrioridade.add(new Conexao(inicio, 0.0));

        while (!filaPrioridade.isEmpty()) {
            Conexao atual = filaPrioridade.poll();
            String u = atual.destino;

            if (u.equals(fim)) break;

            if (grafo.containsKey(u)) {
                for (Conexao vizinha : grafo.get(u)) {
                    double novoCusto = menoresCustos.get(u) + vizinha.custo;
                    if (novoCusto < menoresCustos.get(vizinha.destino)) {
                        menoresCustos.put(vizinha.destino, novoCusto);
                        rotaAnterior.put(vizinha.destino, u);
                        filaPrioridade.add(new Conexao(vizinha.destino, novoCusto));
                    }
                }
            }
        }

        // Verifica se o destino foi alcançado
        if (!menoresCustos.containsKey(fim) || menoresCustos.get(fim) == Double.MAX_VALUE) {
            System.out.println("Infelizmente não há rotas cadastradas que levem até '" + fim + "'.");
        } else {
            exibirItinerario(inicio, fim, menoresCustos.get(fim), rotaAnterior);
        }
    }

    private static void exibirItinerario(String inicio, String fim, double custoTotal, Map<String, String> rotaAnterior) {
        List<String> caminho = new ArrayList<>();
        for (String no = fim; no != null; no = rotaAnterior.get(no)) {
            caminho.add(no);
        }
        Collections.reverse(caminho);

        System.out.println("\n>>> ROTA ENCONTRADA <<<");
        System.out.println("Caminho: " + String.join(" -> ", caminho));
        System.out.println("Custo Total (Menor caminho possível): " + custoTotal);
    }
}

