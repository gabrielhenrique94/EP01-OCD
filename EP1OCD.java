
public class EP1OCD {

	public static void main(String[] args) {
		int tamanho = 32;
		Inteiro i = new Inteiro(tamanho,7);
		Inteiro a = new Inteiro(tamanho, -2);
		Inteiro soma = Inteiro.soma(tamanho, i, a);
		Inteiro subtracao = Inteiro.subtrai(tamanho,i,a);
		Inteiro multiplicacao = Inteiro.multiplica(i, a);
		Inteiro[] divisao = Inteiro.divide(i, a);
		PontoFlutuante flut1 = new PontoFlutuante(4);
		PontoFlutuante flut2 = new PontoFlutuante(-3);
		

		System.out.println("a = " + a + " = " + a.toInteger()); // a
		System.out.println("i = " + i + " = " + i.toInteger()); // i
		System.out.println("i + a = " + soma + " = " + soma.toInteger()); // i + a
		System.out.println("i - a = " + subtracao + " = " + subtracao.toInteger()); // i - a
		System.out.println("i * a = " + multiplicacao + " = " + multiplicacao.toInteger()); // i * a
		System.out.println("i / a = " + divisao[1] + " = " + divisao[1].toInteger()); // i / a
		System.out.println("i % a = " + divisao[0] + " = " + divisao[0].toInteger()); // i % a
		
		PontoFlutuante multFlut = PontoFlutuante.multiplicaFlutuante(flut1, flut2);
		PontoFlutuante divFlut = PontoFlutuante.divideFlutuante(flut1, flut2);
		System.out.println("i % a = " + multFlut + " = " + multFlut.getNumeroDecimal()); // i * a
		System.out.println("i % a = " + divFlut + " = " + divFlut.getNumeroDecimal()); // i / a
	}

}
