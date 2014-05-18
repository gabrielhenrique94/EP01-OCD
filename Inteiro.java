import java.util.ArrayList;

class Inteiro implements Cloneable{
	
	public static final Inteiro ZERO = new Inteiro(0);
	
	// a posiÁ„o 0 corresponde ao digito mais a direita de um numero
	private int[] bits;

	// true para numero negativo, false para numero positivo
	private boolean negativo;
	
	/**
	* construtor que incializa o interio com um valor pre-definido
	* @param valor inicial que o numero recebera
	*/
	public Inteiro(int tamanho, int numero){
		this.bits = this.toBinario(tamanho, numero);
	}
	
	public Inteiro(int numero) {
		this.bits = this.toBinario(numero);
	}

	private Inteiro() {}
//
//	private Inteiro (int tamanho){
//		//aqui eu me aproveito do fato do java inicializar tudo com 0
//		this.bits = new int[tamanho];
//	}


	private Inteiro(int tamanho, int[] numero){
		this.bits = new int[tamanho];
		for(int i = 0 ; i < tamanho; i++){
			if(i >= numero.length)
				this.bits[i] = 0;
			else if(numero[i] == 0 || numero[i] == 1)
				this.bits[i] = numero[i];
			else{
				System.err.println("Inteiro.contrutor: vetor contem numeros que nao sao 0 ou 1");
				break;
			}
		}
	}

	public int max(int a, int b){
		return (a > b )? a : b;
	}

	public Inteiro complementoDe2(){
		return new Inteiro(this.bits.length, complementoDe2(this.bits));
	}
	
	private static int[] complementoDe2(int[] bits) {
		int[] otherBits = new int[bits.length];
		for(int a = 0; a < bits.length; a++)
			otherBits[a] = (bits[a] == 0)? 1 : 0;
		int[] soma1 = {1};
		return soma(otherBits.length,otherBits, soma1);
	}


	public static Inteiro subtrai(int tamanho, Inteiro int1, Inteiro int2){
		return soma(tamanho, int1, int2.complementoDe2());
	}
	
	public static int[] subtrai(int tamanho, int[] int1, int[] int2){
		return soma(tamanho, int1, new Inteiro(int2.length, int2).complementoDe2().bits);
	}
	
	public void soma(Inteiro i) {
		this.bits = soma(this.bits.length, this.bits, i.bits);
	}
	
	public void subtrai(Inteiro i) {
		this.bits = subtrai(this.bits.length, this.bits, i.bits);
	}

	public static Inteiro soma(int tamanho,Inteiro int1, Inteiro int2){
		return new Inteiro(tamanho,soma(tamanho, int1.bits, int2.bits));
	}
	
	public static int[] soma(int tamanho,int[] int1, int[] int2){
		int[] soma = new int[tamanho];
		boolean vem = false;//0
		boolean sum ;
		for(int i = 0 ; i < tamanho; i++){
			boolean B1 = (i < int1.length) ? (int1[i] == 1) : false;
			boolean B2 = (i < int2.length) ? int2[i] == 1 : false;
			sum = (B2 ^ B1 ^ vem) || (B1&&B2&&vem);
			vem = (B2 && B1) || (B2&&vem) || (B1&&vem);
			soma[i] = (sum)?1:0;
		}
		return soma;
	}


	public void Rshift(){
		this.bits = rightShift(this.bits);
	}
	
	public void RshiftSimples(){
		this.bits = rightShiftSimples(this.bits);
	}

	public void Lshift(){
		this.bits = leftShift(this.bits);
	}
	
	public static int[] rightShift(int[] bits) {
		for(int i = 0; i < (bits.length - 1); i++)
			bits[i] = bits[i+1];
		bits[bits.length - 1] = 0;
		return bits;
	}
	
	public static int[] rightShiftSimples(int[] bits) {
		for(int i = 0; i < (bits.length - 1); i++)
			bits[i] = bits[i+1];
		bits[bits.length - 1] = 0;
		return bits;
	}
	
	public static int[] leftShift(int[] bits) {
		for(int i = (bits.length - 1); i >0; i--)
			bits[i] = bits[i-1];
		bits[0] = 0;
		return bits;
	}
	
	/**
	 * Realiza a multiplica√ß√£o de dois n√∫meros bin√°rios atrav√©s do algoritmo de Booth
	 * @param multiplicando
	 * @param multiplicador
	 * @return Objeto Inteiro com resultado da multiplica√ß√£o
	 */
	public static Inteiro multiplica(Inteiro multiplicando, Inteiro multiplicador) {
		int anterior = 0;
		int[] multiplicadorBits = multiplicador.getBits();
		int multiplicadorLenghBits = multiplicador.getLenghofBits();
		int tamanhoProduto = multiplicadorLenghBits*2;
		int[] produto = soma(tamanhoProduto, new int[tamanhoProduto], multiplicadorBits);
		for (int x=0; x < multiplicadorLenghBits; x++) {
			int atual = multiplicadorBits[x];
			if (atual == 1 && anterior == 0) {
				produto = subtraiMetadeEsq(multiplicando.getBits(), produto);
			} else if (atual == 0 && anterior == 1){
				produto = somaMetadeEsq(multiplicando.getBits(), produto);
			}
			produto = rightShift(produto);
			anterior = atual;
		}
		return new Inteiro(tamanhoProduto, produto);
	}
	

