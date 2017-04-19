/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processamentodigitalimagem;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.chart.JFreeChart;

/**
 *
 * @author dn34083
 */
public class Imagem {
    private String imagem_path;
    public BufferedImage imagem = null;
    public Integer largura;
    public Integer altura;
    private Integer[][] matriz_cinza;
    private Integer[][][] matriz_original;
    private Integer[][][] matriz_temp = null;
    private Integer[][][] matriz_bkp = null;
    private Integer[][][] matrizCinza;
    private double[][] matriz_multiplicacao = new double[3][3];
    
    public Imagem(){
        this.resetMatrizMult();
    }
    
    public void resetMatrizMult(){
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                this.matriz_multiplicacao[i][j] = (double)0;
            }
        }
    }
  
    public BufferedImage find_imagem() throws IOException {


        JFileChooser fc_imagem = new JFileChooser();
        File folder = Tela.getApplicationImagesPath();
        fc_imagem.setCurrentDirectory(folder);
        fc_imagem.setDialogTitle("Selecione uma imagem (.jpg)");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".jpg, .png", "jpg", "jpeg", "JPG", "JPEG", "png", "PNG");
        fc_imagem.setFileFilter(filter);
        int retorno = fc_imagem.showOpenDialog(null);
        if (retorno == JFileChooser.APPROVE_OPTION) {
            //a janela de escolher arquivo abriu
            this.imagem_path = fc_imagem.getSelectedFile().getAbsolutePath();
            if (!this.checkExtensoes()) {
                JOptionPane alert = new JOptionPane();
                alert.showMessageDialog(null, "Apenas arquivos de imagens jpg e png");
            }
            File f = new File(imagem_path);
            this.imagem = ImageIO.read(f);
            this.load_image_properties();
            this.matriz_cinza = this.getMatrizCinzaCalc();
            return this.imagem;
            
        }
        return null;
    }

