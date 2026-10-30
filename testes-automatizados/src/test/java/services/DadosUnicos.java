package services;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Gera valores únicos por execução a partir do timestamp, para que os testes possam rodar
 * várias vezes sem bater nas restrições de unicidade da API (CNPJ, CPF, CNH, placa, e-mail).
 */
public final class DadosUnicos {

    private static final String LETRAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALFANUMERICOS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // Parte do timestamp atual e incrementa a cada uso, evitando colisão dentro do mesmo milissegundo
    private static final AtomicLong sequencia = new AtomicLong(System.currentTimeMillis());

    private DadosUnicos() {
    }

    public static long proximo() {
        return sequencia.incrementAndGet();
    }

    /** Últimos {@code tamanho} dígitos da sequência, completando com zeros à esquerda. */
    public static String digitos(int tamanho) {
        String valor = String.valueOf(proximo());
        if (valor.length() >= tamanho) {
            return valor.substring(valor.length() - tamanho);
        }
        return "0".repeat(tamanho - valor.length()) + valor;
    }

    /** Placa no padrão aceito pela API: [A-Z]{3}[0-9][A-Z0-9][0-9]{2}. */
    public static String placa() {
        long n = proximo();
        int finais = (int) (n % 100);
        n /= 100;
        char quinto = ALFANUMERICOS.charAt((int) (n % 36));
        n /= 36;
        int quarto = (int) (n % 10);
        n /= 10;
        char l3 = LETRAS.charAt((int) (n % 26));
        n /= 26;
        char l2 = LETRAS.charAt((int) (n % 26));
        n /= 26;
        char l1 = LETRAS.charAt((int) (n % 26));
        return "" + l1 + l2 + l3 + quarto + quinto + String.format("%02d", finais);
    }
}