	public static Inteiro[] divide(Inteiro dividendo, Inteiro divisor) {
		int tamanhoAux = dividendo.getLenghofBits()*2;
		int[] dividendoAux = new int[tamanhoAux];
		int[] divisorAux = novaMetadeEsq(new int[tamanhoAux], divisor.getBits());
		dividendoAux = soma(tamanhoAux, dividendoAux, dividendo.getBits());
		for(int i=0; i<dividendo.getLenghofBits(); i++) {
			dividendoAux = leftShift(dividendoAux);
			dividendoAux = subtrai(tamanhoAux, dividendoAux, divisorAux);
	
			// Verifica se o resultado da subtracao mudou o sinal do dividendo
			if(dividendoAux[tamanhoAux-1] != (dividendo.negativo?1:0)) {
				// Se o sinal mudou, volta ao que estava antes
				dividendoAux = soma(tamanhoAux, dividendoAux, divisorAux);
				dividendoAux[0] = 0;
			}
			else dividendoAux[0] = 1;
		}
		return new Inteiro[] { new Inteiro(tamanhoAux/2, getMetadeEsq(dividendoAux)),
			new Inteiro(tamanhoAux/2, getMetadeDir(dividendoAux)) };
	}

	
	private static int[] somaMetadeEsq(int[] bitsMultiplicando, int[] produto) {
		if (produto.length  % 2 == 0) {
			int[] metadeEsq = getMetadeEsq(produto); 
			int[] metadeEsqSomada = soma(metadeEsq.length, bitsMultiplicando, metadeEsq);
			produto = novaMetadeEsq(produto, metadeEsqSomada);
		}
		return produto;
	}
	
	private static int[] subtraiMetadeEsq(int[] bitsMultiplicando, int[] produto) {
		if (produto.length % 2 == 0) {
			int[] metadeEsq = getMetadeEsq(produto); 
			int[] metadeEsqSubtraida = subtrai(metadeEsq.length, metadeEsq, bitsMultiplicando);
			produto = novaMetadeEsq(produto, metadeEsqSubtraida);
		}
		return produto;
	}
	
	/**
	 * Retorna a metade esquerda de um binario de 8 bits
	 */
	private static int[] getMetadeEsq(int[] bits) {
		int tamanhoBits = bits.length;
		int tamanhoMetadeEsq = tamanhoBits/2;
		int[] metadeEsq = new int[tamanhoMetadeEsq];
		for (int i=tamanhoMetadeEsq; i<tamanhoBits;i++) {
			metadeEsq[i-tamanhoMetadeEsq] = bits[i];
		}
		return metadeEsq;
	}
	
	/**
	 * Acrescenta a metade esquerda processada de volta a um binario de 8 bits
	 */
	private static int[] novaMetadeEsq(int[] bits, int[] metadeEsq) {
		int finalMetadeEsq = bits.length;
		int inicioMetadeEsq = finalMetadeEsq/2;
		for (int i=inicioMetadeEsq; i<finalMetadeEsq;i++) {
			bits[i] = metadeEsq[i-inicioMetadeEsq];
		}
		return bits;
	}
	
	/**
	 * Retorna a metade direita de um binario de 8 bits
	 */
	private static int[] getMetadeDir(int[] bits) {
		int tamanhoBits = bits.length;
		int tamanhoMetadeDir = tamanhoBits/2;
		int[] metadeDir = new int[tamanhoMetadeDir];
		for (int i=0; i<tamanhoMetadeDir;i++) {
			metadeDir[i] = bits[i];
		}
		return metadeDir;
	}
	
	/**
	 * Acrescenta a metade direita processada de volta a um binario de 8 bits
	 */
	private static int[] novaMetadeDir(int[] bits, int[] metadeDir) {
		int finalMetadeDir = bits.length;
		int inicioMetadeDir = finalMetadeDir/2;
		for (int i=0; i<inicioMetadeDir; i++) {
			bits[i] = metadeDir[i];
		}
		return bits;
	}
	
	public int[] getBits() {
		return bits.clone();
	}
	
	private int getLenghofBits() {
		return bits.length;
	}

	private int[] toBinario(int tamanho,int numero){
		int binario[] = new int[tamanho];
		this.negativo = false;
		if (numero<0) {
			negativo = true;
			numero *= -1;
		}
		int aux = numero;
		int pBinario = 0;
		while(aux >= 2){
			if(pBinario == tamanho) break;
			binario[pBinario] = aux%2;
			pBinario++;
			aux = aux/2;
		}
		if(aux == 1)
			if(pBinario < tamanho)
				binario[pBinario]  = 1;
		if (negativo)
			return complementoDe2(binario);
		return binario;
	}
	