//    public BufferedImage get_imagem_cinza() {
//        BufferedImage img_cinza = new BufferedImage(this.largura, this.altura, BufferedImage.TYPE_BYTE_GRAY);
//        WritableRaster wRastro = img_cinza.getRaster();
//
//        for (int x = 0; x < this.largura; x++) {
//            for (int y = 0; y < this.altura; y++) {
//                int[] px = {this.matriz_cinza[x][y], this.matriz_cinza[x][y], this.matriz_cinza[x][y]};
//                wRastro.setPixel(x, y, px);
//            }
//        }
//        img_cinza.setData(wRastro);
//        return img_cinza;
//    }
     public BufferedImage getImagemExercicio(String selectedItem) {
          System.out.println(selectedItem);
        BufferedImage imgExercicio = new BufferedImage(this.largura, this.altura, BufferedImage.TYPE_INT_RGB);
        WritableRaster wRastro = imgExercicio.getRaster();
        String exercicio = selectedItem;
       // System.out.println("teste");
        int comparar = 150;
        double mediaD =  Double.parseDouble(getMedia());
        int media = (int) mediaD;
        double medianaD =  Double.parseDouble(getMediana());
        int mediana = (int) medianaD;
        double modaD =  Double.parseDouble(getModa());
        int moda = (int) modaD;


        for (int x = 0; x < this.largura; x++) {
            for (int y = 0; y < this.altura; y++) {
                int cor = this.matriz_cinza[x][y];
                if((exercicio == "A" ) && cor >= comparar){//recebem o inteiro do resultado obtido no exercício 5(media)
                    cor = media;
                }else if((exercicio == "B"  ) && cor >= comparar){//recebem o inteiro do resultado obtido no exercício 6(mediana)
                    cor = mediana;
                }else if(( exercicio == "C" ) && cor >= comparar){//recebem o inteiro do resultado obtido no exercício 7(moda)
                    cor = moda;
                }else if (exercicio == "D"){ //Valores maiores que a media de toda a imagem recebem Branco
                    if(cor > media){
                        cor = 255;
                    }
                    
                }else if(exercicio == "E"){  //Valores maiores que a media de toda a imagem recebem Branco, e menores recebem preto
                    if(cor < mediana){
                        cor = 0;
                    }  
                    if(cor > media){
                        cor = 255;
                    }
                }        
                int[] px = {cor,cor,cor};
                wRastro.setPixel(x, y, px);
            }
        }
        imgExercicio.setData(wRastro);
        return imgExercicio;
    }
    

    public void gera_histograma() {
        HistogramDataset dsHistogramaImagem = new HistogramDataset();

        double[] arrPixels = new double[this.largura * this.altura];
        int aux = 0;

        for (int x = 0; x < this.largura; x++) {
            for (int y = 0; y < this.altura; y++) {
                arrPixels[aux] = this.matriz_cinza[x][y];
                aux = aux + 1;
            }
        }

   //      adiciona os itens dos píxels no gráfico
        dsHistogramaImagem.addSeries("Total pixels", arrPixels, 256, 0, 256);
        JFreeChart chart = ChartFactory.createHistogram(
                                                            "Histograma da Imagem Cinza",
                                                           "Tonalidade de cinza",
                                                            "Quantidade de vezes",
                                                            dsHistogramaImagem,
                                                            PlotOrientation.VERTICAL,
                                                            false,
                                                            true,
                                                            true
                                                       );

        ChartPanel pnlChart = new ChartPanel(chart);
        pnlChart.setBounds(0, 0, 800, 600);
        JDialog painel = new JDialog();
        painel.setResizable(false);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int pnlW= 807;
        int pnlH= 630;
        int pnlX = (int)(screenSize.getWidth()-pnlW)/2;
        int pnlY = (int)(screenSize.getHeight()-pnlH)/2;
        if(pnlX<0){
            pnlX = 0;
        }
        if(pnlY<0){
            pnlY = 0;
        }
        painel.setBounds(pnlX, pnlY, pnlW, pnlH);
        painel.setTitle("Histograma da Imagem Cinza");
        painel.setVisible(true);
        painel.add(pnlChart);
    }
    public Integer[][] getMatrizCinzaCalc() {
        Integer[][] matriz = new Integer[this.largura][this.altura];
        Raster rastro = this.imagem.getData();
        int px[] = new int[3];
        this.matriz_original = new Integer[this.largura][this.altura][px.length];
        for (int x = 0; x < this.largura; x++) {
            for (int y = 0; y < this.altura; y++) {
                for (int b = 0; b < px.length; b++) {
                    px[b] = rastro.getSample(x, y, b);
                }
                this.matriz_original[x][y][0] = rastro.getSample(x, y, 0);
                this.matriz_original[x][y][1] = rastro.getSample(x, y, 1);
                this.matriz_original[x][y][2] = rastro.getSample(x, y, 2);
                double red = px[0] * 0.5;//converte vermelho para tom cinza
                double green = px[1] * 0.419;//converte verde para tom cinza
                double blue = px[2] * 0.081;//converte azul para tom cinza
//                double red = px[0] * 0.3;//converte vermelho para tom cinza
//                double green = px[1] * 0.59;//converte verde para tom cinza
//                double blue = px[2] * 0.11;//converte azul para tom cinza
                matriz[x][y] = (int) Math.round(red + green + blue);//seta as novas cores do pixel

            }
        }
        return matriz;
    }
    //Tratar os valores Brancos
    public Integer[][][] getMatrizCinza() {
        Integer[][][] matriz = new Integer[this.largura][this.altura][3];
        Integer px[] = new Integer[3];
        //this.matriz_original = new Integer[this.largura][this.altura][px.length];
        for (int x = 0; x < this.largura; x++) {
            for (int y = 0; y < this.altura; y++) {
                   px = this.matriz_original[x][y];

                
                double red = px[0] * 0.2125;//converte vermelho para tom cinza
                double green = px[1] * 0.7154;//converte verde para tom cinza
                double blue = px[2] * 0.0721;//converte azul para tom cinza
                matriz[x][y][0] = (int) (red + green + blue);//seta as novas cores do pixel
                matriz[x][y][1]= (int) (red + green + blue);//seta as novas cores do pixel
                matriz[x][y][2] = (int)(red + green + blue);//seta as novas cores do pixel
//                matriz[x][y][0] = (int) Math.round(red + green + blue);//seta as novas cores do pixel
//                matriz[x][y][1]= (int) Math.round(red + green + blue);//seta as novas cores do pixel
//                matriz[x][y][2] = (int) Math.round(red + green + blue);//seta as novas cores do pixel
            }
        }
        
        this.matriz_temp = this.matriz_original;
        matriz_original = matriz;
        return matriz;
    }
    
