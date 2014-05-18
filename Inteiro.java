class Inteiro{
	
	// a posição 0 corresponde ao digito mais a direita de um numero
	protected int[] bits;
	
	/**
	* construtor que incializa o interio com um valor pre-definido
	* @param valor inicial que o numero recebera
	*/
	public Inteiro(int tamanho, int numero){
		this.bits = this.toBinario(tamanho, numero);
	}
	
	public Inteiro(int numero) {
		int size = 0;
		for (int n = numero; n != 0 ; n = n/2) size++;
		this.bits = this.toBinario((size + 1), numero);
	}

	private Inteiro(int tamanho, int[] numero){
		this.bits = copiaBinario(tamanho,numero);
	}

	private static int[] copiaBinario(int tamanho, int[] numero){
		boolean negativo = numero[numero.length - 1] == 1;
		if(negativo) numero = complementoDe2(numero);
		int[] result = new int[tamanho];
		for(int i = 0 ; i < tamanho; i++){
			if(i >= numero.length)
				result[i] = 0;
			else if(numero[i] == 0 || numero[i] == 1)
				result[i] = numero[i];
			else{
				System.err.println("Inteiro.contrutor: vetor contem numeros que nao sao 0 ou 1");
				System.exit(1);
			}
		}
		if(negativo) result = complementoDe2(result);
		return result;
	}
	public static int max(int a, int b){
		return (a > b )? a : b;
	}

	public Inteiro complementoDe2(){
		return new Inteiro(this.bits.length, complementoDe2(this.bits));
	}
	
	private static int[] complementoDe2(int[] bits) {
		int[] otherBits = new int[bits.length];
		for(int a = 0; a < bits.length; a++)
			otherBits[a] = (bits[a] == 0)? 1 : 0;
		return soma1(otherBits);//soma 1 a otherBits
	}

	public static Inteiro subtrai(int tamanho, Inteiro int1, Inteiro int2){
		return soma(tamanho, int1, int2.complementoDe2());
	}
	
	public static int[] subtrai(int tamanho, int[] int1, int[] int2){
		return soma(tamanho, int1, new Inteiro(int2.length, int2).complementoDe2().bits);
	}

	public static Inteiro soma(int tamanho,Inteiro int1, Inteiro int2){
		return new Inteiro(tamanho,soma(tamanho, int1.bits, int2.bits));
	}
	
	public static int[] soma(int tamanho,int[] int1, int[] int2){
		int[] soma = new int[tamanho];
		//essas duas linhas normalizam os tamanhos dos numeros;
		int[] n1 = copiaBinario(tamanho, int1);
		int[] n2 = copiaBinario(tamanho, int2);
		boolean vem = false;//0
		boolean sum ;
		for(int i = 0 ; i < tamanho; i++){
			boolean B1 = n1[i] == 1;
			boolean B2 = n2[i] == 1;
			sum = (B2 ^ B1 ^ vem) || (B1&&B2&&vem);
			vem = (B2 && B1) || (B2&&vem) || (B1&&vem);
			soma[i] = (sum) ? 1 : 0;
		}
		return soma;
	}

	public void Rshift(){
		this.bits = rightShift(this.bits);
	}

	public void Lshift(){
		this.bits = leftShift(this.bits);
	}
	
	public static int[] rightShift(int[] bits) {
		for(int i = 0; i < (bits.length - 1); i++)
			bits[i] = bits[i+1];
		//bits[bits.length - 1] = bits[bits.length - 2];
		return bits;
	}
	
	public static int[] leftShift(int[] bits) {
		for(int i = (bits.length - 1); i >0; i--)
			bits[i] = bits[i-1];
		bits[0] = 0;
		return bits;
	}

	/**
	 * Realiza a multiplicação de dois números binários através do algoritmo de Booth
	 * @param multiplicando
	 * @param multiplicador
	 * @return Objeto Inteiro com resultado da multiplicação
	 */
	public static Inteiro multiplica(Inteiro multiplicando, Inteiro multiplicador) {
	
		//checa se os numeros são negativos
		boolean isMultiplicadorNegativo = multiplicador.bits[multiplicador.bits.length - 1] == 1;
		boolean isMultiplicandoNegativo = multiplicando.bits[multiplicando.bits.length - 1] == 1;
		//caso positivo, tira os numeros de complemento de dois
		int[] bitsMultiplicador = (isMultiplicadorNegativo) ? complementoDe2(multiplicador.bits) : multiplicador.bits;
		int[] bitsMultiplicando = (isMultiplicandoNegativo) ? complementoDe2(multiplicando.bits) : multiplicando.bits;
		// normaliza os tamanhos
		int tamanho = max(bitsMultiplicando.length, bitsMultiplicador.length);
		bitsMultiplicador = copiaBinario(tamanho,bitsMultiplicador);
		bitsMultiplicando = copiaBinario(tamanho,bitsMultiplicando);


		int anterior = 0;
		int tamanhoProduto = multiplicador.bits.length*2;
		int[] produto = soma(tamanhoProduto, new int[tamanhoProduto], bitsMultiplicador);
		
		for (int x = 0; x < bitsMultiplicador.length; x++) {
			int atual = bitsMultiplicador[x];
			if (atual == 1 && anterior == 0) {
				produto = subtraiMetadeEsq(multiplicando.getBits(), produto);
			} else if (atual == 0 && anterior == 1){
				produto = somaMetadeEsq(multiplicando.getBits(), produto);
			}
			produto = rightShift(produto);
			anterior = atual;
		}
		//caso apenas um deles for negativo , é preciso colocar o complemento de 2 no resultado,
		//pois se os dois forem positivos , ou os dois negativos, o resultado será positivo
		if(isMultiplicandoNegativo ^ isMultiplicadorNegativo){
			produto = complementoDe2(produto);
		}
		return new Inteiro(tamanhoProduto/2, produto);
	}
	
	public static Inteiro[] divide(Inteiro dividendo, Inteiro divisor) {
		int tamanhoAux = dividendo.getLengthOfBits()*2;

		//checa se os numeros são negativos
		boolean isDividendoNegativo = dividendo.bits[dividendo.bits.length - 1] == 1;
		boolean isDivisorNegativo = divisor.bits[divisor.bits.length - 1] == 1;
		//caso positivo, tira os numeros de complemento de dois
		int[] bitsDividendo = (isDividendoNegativo) ? complementoDe2(dividendo.bits) : dividendo.bits;
		int[] bitsDivisor = (isDivisorNegativo) ? complementoDe2(divisor.bits) : divisor.bits;


		int[] dividendoAux = copiaBinario(tamanhoAux, bitsDividendo);
		int[] divisorAux = novaMetadeEsq(new int[tamanhoAux], bitsDivisor);

		for(int i=0; i<dividendo.getLengthOfBits(); i++) {
			dividendoAux = leftShift(dividendoAux);
			dividendoAux = subtrai(tamanhoAux, dividendoAux, divisorAux);
			// Verifica se o resultado da subtracao mudou o sinal do dividendo
			if(dividendoAux[tamanhoAux-1] != (dividendo.bits[dividendo.bits.length - 1])) {
				// Se o sinal mudou, volta ao que estava antes
				dividendoAux = soma(tamanhoAux, dividendoAux, divisorAux);
				dividendoAux[0] = 0;
			}
			else dividendoAux[0] = 1;
		}
		int[] quociente = null;
		//caso apenas um deles for negativo , é preciso colocar o complemento de 2 no resultado,
		//pois se os dois forem positivos , ou os dois negativos, o resultado será positivo
		if(isDividendoNegativo ^ isDivisorNegativo){
			quociente = complementoDe2(getMetadeDir(dividendoAux));
		}else{
			quociente = getMetadeDir(dividendoAux);
		}
		return new Inteiro[] { new Inteiro(tamanhoAux/2, getMetadeEsq(dividendoAux)),
			new Inteiro(tamanhoAux/2, quociente )};
	}
	
	private static int[] somaMetadeEsq(int[] bitsMultiplicando, int[] produto) {
		if (produto.length  % 2 == 0) {
			int[] metadeEsq = getMetadeEsq(produto); 
			int[] metadeEsqSomada = soma(metadeEsq.length, bitsMultiplicando, metadeEsq);
			produto = novaMetadeEsq(produto, metadeEsqSomada);
		}
		return produto;
	}

	/**
	* Adiciona 1 ao binario passado como parametro
	*/
	private static int[] soma1(int[] v){
		int[] soma = new int[v.length];
		boolean vem = false;//inicialização inutil;
		boolean sum ;
		boolean B1 = (v[0] == 1);
		//assumindo vem = 0 e o primeiro bit = 1, 
		//o resultado só fica com 1 na primeira casa caso o numero ao qual eu quero adicionar 1 tenha a primeira casa = 0;
		sum = !B1;
		//assumindo vem = 0 , e o primeiro bit = 1, só "vai" 1 caso o numero ao qual eu quero adicionar 1 tenha o primeiro bit = 1
		vem = B1; 
		soma[0] = (sum) ? 1 : 0;
		for(int i = 1; i < v.length; i++){
			//aqui eu posso assumir que o segundo numero que eu estou adicionando é zero, ou seja, estou na verdade fazendo B1 + vem
			B1 = (v[i] == 1);
			sum = (B1 ^ vem);
			vem = (B1 && vem);
			soma[i] = (sum) ? 1 : 0;
		}
		return soma;
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
	
	private int getLengthOfBits() {
		return bits.length;
	}

	private int[] toBinario(int tamanho,int numero){
		int binario[] = new int[tamanho];
		boolean negativo = (numero < 0);
		if(negativo) numero = numero * -1;
		int aux = numero;
		int pBinario = 0;
		while(aux >= 2){
			if(pBinario == tamanho) {
				System.err.printf("Esse número não cabe em %d bits", tamanho);
				System.exit(1);
			}
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
		boolean negativo = bitsPraInt[bitsPraInt.length - 1] == 1;
		if (negativo)
			bitsPraInt = complementoDe2(bitsPraInt);
		for(int i = 0 ; i < this.bits.length; i++){
			result += bitsPraInt[i] * p2;
			p2 *= 2;
		}
		if(negativo) result = result * -1;
		return result;
	}
}