	private int[] toBinario(int numero){
		ArrayList<Integer> binario = new ArrayList<Integer>();
		this.negativo = false;
		if (numero<0) {
			negativo = true;
			numero *= -1;
		}
		int aux = numero;
		while(aux >= 2){
			binario.add(aux%2);
			aux = aux/2;
		}
		binario.add(aux);
		if (negativo)
			return complementoDe2(getIntArray(binario));
		return getIntArray(binario);
	}
	
	private static int[] getIntArray(ArrayList<Integer> arrayList) {
		int[] binario = new int[arrayList.size()];
		for (int i=0; i < arrayList.size(); i++) {
			binario[i] = arrayList.get(i);
		}
		return binario;
	}
	
	/**
	 * Substitui o numero binario antigo por um novo conservando o tamanho em bits do objeto
	 * @param bits
	 */
	public void setNewBits(int[] bits) {
		// zera bits antigos
		this.bits = new int[this.bits.length];
		for(int i = 0; i < this.bits.length && i < bits.length; i++) {
			// atribui bits novos
			this.bits[i] = bits[i];
		}
	}
	/**
	 * Substitui o numero binario antigo por um novo conservando o tamanho em bits do objeto a partir dos bits mais a esquerda
	 * @param bits
	 */
	public void setNewBitsPelaEsq(int[] bits) {
		// zera bits antigos
		this.bits = new int[this.bits.length];
		for(int i = this.bits.length-1; i >= 0 && (bits.length-(this.bits.length - i)-1) >= 0; i--) {
			// atribui bits novos
			this.bits[i] = bits[bits.length-(this.bits.length - (i + 1)) - 1];
		}
	}
	
	/**
	 * Substitui o numero binario antigo por um novo conservando o tamanho em bits do objeto e joga todos os bits para a esquerda
	 * @param bits
	 */
	public void setNewBitsEsq(int[] bits) {
		setNewBits(bits);
		while (this.bits[this.bits.length-1] == 0)
			this.Lshift();
	}

	public String toString(){
		String result = "";
		for(int i = 0; i < this.bits.length ; i++)
			result = result + String.valueOf(this.bits[this.bits.length-i-1]);
		return result;
	}

	public int toInteger(){
		int p2 = 1;
		int result = 0;
		int[] bitsPraInt = this.bits;
		if (negativo)
			bitsPraInt = complementoDe2(bitsPraInt);
		for(int i = 0 ; i < this.bits.length; i++){
			result += bitsPraInt[i] * p2;
			p2 *= 2;
		}
		if(negativo) result = result * -1;
		return result;
	}
//	
//	public int toIntBinario(){
//		int result = 0;
//		int multiplicador = 1;
//		for (int i=0; i<bits.length; i++) {
//			result = this.bits[i] * multiplicador;
//			multiplicador *= 10;
//		}
//		if (negativo)
//			result *= -1;
//		return result;
//	}
	
	/**
	 * Retorna a quantidade de digitos utilizadas para representar o n˙mero
	 * @return quantidade de digitos utilizadas para representar o n˙mero
	 */
	public int getNumberLength() {
		int ultimoUm =0;
		for (int i=0; i < this.bits.length; i++) {
			if (this.bits[i] == 1) {
				ultimoUm = i;
			}
		}
		return ultimoUm+1;
	}
	
	/**
	 * Retorna os bits necessarios para representacao do numero, o que pode ser maior que o tamanho definido para o objeto
	 * @return bits necessarios para representacao do numero, o que pode ser maior que o tamanho definido para o objeto
	 */
	public int[] getShortenedBits() {
		int tamanhoReal = this.getNumberLength();
		int[] shortenedBits = new int[tamanhoReal];
		for (int i=0; i < tamanhoReal; i++) {
			shortenedBits[i] = this.bits[i];
		}
		return shortenedBits;
	}
	
	public boolean isNegativo() {
		return negativo;
	}
	
	@Override
	protected Inteiro clone() {
		Inteiro i = new Inteiro();
		i.bits = this.getBits();
		i.negativo = this.negativo;
		return i;
	}

	public static void main(String[] args){
		int tamanho = 32;
		Inteiro i = new Inteiro(tamanho, 14);
		Inteiro a = new Inteiro(tamanho, 3);
		Inteiro soma = Inteiro.soma(tamanho, i, a);
		Inteiro subtracao = Inteiro.subtrai(tamanho,i,a);
		Inteiro multiplicacao = Inteiro.multiplica(i, a);
		Inteiro[] divisao = Inteiro.divide(i, a);
		
		System.out.println("a = " + a + " = " + a.toInteger()); // a
		System.out.println("i = " + i + " = " + i.toInteger()); // i
		System.out.println("i + a = " + soma + " = " + soma.toInteger()); // i + a
		System.out.println("i - a = " + subtracao + " = " + subtracao.toInteger()); // i - a
		System.out.println("i * a = " + multiplicacao + " = " + multiplicacao.toInteger()); // i * a
		System.out.println("i / a = " + divisao[1] + " = " + divisao[1].toInteger()); // i / a
		System.out.println("i % a = " + divisao[0] + " = " + divisao[0].toInteger()); // i % a
	}
}