//    public Integer[][][] getMatrizCinza() {
//        Integer[][][] matriz = new Integer[this.largura][this.altura][3];
//        Raster rastro = this.imagem.getData();
//        int px[] = new int[3];
//        this.matriz_original = new Integer[this.largura][this.altura][px.length];
//        for (int x = 0; x < this.largura; x++) {
//            for (int y = 0; y < this.altura; y++) {
//                for (int b = 0; b < px.length; b++) {
//                    px[b] = rastro.getSample(x, y, b);
//                }
//                this.matriz_original[x][y][0] = rastro.getSample(x, y, 0);
//                this.matriz_original[x][y][1] = rastro.getSample(x, y, 1);
//                this.matriz_original[x][y][2] = rastro.getSample(x, y, 2);
//                double red = px[0] * 0.2125;//converte vermelho para tom cinza
//                double green = px[1] * 0.7154;//converte verde para tom cinza
//                double blue = px[2] * 0.0721;//converte azul para tom cinza
//                matriz[x][y][0] = (int) Math.round(red + green + blue);//seta as novas cores do pixel
//                matriz[x][y][1]= (int) Math.round(red + green + blue);//seta as novas cores do pixel
//                matriz[x][y][2] = (int) Math.round(red + green + blue);//seta as novas cores do pixel
//            }
//        }
//        this.matriz_temp = this.matriz_original;
//        matriz_original = matriz;
//        return matriz;
//    }
//    
    public String getMedia( ){
        double media = 0;
        String aux;
        int soma = 0;
        for(int x=0;x<this.largura;x++){
            for(int y=0;y<this.altura;y++){
                soma = this.matriz_cinza[x][y]+soma;
            }
        }
        media = soma/(this.largura*this.altura);
        aux = String.valueOf(media);
        return aux; 
    }
    
    
     public String getMediana(){
        double mediana = 0;
        String aux;
        int index = 0;
        Integer[] vtCores = new Integer[this.largura*this.altura];
        for(int x = 0; x<this.largura;x++){
            for(int y = 0; y<this.altura;y++){
                vtCores[index] = this.matriz_cinza[x][y];
                index++;
            }
        }
        Arrays.sort(vtCores);
        mediana = vtCores[(vtCores.length/2)+1];
        aux = String.valueOf(mediana);
        return aux; 
  
    }
    

        public String getModa(){
        int moda = 0;
        String aux;
        Integer[] vtCores = new Integer[256];
        int cor = 0;
        for(int x = 0;x<this.largura;x++){
            for(int y=0;y<this.altura;y++){
                cor = this.matriz_cinza[x][y];
                if(vtCores[cor] == null){
                    vtCores[cor] = 0;
                }
                vtCores[cor] = vtCores[cor]+1;
            }
        }
        
        int total = 0;
        
        for(cor = 0; cor<vtCores.length;cor++){
            if(vtCores[cor] != null && vtCores[cor] > total){
                total = vtCores[cor];
                moda = cor;
            }
        }
        aux = String.valueOf(moda);
        return aux;
    }

    
    public String getVariancia(){
        double variancia = 10001d;
        double somaDesvios = 0;
        double desvio_padrao = 0;
        double media = Double.parseDouble(this.getMedia()); 

        DecimalFormat format = new DecimalFormat();  
        format.setMaximumFractionDigits(2);  
        format.setMinimumFractionDigits(1);  
        for(int x=0;x<this.largura;x++){
            for(int y=0;y<this.altura;y++){
                desvio_padrao = Math.pow(this.matriz_cinza[x][y]-media,2);
                somaDesvios = somaDesvios+desvio_padrao;
            }
        }
        variancia = somaDesvios/(this.largura*this.altura);
        return format.format(variancia);
        
    }
     public Integer[][][] resetMatriz(){

       // Integer[][][] matrizRetorno = new Integer[this.largura][this.altura][3];
        matriz_original = matriz_temp;
        return matriz_original;
     }
    public Integer[][][] multiplicaMatriz(double xoffset,double yoffset,double zoom){
        int minX = 0;
        int minY = 0;
        int tempLargura=largura*(int)zoom;
        int tempAltura=altura*(int)zoom;
        int aux = (int)zoom;
        
        Integer[][][] matriz_retorno = new Integer[this.largura][this.altura][3];
        
        for(int x=0;x<this.largura;x++){
            for(int y=0;y<this.altura;y++){
                Integer[] px = this.matriz_original[x][y];
                //Integer[] px = this.matriz_temp[x][y];
                double newX = ((x*this.matriz_multiplicacao[0][0])+(y*this.matriz_multiplicacao[0][1])+(1*this.matriz_multiplicacao[0][2]));
                double newY = ((x*this.matriz_multiplicacao[1][0])+(y*this.matriz_multiplicacao[1][1])+(1*this.matriz_multiplicacao[1][2]));
           
                newX = newX + xoffset;
                newY = newY + yoffset;
                if(newX < minX){
                    minX = (int)newX;
                }
                if(newY < minY){
                    minY = (int)newY;
                }
                if(newX < this.largura && newY < this.altura && newX >= 0 && newY >=0){
                    matriz_retorno[(int)newX][(int)newY] = px;
                }
            }
        }
        xoffset = minX * -1;
        yoffset = minY * -1;
        if(xoffset != 0 || yoffset != 0){
            matriz_retorno = multiplicaMatriz(xoffset, yoffset,zoom);//a posição inicial precisa ser 0
        }
        matriz_original = matriz_retorno;
       // this.matriz_cinza = getMatrizCinza();
        return matriz_retorno;
    }
    
     public Integer[][][] getMatrizRotacao(int angulo){
        resetMatrizMult();
        
        double rad = (Math.PI/180) * angulo;
	double seno = (int) Math.sin(rad);
	double cosseno = (int)Math.cos(rad);
        double senoNeg = seno *(-1);

        setXYMatrizMult(0, 0, cosseno);
        setXYMatrizMult(0, 1, senoNeg);
        setXYMatrizMult(1, 0, seno);
        setXYMatrizMult(1, 1, cosseno);
        setXYMatrizMult(2, 2, 1);
        
        matriz_original = this.multiplicaMatriz(0,0,1);
        return matriz_original;
    }
    
        public Integer[][][] getMatrizEspelhamento(int direcao){
        this.resetMatrizMult();
        if(direcao == 0){//horizontal
            this.setXYMatrizMult(0, 0, -1);
            this.setXYMatrizMult(1, 1, 1);
            this.setXYMatrizMult(2, 2, 1);
        }else{//vertical
            this.setXYMatrizMult(0, 0, 1);
            this.setXYMatrizMult(1, 1, -1);
            this.setXYMatrizMult(2, 2, 1);
        }
        matriz_original = this.multiplicaMatriz(0, 0,1);
        return matriz_original;
    }
        public Integer[][][] getZoom(double nivel){
        this.resetMatrizMult();
        this.setXYMatrizMult(0, 0, nivel);
        this.setXYMatrizMult(1, 1, nivel);
        this.setXYMatrizMult(2, 2, 1);
        
//        for(int x=0;x<this.largura;x++){
//            for(int y=0;y<this.altura;y++){
//                matriz_original[x][y] = this.matriz_original[ x * (int)nivel][y* (int) nivel];
//            }    
//        }       
        matriz_original = this.multiplicaMatriz(0,0,nivel); 
        return matriz_original;
    }
        
        
        
    public Integer[][][] getMatrizTranslacao(int x, int y){
     this.resetMatrizMult();
        this.setXYMatrizMult(0, 2, x);
        this.setXYMatrizMult(0, 0, 1);
        this.setXYMatrizMult(1, 2, y);
        this.setXYMatrizMult(1, 1, 1);
        this.setXYMatrizMult(2, 2, 1);
        
        //matriz_temp = matriz_original;
        matriz_original = this.multiplicaMatriz(0, 0,1);
        return this.multiplicaMatriz(0, 0,1);
        
    }

   
     
    public BufferedImage createImagemByMatriz(Integer matriz[][][]){
        BufferedImage imagem_nova = new BufferedImage(matriz.length, matriz[0].length, BufferedImage.TYPE_INT_RGB);
        WritableRaster wRastro = imagem_nova.getRaster();
        
        for (int x = 0; x < matriz.length; x++) {
            for (int y = 0; y < matriz[0].length; y++) {
                int px[] = new int[3];
                for(int i=0;i<px.length;i++){
                    if(matriz[x][y][i] != null){
                        px[i] = matriz[x][y][i];
                        continue;
                    }
                    px[i] = 255;//se for nulo o px, seta como preto
                }
                wRastro.setPixel(x, y, px);
            }
        }
        imagem_nova.setData(wRastro);
        return imagem_nova;
    }
    
    public Integer[][][] get_matriz_imagem_temp(){
        return this.matriz_temp;
    }
    
    private void load_image_properties() {
        this.altura = this.imagem.getHeight();
        this.largura = this.imagem.getWidth();
    }

    private boolean checkExtensoes() {
        if (this.imagem_path.toLowerCase().endsWith(".jpg") || this.imagem_path.toLowerCase().endsWith(".png")) {
            return true;
        }
        return false;
    }
    
    private void setXYMatrizMult(int x, int y, double valor){
        this.matriz_multiplicacao[x][y] = valor;
    }

    Integer[][][] getSalvar() {
        matriz_temp = matriz_original;
        return matriz_temp;
    }
    //Ajustar metodo
    Integer[][][] getExtracaoRuidos() {
         
        int fundo=0;
        Integer[][][] matrizRetorno = new Integer[this.largura][this.altura][3];
        Integer[][] matriz = new Integer[3][3];
        matriz[0][0] = 1;
	matriz[1][0] = 2;
	matriz[2][0] = 1;
	matriz[0][1] = 2;
	matriz[1][1] = 4;
	matriz[2][1] = 2;
	matriz[0][2] = 1;
	matriz[1][2] = 2;
	matriz[2][2] = 1;
	Integer[][][] matrizAux = new Integer[this.largura][this.altura][3];
		for(int x = 0;x<this.largura-1;x++){
		    for(int y = 0;y<this.altura-1;y++){
			Integer[] min = this.matriz_original[x][y];
		       	for(int m=0;m<3;m++){
			   for(int i = 0;i<matriz.length;i++){
				if(x+i>=this.largura){
				break;
                                }
				for(int j = 0; j<matriz.length;j++){
				    if(y+j>=this.altura){
                                    break;
                                    }
                                    if(this.matriz_original[x+i][y+j][m] != fundo && matriz[i][j] != null){
					if(fundo <=128){
                                            min[m] = Math.min(min[m], this.matriz_original[x+i][y+j][m]);
					}else{
                                           min[m] = Math.max(min[m], this.matriz_temp[x+i][y+j][m]);
					}
				    }
				}
			    }
			    for(int i = 0;i<matriz.length;i++){
				if(x+i>=this.largura){
                                   break;
				}
                                for(int j = 0; j<matriz.length;j++){
                                    if(y+j>=this.altura){
                                       break;
                                    }
                                    if(matriz[i][j] != null){
					matrizAux[x+i][y+j][m] = min[m]+matriz[i][j];
                                    }
                        	}
                            }
                        }
		   }
		}
			
	return this.matriz_original;

    }

    Integer[][][] getBrilho(int brilho) {
         int a = 1;
         int b = brilho;
       Integer[][][] matriz_retorno = new Integer[this.largura][this.altura][3]; 
        for(int x=0;x<this.largura;x++){
            for(int y=0;y<this.altura;y++){
                Integer[] px = this.matriz_original[x][y];
               /// System.out.println(px);
                Integer[] aux = new Integer[3];    
                    for(int j=0;j<3;j++){
                  //      System.out.println(px[j]);
                        aux[j] = (a * px[j] )+b;
                        if(aux[j] > 255){
                            aux[j] = 255;
                        }
                        if(aux[j] < 0){
                            aux[j] = 0;
                        }
                    //     System.out.println(px[j]);
                    }
                matriz_retorno [x][y] = aux;
            }
        }
     //matriz_temp = matriz_original;  
     matriz_original = matriz_retorno;
     return matriz_retorno;

    }

    Integer[][][] getContraste(double contraste) {
       double a = contraste;
       int b = 0;
       Integer[][][] matriz_retorno = new Integer[this.largura][this.altura][3]; 
        for(int x=0;x<this.largura;x++){
            for(int y=0;y<this.altura;y++){
                Integer[] px = this.matriz_original[x][y];
               /// System.out.println(px);
                Integer[] aux = new Integer[3];    
                    for(int j=0;j<3;j++){
                  //      System.out.println(px[j]);
                        aux[j] = (int)(a * px[j] )+b;
                        if(aux[j] > 255){
                            aux[j] = 255;
                        }
                        if(aux[j] < 0){
                            aux[j] = 0;
                        }
                    //     System.out.println(px[j]);
                    }
                matriz_retorno [x][y] = aux;
            }
        }
     //matriz_temp = matriz_original;
     matriz_original = matriz_retorno;
     return matriz_retorno;
    }
}    
