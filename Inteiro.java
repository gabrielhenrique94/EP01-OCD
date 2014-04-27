class Inteiro{
	
	protected int[] bits;
	
	/**
	* construtor que incializa o interio com um valor pre-definido
	* @param valor inicial que o numero recebera
	*/
	Inteiro(int tamanho, int numero){
		this.bits = this.toBinario(tamanho, numero);
	}

	Inteiro (int tamanho){
		//aqui eu me aproveito do fato do java inicializar tudo com 0
		this.bits = new int[tamanho];
	}

	Inteiro(int tamanho, int[] numero){
		this.bits = new int[tamanho];
		for(int i = 0 ; i < tamanho; i++){
			if(i >= numero.length)
				this.bits[i] = 0;
			else if(numero[i] == 0 || numero[i] == 1)
				this.bits[i] = numero[i];
			else{
				System.err.println("Inteiro.contrutor: vetor contem numeros que não são 0 ou 1");
				break;
			}
		}
	}

	public int max(int a, int b){
		return (a > b )? a : b;
	}

	public Inteiro complementoDe2(){
		Inteiro notThis = new Inteiro(this.bits.length);
		for(int a = 0; a < this.bits.length; a++)
			notThis.bits[a] = (this.bits[a] == 0)? 1 : 0;
		return soma(notThis.bits.length,notThis, new Inteiro(1,1));
	}

	public static Inteiro subtrai(int tamanho, Inteiro int1, Inteiro int2){
		return soma(tamanho, int1, int2.complementoDe2());
	}

	public static Inteiro soma(int tamanho,Inteiro int1, Inteiro int2){
		int[] soma = new int[tamanho];
		int vem = 0;
		for(int i = 0 ; i < tamanho; i++){
			if(i >= int1.bits.length)
				soma[i] = int2.bits[i];
			else if(i >= int2.bits.length)
				soma[i] = int1.bits[i];
			else{
				int sum = int1.bits[i] + int2.bits[i] + vem;
				if(sum > 1){
					sum -= 2;
					vem = 1;
				}else{
					vem = 0;
				}
				soma[i] = sum;
			}
		}
		return new Inteiro(tamanho,soma);
	}

	public void Rshift(){
		int aux;
		for(int i = 0; i <	(this.bits.length - 1);i++)
			this.bits[i+1] = this.bits[i];
	}

	public void Lshift(){
		int aux;
		for(int i = 0; i <	(this.bits.length - 1);i++)
			this.bits[i] = this.bits[i+1];
	}

	public int[] toBinario(int tamanho,int numero){
		int binario[] = new int[tamanho];
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
		for(int i = 0 ; i < this.bits.length; i++){
			result += this.bits[i] * p2;
			p2 *= 2;
		}
		return result;
	}

	public static void main(String[] args){
		Inteiro i = new Inteiro(32,200);
		Inteiro a = new Inteiro(32,105);
		System.out.println(i);
		System.out.println(i.toInteger());
		Inteiro soma = Inteiro.soma(32, i,a);
		System.out.println(soma.toInteger());
		Inteiro subtracao = Inteiro.subtrai(32,i,a);
		System.out.println(subtracao.toInteger());
	}
}