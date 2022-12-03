import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Mapa {

    private Integer codigoAtual = 0;

    public class Local {
        public int x;
        public int y;

        public Local(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Local(Local pos) {
            this.x = pos.x;
            this.y = pos.y;
        }

        public int distancia(Local l) {
            return Math.abs(l.x - this.x) + Math.abs(l.y - this.y);
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Local local = (Local) o;
            return this.x == local.x && this.y == local.y;
        }

        public String toString() {
            return String.format("(%d,%d)",this.x,this.y);
        }

        public Local clone() { return new Local(this); }
    }

    // Reserva de uma trotinete
    public class Reserva {
        private Integer codigo;
        private String username;
        private Local local;

        public Reserva(Reserva r) {
            this.codigo = r.codigo;
            this.username = r.username;
            this.local = r.local.clone();
        }

        public Reserva(Integer codigo, String username, Local local) {
            this.codigo = codigo;
            this.username = username;
            this.local = local.clone();
        }

        public Reserva clone() { return new Reserva(this); }
    }

    private int n; // Nº de linhas/colunas do mapa
    private Local[][] mapa;
    private HashMap<String, Local> utilizadoresLocal;
    private HashMap<Local, Integer> trotinetesLivres;
    private Queue<Reserva> reservas; // Integer -> código de reserva
    // Diz se o cliente já efetuou alguma reserva
    private HashMap<String, Boolean> clientesReservas; // Cada cliente só pode realizar uma reserva simultaneamente


    public Mapa(Integer n) {
        this.n = n;
        this.mapa = new Local[this.n][this.n];
        for (int i=0; i<n; i++) {
            for (int j=0; i<n; j++) {
                Local novoLocal = new Local(i,j);
                this.mapa[i][j] = novoLocal;
            }
        }
        this.utilizadoresLocal = new HashMap<>();
        this.trotinetesLivres = new HashMap<>();
        this.reservas = new LinkedList<>();
        this.clientesReservas = new HashMap<>();
    }

    public void movimentoUtilizador(String username, Local proximo) {
        this.utilizadoresLocal.put(username, proximo);
    }

    public void realizarReserva(String username, Local l) {
        if (this.clientesReservas.containsKey(username)) {
            if (!this.clientesReservas.get(username)) {
                Reserva reserva = new Reserva(this.codigoAtual, username, l);
                this.reservas.add(reserva);
                this.clientesReservas.put(username, true);
                this.codigoAtual++;
            }
        }
        else {
            Reserva reserva = new Reserva(this.codigoAtual, username, l);
            this.reservas.add(reserva);
            this.clientesReservas.put(username, true);
            this.codigoAtual++;
        }
    }

    public Reserva receberReserva() {
        return this.reservas.peek();
    }

    public boolean localComTrotinetesLivres(Local l) {
        if (!this.trotinetesLivres.containsKey(l)) return false;
        else if (this.trotinetesLivres.get(l) == 0) return false;
        else return true;
    }

    /*
    // Devolve uma lista com os locais com trotinetes livres 
    public List<Local> locaisTrotinetesLivres(Integer d) {
        List<Local> comTrotinestesLivres = new ArrayList<>();

        return comTrotinetesLivres;
    }
    */


}