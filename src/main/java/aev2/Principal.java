package aev2;

public class Principal {

	public static void main(String[] args) throws Exception {
        Vista vista = new Vista();
        Modelo modelo = new Modelo();
        Controlador controlador = new Controlador(modelo, vista);
	}
}

