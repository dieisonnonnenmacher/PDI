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
            this.matriz_cinza = this.getMatrizCinza();
            return this.imagem;
        }
        return null;
    }

    public BufferedImage get_imagem_cinza() {
        BufferedImage img_cinza = new BufferedImage(this.largura, this.altura, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster wRastro = img_cinza.getRaster();

        for (int x = 0; x < this.largura; x++) {
            for (int y = 0; y < this.altura; y++) {
                int[] px = {this.matriz_cinza[x][y], this.matriz_cinza[x][y], this.matriz_cinza[x][y]};
                wRastro.setPixel(x, y, px);
            }
        }
        img_cinza.setData(wRastro);
        return img_cinza;
    }
     public BufferedImage get_imagem_exercicio(String selectedItem) {
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

    public Integer[][] getMatrizCinza() {
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
                double red = px[0] * 0.3;//converte vermelho para tom cinza
                double green = px[1] * 0.59;//converte verde para tom cinza
                double blue = px[2] * 0.11;//converte azul para tom cinza
                matriz[x][y] = (int) Math.round(red + green + blue);//seta as novas cores do pixel
            }
        }
        this.matriz_temp = this.matriz_original;
        return matriz;
    }
    
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
    
     
    public Integer[][][] getMultiplicaMatriz( Integer [][] matrizTipo ) {
        
        Raster rastro = this.imagem.getData();
        int px[] = new int[3];
        this.matriz_original = new Integer[this.largura][this.altura][px.length];
        for (int x = 0; x < this.largura; x++) {
            for (int y = 0; y < this.altura; y++) {
                for (int b = 0; b < px.length; b++) {
                    px[b] = rastro.getSample(x, y,b);
                    px[b] = px[b]*matrizTipo[x][y]; 
                    
                }
          
            }
        }
        this.matriz_temp = this.matriz_original;
        return matriz_original;
    }

        public Integer[][][] getMatrizTranslacao(int tx, int ty){
        Integer[][] matrizTipo = new Integer[3][3];
        Integer[][][] matriz = new Integer[this.largura][this.altura][3];          
            matrizTipo[0][0] = 1;
            matrizTipo[0][1] = 0;
            matrizTipo[0][2] = tx;
            matrizTipo[1][0] = 0;
            matrizTipo[1][1] = 1;
            matrizTipo[1][0] = ty;
            matrizTipo[2][0] = 0;
            matrizTipo[2][1] = 0;
            matrizTipo[2][2] = 1;
            
            matriz = getMultiplicaMatriz(matrizTipo);
        
        return matriz;
    }


    
//    public Integer[][][] getMatrizTranslacao(int tx, int ty){
//        Integer[][][] matriz = new Integer[this.largura][this.altura][3];
//        
//        for(int x = 0;x<this.largura;x++){
//            for(int y = 0;y<this.altura;y++){
//                Integer px[] = this.matriz_original[x][y];
//                
//                int newX = x + tx;
//                int newY = y + ty;
//                if(newX < this.largura && newY < this.altura && newX >= 0 && newY >=0){
//                    matriz[newX][newY] = px;
//                }
//            }
//        }
//        
//        return matriz;
//    }
    
    
    


    
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



   
}
